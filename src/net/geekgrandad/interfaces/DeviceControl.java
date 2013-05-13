package net.geekgrandad.interfaces;

import java.io.IOException;

public interface DeviceControl extends Controller {
	
	public void turnOn(int id) throws IOException;
	
	public void turnOff(int id) throws IOException;
	
	public boolean getStatus(int id) throws IOException;
	
	public int getValue(int id) throws IOException;
	
	public void setValue(int id, int value) throws IOException;
	
}
