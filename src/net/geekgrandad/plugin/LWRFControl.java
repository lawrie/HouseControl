package net.geekgrandad.plugin;

import java.io.IOException;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.LightControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.SocketControl;
import net.geekgrandad.interfaces.SwitchControl;

public class LWRFControl implements LightControl, SocketControl, SwitchControl {
	
	private static final byte LW_OFF = 0;
	private static final byte LW_ON = 1;
	private static final byte LW_MOOD = 2;
	
	private static byte[] id = {(byte) 0x6f,(byte) 0xeb,(byte) 0xbe, (byte) 0xed, (byte) 0xb7, (byte) 0x7b};

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
		lwCmd((byte) 0,(byte) (4+light),(on ? LW_ON : LW_OFF), id);
		//sendLWRF((byte) ((LIGHT_BASE + light * 2) + (on ? 0 : 1)));		
	}

	@Override
	public void dimLight(int light, int level) throws IOException {
		if (level < 0)
			level = 0;
		lightValue[light] = level;
		lightOn[light] = true;
		lwCmd((byte) level,(byte) (4+light),LW_ON, id);
		//sendLevel((byte) light, (byte) (level * 32 / 100));	
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
					byte[] msg = new byte[10];
					
					for(int i=0;i<10;i++) {
						msg[i] = (byte) rfc434.readByte();
					}
					
					Message m = new Message(msg);
					reporter.print("LWRF Channel: " + m.getChannel());
					reporter.print("LWRF Command: " + m.getCommand());
					
					/*int b = rfc434.readByte();
					reporter.print("Read LightwaveRF: " + b);

					if (b < Config.MAX_SOCKETS * 2) {
						socketOn[b / 2] = (b % 2 == 0); // Even on, odd off
					} else if (b < (Config.MAX_SOCKETS + Config.MAX_SWITCHES) * 2) {
						windowOpen[(b - (Config.MAX_SOCKETS * 2)) / 2] = (b % 2 == 0);
					} else if (b < (Config.MAX_SOCKETS + Config.MAX_SWITCHES + Config.MAX_LIGHTS) * 2) {
						lightOn[(b - (Config.MAX_SOCKETS + Config.MAX_SWITCHES) * 2) / 2] = (b % 2 == 0);
					}*/
					
					
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
	
	private static final byte[] LW_NIBBLE = {(byte) 0xF6, (byte) 0xEE, (byte) 0xED, (byte) 0xEB,
		                                     (byte) 0xDE, (byte) 0xDD, (byte) 0xDB, (byte) 0xBE,
		                                     (byte) 0xBD, (byte) 0xBB, (byte) 0xB7, (byte) 0x7E,
		                                     (byte) 0x7D, (byte) 0x7B, (byte) 0x77 ,(byte) 0x6F};
	
	/**
	  Send a LightwaveRF command
	**/
	void lwCmd(byte level, byte channel, byte cmd, byte[] id) throws IOException {
	  byte[] msg = new byte[10];;
	  
	  msg[0] = LW_NIBBLE[level >> 4];
	  msg[1] = LW_NIBBLE[level & 0xF];
	  msg[2] = LW_NIBBLE[channel];
	  msg[3] = LW_NIBBLE[cmd];
	  
	  for(int i=0;i<6;i++) {
	    msg[4+i] = id[i];
	  }
	   
	  StringBuilder sb = new StringBuilder();
	  for(int i=0;i<10;i++) {
		  sb.append(String.format("%02X ", msg[i]));
		  sendLWRF(msg[i]);
	  }
	  reporter.print("LWRF Sending: " + sb);
	}
	
	class Message {
	  private byte level, command, channel;
	  private byte[] id;
	  
	  public Message(byte level, byte channel, byte command, byte[] id) {
	    this.level = level;
	    this.channel = channel;
	    this.command = command;
	    this.id = id;
	  }
	  
	  public Message(byte[] msg) {
		  
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<10;i++) {
		  sb.append(String.format("%02X ", msg[i]));
		}
		reporter.print("LWRF Received: " + sb.toString());
		int level1 = findNibble(msg[0]);
		int level2 = findNibble(msg[1]); 
		id = new byte[6];
		level = (byte) ((level1 << 4) + level2);
		channel = findNibble(msg[2]);
		command = findNibble(msg[3]);
		  
		for(int i=0;i<6;i++) {
		  id[i] = msg[i+4];
		}
	  }
	  
	  public byte getLevel() {
		  return level;
	  }
	  
	  public byte getCommand() {
		  return command;
	  }
	  
	  public byte getChannel() {
		  return channel;
	  }
	  
	  public byte[] getId() {
		  return id;
	  }
		
	  private byte findNibble(byte b) {
		for(int i=0;i<16;i++) {
		  if (b == LW_NIBBLE[i]) return (byte) i;
		}
		return -1;
	  }
	}
}
