package net.geekgrandad.plugin;

import net.geekgrandad.interfaces.HeatingControl;
import net.geekgrandad.interfaces.Provider;

public class DummyHeating implements HeatingControl {
	int requiredTemperature;
	
	@Override
	public boolean getHeatingStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRequiredTemperature() {
		return requiredTemperature;
	}

	@Override
	public void setRequiredTemperature(int temperature) {
		requiredTemperature = temperature;		
	}

	@Override
	public void setHeating(boolean on) {
		// TODO: Do we need this	
	}

	@Override
	public void setProvider(Provider provider) {
		// TODO Auto-generated method stub
		
	}

}
