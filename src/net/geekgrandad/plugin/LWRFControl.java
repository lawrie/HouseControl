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
		lwCmd((byte) 0,(byte) (byte) (config.socketChannels[socket]-1),(on ? LW_ON : LW_OFF), config.socketCodes[socket]);		
	}

	@Override
	public boolean getSocketStatus(int socket) {
		return socketOn[socket];
	}

	@Override
	public void switchLight(int light, boolean on) throws IOException {
		lightOn[light] = on;
		reporter.print("Switching light " + (light + 1) + " " + (on ? "on" : "off"));
		lwCmd((byte) 0,(byte) (byte) (config.lightChannels[light]-1),(on ? LW_ON : LW_OFF), config.lightCodes[light]);		
	}

	@Override
	public void dimLight(int light, int level) throws IOException {
		if (level < 0)
			level = 0;
		lightValue[light] = level;
		lightOn[light] = true;
		lwCmd((byte) level,(byte) (byte) (config.lightChannels[light]-1),LW_ON, config.lightCodes[light]);
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
					byte channel = m.getChannel();
					byte command = m.getCommand();
					byte[] id = m.getId();
					reporter.print("LWRF Channel: " + channel);
					reporter.print("LWRF Command: " + command);	
					reporter.print("LWRF Level: " + m.getLevel());
					reporter.print("LWRF Id = " + m.getHexId());
					
					int light = findLight(channel);
					
					if (light >= 0) {
						reporter.print("LWRF Found light " + (light+1));
						lightOn[light] = (command == LW_ON ? true  : false);
					} else {
						int socket = findSocket(channel);
						
						if (socket >= 0) {
							reporter.print("LWRF Found socket " + (socket+1));
							socketOn[socket] = (command == LW_ON ? true  : false);							
						} else {
							int swit = findSwitch(id);
							
							reporter.print("LWRF Found switch " + (swit+1));
							windowOpen[swit] = (command == LW_ON ? true  : false);
						}
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
	
	int findLight(int channel) {
		for(int i=0;i<config.lightChannels.length;i++) {
			if (config.lightChannels[i] == channel) return i;
		}
		return -1;
	}
	
	int findSocket(int channel) {
		for(int i=0;i<config.socketChannels.length;i++) {
			if (config.socketChannels[i] == channel) return i;
		}
		return -1;
	}
	
	int findSwitch(byte[] id) {
		for(int i=0;i<config.switchCodes.length;i++) {
			if (compareId(config.switchCodes[i],id)) return i;
		}
		return -1;		
	}
	
	boolean compareId(byte [] id1, byte[] id2) {
		for(int i=0;i<6;i++) {
			if (id1[i] != id2[i]) return false;
		}
		return true;
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
		channel = (byte) (findNibble(msg[2]) + 1);
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
	  
	  public String getHexId() {
		  StringBuilder sb = new StringBuilder();
		  for(int i=0;i<6;i++) {
			  sb.append(String.format("%02X ", id[i]));
		  }
		  return sb.toString();
	  }
		
	  private byte findNibble(byte b) {
		for(int i=0;i<16;i++) {
		  if (b == LW_NIBBLE[i]) return (byte) i;
		}
		return -1;
	  }
	}
}
