package net.geekgrandad.plugin;

import java.io.IOException;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.LightControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.SocketControl;
import net.geekgrandad.interfaces.SwitchControl;

public class LWRFControl implements LightControl, SocketControl, SwitchControl {
	

	// Start of byte codes for switching LightwaveRF sockets on or off
	private static final int SOCKET_BASE = 224;

	// Start of byte codes for switching lights on or off
	private static final int LIGHT_BASE = 234;
	
	private Reporter reporter;
	private Config config;
	private RFControl rfc434;
	private Thread inThread434 = new Thread(new ReadLWRFInput());
	private boolean[] socketOn = new boolean[Config.MAX_SOCKETS];
	private boolean[] lightOn = new boolean[Config.MAX_LIGHTS];
	private int[] lightValue = new int[Config.MAX_LIGHTS];
	private boolean[] windowOpen = new boolean[Config.MAX_SWITCHES];
	
	@Override
	public boolean getSwitchStatus(int id) {
		return windowOpen[id];
	}

	@Override
	public void switchSocket(int socket, boolean on) throws IOException {
		socketOn[socket] = on;
		reporter.print("Switching socket " + (socket + 1) + " " + (on ? "on" : "off"));
		sendLWRF((byte) (SOCKET_BASE + socket * 2 + (on ? 0 : 1)));	
	}

	@Override
	public boolean getSocketStatus(int socket) {
		return socketOn[socket];
	}

	@Override
	public void switchLight(int light, boolean on) throws IOException {
		lightOn[light] = on;
		reporter.print("Switching light " + (light + 1) + " " + (on ? "on" : "off"));
		sendLWRF((byte) ((LIGHT_BASE + light * 2) + (on ? 0 : 1)));		
	}

	@Override
	public void dimLight(int light, int level) throws IOException {
		if (level < 0)
			level = 0;
		lightValue[light] = level;
		lightOn[light] = true;
		sendLevel((byte) light, (byte) (level * 32 / 100));	
	}
	
	private void sendLevel(int light, int level) throws IOException {
		sendLWRF((byte) ((light << 5) | level));
	}

	@Override
	public boolean getLightStatus(int light) {
		return lightOn[light];
	}

	@Override
	public int getLightLevel(int light) {
		return lightValue[light];
	}
	
	// Thread to process LightwaveRF input
	class ReadLWRFInput implements Runnable {
		public void run() {
			for (;;) {
				try {
					int b = rfc434.readByte();
					reporter.print("Read LightwaveRF: " + b);

					if (b < Config.MAX_SOCKETS * 2) {
						socketOn[b / 2] = (b % 2 == 0); // Even on, odd off
					} else if (b < (Config.MAX_SOCKETS + Config.MAX_SWITCHES) * 2) {
						windowOpen[(b - (Config.MAX_SOCKETS * 2)) / 2] = (b % 2 == 0);
					} else if (b < (Config.MAX_SOCKETS + Config.MAX_SWITCHES + Config.MAX_LIGHTS) * 2) {
						lightOn[(b - (Config.MAX_SOCKETS + Config.MAX_SWITCHES) * 2) / 2] = (b % 2 == 0);
					}
				} catch (IOException e) {
					reporter.error("IOException in ReadLWRFInput");
				}
			}
		}
	}

	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();
		reporter = provider.getReporter();
		
		// Connect to the LightwaveRF network, if transceiver port defined
		if (config.lwrfPort != null && config.lwrfPort.length() > 0) {
			try {
				rfc434 = new RFControl(config.lwrfPort);
			} catch (IOException e1) {
				reporter.error(e1.getMessage());
			}
		}
		
		// Start the LightwaveRF thread, if required
		if (rfc434 != null) {
			inThread434.setDaemon(true);
			inThread434.start();
		}
	}

	// Send a byte to LightWaveRF transceiver
	private void sendLWRF(byte cmd) throws IOException {
		if (rfc434 != null)
			rfc434.sendCmd(cmd);
	}
}
