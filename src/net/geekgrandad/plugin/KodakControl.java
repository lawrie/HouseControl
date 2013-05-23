package net.geekgrandad.plugin;

import java.io.IOException;

import net.geekgrandad.interfaces.InfraredControl;
import net.geekgrandad.interfaces.MediaControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class KodakControl implements MediaControl {
	private Reporter reporter;
	InfraredControl infraredControl;
	Provider provider;
	
	private static final int RIGHT = 1;
	private static final int LEFT = 2;
	private static final int UP = 3;
	private static final int DOWN = 4;
	private static final int PAUSE = 5;
	private static final int OK = 6;
	private static final int STOP = 7;
	private static final int FF = 8;
	private static final int BACK = 9;
	private static final int FB = 10;
	private static final int MUTE = 11;
	private static final int PLUS = 12;
	private static final int MINUS = 13;
	private static final int MENU = 14;
	private static final int HOME = 15;
	private static final int ON = 16;
	private static final int MUSIC = 17;
	private static final int ZOOM = 18;
	private static final int TICK = 19;
	private static final int ROTATE = 20;
	private static final int FILES = 21;
	
	
	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();
		infraredControl = provider.getInfraredControl();
		this.provider = provider;
	}

	@Override
	public void start(int id, String playlist, boolean repeat)
			throws IOException {
		reporter.error("KODAK: start not supported");
	}

	@Override
	public void open(int id, String file, boolean repeat) throws IOException {
		reporter.error("KODAK: open not supported");	
	}

	@Override
	public void type(int id, String s) throws IOException {
		reporter.error("KODAK: type not supported");	
	}

	@Override
	public void select(int id, String service) throws IOException {
		if (service.equals("home")) sendCommand(HOME);
		else if (service.equals("menu")) sendCommand(MENU);
		else if (service.equals("music")) sendCommand(MUSIC);
		else reporter.error("KODAK: service not supported: " + service);
	}

	@Override
	public void pause(int id) throws IOException {
		sendCommand(PAUSE);		
	}

	@Override
	public void stop(int id) throws IOException {
		sendCommand(STOP);	
	}

	@Override
	public void play(int id) throws IOException {
		sendCommand(PAUSE);	
	}

	@Override
	public void record(int id) throws IOException {
		reporter.error("KODAK: record not supported");	
	}

	@Override
	public void ff(int id) throws IOException {
		sendCommand(FF);
	}

	@Override
	public void fb(int id) throws IOException {
		sendCommand(FB);		
	}

	@Override
	public void skip(int id) throws IOException {
		sendCommand(RIGHT);	
	}

	@Override
	public void skipb(int id) throws IOException {
		sendCommand(LEFT);
	}

	@Override
	public void slow(int id) throws IOException {
		reporter.error("KODAK: slow not supported");		
	}

	@Override
	public void delete(int id) throws IOException {
		reporter.error("KODAK: up not supported");
	}

	@Override
	public void up(int id) throws IOException {
		sendCommand(UP);
	}

	@Override
	public void down(int id) throws IOException {
		sendCommand(DOWN);		
	}

	@Override
	public void left(int id) throws IOException {
		sendCommand(LEFT);	
	}

	@Override
	public void right(int id) throws IOException {
		sendCommand(RIGHT);
	}

	@Override
	public void ok(int id) throws IOException {
		sendCommand(OK);		
	}

	@Override
	public void back(int id) throws IOException {
		sendCommand(BACK);	
	}

	@Override
	public void lastChannel(int id) throws IOException {
		reporter.error("KODAK: lastch not supported");
		
	}

	@Override
	public void option(int id, String option) throws IOException {
		reporter.error("KODAK: option not supported");	
	}

	@Override
	public void volumeUp(int id) throws IOException {
		sendCommand(PLUS);		
	}

	@Override
	public void volumeDown(int id) throws IOException {
		sendCommand(MINUS);	
	}

	@Override
	public void mute(int id) throws IOException {
		sendCommand(MUTE);	
	}

	@Override
	public int getVolume(int id) throws IOException {
		return 0;
	}

	@Override
	public void setVolume(int id, int volume) throws IOException {
		reporter.error("KODAK: set volume not supported");	
	}

	@Override
	public void setChannel(int id, int channel) throws IOException {
		reporter.error("KODAK: set channel not supported");	
	}

	@Override
	public String getTrack(int id) throws IOException {
		reporter.error("KODAK: get track not supported");
		return "";
	}

	@Override
	public int getChannel(int id) throws IOException {
		reporter.error("KODAK: channels not supported");
		return 0;
	}

	@Override
	public void channelUp(int id) throws IOException {
		reporter.error("KODAK: channels not supported");	
	}

	@Override
	public void channelDown(int id) throws IOException {
		reporter.error("KODAK: channels not supported");
	}

	@Override
	public void thumbsUp(int id) throws IOException {
		reporter.error("KODAK: rating not supported");		
	}

	@Override
	public void thumbsDown(int id) throws IOException {
		reporter.error("KODAK: rating not supported");
		
	}

	@Override
	public void digit(int id, int n) throws IOException {
		reporter.error("KODAK: digits not supported");
	}

	@Override
	public void color(int id, int n) throws IOException {
		reporter.error("KODAK: colors not supported");
	}

	@Override
	public void turnOn(int id) throws IOException {
		sendCommand(ON);
	}

	@Override
	public void turnOff(int id) throws IOException {
		sendCommand(ON);		
	}

	@Override
	public void pin(int id) throws IOException {
		reporter.error("KODAK: pin not supported");	
	}

	@Override
	public void setSource(int id, int source) throws IOException {
		reporter.error("KODAK: set source not supported");	
	}

	@Override
	public String getArtist(int id) throws IOException {
		reporter.error("KODAK: get artist not supported");
		return "";
	}

	@Override
	public String getAlbum(int id) throws IOException {
		reporter.error("KODAK: get album not supported");
		return "";
	}

	@Override
	public String getPlaylist(int id) throws IOException {
		reporter.error("KODAK: playlist not supported");
		return "";
	}

	@Override
	public void say(int id, String text) throws IOException {
		reporter.error("KODAK: say not supported");
	}

	@Override
	public void pageUp(int id) throws IOException {
		reporter.error("KODAK: page up not supported");
	}

	@Override
	public void pageDown(int id) throws IOException {
		reporter.error("KODAK: page down not supported");	
	}

	@Override
	public boolean isPlaying(int id) throws IOException {
		reporter.error("KODAK: playing not supported");
		return false;
	}

	@Override
	public void reboot(int id) throws IOException {
		reporter.error("KODAK: reboot not supported");	
	}

	@Override
	public void setPlayer(int id, int playerId) throws IOException {
		reporter.error("KODAK: player not supported");
		
	}

	@Override
	public void setRepeat(int id, boolean repeat) throws IOException {
		reporter.error("KODAK: repeat not supported");	
	}

	@Override
	public void setShuffle(int id, boolean shuffle) throws IOException {
		reporter.error("KODAK: shufffle not supported");
	}
	
	private void sendCommand(int cmd) throws IOException {
		if (infraredControl == null) infraredControl = provider.getInfraredControl();
		reporter.print("KODAK: Sending " + cmd + ", " + infraredControl);
		if (infraredControl != null) infraredControl.sendCommand(0, cmd);
	}

}
