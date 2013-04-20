package net.geekgrandad.interfaces;

public interface HeatingControl extends Controller {
	
	public boolean getHeatingStatus();
	
	public int getRequiredTemperature();
	
	public void setRequiredTemperature(int temperature);
	
	public void setHeating(boolean on);
	
}
