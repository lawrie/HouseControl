package net.geekgrandad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.AV;
import net.geekgrandad.interfaces.AlarmControl;
import net.geekgrandad.interfaces.Alerter;
import net.geekgrandad.interfaces.ApplianceControl;
import net.geekgrandad.interfaces.CalendarControl;
import net.geekgrandad.interfaces.CameraControl;
import net.geekgrandad.interfaces.Controller;
import net.geekgrandad.interfaces.DatalogControl;
import net.geekgrandad.interfaces.EmailControl;
import net.geekgrandad.interfaces.HTTPControl;
import net.geekgrandad.interfaces.HeatingControl;
import net.geekgrandad.interfaces.InfraredControl;
import net.geekgrandad.interfaces.LightControl;
import net.geekgrandad.interfaces.MusicControl;
import net.geekgrandad.interfaces.PlantControl;
import net.geekgrandad.interfaces.PowerControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.SensorControl;
import net.geekgrandad.interfaces.SocketControl;
import net.geekgrandad.interfaces.SpeechControl;
import net.geekgrandad.interfaces.SwitchControl;
import net.geekgrandad.parser.Parser;
import net.geekgrandad.parser.Token;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HouseControl implements Reporter, Alerter, Provider {

	private static final String CONFIG_FILE = "conf/house.xml";
	private static Config config = new Config(CONFIG_FILE);

	// The name of the log4j2 logger used by this class
	private static final String LOGGER_NAME = "HouseControl";

	private static final String ERROR = "error";
	private static final String SUCCESS = "ok";
	
	private static final int TV = 1; 
	private static final int RECEIVER = 2;
	private static final int TIVO = 3;

	// Variable data
	private ServerSocket ss;
	private Socket s;
	private InputStream is;
	private OutputStream os;
	private Thread backGround = new Thread(new Background());
	private boolean phoneConnected = false;
	private boolean musicOn = true;
	private int volume;
	private static int numCmds = 0;
	private static int state = 0;
	private String cmd;
	private boolean noisy = false;
	private Logger logger = LogManager.getLogger(LOGGER_NAME);
	
    private CameraControl[] cameraControl = new CameraControl[Config.MAX_CAMERAS];
    private MusicControl[] musicControl = new MusicControl[Config.MAX_MUSIC_SERVERS];
    private SpeechControl speechControl;
    private EmailControl emailControl;
    private AlarmControl alarmControl;
    private HeatingControl heatingControl;
    private DatalogControl datalogControl;
    private CalendarControl calendarControl;
    private LightControl[] lightControl = new LightControl[Config.MAX_LIGHTS];
    private SocketControl[] socketControl = new SocketControl[Config.MAX_SOCKETS];
    private SwitchControl[] switchControl = new SwitchControl[Config.MAX_SWITCHES];
    private SensorControl[] sensorControl = new SensorControl[Config.MAX_SENSORS];
    private ApplianceControl[] applianceControl = new ApplianceControl[Config.MAX_APPLIANCES];
    private InfraredControl infraredControl;
    private PowerControl powerControl;
    private PlantControl plantControl;
    private HTTPControl httpControl;
    
    private Token[] tokens;
    
    private Parser parser;
    
    private int defaultRoom = 1;
    private String defaultDeviceType = null;
    private int defaultDeviceNumber = -1;
    private String defaultService = null;

	// Run the main thread
	public void run() {
		
		// Load plugins		
		for(String c: config.classInterfaces.keySet()) {
			try {
				String t = config.classType.get(c);
				print("Loading type " + t + ": " + c);
				@SuppressWarnings("unchecked")
				Class<Controller> cc = (Class<Controller>) Class.forName(c);
				Controller o = (Controller) cc.newInstance();
				o.setProvider(this);
				for(String s: config.classInterfaces.get(c)) {
					print("  Interface: " + s);
					if (s.equals("LightControl")) {
						for(int i=0;i<Config.MAX_LIGHTS;i++) {
							if (t == null || (config.lightTypes[i] != null && config.lightTypes[i].equals(t)))
								lightControl[i] = (LightControl) o;
						}
					} else if (s.equals("SocketControl")) {
						for(int i=0;i<Config.MAX_SOCKETS;i++) {
							if (t == null || (config.socketTypes[i] != null && config.socketTypes[i].equals(t)))
								socketControl[i] = (SocketControl) o;
						}
					} else if (s.equals("ApplianceControl")) {
						for(int i=0;i<Config.MAX_APPLIANCES;i++) {
							if (t == null || (config.applianceTypes[i] != null && config.applianceTypes[i].equals(t)))
								applianceControl[i] = (ApplianceControl) o;
						}
					} else if (s.equals("CameraControl")) {
						for(int i=0;i<Config.MAX_CAMERAS;i++) {
							if (t == null || (config.cameraTypes[i] != null && config.cameraTypes[i].equals(t)))
								cameraControl[i] = (CameraControl) o;
						}
					} else if (s.equals("SwitchControl")) {
						for(int i=0;i<Config.MAX_SWITCHES;i++) {
							if (t == null || (config.switchTypes[i] != null && config.switchTypes[i].equals(t)))
								switchControl[i] = (SwitchControl) o;
						}
					} else if (s.equals("SensorControl")) {
						for(int i=0;i<Config.MAX_SENSORS;i++) {
							if (t == null || (config.sensorTypes[i] != null && config.sensorTypes[i].equals(t)))
								sensorControl[i] = (SensorControl) o;
						}
					} else if (s.equals("MusicControl")) {
						for(int i=0;i<Config.MAX_MUSIC_SERVERS;i++) {
							musicControl[i] = (MusicControl) o;
						}
					} else if (s.equals("PowerControl")) {
						powerControl = (PowerControl) o;
					} else if (s.equals("PlantControl")) {
						plantControl = (PlantControl) o;
					} else if (s.equals("SpeechControl")) {
						speechControl = (SpeechControl) o;
					} else if (s.equals("AlarmControl")) {
						alarmControl = (AlarmControl) o;
					} else if (s.equals("HeatingControl")) {
						heatingControl = (HeatingControl) o;
					} else if (s.equals("CalendarControl")) {
						calendarControl = (CalendarControl) o;
					} else if (s.equals("EmailControl")) {
						emailControl = (EmailControl) o;
					} else if (s.equals("DatalogControl")) {
						datalogControl = (DatalogControl) o;
					} else if (s.equals("InfraredControl")) {
						infraredControl = (InfraredControl) o;
					} else if (s.equals("HTTPControl")) {
						httpControl = (HTTPControl) o;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		// Create and configure a parser
		parser = new Parser();
		parser.setReporter(this);
		parser.setConfig(config);

		// Start background thread
		backGround.setDaemon(true);
		backGround.start();

		// Start the server socket
		try {
			ss = new ServerSocket(config.listenPort);
		} catch (IOException e) {
			error("Failed to create server socket:" + e.getMessage());
			System.exit(1);
		}

		// Main loop
		while (true) {
			try {
				// Wait for a connection
				is = null;
				os = null;
				state = 1;
				s = ss.accept();
				state = 2;
				// Open input and output streams
				is = s.getInputStream();
				os = s.getOutputStream();

				// Read a command line
				cmd = getCmd();

				state = 3;
				debug("cmd: " + cmd);
				execute(cmd);
				state = 4;

			} catch (Exception e) {
				error("Exception executing cmd: " + e);
			} finally {
				// After a line read, close streams and socket
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
					}
				if (os != null)
					try {
						os.close();
					} catch (IOException e) {
					}
				if (s != null)
					try {
						s.close();
					} catch (IOException e) {
					}
			}
		}
	}

	// Check if host is reachable
	public boolean isReachableByPing(String host) {
		try {
			return InetAddress.getByName(host).isReachable(config.pingTimeout);
		} catch (Exception e) {
			return false;
		}
	}

	// main method
	public static void main(String[] args) {
		(new HouseControl()).run();
	}
	
	// Thread for implementing time consuming background tasks, and doing some
	// periodic checks
	class Background implements Runnable {
		@Override
		public void run() {
			for (;;) {
				if (alarmControl != null && musicControl[0] != null) {
					int alarmTime = alarmControl.getAlarmTime();
					if (alarmTime != 0 && alarmTime < System.currentTimeMillis()) {
						// Save the current volume
						int vol = volume;
						// Stop the music
						musicControl[0].pauseMusic(1);
						// Set the volume to max
						musicControl[0].setVolume(1, 100);
						// Sound the alarm
						alarmControl.soundAlarm();
						// Set the volume back
						musicControl[0].setVolume(1, vol);
						// Restart the music
						musicControl[0].playMusic(1);
					}
				}
				if (musicControl[0] != null) {
					volume = musicControl[0].getVolume(1);
					print("Music on: " + musicOn);
				}
				if (config.phoneName != null) phoneConnected = isReachableByPing(config.phoneName);
				
				try {
					Thread.sleep((config.backgroundDelay));
				} catch (InterruptedException e) {
				}
				print("Number of commands: " + numCmds + " state: " + state
						+ " command: " + cmd);
			}
		}
	}

	// Send string reply
	private void writeString(String s) throws IOException {
		os.write(s.getBytes());
	}

	// Print a message on the console
	public void print(String s) {
		logger.info(s);
	}

	// Print a message on the console
	public void debug(String s) {
		logger.debug(s);
	}

	// Print an error message
	public void error(String s) {
		logger.error(s);
	}

	// Get the command from the socket
	private String getCmd() {
		numCmds++;
		StringBuilder s = new StringBuilder();
		try {
			while (true) {
				int b = is.read();
				if (b < 0)
					break;
				//debug("Read: " + b);
				if (b == '\r' || b == '\n') {
					return s.toString();
				} else
					s.append((char) b);
			}
		} catch (IOException e) {
			error("IOException reading command");
		}
		return null;
	}

	// Send a playlist request to the music computer
	private String play(String playlist) {
		if (musicControl[0] == null) return ERROR;
		else return musicControl[0].play(1, playlist);
	}

	// Speak a message on this computer
	private void sayLocal(String msg) {
		if (speechControl != null) speechControl.say(msg);
	}

	// Speak a message on the music server or locally, if music server not
	// available
	public void say(String msg) {
		print("Saying " + msg);
		if (!noisy)
			return;
		if (musicOn)
			musicControl[0].say(1, msg);
		else
			sayLocal(msg);
	}

	// Send an email
	public void sendEmail(String subject, String msg) {
		if (emailControl != null) emailControl.email(subject, msg);
	}

	String parse(String source) throws IOException {
		debug("Command is " + source);
		tokens = parser.parse(source);
		if (tokens == null) return ERROR;

		int numTokens = tokens.length;
		// Deal with various abbreviations
		if (numTokens == 1 && tokens[0].getType() == Parser.QUANTITY) {
			// Single token quantity command
			// set the second (non-apace) token to 1
			expandTokens(1);
			tokens[1].setValue("1");
			tokens[1].setType(Parser.NUMBER);
			numTokens = 2;
			debug("Added number");
			debug("Type = " + tokens[0].getType());
		} else if (numTokens >= 2 && tokens[0].getType() == Parser.DEVICE && tokens[1].getType() != Parser.NUMBER) {
			// Assume device number one
			expandTokens(1);
			if (tokens.length > 3) tokens[3] = tokens[2];
			tokens[2] = tokens[1];
			tokens[1] = new Token("1", Parser.NUMBER, -1);
			numTokens += 1;
		} else if (tokens[0].getType() == Parser.VT_ACTION || tokens[0].getType() == Parser.PAN_ACTION) {
			// Assume vt 1
			expandTokens(2);
			if (tokens.length > 3) tokens[3] = tokens[1];
			tokens[2] = tokens[0];
			tokens[1] = new Token("1", Parser.NUMBER, -1);
			tokens[0] = new Token(Parser.devices[Parser.VT],Parser.DEVICE, -1);
			numTokens += 2;
		} else if (tokens[0].getType() == Parser.SERVICE || tokens[0].getType() == Parser.DIGIT || tokens[0].getType() == Parser.COLOR) {
			// Assume vt 1
			expandTokens(2);
			if (tokens.length > 3) tokens[3] = tokens[1];
			tokens[2] = tokens[0];
			tokens[1] = new Token("1", Parser.NUMBER, -1);
			tokens[0] = new Token(Parser.devices[Parser.VT],Parser.DEVICE, -1);
			numTokens += 2;
		} else if (tokens[0].getType() == Parser.ROBOT_ACTION) {
			// Assume robot 1
			expandTokens(2);
			if (tokens.length > 3) tokens[3] = tokens[1];
			tokens[2] = tokens[0];
			tokens[1] = new Token("1", Parser.NUMBER, -1);
			tokens[0] = new Token(Parser.devices[Parser.ROBOT], Parser.DEVICE, -1);
			numTokens += 2;
		} else if (tokens[0].getType() == Parser.AV_ACTION) {
			// Assume av 1
			expandTokens(2);
			if (tokens.length > 3) tokens[3] = tokens[1];
			tokens[2] = tokens[0];
			tokens[1] = new Token("1", Parser.NUMBER, -1);
			tokens[0] = new Token(Parser.devices[Parser.AV], Parser.DEVICE, -1);
			numTokens += 2;
		} else if (tokens[0].getType() == Parser.MUSIC_ACTION) {
			// Assume musicserver 1
			debug("Music action");
			expandTokens(2);
			if (tokens.length > 3)tokens[3] = tokens[1];
			tokens[2] = tokens[0];
			tokens[1] = new Token("1", Parser.NUMBER, -1);
			tokens[0] = new Token(Parser.devices[Parser.MUSIC_SERVER], Parser.DEVICE, -1);
			numTokens += 2;
		} else if (tokens[0].getType() == Parser.DEVICE_SET) {
			// assume default room
			expandTokens(2);
			if (tokens.length > 4) tokens[4] = tokens[2];
			if (tokens.length > 3)tokens[3] = tokens[1];
			tokens[2] = tokens[0];
			tokens[1] = new Token("" + defaultRoom, Parser.NUMBER, -1);
			tokens[0] = new Token(Parser.areas[Parser.ROOM], Parser.AREA, -1);
			numTokens += 2;			
		}

		// Execute command
		switch (tokens[0].getType()) {
		default:
			error("Command not recognised: " + source);
			return ERROR;
		case Parser.DEFAULT:
			print("Default command");
			switch (parser.find(tokens[0].getValue(), Parser.defaults)) {
			case Parser.DEFAULT_LOCATION:
				int n = Integer.parseInt(tokens[1].getValue());
				print("Setting default room to " + n);
				defaultRoom = n;
				return SUCCESS;
			default:
				error("Default command not supported");
				return ERROR;
			}
		case Parser.SOCKET_NAME:
			int sock = parser.find(tokens[0].getValue(),config.socketNames);
			if (numTokens == 1) {		
				return "" + sock;
			} else if (tokens[1].getType() == Parser.ACTION) {
				int act = parser.find(tokens[1].getValue(), Parser.actions);
				switch(act) {
				case Parser.ON:
				case Parser.OFF:
					socketControl[sock].switchSocket(sock,(act == Parser.ON));
					return SUCCESS;
				case Parser.STATUS:
					return switchString(socketControl[sock].getSocketStatus(sock));
				default:
					error("Socket name action not supported");
					return ERROR;
				}		
			}
			error("Invalid socket name command");
			return ERROR;
		case Parser.LIGHT_NAME:
			int lightNumber = parser.find(tokens[0].getValue(),config.lightNames);
			if (numTokens == 1) {		
				return "" + lightNumber;
			} else if (tokens[1].getType() == Parser.ACTION) {
				int act = parser.find(tokens[1].getValue(), Parser.actions);
				switch(act) {
				case Parser.ON:
				case Parser.OFF:
					lightControl[lightNumber].switchLight(lightNumber,(act == Parser.ON));
					return SUCCESS;
				case Parser.STATUS:
					return switchString(lightControl[lightNumber].getLightStatus(lightNumber));
				case Parser.SET:
					lightControl[lightNumber].dimLight(lightNumber,Integer.parseInt(tokens[2].getValue()));
					return SUCCESS;
				default:
					error("Light name action not supported");
					return ERROR;
				}		
			}
			error("Invalid light name command");
			return ERROR;
		case Parser.CAMERA_NAME:
			int cameraNumber = parser.find(tokens[0].getValue(),config.cameraNames);
			if (numTokens == 1) {		
				return "" + cameraNumber;
			} else if (tokens[1].getType() == Parser.PAN_ACTION) {
				int act = parser.find(tokens[1].getValue(), Parser.panActions);
				switch(act) {
				case Parser.UP:
					cameraControl[cameraNumber-1].panUp();
					return SUCCESS;
				case Parser.DOWN:
					cameraControl[cameraNumber-1].panDown();
					return SUCCESS;
				case Parser.LEFT:
					cameraControl[cameraNumber-1].panLeft();
					return SUCCESS;
				case Parser.RIGHT:
					cameraControl[cameraNumber-1].panRight();
					return SUCCESS;
				default:
					error("Camera action not supported");
					return ERROR;
				}		
			}
			error("Invalid camera name command");
			return ERROR;
		case Parser.APPLIANCE_NAME:
			int appl = parser.find(tokens[0].getValue(),config.applianceNames);
			if (numTokens == 1) {		
				return "" + appl;
			} else if (tokens[1].getType() == Parser.ACTION) {
				int act = parser.find(tokens[1].toString(), Parser.actions);
				switch(act) {
				case Parser.ON:
				case Parser.OFF:
					applianceControl[appl].switchAppliance(appl, (act == Parser.ON));
					return SUCCESS;
				case Parser.STATUS:
					return switchString(applianceControl[appl].getApplianceStatus(appl));
				case Parser.VALUE:
					return "" + applianceControl[appl].getAppliancePower(appl);
				default:
					error("Appliance name action not supported");
					return ERROR;
				}		
			}
			error("Invalid appliance name command");
			return ERROR;
		case Parser.QUANTITY:	
			switch (tokens[1].getType()) {
			case Parser.NUMBER:
				int n = Integer.parseInt(tokens[1].getValue());
				switch(parser.find(tokens[0].getValue(),Parser.quantities)) {
				case Parser.TEMPERATURE:
					return "" + sensorControl[n].getTemperature(n);
				case Parser.HUMIDITY:
				case Parser.SOIL_MOISTURE:
					return "" + sensorControl[n].getHumidity(n);
				case Parser.OCCUPIED:
					return switchString(sensorControl[n].getMotion(n));
				case Parser.LIGHT_LEVEL:
					return "" + sensorControl[n].getLightLevel(n);
				case Parser.BATTERY:
					return switchString(!sensorControl[n].getBatteryLow(n));
				case Parser.POWER:
					return "" + powerControl.getPower();
				case Parser.ENERGY:
					return "" + powerControl.getEnergy();
				default:
					error("Quantity not supported");
					return ERROR;
				}
			default:
				error("Invalid quantity command");
				return ERROR;
			}
		case Parser.DEVICE:
			// Its a device
			debug("Device command");
			
			if (numTokens < 2) {
				error("Invalid device command");
				return ERROR;
			}

			switch (tokens[1].getType()) {
			case Parser.NUMBER:
				debug("Device command with number");
				int n = Integer.parseInt(tokens[1].getValue()) -1;
				int device = parser.find(tokens[0].getValue(), Parser.devices);
				debug("Device = " + device);
				if (numTokens > 2 && tokens[2].getType() == Parser.ACTION) {
					int act = parser.find(tokens[2].getValue(), Parser.actions);
					switch (act) {
					case Parser.ON:
					case Parser.OFF:
						switch (device) {
						case Parser.TV:
							sendCommand(TV, AV.ON);
							return SUCCESS;
						case Parser.VT:
							sendCommand(TIVO, 0x8c);
							return SUCCESS;
						case Parser.AV:
							// TODO
							return ERROR;
						case Parser.LIGHT:
							lightControl[n].switchLight(n,(act == Parser.ON));
							return SUCCESS;
						case Parser.SOCKET:
							socketControl[n].switchSocket(n, (act == Parser.ON));
							return SUCCESS;
						case Parser.APPLIANCE:
							applianceControl[n].switchAppliance(n, (act == Parser.ON));
							return SUCCESS;
						case Parser.HEATING:
							heatingControl.setHeating(act == Parser.ON);
							return SUCCESS;
						case Parser.CAMERA:
							// TODO
						case Parser.SWITCH:
						case Parser.SENSOR:
						case Parser.PHONE:
							error("Cannot turn this device on or off");
							return ERROR;
						case Parser.MUSIC_SERVER:
							if (act == Parser.ON) {
								musicControl[0].shutDown(1);
								return SUCCESS;
							} else {
								error("Cannot turn a music server on");
								return ERROR;
							}
						default:
							error("Unsupported device");
							return ERROR;
						}			
					case Parser.STATUS:
						switch (device) {
						case Parser.SOCKET:
							return switchString(socketControl[n].getSocketStatus(n));
						case Parser.LIGHT:
							return switchString(lightControl[n].getLightStatus(n));
						case Parser.APPLIANCE:
							return switchString(applianceControl[n].getApplianceStatus(n));
						case Parser.PHONE:
							return switchString(phoneConnected);
						case Parser.MUSIC_SERVER:
							return switchString(musicOn);
						case Parser.SWITCH:
							return switchString(switchControl[n].getSwitchStatus(n));
						case Parser.HEATING:
							return switchString(heatingControl.getHeatingStatus());
						case Parser.ALARM:
							int togo = alarmControl.getAlarmTime();
							return switchString(togo > 0);
						default:
							error("Cannot return the status of this device");
							return ERROR;
						}
					case Parser.VALUE:
						switch (device) {
						case Parser.APPLIANCE:
							return "" + applianceControl[n].getAppliancePower(n);
						case Parser.LIGHT:
							return "" + lightControl[n].getLightLevel(n);
						case Parser.HEATING:
							return "" + heatingControl.getRequiredTemperature();
						case Parser.ALARM:
							int togo = alarmControl.getAlarmTime();
							return "" + (togo < 0 ? 0 : togo);
						default:
							error("This device does not have a value");
							return ERROR;
						}
					case Parser.SET:						
						if (numTokens < 4) {
							error("Set command needs a value");
							return ERROR;
						}
						
						switch (device) {
						case Parser.LIGHT:
							lightControl[n].dimLight(n,Integer.parseInt(tokens[3].getValue()));
							return SUCCESS;
						case Parser.HEATING:
							heatingControl.setRequiredTemperature(Integer.parseInt(tokens[3].getValue()));
							return SUCCESS;
						case Parser.MUSIC_SERVER:
							musicControl[0].setVolume(1, Integer.parseInt(tokens[3].getValue()));
							return SUCCESS;
						case Parser.ALARM:
							alarmControl.setAlarmTime(Integer.parseInt(tokens[3].getValue()));
							return SUCCESS;
						default:
							error("This device cannot have a value set");
							return ERROR;
						}
					case Parser.CLEAR:
						if (device == Parser.ALARM) {
							alarmControl.clearAlarm();
							return SUCCESS;
						} else {
							error("Device does not support clear");
							return ERROR;
						}
					case Parser.DELETE:
						if (device == Parser.VT) {
							sendCommand(TIVO, AV.DELETE);
							return SUCCESS;
						} else {
							error("Device does not support DELETE");
							return ERROR;
						}
					case Parser.BACK:
						if (device == Parser.VT) {
							sendCommand(TIVO, AV.BACK);
							return SUCCESS;
						} else {
							error("Device does not support BACK");
							return ERROR;
						}						
					default:
						error("Unsupported action");
						return ERROR;
					}
				} else if (device == Parser.VT && numTokens > 2 && tokens[2].getType() == Parser.DIGIT) {
					sendCommand(TIVO, 0x80 + parser.find(tokens[2].getValue(),Parser.digits));
					return SUCCESS;
				} else if (device == Parser.VT && numTokens > 2 && tokens[2].getType() == Parser.COLOR) {
					switch(parser.find(tokens[2].getValue(),Parser.colors)) {
					case Parser.BLUE:
						sendCommand(TIVO, AV.BLUE);
						return SUCCESS;
					case Parser.RED:
						sendCommand(TIVO, AV.RED);
						return SUCCESS;
					case Parser.YELLOW:
						sendCommand(TIVO, AV.YELLOW);
						return SUCCESS;
					case Parser.GREEN:
						sendCommand(TIVO, AV.GREEN);
						return SUCCESS;
					}
				} else if (numTokens > 2 && tokens[2].getType() == Parser.SERVICE) {
					switch(parser.find(tokens[2].getValue(),Parser.services)) {
					case Parser.GUIDE:
						sendCommand(TIVO, AV.GUIDE);
						return SUCCESS;
					case Parser.HOME:
						sendCommand(TIVO, AV.HOME);
						return SUCCESS;
					case Parser.SHOWS:
						sendCommand(TIVO, AV.SHOWS);
						return SUCCESS;
					case Parser.VOLUME:
						if (numTokens == 3) {
							return "" + volume;
						} else  if (tokens[3].getType() == Parser.PAN_ACTION) {
							switch(parser.find(tokens[3].getValue(),Parser.panActions)) {
							case Parser.UP:
								sendCommand(RECEIVER, AV.VOLUME_UP);
								return SUCCESS;
							case Parser.DOWN:
								sendCommand(RECEIVER, AV.VOLUME_DOWN);
								return SUCCESS;
							default:
								error("Invalid volume command");
								return ERROR;
							}
						} else {
							error("Invalid volume command");
							return ERROR;
						}
					case Parser.CHANNEL:
						if (device == Parser.VT) {
							if (tokens[3].getType() == Parser.PAN_ACTION) {
								switch(parser.find(tokens[3].getValue(),Parser.panActions)) {
								case Parser.UP:
									sendCommand(TIVO, AV.CHANNEL_UP);
									return SUCCESS;
								case Parser.DOWN:
									sendCommand(TIVO, AV.CHANNEL_DOWN);
									return SUCCESS;
								default:
									error("Invalid channel command");
									return ERROR;
								}
							} else if (tokens[3].getType() == Parser.NUMBER) {
								infraredControl.setChannel(tokens[3].getValue());
								return SUCCESS;
							} else {
								error("Invalid channel command");
								return ERROR;
							}
						} else {
							error("Device does not support channel");
							return ERROR;
						}
					case Parser.SPOTIFY:
						play(tokens[3].getValue());
						return SUCCESS;
					case Parser.BROADCAST:
						sendCommand(TIVO, AV.TV);
						return SUCCESS;
					case Parser.SUBTITLES:
						sendCommand(TIVO, AV.SUBTITLES);
						return SUCCESS;
					case Parser.INFO:
						sendCommand(TIVO, AV.INFO);
						return SUCCESS;
					case Parser.IPLAYER:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.ITVPLAYER:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.OD4:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.DEMAND5:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.SETTINGS:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.PARENTAL:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.PURCHASE:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.MESSAGES:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.HELP:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.ONDEMAND:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						return SUCCESS;
					case Parser.CATCHUP:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.TVXL:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.MOVIES:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
					    sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;					
					case Parser.MUSIC:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();						
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;	
					case Parser.SPORTS:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();	
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;	
					case Parser.GAMES:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;	
					case Parser.LIFESTYLE:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.NEWS:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.APPS:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;	
					case Parser.FEATURED:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.SYSTEM:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.RENTALS:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;							
					case Parser.YOUTUBE:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_RIGHT);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();						
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.PAY_PER_VIEW:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_DOWN);
						delay();
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.SERIES:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.DIGIT_1);
						return SUCCESS;	
					case Parser.PLANNED:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.DIGIT_2);
						return SUCCESS;
					case Parser.WISHLIST:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.DIGIT_3);
						return SUCCESS;
					case Parser.BROWSE:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.DIGIT_5);
						return SUCCESS;	
					case Parser.SOURCE:
						switch(parser.find(tokens[3].getValue(),Parser.source)) {
						case Parser.STB:
							sendCommand(RECEIVER, AV.SOURCE_STB);
							return SUCCESS;
						case Parser.DVD:
							sendCommand(RECEIVER, AV.SOURCE_DVD);
							return SUCCESS;
						case Parser.BLUERAY:
							sendCommand(RECEIVER, AV.SOURCE_BLUERAY);
							return SUCCESS;	
						default:
							error("Unknown source");
							return ERROR;
						}
					case Parser.SEARCH:
						sendCommand(TIVO, AV.HOME);
						delay();
						sendCommand(TIVO, AV.DIGIT_4);
						return SUCCESS;
					case Parser.THUMBS:
						if (tokens[3].getType() == Parser.PAN_ACTION) {
							switch(parser.find(tokens[3].getValue(),Parser.panActions)) {
							case Parser.UP:
								sendCommand(TIVO, AV.THUMBS_UP);
								return SUCCESS;
							case Parser.DOWN:
								sendCommand(TIVO, AV.THUMBS_DOWN);
								return SUCCESS;
							default:
								error("Invalid thumbs command");
								return ERROR;
							}
						} else {
							error("Invalid thumbs command");
							return ERROR;
						}
					case Parser.PIN:
						sendCommand(TIVO,AV.DIGIT_1);
						delay(1000);
						sendCommand(TIVO,AV.DIGIT_2);
						delay(1000);
						sendCommand(TIVO,AV.DIGIT_3);
						delay(1000);
						sendCommand(TIVO,AV.DIGIT_4);
						return SUCCESS;
					default:
						error("Unsupported service");
						return ERROR;
					}
				} else if (device == Parser.VT && numTokens > 2 && tokens[2].getType() == Parser.VT_ACTION) {
					switch(parser.find(tokens[2].getValue(),Parser.vtActions)) {
					case Parser.OK:
						sendCommand(TIVO, AV.MENU_OK);
						return SUCCESS;
					case Parser.LAST_CH:
						sendCommand(TIVO, AV.LAST_CHANNEL);
						return SUCCESS;
					case Parser.SEND:
						int code = Integer.parseInt(tokens[3].getValue(), 16);
						sendCommand(TIVO, code);
						return SUCCESS;
					case Parser.TYPE:
						type(tokens[3].getValue());
						return SUCCESS;
					default:
						error("Unsupported TiVo command");
						return ERROR;
					}
				} else if (device == Parser.AV && numTokens > 2 && tokens[2].getType() == Parser.AV_ACTION) {
					switch(parser.find(tokens[2].getValue(), Parser.avActions)) {	
					case Parser.MUTE:
						sendCommand(RECEIVER, AV.MUTE);
						return SUCCESS;
					case Parser.VOLUP:
						sendCommand(RECEIVER, AV.VOLUME_UP);
						return SUCCESS;
					case Parser.VOLDOWN:
						sendCommand(RECEIVER, AV.VOLUME_DOWN);
						return SUCCESS;
					default:
						error("Unsupported AV action");
						return ERROR;
					}
				} else if (device == Parser.VT && numTokens > 2 && tokens[2].getType() == Parser.PAN_ACTION) {
					switch(parser.find(tokens[2].getValue(), Parser.panActions)) {
					case Parser.UP:
						sendCommand(TIVO, AV.MENU_UP);
						return SUCCESS;
					case Parser.DOWN:
						sendCommand(TIVO, AV.MENU_DOWN);
						return SUCCESS;
					case Parser.LEFT:
						sendCommand(TIVO, AV.MENU_LEFT);
						return SUCCESS;
					case Parser.RIGHT:
						sendCommand(TIVO, AV.MENU_RIGHT);
						return SUCCESS;
					default:
						error("Invalid pan action");
						return ERROR;
					}
				} else if (device == Parser.VT && numTokens > 2 && tokens[2].getType() == Parser.MUSIC_ACTION) {
					switch(parser.find(tokens[2].getValue(), Parser.musicActions)) {
					case Parser.PLAY:
						sendCommand(TIVO, AV.PLAY);
						return SUCCESS;
					case Parser.PAUSE:
						sendCommand(TIVO, AV.PAUSE);
						return SUCCESS;
					case Parser.STOP:
						sendCommand(TIVO, AV.STOP);
						return SUCCESS;
					case Parser.FF:
						sendCommand(TIVO, AV.FAST_FORWARD);
						return SUCCESS;
					case Parser.FB:
						sendCommand(TIVO, AV.FAST_BACKWARDS);
						return SUCCESS;
					case Parser.SKIP:
						sendCommand(TIVO, AV.SKIP_FORWARDS);
						return SUCCESS;	
					case Parser.SKIPB:
						sendCommand(TIVO, AV.SKIP_BACKWARDS);
						return SUCCESS;	
					case Parser.SLOW:
						sendCommand(TIVO, AV.SLOW);
						return SUCCESS;	
					default:
						error("Invalid TIVO music command");
						return ERROR;
					}
				} else if (device == Parser.MUSIC_SERVER && numTokens > 2 && tokens[2].getType() == Parser.MUSIC_ACTION) {
					switch(parser.find(tokens[2].getValue(), Parser.musicActions)) {
					case Parser.STOP:
						musicControl[n].pauseMusic(n);
						return SUCCESS;
					case Parser.SKIP:
						musicControl[n].skipTrack(n);
						return SUCCESS;
					case Parser.SHUT_DOWN:
						musicControl[n].shutDown(n);
						return SUCCESS;
					case Parser.SPEAK:
						print("Setting noisy true");
						noisy = true;
						return SUCCESS;
					case Parser.SILENT:
						print("Setting noisy false");
						noisy = false;
						return SUCCESS;
					case Parser.PLAY:
						play(tokens[3].getValue());
						return SUCCESS;
					case Parser.SAY:
						say(source.substring(source.indexOf(Parser.musicActions[Parser.SAY]) + Parser.musicActions[Parser.SAY].length() + 1)); 
						return SUCCESS;
					case Parser.PLAYLIST:
						return musicControl[n].getPlaylist(n);
					default:
						error("Invalic music server command");
						return ERROR;
					}
				} else if (device == Parser.ROBOT && numTokens > 2 && tokens[2].getType() == Parser.ROBOT_ACTION) {
					switch(parser.find(tokens[2].getValue(),Parser.robotActions)) {
					case Parser.CLEAN:
						say("I'm sorry, Elliot, I'm afraid I can't do that");
						return ERROR;
					case Parser.FETCH:
						say("I'm sorry, Lawrie, I'm afraid I can't do that");
						return ERROR;	
					default:
						error("Invalid robot action");
						return ERROR;
					}
				} else if (numTokens > 2 && tokens[2].getType() == Parser.QUANTITY) {
					switch(parser.find(tokens[2].getValue(),Parser.quantities)) {
					case Parser.TEMPERATURE:
						return "" + sensorControl[n+1].getTemperature(n+1);
					case Parser.HUMIDITY:
					case Parser.SOIL_MOISTURE:
						return "" + sensorControl[n+1].getHumidity(n+1);
					case Parser.LIGHT_LEVEL:
						return "" + sensorControl[n+1].getLightLevel(n+1);
					case Parser.BATTERY:
						return "" + !sensorControl[n+1].getBatteryLow(n+1);
					case Parser.POWER:
						return "" + powerControl.getPower();
					case Parser.ENERGY:
						return "" + powerControl.getEnergy();
					case Parser.OCCUPIED:
						return switchString(sensorControl[n+1].getMotion(n+1));
					default:
						error("Unsupported quantity");
						return ERROR;
					}
				}
				error("Invalid device number command");
				return ERROR;
			default:
				error("Invalid device command");
				return ERROR;
			}
		case Parser.ACTION:
			// Its a command without a device
			switch (parser.find(tokens[0].getValue(), Parser.actions)) {
			case Parser.MOOD:
				debug("Setting mood to " + tokens[1].getValue());
				break;
			case Parser.EMAIL:
				sendEmail("Home automation email",
						source.substring(Parser.actions[Parser.EMAIL].length() + 1));
				return SUCCESS;
			case Parser.SIGNAL:
				if (tokens[1].getValue().equals("highpower")) {
					say("You are using " + powerControl.getPower() + " watts of electricity");
					return SUCCESS;
				}
				error("Unrecognised signal");
				return ERROR;
			default:
				error("Invalid command");
				return ERROR;
			}
			break;
		case Parser.AREA:
			// Its an area query
			boolean floor = parser.find(tokens[0].getValue(), Parser.areas) == Parser.FLOOR;
			switch (tokens[1].getType()) {
			case Parser.NUMBER:
				debug("Area command with number");
				int n = Integer.parseInt(tokens[1].getValue());

				if (floor) {
					if (n < 1 || n > Config.MAX_FLOORS) {
						error("Floor number out of range");
						return ERROR;
					}
				} else {
					if (n < 1 || n > Config.MAX_ROOMS) {
						error("Room number out of range");
						return ERROR;
					}
				}
				
				if (numTokens == 2) {
					// Just floor or room with no parameters - return name
					if (floor) {
						return config.floorNames[n-1];
					} else {
						return config.roomNames[n-1];
					}
				} else if (tokens[2].getType() == Parser.ACTION) {
					switch (parser.find(tokens[2].getValue(), Parser.actions)) {
					case Parser.ON:
					case Parser.OFF:
						error("Cannot switch an area on or off");
						return ERROR;
					case Parser.STATUS:
					case Parser.VALUE:
						error("Areas do not have a status or value");
						return ERROR;
					default:
						error("Unsupported area action");
						return ERROR;
					}
				} else if (tokens[2].getType() == Parser.QUANTITY) {
					switch (parser.find(tokens[2].getValue(), Parser.quantities)) {
					case Parser.TEMPERATURE:
						int t = 0;
						int i = 0;
						for(int s: config.roomSensors[n-1]) {
							print("Adding " + sensorControl[s].getTemperature(s) + " for room " + n + " sensor " + s);
							t += sensorControl[s].getTemperature(s);
							i++;			
						}
						return "" + (i == 0 ? 0 : (t/i));
					case Parser.HUMIDITY:
						int h = 0;
						i = 0;
						for(int s: config.roomSensors[n-1]) {
							print("Adding " + sensorControl[s].getHumidity(s) + " for room " + n + " sensor " + s);
							h += sensorControl[s].getHumidity(s);
							i++;			
						}
						return "" + (i == 0 ? 0 : (h/i));
					case Parser.LIGHT_LEVEL:
						int l = 0;
						i = 0;
						for(int s: config.roomSensors[n-1]) {
							print("Adding " + sensorControl[s].getLightLevel(s) + " for room " + n + " sensor " + s);
							l += sensorControl[s].getLightLevel(s);
							i++;			
						}
						return "" + (i == 0 ? 0 : (l/i));
					case Parser.OCCUPIED:
						boolean occ = false;
						i = 0;
						for(int s: config.roomSensors[n-1]) {
							print("Testing " + sensorControl[s].getMotion(s) + " for sensor " + s);
							if (sensorControl[s].getMotion(s)) occ = true;
							i++;			
						}
						return switchString(occ);
					default:
						error("Area quantity command not supported");
						return ERROR;
					}
				} else if (tokens[2].getType() == Parser.DEVICE_SET) {
					int dSet = parser.find(tokens[2].getValue(), Parser.deviceSets);
					if (numTokens > 3 && tokens[3].getType() == Parser.ACTION) {
						int act = parser.find(tokens[3].getValue(), Parser.actions);
						switch (act) {
						case Parser.ON:
						case Parser.OFF:
							switch (dSet) {
							case Parser.LIGHTS:
								switchLights(n,(act == Parser.ON));
								return SUCCESS;
							case Parser.SOCKETS:
								switchSockets(n,(act == Parser.ON));
								return SUCCESS;
							default:
								error("Device set cannot be switched on or off");
								return ERROR;
							}
						case Parser.SET:
							switch (dSet) {
							case Parser.LIGHTS:
								if (numTokens > 4 && tokens[4].getType() == Parser.NUMBER) {
									dimLights(n,Integer.parseInt(tokens[4].getValue()));
									return SUCCESS;
								} else {
									error("Invalid device-set set command");
									return ERROR;
								}
							default:
								error("Device set cannot be set");
								return ERROR;
							}
						default:
							error("Invalid device-set command");
							return ERROR;
						}
					}
				}
				error("Invalid area command");
				return ERROR;
			default:
				error(tokens[0].getValue() + " must be followed by a number");
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	// Execute an HTTP or housish command
	private void execute(String cmd) throws IOException {
		if (cmd.length() > 4 && cmd.substring(0,3).equals("GET") ) writeString(httpControl.httpCommand(cmd));
		else writeString(parse(cmd));
	}
	
	// Convert boolean to on/off string
	private String switchString(boolean on) {
		return (on ? "on" : "off");
	}
	
	// Switch all the lights in a room on or off
	private void switchLights(int room, boolean on) throws IOException {
		print("Switching lights for room " + room);
		for(int light: config.roomLights[room-1]) {
			lightControl[light-1].switchLight(light-1,on);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// Switch all the sockets i a room on or off
	private void switchSockets(int room, boolean on) throws IOException {
		for(int socket: config.roomSockets[room-1]) {
			socketControl[socket-1].switchSocket(socket-1,on);
		}
	}
	
	// Dim all the lights in a room to the same level
	private void dimLights(int room, int level) throws IOException {
		for(int light: config.roomLights[room-1]) {
			lightControl[light-1].dimLight(light-1,level);
		}
	}
	
	// Expand the token array with new empty tokens
	private void expandTokens(int n) {
		debug("Expanding tokens by " + n);
		int l = tokens.length;
		Token[] oldTokens = tokens;
		tokens = new Token[l+n];
		for(int i=0;i<l;i++) tokens[i] = oldTokens[i];
		for(int i=l;i<l+n;i++) tokens[i] = new Token("",-1,-1);
		debug("Expansion done");
	}

	@Override
	public Config getConfig() {
		return config;
	}

	@Override
	public Reporter getReporter() {
		return this;
	}

	@Override
	public Alerter getAlerter() {
		return this;
	}

	@Override
	public LightControl getLightControl(int n) {
		return lightControl[n];
	}

	@Override
	public SocketControl getSocketControl(int n) {
		return socketControl[n];
	}

	@Override
	public CameraControl getCameraControl(int n) {
		return cameraControl[n];
	}

	@Override
	public MusicControl getMusicControl(int n) {
		return musicControl[n];
	}

	@Override
	public DatalogControl getDatalogControl() {
		return datalogControl;
	}
	
	private void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	private void delay() {
		delay(2000);
	}
	
	void type(char c) throws IOException {
		int repeats = 0;
		int digit = -1;
		
		if (c >= 'a' && c <= 'c') {
			digit = 2;
			repeats = (c - 'a');
		} else if (c >= 'd' && c <= 'f') {
			digit = 3;
			repeats = c - 'd';
		} else if (c >= 'g' && c <= 'i') {
			digit = 4;
			repeats = c - 'g';
		} else if (c >= 'j' && c <= 'l') {
			digit = 5;
			repeats = c - 'j';
		} else if (c >= 'm' && c <= 'o') {
			digit = 6;
			repeats = c - 'm';
		} else if (c >= 'p' && c <= 's') {
			digit = 7;
			repeats = c - 'p';
		} else if (c >= 't' && c <= 'v') {
			digit = 8;
			repeats = c - 't';
		} else if (c >= 'w' && c <= 'z') {
			digit = 9;
			repeats = c - 'w';
		} else if (c == ' ') {
			digit = 0;
			repeats = 0;
		} else if (c >= '0' && c <= '9' ) {
			digit = c - '0';
			if (digit == 0) repeats = 1;
			else if (digit == 1) repeats = 0;
			else if (digit == 7 || digit == 9) repeats = 4;
			else repeats = 3;
		}
		
		if (infraredControl != null && digit >= 0) {
			for(int i=0;i<=repeats;i++) {
				sendCommand(TIVO, 0x80 + digit);
				delay(100);
			}
		}
	}
	
	// Type in Virgin TV search box
	private void type(String s) throws IOException {
		for(int i=0;i<s.length();i++) {
			type(s.charAt(i));
			delay(1500);
		}
	}
	
	// Send an IR command
	private void sendCommand(int id, int cmd)throws IOException {
		if (infraredControl != null) infraredControl.sendCommand(id, cmd);
	}
}
