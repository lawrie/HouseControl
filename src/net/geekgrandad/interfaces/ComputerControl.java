package net.geekgrandad.interfaces;

import java.io.IOException;

public interface ComputerControl extends Controller {
	
	public void shutdown();
	
	public void reboot();
	
	public int getVolume();
	
	public void setVolume(int vol);
	
	public int execute(String cmd) throws IOException;
	
	public void sendKey(String program, int keyCode);
	
}
