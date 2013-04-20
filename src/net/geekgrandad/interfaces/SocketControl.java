package net.geekgrandad.interfaces;

import java.io.IOException;

public interface SocketControl extends Controller {
	
	public void switchSocket(int socket, boolean on) throws IOException;
	
	public boolean getSocketStatus(int socket);
	
}
