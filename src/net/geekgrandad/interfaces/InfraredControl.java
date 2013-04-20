package net.geekgrandad.interfaces;

import java.io.IOException;

public interface InfraredControl extends Controller, AV {
	
	public void setChannel(String channel) throws IOException;

}
