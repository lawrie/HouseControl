package net.geekgrandad.interfaces;

public interface WeatherControl extends Controller {

	public float getTemperature();
	
	public float getHumidity();
		
	public String getWeather();
}
