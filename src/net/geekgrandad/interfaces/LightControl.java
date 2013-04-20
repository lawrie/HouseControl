package net.geekgrandad.interfaces;

import java.io.IOException;

public interface LightControl extends Controller {
	
	public void switchLight(int light, boolean on) throws IOException;
	
	public void dimLight(int light, int level) throws IOException;
	
	public boolean getLightStatus(int light);
	
	public int getLightLevel(int light);
	
}
