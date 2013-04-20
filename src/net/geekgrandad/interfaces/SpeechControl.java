package net.geekgrandad.interfaces;

public interface SpeechControl extends Controller {
	
	public void setMusicServer(MusicControl musicServer);
	
	public void say(String msg);
	
	public void setSpeech(Boolean on);
	
}
