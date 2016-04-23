package net.geekgrandad.plugin;

import net.geekgrandad.interfaces.HeatingControl;
import net.geekgrandad.interfaces.MQTT;
import net.geekgrandad.interfaces.Provider;

public class MQTTHeating implements HeatingControl {
	private MQTT mqtt;
	private Provider provider;
	private int requiredTemperature;
	
	private static final String requiredTopic = "/house/heating/target-temperature";
	
	@Override
	public boolean getHeatingStatus() {
		return true;
	}

	@Override
	public int getRequiredTemperature() {
		return requiredTemperature;
	}

	@Override
	public void setRequiredTemperature(int temperature) {
		System.out.println("Setting target temperature to " + temperature);
		if (mqtt == null) mqtt = provider.getMQTTControl();
		requiredTemperature = temperature;	
		mqtt.publish(requiredTopic, "" + temperature, 0);
	}

	@Override
	public void setHeating(boolean on) {
		// TODO: Do we need this	
	}

	@Override
	public void setProvider(Provider provider) {
		System.out.println("MQTT Heating started");
		this.provider = provider;
	}
}
