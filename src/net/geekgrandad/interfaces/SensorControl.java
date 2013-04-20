package net.geekgrandad.interfaces;

public interface SensorControl extends Controller {
	
	public boolean getSensorStatus(int sensor);
	
	public int getTemperature(int sensor);
	
	public int getHumidity(int sensor);
	
	public int getLightLevel(int sensor);
	
	public boolean getMotion(int sensor);
	
	public boolean getBatteryLow(int sensor);
	
}
