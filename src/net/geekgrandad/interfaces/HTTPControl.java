package net.geekgrandad.interfaces;

import java.io.IOException;

public interface HTTPControl extends Controller {
	
	public void setLightControl(LightControl lightControl);
	
	public String httpCommand(String cmd) throws IOException;

}
