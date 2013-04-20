package net.geekgrandad.interfaces;

public interface MusicControl extends Controller {
	
	public String play(int id, String playlist);
	
	public String playWave(int id, String waveFile);
	
	public void setVolume(int id, int volume);
	
	public int getVolume(int id);
	
	public String getArtist(int id);
	
	public String getTrack(int id);
	
	public String getPlaylist(int id);
	
	public boolean musicStatus(int id);
	
	public void shutDown(int id);
	
	public void pauseMusic(int id);
	
	public void playMusic(int id);
	
	public void skipTrack(int id);
	
	public void say(int id, String msg);
	
}
