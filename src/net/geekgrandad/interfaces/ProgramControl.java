package net.geekgrandad.interfaces;

public interface ProgramControl extends Controller {
	
	public void activate(int id, String program);
	
	public void sendKey(int id, int keyCode);

}
