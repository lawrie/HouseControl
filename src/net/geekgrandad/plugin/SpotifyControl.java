package net.geekgrandad.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.MediaControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class SpotifyControl implements MediaControl {
	
	// Music server commands
	private static final String CMD_SET_VOLUME = "setvol";
	private static final String CMD_GET_VOLUME = "getvol";
	private static final String CMD_PAUSE_MUSIC = "stop";
	private static final String CMD_SKIP_TRACK = "skip";
	private static final String CMD_SHUT_DOWN = "shutdown";
	private static final String CMD_REBOOT = "reboot";
	
    private Reporter reporter;
	private Config config;
	private boolean musicOn = true;
	private int volume = 0;
	private String currentPlaylist = " ";
	boolean playing = true;

	@Override
	public void setVolume(int id, int volume) {
		sendMusicCmd(id, CMD_SET_VOLUME + " " + volume, false);
	}

	@Override
	public int getVolume(int id) {
		try {
			int r = Integer.parseInt(sendMusicCmd(id, CMD_GET_VOLUME, true));
			musicOn = true;
			return r;
		} catch (Exception e) {
			musicOn = false;
			return volume;
		}
	}

	@Override
	public String getArtist(int id) {
		reporter.error("Spotify: getArtist not supported");
		return null;
	}

	@Override
	public String getTrack(int id) {
		reporter.error("Spotify: getTrack not supported");
		return null;
	}
	
	@Override
	public void say(int id, String msg) {
		sendMusicCmd(id, "say " + msg, false);
	}

	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();	
		reporter = provider.getReporter();
	}
	
	private synchronized String sendMusicCmd(int id, String cmd, boolean force) {
		if (!force && !musicOn)
			return "Error";
		try {
			Socket sock = new Socket(config.mediaHosts[id-1], config.listenPort);
			sock.setSoTimeout(config.mediaSocketTimeout);
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			out.println(cmd);
			reporter.print("Sending " + cmd + " to media server: " + config.mediaHosts[id-1]);
			String ret = in.readLine();
			reporter.print("Media server: " + ret);
			musicOn = true;
			out.close();
			in.close();
			sock.close();
			return ret;
		} catch (UnknownHostException e) {
			reporter.error("Unknown host in sendCmd");
			musicOn = false;
			return ("Error");
		} catch (IOException e) {
			reporter.error("IO Exception in sendCmd");
			musicOn = false;
			return "Error";
		}
	}

	@Override
	public void start(int id, String playlist, boolean repeat) throws IOException {
		reporter.print("Playing " + playlist);
		String list = config.getPlaylists().get(playlist);
		if (list != null) {
			currentPlaylist = playlist;
			playing = true;
			sendMusicCmd(id, "play " + list + "?autoplay=true", false);
		} else {
			reporter.error("No such playlist: " + playlist);
		}	
	}

	@Override
	public void open(int id, String file, boolean repeat) throws IOException {
		reporter.error("Spotify: open not supported");	
	}

	@Override
	public void type(int id, String s) throws IOException {
		reporter.error("Spotify: type not supported");	
	}

	@Override
	public void select(int id, String service) throws IOException {
		reporter.error("Spotify: select not supported");	
	}

	@Override
	public void pause(int id) throws IOException {
		playing = !playing;
		sendMusicCmd(id, CMD_PAUSE_MUSIC, false);
	}

	@Override
	public void stop(int id) throws IOException {
		playing = !playing;
		sendMusicCmd(id, CMD_PAUSE_MUSIC, false);	
	}

	@Override
	public void play(int id) throws IOException {
		playing = !playing;
		sendMusicCmd(id, CMD_PAUSE_MUSIC, false);	
	}

	@Override
	public void record(int id) throws IOException {
		reporter.error("Spotify: record not supported");
	}

	@Override
	public void ff(int id) throws IOException {
		reporter.error("Spotify: ff not supported");	
	}

	@Override
	public void fb(int id) throws IOException {
		reporter.error("Spotify: fb not supported");		
	}

	@Override
	public void skip(int id) throws IOException {
		sendMusicCmd(id, CMD_SKIP_TRACK, false);	
	}

	@Override
	public void skipb(int id) throws IOException {
		reporter.error("Spotify: skipb not supported");	
	}

	@Override
	public void slow(int id) throws IOException {
		reporter.error("Spotify: slow not supported");	
	}

	@Override
	public void delete(int id) throws IOException {
		reporter.error("Spotify: delete not supported");	
	}

	@Override
	public void up(int id) throws IOException {
		reporter.error("Spotify: up not supported");
	}

	@Override
	public void down(int id) throws IOException {
		reporter.error("Spotify: up not supported");	
	}

	@Override
	public void left(int id) throws IOException {
		reporter.error("Spotify: left not supported");
	}

	@Override
	public void right(int id) throws IOException {
		reporter.error("Spotify: right not supported");	
	}

	@Override
	public void ok(int id) throws IOException {
		reporter.error("Spotify: ok not supported");
	}

	@Override
	public void back(int id) throws IOException {
		reporter.error("Spotify: back not supported");	
	}

	@Override
	public void lastChannel(int id) throws IOException {
		reporter.error("Spotify: lastChannel not supported");	
	}

	@Override
	public void option(int id, String option) throws IOException {
		reporter.error("Spotify: option not supported");
	}

	@Override
	public void volumeUp(int id) throws IOException {
		reporter.error("Spotify: volumeUp not supported");
	}

	@Override
	public void volumeDown(int id) throws IOException {
		reporter.error("Spotify: volumeDown not supported");		
	}

	@Override
	public void mute(int id) throws IOException {
		reporter.error("Spotify: mute not supported");
	}

	@Override
	public void setChannel(int id, int channel) throws IOException {
		reporter.error("Spotify: setChannel not supported");	
	}

	@Override
	public int getChannel(int id) throws IOException {
		reporter.error("Spotify: getChannel not supported");
		return 0;
	}

	@Override
	public void channelUp(int id) throws IOException {
		reporter.error("Spotify: channelUp not supported");	
	}

	@Override
	public void channelDown(int id) throws IOException {
		reporter.error("Spotify: channelDown not supported");
	}

	@Override
	public void thumbsUp(int id) throws IOException {
		reporter.error("Spotify: thumbsUp not supported");
		
	}

	@Override
	public void thumbsDown(int id) throws IOException {
		reporter.error("Spotify: thumbsDown not supported");
	}

	@Override
	public void digit(int id, int n) throws IOException {
		reporter.error("Spotify: digit not supported");
	}

	@Override
	public void color(int id, int n) throws IOException {
		reporter.error("Spotify: color not supported");		
	}

	@Override
	public void turnOn(int id) throws IOException {
		reporter.error("Spotify: turnOn not supported");
	}

	@Override
	public void turnOff(int id) throws IOException {
		sendMusicCmd(id, CMD_SHUT_DOWN, false);	
	}

	@Override
	public void pin(int id) throws IOException {
		reporter.error("Spotify: pin not supported");		
	}

	@Override
	public void setSource(int id, int source) throws IOException {
		reporter.error("Spotify: setSource not supported");		
	}

	@Override
	public String getAlbum(int id) throws IOException {
		reporter.error("Spotify: getAlbum not supported");
		return "Unknown";
	}

	@Override
	public String getPlaylist(int id) throws IOException {
		return currentPlaylist;
	}

	@Override
	public void pageUp(int id) throws IOException {
		reporter.error("SPOTIFY: pageup not supported");
	}

	@Override
	public void pageDown(int id) throws IOException {
		reporter.error("SPOTIFY: page down not supported");	
	}

	@Override
	public boolean isPlaying(int id) throws IOException {
		return playing;
	}

	@Override
	public void reboot(int id) throws IOException {
		sendMusicCmd(id, CMD_REBOOT, false);
	}

	@Override
	public void setPlayer(int id, int playerId) throws IOException {
		reporter.error("SPOTIFY: player not supported");	
	}

	@Override
	public void setRepeat(int id, boolean repeat) throws IOException {
		reporter.error("SPOTIFY: shuffle not supported");
	}

	@Override
	public void setShuffle(int id, boolean shuffle) throws IOException {
		reporter.error("SPOTIFY: shuffle not supported");
	}
}
