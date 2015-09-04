package net.geekgrandad.interfaces;

import java.io.IOException;

import net.geekgrandad.config.*;

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
	
	public ApplianceControl getApplianceControl(int n);
	
	public int getCurrentMediaDevice();
	
	public void setCurrentMediaDevice(int id);
	
	public Browser getBrowser();
	
	public MQTT getMQTTControl();
	
	public String parse(String cmd) throws IOException;

}
