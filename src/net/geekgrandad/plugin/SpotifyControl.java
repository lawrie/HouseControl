package net.geekgrandad.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.MusicControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class SpotifyControl implements MusicControl {
	
	// Music server commands
	private static final String CMD_SET_VOLUME = "setvol";
	private static final String CMD_GET_VOLUME = "getvol";
	private static final String CMD_PAUSE_MUSIC = "stop";
	private static final String CMD_SKIP_TRACK = "skip";
	private static final String CMD_SHUT_DOWN = "shutdown";
	
	private static final String ERROR = "Error";
	
    private Reporter reporter;
	private Config config;
	private boolean musicOn = true;
	private int volume = 0;
	private String currentPlaylist = " ";

	@Override
	public String play(int id, String playlist) {
		String list = config.getPlaylists().get(playlist);
		if (list != null) {
			currentPlaylist = playlist;
			return sendMusicCmd("play " + list + "?autoplay=true", false);
		} else {
			reporter.error("No such playlist: " + playlist);
			return ERROR;
		}
	}

	@Override
	public String playWave(int id, String waveFile) {
		// TODO
		return null;
	}

	@Override
	public void setVolume(int id, int volume) {
		sendMusicCmd(CMD_SET_VOLUME + " " + volume, false);
	}

	@Override
	public int getVolume(int id) {
		try {
			int r = Integer.parseInt(sendMusicCmd(CMD_GET_VOLUME, true));
			musicOn = true;
			return r;
		} catch (Exception e) {
			musicOn = false;
			return volume;
		}
	}

	@Override
	public String getArtist(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTrack(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlaylist(int id) {
		return currentPlaylist;
	}

	@Override
	public boolean musicStatus(int id) {
		return musicOn;
	}

	@Override
	public void pauseMusic(int id) {
		sendMusicCmd(CMD_PAUSE_MUSIC, false);

	}

	@Override
	public void playMusic(int id) {
		sendMusicCmd(CMD_PAUSE_MUSIC, false);
	}

	@Override
	public void skipTrack(int id) {
		sendMusicCmd(CMD_SKIP_TRACK, false);
	}

	@Override
	public void say(int id, String msg) {
		sendMusicCmd("say " + msg, false);
	}

	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();	
		reporter = provider.getReporter();
	}
	
	private synchronized String sendMusicCmd(String cmd, boolean force) {
		if (!force && !musicOn)
			return "Error";
		try {
			Socket sock = new Socket(config.musicServerHost, config.listenPort);
			sock.setSoTimeout(config.musicSocketTimeout);
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			out.println(cmd);
			reporter.print("Sending " + cmd + " to music server");
			String ret = in.readLine();
			reporter.print("Music server: " + ret);
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
	public void shutDown(int id) {
		sendMusicCmd(CMD_SHUT_DOWN, false);
	}

	@Override
	public void selectService(String name) {
		// TODO Auto-generated method stub
		
	}
}
