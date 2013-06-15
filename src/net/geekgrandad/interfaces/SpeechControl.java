package net.geekgrandad.interfaces;

public interface SpeechControl extends Controller {
	
	public void say(int id, String msg);
	
	public void setSpeech(int id, boolean on);
	
}
