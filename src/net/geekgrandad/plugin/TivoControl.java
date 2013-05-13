package net.geekgrandad.plugin;

import java.io.IOException;

import net.geekgrandad.interfaces.AV;
import net.geekgrandad.interfaces.InfraredControl;
import net.geekgrandad.interfaces.MediaControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class TivoControl implements MediaControl {
	private Reporter reporter;
	InfraredControl infraredControl;
	private int volume, channel;
	private Provider provider;
	
	@Override
	public void setProvider(Provider provider) {
		this.provider = provider;
		reporter = provider.getReporter();
		infraredControl = provider.getInfraredControl();
	}

	@Override
	public void start(int id, String playlist, boolean repeat) throws IOException {
		reporter.error("TIVO: start not supported");
	}

	@Override
	public void open(int id, String file, boolean repeat) throws IOException {
		reporter.error("TIVO: open not supported");
	}

	@Override
	// Type in Virgin TV search box
	public void type(int id, String s) throws IOException {
		for(int i=0;i<s.length();i++) {
			type(s.charAt(i));
			delay(1500);
		}
	}
	
	private void type(char c) throws IOException {
		int repeats = 0;
		int digit = -1;
		
		if (c >= 'a' && c <= 'c') {
			digit = 2;
			repeats = (c - 'a');
		} else if (c >= 'd' && c <= 'f') {
			digit = 3;
			repeats = c - 'd';
		} else if (c >= 'g' && c <= 'i') {
			digit = 4;
			repeats = c - 'g';
		} else if (c >= 'j' && c <= 'l') {
			digit = 5;
			repeats = c - 'j';
		} else if (c >= 'm' && c <= 'o') {
			digit = 6;
			repeats = c - 'm';
		} else if (c >= 'p' && c <= 's') {
			digit = 7;
			repeats = c - 'p';
		} else if (c >= 't' && c <= 'v') {
			digit = 8;
			repeats = c - 't';
		} else if (c >= 'w' && c <= 'z') {
			digit = 9;
			repeats = c - 'w';
		} else if (c == ' ') {
			digit = 0;
			repeats = 0;
		} else if (c >= '0' && c <= '9' ) {
			digit = c - '0';
			if (digit == 0) repeats = 1;
			else if (digit == 1) repeats = 0;
			else if (digit == 7 || digit == 9) repeats = 4;
			else repeats = 3;
		}
		
		if (digit >= 0) {
			for(int i=0;i<=repeats;i++) {
				sendCommand(2, 0x80 + digit);
				delay(100);
			}
		}
	}
	
	
	private void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	private void delay() {
		delay(2000);
	}


	@Override
	public void select(int id, String service)  throws IOException {
		if (service.equals("iplayer")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("broadcast")) {
			sendCommand(0, AV.TV);
		} else if (service.equals("guide")) {
			sendCommand(0, AV.GUIDE);
		} else if (service.equals("home")) {
			sendCommand(0, AV.HOME);
		} else if (service.equals("shows")) {
			sendCommand(0, AV.SHOWS);
		} else if (service.equals("itvplayer")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("4od")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("demand5")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("settings")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("parental")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("purchase")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("messages")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("help")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("ondemand")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
		} else if (service.equals("catchup")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("tvxl")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("movies")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
		    sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("music")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();						
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("sports")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();	
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("games")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("lifestyle")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("news")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("apps")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("featured")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("system")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("rentals")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("youtube")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_RIGHT);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();						
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("payperview")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_DOWN);
			delay();
			sendCommand(0, AV.MENU_OK);
		} else if (service.equals("series")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.DIGIT_1);
		} else if (service.equals("planned")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.DIGIT_2);
		} else if (service.equals("wishlist")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.DIGIT_3);
		} else if (service.equals("browse")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.DIGIT_5);
		} else if (service.equals("search")) {
			sendCommand(0, AV.HOME);
			delay();
			sendCommand(0, AV.DIGIT_4);
		}	
	}

	private void sendCommand(int id, int cmd) throws IOException {
		if (infraredControl == null) infraredControl = provider.getInfraredControl();
		infraredControl.sendCommand(id, cmd);
	}

	@Override
	public void pause(int id)  throws IOException {
		sendCommand(0,AV.PAUSE);	
	}

	@Override
	public void stop(int id)  throws IOException {
		sendCommand(0,AV.STOP);	
	}

	@Override
	public void play(int id) throws IOException {
		sendCommand(0,AV.PLAY);		
	}

	@Override
	public void record(int id) throws IOException {
		sendCommand(0,AV.RECORD);	
	}

	@Override
	public void ff(int id) throws IOException {
		sendCommand(0,AV.FAST_FORWARD);
	}

	@Override
	public void fb(int id) throws IOException {
		sendCommand(0,AV.FAST_BACKWARDS);	
	}

	@Override
	public void skip(int id) throws IOException {
		sendCommand(0,AV.SKIP_FORWARDS);
	}

	@Override
	public void skipb(int id) throws IOException {
		sendCommand(0,AV.SKIP_FORWARDS);	
	}

	@Override
	public void slow(int id) throws IOException {
		sendCommand(0,AV.SLOW);
	}

	@Override
	public void delete(int id) throws IOException {
		sendCommand(0,AV.DELETE);
	}

	@Override
	public void up(int id) throws IOException {
		sendCommand(0,AV.MENU_UP);	
	}

	@Override
	public void down(int id) throws IOException {
		sendCommand(0,AV.MENU_DOWN);		
	}

	@Override
	public void left(int id) throws IOException {
		sendCommand(0,AV.MENU_LEFT);		
	}

	@Override
	public void right(int id) throws IOException {
		sendCommand(0,AV.MENU_RIGHT);		
	}

	@Override
	public void ok(int id) throws IOException {
		sendCommand(0,AV.MENU_OK);
	}

	@Override
	public void back(int id) throws IOException {
		sendCommand(0,AV.BACK);
	}

	@Override
	public void mute(int id) throws IOException {
		sendCommand(0,AV.MUTE);
	}

	@Override
	public int getVolume(int id) throws IOException {
		return volume;	
	}

	@Override
	public void setVolume(int id, int volume) throws IOException {
		this.volume=volume;	
	}

	@Override
	public void setChannel(int id, int channel) throws IOException {
		setChannel("" + channel);
		this.channel = channel;
	}
	
	// Change the Virgin TiVo TV channel
	public void setChannel(String channel) throws IOException {
		for (int i = 0; i < 3; i++) {
			int code = (128 + channel.charAt(i) - '0');
			sendCommand(3, code);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public String getTrack(int id) throws IOException {
		return "Unknown";
	}

	@Override
	public int getChannel(int id) throws IOException {
		return channel;
	}

	@Override
	public void channelUp(int id) throws IOException {
		sendCommand(0, AV.CHANNEL_UP);
		channel++;
	}

	@Override
	public void channelDown(int id) throws IOException {
		sendCommand(0, AV.CHANNEL_DOWN);
		channel--;
	}

	@Override
	public void thumbsUp(int id) throws IOException {
		sendCommand(0, AV.THUMBS_UP);	
	}

	@Override
	public void thumbsDown(int id) throws IOException {
		sendCommand(0, AV.THUMBS_DOWN);
	}

	@Override
	public void digit(int id, int n) throws IOException {
		sendCommand(0,AV.DIGIT_0 + n);	
	}

	@Override
	public void color(int id, int n) throws IOException {
		sendCommand(0,n);		
	}

	@Override
	public void pin(int id) throws IOException {
		sendCommand(0,AV.DIGIT_1);
		delay(1000);
		sendCommand(0,AV.DIGIT_2);
		delay(1000);
		sendCommand(0,AV.DIGIT_3);
		delay(1000);
		sendCommand(0,AV.DIGIT_4);		
	}

	@Override
	public void turnOn(int id) throws IOException {
		sendCommand(0,AV.ON);
	}

	@Override
	public void turnOff(int id) throws IOException {
		sendCommand(0,AV.OFF);
	}

	@Override
	public void setSource(int id, int source) throws IOException {
		sendCommand(0, source);	
	}

	@Override
	public String getArtist(int id) throws IOException {
		return "Unknown";
	}

	@Override
	public String getAlbum(int id) throws IOException {
		return "Unknown";
	}

	@Override
	public void say(int id, String text) {
		reporter.error("TIVO: say not supported");		
	}

	@Override
	public void lastChannel(int id) throws IOException {
		sendCommand(0,AV.LAST_CHANNEL);	
	}

	@Override
	public void option(int id, String option) throws IOException {
		if (option.equals("subtitles")) {
			sendCommand(0, AV.SUBTITLES);
		} else if (option.equals("info")) {
			sendCommand(0, AV.INFO);
		}
	}
	
	@Override
	public void volumeUp(int id) throws IOException {
		sendCommand(0,AV.VOLUME_UP);
		volume++;
	}

	@Override
	public void volumeDown(int id) throws IOException {
		sendCommand(0,AV.VOLUME_DOWN);
		volume--;
	}
}
