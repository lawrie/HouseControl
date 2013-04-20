package net.geekgrandad.interfaces;

import java.io.IOException;

public interface CameraControl extends Controller {
	
	public void panUp() throws IOException;
		
	public void panDown() throws IOException;
	
	public void panLeft() throws IOException;
	
	public void panRight() throws IOException;

}
