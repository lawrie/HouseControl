package net.geekgrandad.plugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.MediaControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class XBMCControl implements MediaControl {
	private boolean debug = true;
	private Reporter reporter;
	private Config config;
	private HttpClient client = new DefaultHttpClient();
	private int volume = 0; 
	private int playerId = 0;

	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();	
		config = provider.getConfig();
	}
	
	private String execute(int id, String json) {
		if (debug) reporter.print("XBMC: " + json + " host = " + config.mediaServers[id-1]);
		HttpPost post = new HttpPost("http://" + config.mediaServers[id-1] + "/jsonrpc");
		BasicHeader header = new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
		HttpEntity entity;
		HttpResponse response;
		
		post.addHeader(BasicScheme.authenticate(
				 new UsernamePasswordCredentials("xbmc", "xbmc"),
				 "UTF-8", false));
		post.addHeader(header);
        try {
        	entity = new StringEntity(json);
        	post.setEntity(entity);
        	response = client.execute(post);
        	String responseString = EntityUtils.toString(response.getEntity());
        	if (debug) reporter.print("XBMC Response: " + responseString);
        	return responseString;
		} catch (UnsupportedEncodingException e) {
			// nothing
		} catch (ClientProtocolException e) {
			// nothing
		} catch (IOException e) {
			// nothing
		}
        reporter.error("XBMC: Exception in execute");
        return "Error";
	}
	
	public void home(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.Home\", \"id\": 1}");
	}


	@Override
	public void start(int id, String playlist, boolean repeat) {
		reporter.print("XBMC: playlist:" + playlist);
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Open\", \"params\": { \"item\": {\"file\": \"special://profile/playlists/music/" + playlist + ".xsp\" } }, \"id\": 1}"); 	
	}

	@Override
	public void open(int id, String file, boolean repeat) {
		getArtists(id);
		getGenres(id);
		getAlbums(id);
		getMusicVideos(id);
		int sp = file.indexOf(" ");
		String xbmcId = file.substring(sp+1);
		String prop = file.substring(0, sp);
		String quote = "\"";
		try {
			Integer.parseInt(xbmcId);
		} catch (NumberFormatException e) {
			if (prop.equals("artist")) xbmcId = "" + artists.get(xbmcId);
			else if (prop.equals("genre")) xbmcId = "" + genres.get(xbmcId);
			else if (prop.equals("album")) xbmcId = "" + albums.get(xbmcId);
			else if (prop.equals("musicvideo")) xbmcId = "" + musicVideos.get(xbmcId);
		}
		
		if (prop.equals("slideshow")) {
			prop = "directory";
			xbmcId = "/storage/pictures/" + xbmcId;
		}
		
		if (!prop.equals("directory") && !prop.equals("file")) {
			prop = prop + "id";
			quote = "";
		}

		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Open\", \"params\": { \"item\": {\"" + prop + "\":" + quote + xbmcId + quote + "} }, \"id\": 1}");
	}
	@Override
	public void type(int id, String s) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.SendText\", \"params\": {\"text\":\"" + s +"\"}, \"id\": 1}");	
	}
	
	public void activate(int id, String window) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"GUI.ActivateWindow\", \"params\": {\"window\": \"" + window + "\"}, \"id\": 1}");
	}
	
	public void activate(int id, String window, String section) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"GUI.ActivateWindow\", \"params\": {\"window\": \"" + window + "\", \"parameters\":[\"" + section + "\"]}, \"id\": 1}");
	}

	@Override
	public void select(int id, String service) {
		getAddons(id);
		if (service.equals("home")) home(id);
		else if (service.equals("context")) contextMenu(id);
		else if (service.equals("info")) activate(id,"infodialog");
		else if (service.equals("system")) activate(id, "systeminfo");
		else if (isWindow(service)) activate(id, service);
		else execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Addons.ExecuteAddon\", \"params\": {\"addonid\": \"" + findPlugin(service) + "\"}, \"id\": 1}");	
	}

	@Override
	public void pause(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.PlayPause\", \"params\": { \"playerid\":" + playerId + "}, \"id\": 1}");	
	}

	@Override
	public void stop(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Stop\", \"params\": { \"playerid\":" + playerId + "}, \"id\": 1}");	
	}

	@Override
	public void play(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.PlayPause\", \"params\": { \"playerid\": " + playerId + "}, \"id\": 1}");	
	}

	@Override
	public void record(int id) {
		reporter.error("XBMC: record not supported");
	}

	@Override
	public void ff(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.SetSpeed\", \"params\": { \"playerid\":" + playerId + ", \"speed\":2 }, \"id\": 1}");	
	}

	@Override
	public void fb(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.SetSpeed\", \"params\": { \"playerid\":" +playerId + ", \"speed\":-2 }, \"id\": 1}");	
	}

	@Override
	public void skip(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Move\", \"params\": { \"playerid\": " + playerId + ", \"direction\":\"right\" }, \"id\": 1}");		
	}

	@Override
	public void skipb(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Move\", \"params\": { \"playerid\": " + playerId + ", \"direction\":\"left\" }, \"id\": 1}");
	}

	@Override
	public void slow(int id) {
		reporter.error("XBMC: slow not supported");	
	}

	@Override
	public void delete(int id) {
		reporter.error("XBMC: delete not yet implemented");
	}

	@Override
	public void up(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.Up\", \"id\": 1}");
	}

	@Override
	public void down(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.Down\", \"id\": 1}");
	}

	@Override
	public void left(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.Left\", \"id\": 1}");	
	}

	@Override
	public void right(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.Right\", \"id\": 1}");
	}

	@Override
	public void ok(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.Select\", \"id\": 1}");		
	}

	@Override
	public void back(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.Back\", \"id\": 1}");		
	}

	@Override
	public void lastChannel(int id) {
		reporter.error("XBMC: Last channel not supported");	
	}

	@Override
	public void option(int id, String option) {
		if (option.equalsIgnoreCase("codec")) showCodec(id);
		else if (option.equalsIgnoreCase("OSD")) showOSD(id);
		else reporter.error("XBMC: option not supported: " + option);	
	}

	@Override
	public void volumeUp(int id) {
		volume++;
		setVolume(id, volume);	
	}

	@Override
	public void volumeDown(int id) {
		volume--;
		setVolume(id, volume);		
	}

	@Override
	public void mute(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Application.SetMute\", \"params\": {\"mute\":\"toggle\"}, \"id\": 1}");	
	}

	@Override
	public int getVolume(int id) {
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Application.GetProperties\", \"params\": { \"properties\": [\"volume\"]}, \"id\": 1}");
		try {
			JSONObject obj = new JSONObject(s);
			JSONObject result = (JSONObject) obj.get("result");
			return (result.getInt("volume"));		
		} catch (JSONException e) {
			reporter.error("JSON error in getTrack");
			return 0;
		}
	}

	@Override
	public void setVolume(int id, int volume) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Application.SetVolume\", \"params\": {\"volume\":" + volume + " }, \"id\": 1}");		
	}

	@Override
	public void setChannel(int id, int channel) {
		reporter.error("XBMC: Channels not supported");	
	}

	@Override
	public String getTrack(int id) {
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetItem\", \"params\": { \"properties\": [\"title\",\"album\",\"artist\"], \"playerid\":" + playerId + "}, \"id\": 1}");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			JSONObject result = (JSONObject) obj.get("result");
			return (((JSONObject) result.get("item")).getString("title"));		
		} catch (JSONException e) {
			reporter.error("JSON error in getTrack");
			return "Error";
		}
	}

	@Override
	public int getChannel(int id) {
		reporter.error("XBMC: Channels not supported");
		return 0;
	}

	@Override
	public void channelUp(int id) {
		reporter.error("XBMC: Channels not supported");	
	}

	@Override
	public void channelDown(int id) {
		reporter.error("XBMC: Channels not supported");	
	}

	@Override
	public void thumbsUp(int id) {
		reporter.error("XBMC: Rating not yet supported");		
	}

	@Override
	public void thumbsDown(int id) {
		reporter.error("XBMC: Rating not yet supported");
	}

	@Override
	public void digit(int id, int n) {
		reporter.error("XBMC: digits not supported");	
	}

	@Override
	public void color(int id, int n) {
		reporter.error("XBMC: color buttons not supported");
	}

	@Override
	public void pin(int id) {
		reporter.error("XBMC: pin not supported");	
	}

	@Override
	public void turnOn(int id) throws IOException {
		reporter.error("XBMC: turnOn not supported");
	}

	@Override
	public void turnOff(int id) throws IOException {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Application.Quit\", \"id\": 1}");	
	}

	@Override
	public void setSource(int id, int source) throws IOException {
		reporter.error("XBMC: source not supported");
	}
	
	private String findPlugin(String name) {
		if (name.equals("iplayer")) return "plugin.video.iplayer";
		else if (name.equals("navix")) return "script.navi-x";
		else if (name.equals("ted")) return "plugin.video.ted.talks";
		else if (name.equals("4od")) return "plugin.video.4od";
		else if (name.equals("aljazeera")) return "plugin.video.aljazeera";
		else if (name.equals("podcasts")) return "plugin.video.itunes_podcasts";
		else if (name.equals("youtube")) return "plugin.video.youtube";
		else if (name.equals("dilbert")) return "plugin.image.dilbert";
		return null;
	}

	@Override
	public String getArtist(int id) throws IOException {
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetItem\", \"params\": { \"properties\": [\"title\",\"album\",\"artist\"], \"playerid\":" + playerId + "}, \"id\": 1}");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			JSONObject result = (JSONObject) obj.get("result");
			return (((JSONObject) result.get("item")).getJSONArray("artist")).toString();		
		} catch (JSONException e) {
			reporter.error("JSON error in getArtist");
			return "Error";
		}
	}

	@Override
	public String getAlbum(int id) throws IOException {
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetItem\", \"params\": { \"properties\": [\"title\",\"album\",\"artist\"], \"playerid\":" + playerId + "}, \"id\": 1}");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			JSONObject result = (JSONObject) obj.get("result");
			return (((JSONObject) result.get("item")).getString("album"));		
		} catch (JSONException e) {
			reporter.error("JSON error in getAlbum");
			return "Error";
		}
	}
	
	private String[] windows = {"home", "filemanager", "infodialog", "scripts", "profiles", 
			                    "weather", "pictures", "video", "music", "programs", "settings"};
	
	public boolean isWindow(String name) {
		for(String w: windows) if (name.equals(w)) return true;
		return false;
	}

	@Override
	public void say(int id, String text) {
		alert(id,"Just saying", text);
	}

	@Override
	public String getPlaylist(int id) throws IOException {
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"PlayList.GetItems\", \"params\": {\"playlistid\":0}, \"id\": 1}");
		JSONObject obj;
		String res = "";
		try {
			obj = new JSONObject(s);
			JSONObject result = (JSONObject) obj.get("result");
			JSONArray items = ((JSONArray) result.get("items"));	
			for(int i=0;i<items.length();i++) {
				JSONObject item =  items.getJSONObject(i);
				res = res + item.getString("label") + ";";
			}
			return res;
		} catch (JSONException e) {
			reporter.error("JSON error in getPlaylist");
			return "Error";
		}
	}
	
	public boolean isPlaying(int id) {
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetActivePlayers\",\"id\": 1}");
		try {
			JSONObject obj = new JSONObject(s);
			JSONArray result = (JSONArray) obj.get("result");
			return (result.length() > 0);
		} catch (JSONException e) {
			reporter.error("JSON error in getPlaylist");
			return false;
		}
	}

	@Override
	public void pageUp(int id) throws IOException {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.ExecuteAction\",\"params\": {\"action\": \"pageup\"}, \"id\": 1}");	
	}

	@Override
	public void pageDown(int id) throws IOException {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.ExecuteAction\",\"params\": {\"action\": \"pageup\"}, \"id\": 1}");		
	}
	
	public void contextMenu(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.ContextMenu\",\"id\": 1}");
	}
	
	public void showOSD(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.ShowOSD\",\"id\": 1}");
	}
	
	public void showCodec(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.ShowCodec\",\"id\": 1}");
	}
	
	public void alert(int id, String title, String msg) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"GUI.ShowNotification\",\"params\": {\"title\": \"" + title + "\", " + "\"message\": \"" + msg + "\"}, \"id\": 1}");
	}

	@Override
	public void reboot(int id) throws IOException {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"System.Reboot\", \"id\": 1}");
	}

	@Override
	public void setPlayer(int id, int playerId) throws IOException {
		this.playerId = playerId;
	}

	@Override
	public void setRepeat(int id, boolean repeat) throws IOException {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.SetRepeat\", \"params\": {\"repeat\":\"" + (repeat ? "all" : "off")  + "\", \"playerid\":" + playerId + "}, \"id\": 1}");	
	}

	@Override
	public void setShuffle(int id, boolean shuffle) throws IOException {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.SetShuffle\", \"params\": {\"shuffle\":\"" +  (shuffle ? "true" : "false") + "\", \"playerid\":" + playerId + "}, \"id\": 1}");	
	}
	
	public void setFullScreen(int id) throws IOException {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"GUI.SetFullscreen\", \"params\": {\"fullscreen\":\"toggle\"}, \"id\": 1}");
	}
	
	public void zoom(int id, int level) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Zoom\", \"params\": { \"playerid\": " + playerId + ", \"zoom\":" + level +  "}, \"id\": 1}");
	}
	
	public void rotate(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Rotate\", \"params\": { \"playerid\": " + playerId + ", \"value\":\"clockwise\"}, \"id\": 1}");
	}
	
	public void togglePartyMode(int id) {
		boolean[] players = activePlayers(id);
		if (players[playerId]) execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.SetPartyMode\", \"params\": { \"playerid\": " + playerId + ", \"partymode\":\"toggle\"}, \"id\": 1}");
	}
	
	private HashMap<String,Integer> artists = new HashMap<String,Integer>();
	
	private void getArtists(int id) {
		if (artists.isEmpty()) {
			String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"AudioLibrary.GetArtists\", \"id\": 1}");
			try {
				JSONObject obj = new JSONObject(s);
				JSONObject result = (JSONObject) obj.get("result");
				JSONArray artists = ((JSONArray) result.get("artists"));	
				for(int i=0;i<artists.length();i++) {
					JSONObject artist =  artists.getJSONObject(i);
					String artistName = (String) artist.get("artist");
					int artistId = (Integer) artist.get("artistid");
					this.artists.put(artistName,artistId);
				}		
			} catch (JSONException e) {
				reporter.error("JSON error in getArtists");
			}
		}
	}
	
	private HashMap<String,Integer> genres = new HashMap<String,Integer>();
	
	private void getGenres(int id) {
		if (genres.isEmpty()) {
			String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"AudioLibrary.GetGenres\", \"id\": 1}");
			try {
				JSONObject obj = new JSONObject(s);
				JSONObject result = (JSONObject) obj.get("result");
				JSONArray items = ((JSONArray) result.get("genres"));	
				for(int i=0;i<items.length();i++) {
					JSONObject genre =  items.getJSONObject(i);
					String genreName = (String) genre.get("label");
					int genreId = (Integer) genre.get("genreid");
					this.genres.put(genreName,genreId);
				}		
			} catch (JSONException e) {
				reporter.error("JSON error in getGenres");
			}
		}
	}
	
	private HashMap<String,Integer> albums = new HashMap<String,Integer>();
	
	private void getAlbums(int id) {
		if (albums.isEmpty()) {
			String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"AudioLibrary.GetAlbums\", \"id\": 1}");
			try {
				JSONObject obj = new JSONObject(s);
				JSONObject result = (JSONObject) obj.get("result");
				JSONArray items = ((JSONArray) result.get("albums"));	
				for(int i=0;i<items.length();i++) {
					JSONObject album =  items.getJSONObject(i);
					String albumName = (String) album.get("label");
					int albumId = (Integer) album.get("albumid");
					this.albums.put(albumName,albumId);
				}		
			} catch (JSONException e) {
				reporter.error("JSON error in getAlbums");
			}
		}
	}
	
	private HashMap<String,Integer> musicVideos = new HashMap<String,Integer>();
	
	private void getMusicVideos(int id) {
		if (musicVideos.isEmpty()) {
			String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"VideoLibrary.GetMusicVideos\", \"id\": 1}");
			try {
				JSONObject obj = new JSONObject(s);
				JSONObject result = (JSONObject) obj.get("result");
				JSONArray items = ((JSONArray) result.get("musicvideos"));	
				for(int i=0;i<items.length();i++) {
					JSONObject item =  items.getJSONObject(i);
					String label = (String) item.get("label");
					int xbmcId = (Integer) item.get("musicvideoid");
					this.musicVideos.put(label,xbmcId);
				}		
			} catch (JSONException e) {
				reporter.error("JSON error in getMusicVideos");
			}
		}
	}
	
	private boolean[] activePlayers(int id) {
		boolean[] players = new boolean[3];
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetActivePlayers\", \"id\": 1}");
		try {
			JSONObject obj = new JSONObject(s);
			JSONArray items = (JSONArray) obj.get("result");	
			for(int i=0;i<items.length();i++) {
				JSONObject item =  items.getJSONObject(i);
				int playerId = (Integer) item.get("playerid");
				players[playerId] = true;
				reporter.print("Player " + playerId + " is active");
			}		
		} catch (JSONException e) {
			reporter.error("JSON error in getAlbums");
		}
		
		return players;
	}
	
	private HashMap<String,String> addons = new HashMap<String,String>();
	
	private void getAddons(int id) {
		if (addons.isEmpty()) {
			String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Addons.GetAddons\", \"id\": 1}");
			try {
				JSONObject obj = new JSONObject(s);
				JSONObject result = (JSONObject) obj.get("result");
				JSONArray items = ((JSONArray) result.get("addons"));	
				for(int i=0;i<items.length();i++) {
					JSONObject item =  items.getJSONObject(i);
					String type = (String) item.get("type");
					String xbmcId = (String) item.get("addonid");
					this.addons.put(xbmcId,type);
					reporter.print("Addon: " + xbmcId + " , type: " + type);
				}		
			} catch (JSONException e) {
				reporter.error("JSON error in getAddons");
			}
		}
	}
}
