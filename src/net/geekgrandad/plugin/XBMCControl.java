package net.geekgrandad.plugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
	private int volume = 0; // TODO: needs array
	private String playlist = " ";

	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();	
		config = provider.getConfig();
	}
	
	private String execute(int id, String json) {
		if (debug) reporter.print("XBMC: " + json + " host = " + config.mediaHosts[id-1]);
		HttpPost post = new HttpPost("http://" + config.mediaHosts[id-1] + "/jsonrpc");
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
		String s = playlist.replaceAll("X", " ");
		this.playlist = s;
		reporter.print("XBMC: playlist:" + s);
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Open\", \"params\": { \"item\": {\"directory\": \"/storage/music/Bob Dylan/" + s + "\" } }, \"id\": 1}"); 	
	}

	@Override
	public void open(int id, String file, boolean repeat) {
		reporter.error("XBMC: open not yet implemented");
	}

	@Override
	public void type(int id, String s) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Input.SendText\", \"params\": {\"text\": " + s +"\"}, \"id\": 1}");	
	}
	
	public void activate(int id, String window) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"GUI.ActivateWindow\", \"params\": {\"window\": \"" + window + "\"}, \"id\": 1}");
	}

	@Override
	public void select(int id, String service) {
		if (service.equals("home")) home(id);
		if (service.equals("info")) activate(id,"infodialog");
		else if (service.equals("system")) activate(id, "systeminfo");
		else if (isWindow(service)) activate(id, service);
		else execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Addons.ExecuteAddon\", \"params\": {\"addonid\": \"" + findPlugin(service) + "\"}, \"id\": 1}");	
	}

	@Override
	public void pause(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.PlayPause\", \"params\": { \"playerid\": 0 }, \"id\": 1}");	
	}

	@Override
	public void stop(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Stop\", \"params\": { \"playerid\": 0 }, \"id\": 1}");	
	}

	@Override
	public void play(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.PlayPause\", \"params\": { \"playerid\": 0 }, \"id\": 1}");	
	}

	@Override
	public void record(int id) {
		reporter.error("XBMC: record not supported");
	}

	@Override
	public void ff(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.SetSpeed\", \"params\": { \"playerid\": 0, \"speed\":2 }, \"id\": 1}");	
	}

	@Override
	public void fb(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.SetSpeed\", \"params\": { \"playerid\": 0, \"speed\":-2 }, \"id\": 1}");	
	}

	@Override
	public void skip(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Move\", \"params\": { \"playerid\": 0, \"direction\":\"right\" }, \"id\": 1}");		
	}

	@Override
	public void skipb(int id) {
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.Move\", \"params\": { \"playerid\": 0, \"direction\":\"left\" }, \"id\": 1}");
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
		reporter.error("XBMC: option not yet implemented");	
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
		execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Application.SetMute\", \"id\": 1}");	
	}

	@Override
	public int getVolume(int id) {
		return volume;
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
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetItem\", \"params\": { \"properties\": [\"title\",\"album\",\"artist\"], \"playerid\": 0}, \"id\": 1}");
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
		reporter.error("XBMC: Rating not supported");		
	}

	@Override
	public void thumbsDown(int id) {
		reporter.error("XBMC: Rating not supported");
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
		else if (name.equals("weather")) return "weather.wunderground";
		else if (name.equals("navix")) return "script.navi-x";
		return null;
	}

	@Override
	public String getArtist(int id) throws IOException {
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetItem\", \"params\": { \"properties\": [\"title\",\"album\",\"artist\"], \"playerid\": 0}, \"id\": 1}");
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
		String s = execute(id, "{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetItem\", \"params\": { \"properties\": [\"title\",\"album\",\"artist\"], \"playerid\": 0}, \"id\": 1}");
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
		reporter.error("XBMC: say not supported");		
	}
}
