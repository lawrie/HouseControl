package net.geekgrandad.interfaces;

import java.io.IOException;

public interface MediaControl extends Controller {
	
	public void start(int id, String playlist, boolean repeat) throws IOException;
	
	public void open(int id, String file, boolean repeat) throws IOException;
	
	public void type(int id, String s) throws IOException;
	
	public void select(int id, String service) throws IOException;
	
	public void pause(int id) throws IOException;
	
	public void stop(int id) throws IOException;
	
	public void play(int id) throws IOException;
	
	public void record(int id) throws IOException;
	
	public void ff(int id) throws IOException;
	
	public void fb(int id) throws IOException;
	
	public void skip(int id) throws IOException;
	
	public void skipb(int id) throws IOException;
	
	public void slow(int id) throws IOException;
	
	public void delete(int id) throws IOException;
	
	public void up(int id) throws IOException;
	
	public void down(int id) throws IOException;
	
	public void left(int id) throws IOException;
	
	public void right(int id) throws IOException;
	
	public void ok(int id) throws IOException;
	
	public void back(int id) throws IOException;
	
	public void lastChannel(int id) throws IOException;
	
	public void option(int id, String option) throws IOException;
	
	public void volumeUp(int id) throws IOException;
	
	public void volumeDown(int id) throws IOException;
	
	public void mute(int id) throws IOException;
	
	public int getVolume(int id) throws IOException;
	
	public void setVolume(int id, int volume) throws IOException;
	
	public void setChannel(int id, int channel) throws IOException;
	
	public String getTrack(int id) throws IOException;
	
	public int getChannel(int id) throws IOException;
	
	public void channelUp(int id) throws IOException;
	
	public void channelDown(int id) throws IOException;
	
	public void thumbsUp(int id) throws IOException;
	
	public void thumbsDown(int id) throws IOException;
	
	public void digit(int id, int n) throws IOException;
	
	public void color(int id, int n) throws IOException;
	
	public void turnOn(int id) throws IOException;
	
	public void turnOff(int id) throws IOException;
	
	public void pin(int id) throws IOException;
	
	public void setSource(int id, int source) throws IOException;
	
	public String getArtist(int id) throws IOException;
	
	public String getAlbum(int id) throws IOException;
	
	public void say(int id, String text);
}
