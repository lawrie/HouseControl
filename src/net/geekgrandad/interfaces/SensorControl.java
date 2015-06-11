package net.geekgrandad.interfaces;

public interface SensorControl extends Controller {
	
	public boolean getSensorStatus(int sensor);
	
	public float getQuantity(int sensor, Quantity q);
	
}
