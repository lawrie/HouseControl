package net.geekgrandad.interfaces;

import java.io.IOException;

public interface ApplianceControl extends Controller {	
	
	public void switchAppliance(int socket, boolean on) throws IOException;
	
	public boolean getApplianceStatus(int appliance);
	
	public int getAppliancePower(int appliance);
}
