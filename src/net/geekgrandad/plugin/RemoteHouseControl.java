package net.geekgrandad.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.RemoteControl;
import net.geekgrandad.interfaces.Reporter;

public class RemoteHouseControl implements RemoteControl {
	private Reporter reporter;
	private Config config;
	
	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();
		config = provider.getConfig();
	}

	@Override
	public String send(int id, String server, String cmd) throws IOException {
		try {
			Socket sock = new Socket(server, config.listenPort);
			sock.setSoTimeout(config.mediaSocketTimeout);
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			out.println(cmd);
			reporter.print("Sending " + cmd + " to server: " + server);
			String ret = in.readLine();
			reporter.print("Remote server: " + ret);
			out.close();
			in.close();
			sock.close();
			return ret;
		} catch (UnknownHostException e) {
			reporter.error("Unknown host in send");
			return ("Error");
		} catch (IOException e) {
			reporter.error("IO Exception in send");
			return "Error";
		}
		
	}

}
