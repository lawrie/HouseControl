package net.geekgrandad.interfaces;

public interface MQTT extends Controller {
	public void publish(String topic, String message, int qos);
	
	public void subscribe(String topic);
	
	public String getValue(String topic);

}
