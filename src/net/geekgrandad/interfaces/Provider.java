package net.geekgrandad.interfaces;

import net.geekgrandad.config.*;
import net.geekgrandad.plugin.MQTTControl;

public interface Provider {
	
	public Config getConfig();
	
	public Reporter getReporter();
	
	public Alerter getAlerter();
	
	public LightControl getLightControl(int n);
	
	public SocketControl getSocketControl(int n);
	
	public CameraControl getCameraControl(int n);
	
	public DatalogControl getDatalogControl();
	
	public InfraredControl getInfraredControl();
	
	public MediaControl getMediaControl(int n);
	
	public int getCurrentMediaDevice();
	
	public void setCurrentMediaDevice(int id);
	
	public Browser getBrowser();
	
	public MQTT getMQTTControl();

}
