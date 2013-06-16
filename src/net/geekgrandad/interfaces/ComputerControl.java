package net.geekgrandad.interfaces;

public interface ComputerControl extends Controller {
	
	public void shutdown();
	
	public void reboot();
	
	public int getVolume();
	
	public void setVolume(int vol);
}
