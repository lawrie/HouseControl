package net.geekgrandad.plugin;

import java.io.IOException;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.Alerter;
import net.geekgrandad.interfaces.Controller;
import net.geekgrandad.interfaces.MediaControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.AV;
import net.geekgrandad.rf.RFControl;

public class X10Remote implements Controller {
	
	private static final int DIGIT_0 = 0x40;
	private static final int DIGIT_1 = 0x41;
	private static final int DIGIT_2 = 0x42;
	private static final int DIGIT_3 = 0x43;
	private static final int DIGIT_4 = 0x44;
	private static final int DIGIT_5 = 0x45;
	private static final int DIGIT_6 = 0x46;
	private static final int DIGIT_7 = 0x47;
	private static final int DIGIT_8 = 0x48;
	private static final int DIGIT_9 = 0x49;
	
	private static final int UP = 0xAB;
	private static final int DOWN = 0xCB;
	private static final int LEFT = 0x4B;
	private static final int RIGHT = 0x1B;
	private static final int OK = 0x4A;
	
	private static final int PLAY = 0x0D;
	private static final int PAUSE = 0x4E;
	private static final int STOP = 0x0E;
	private static final int FF = 0x1D;
	private static final int FB = 0x1C;
	private static final int SKIP = 0x1B;
	private static final int SKIPB = 0x5C;
	
	private static final int VOL_UP = 0x07;
	private static final int VOL_DOWN = 0x08;
	private static final int CHANNEL_UP = 0x02;
	private static final int CHANNEL_DOWN = 0x03;
	
	private static final int MENU = 0x6D;
	private static final int EXIT = 0x93;
	
	private static final int M = 0x05;
	private static final int AV_KEY = 0x5D;
	private static final int POWER = 0x0F;
	
	private Provider provider;
	private Reporter reporter;
	private RFControl rfc;
	private Config config;
	private Alerter alerter;
	
	private final int[] sources = {AV.SOURCE_STB, AV.SOURCE_DVD, AV.SOURCE_BLUERAY};
	
	private Thread inThread = new Thread(new ReadX10Input());
	
	
	@Override
	public void setProvider(Provider provider) {
		this.provider = provider;	
		this.reporter = provider.getReporter();
		this.config = provider.getConfig();
		this.alerter = provider.getAlerter();
		
		reporter.print("X10 port is " + config.x10Port);
		
		// Connect to the X10 network, if transceiver port defined
		if (config.x10Port != null && config.x10Port.length() > 0) {
			try {				
				rfc = new RFControl(config.x10Port, 57600, 2000, "X10");
			} catch (IOException e1) {
				reporter.error(e1.getMessage());
			}
		}
		
		// Start the X10 thread, if required
		if (rfc != null) {
			reporter.print("Starting X10 thread");
			inThread.setDaemon(true);
			inThread.start();
		}
	}
	
	// Thread to process X10 input
	class ReadX10Input implements Runnable {
		public void run() {
			for (;;) {
				try {
					int cmd = (byte) rfc.readByte();
					int id = provider.getCurrentMediaDevice();
					MediaControl m = provider.getMediaControl(id);
					
					reporter.print("X10: Received : " + cmd);
					switch (cmd) {
					case DIGIT_0:
					case DIGIT_1:
					case DIGIT_2:
					case DIGIT_3:
					case DIGIT_4:
					case DIGIT_5:
					case DIGIT_6:
					case DIGIT_7:
					case DIGIT_8:
					case DIGIT_9:
						m.digit(id, cmd - DIGIT_0);
						break;
					case UP:
						m.up(id);
						break;
					case DOWN:
						m.down(id);
						break;
					case LEFT:
						m.left(id);
						break;
					case PAUSE:
					case PLAY:
						m.play(id);
						break;
					case STOP:
						m.stop(id);
						break;
					case FF:
						m.ff(id);
						break;
					case FB:
						m.fb(id);
						break;
					case SKIP:
						m.skip(id);
						break;
					case SKIPB:
						m.skipb(id);
						break;
					case VOL_UP:
						m.volumeUp(id);
						break;
					case VOL_DOWN:
						m.volumeDown(id);
						break;
					case CHANNEL_UP:
						m.channelUp(id);
						break;
					case CHANNEL_DOWN:
						m.channelDown(id);
						break;
					case MENU:
						m.select(id, "home");
						break;
					case EXIT:
						m.back(id);
						break;
					case AV_KEY:
						int n = rfc.readByte() - 0x40;
						reporter.print("Setting media device to " + n);
						if (n >0 && n <= Config.MAX_MEDIA) {
							reporter.print("Name is " + config.mediaNames[n-1]);
							provider.setCurrentMediaDevice(n);
							alerter.say("Setting default media device to " +  config.mediaNames[n-1]);
						}
						break;
					case POWER:
						m.turnOn(id);
						break;
					case M:
						n = rfc.readByte() - 0x40;
						reporter.print("Setting source to " + n);
						m.setSource(id, sources[n-1]);
						break;
					}				
				} catch (IOException e) {
					reporter.error("IOException in ReadX10Input");
				}
			}
		}
	}
}
