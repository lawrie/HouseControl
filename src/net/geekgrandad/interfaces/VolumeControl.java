package net.geekgrandad.interfaces;

import java.io.IOException;

public interface VolumeControl extends Controller {
	
	public void volumeUp(int id) throws IOException;
	
	public void volumeDown(int id) throws IOException;
	
	public void mute(int id) throws IOException;
	
	public int getVolume(int id) throws IOException;
	
	public void setVolume(int id, int volume) throws IOException;
}
