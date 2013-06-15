package net.geekgrandad.interfaces;

import java.io.IOException;

public interface RemoteControl extends Controller {

		public String send(int id, String server, String cmd) throws IOException;
		
}
