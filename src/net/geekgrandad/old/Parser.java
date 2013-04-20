package net.geekgrandad.old;

import java.text.BreakIterator;
import java.util.Locale;

import net.geekgrandad.config.Config;

public class Parser {
	
	final int INVALID = -1;
	
	String[] devices = {"light","socket","appliance","switch","camera","sensor", 
			            "tv", "vt", "av", "phone", "musicserver", "radiator", "robot",
			            "alarm","blind"};
	
	final int LIGHT = 0;
	final int SOCKET = 1;
	final int APPLIANCE = 2;
	final int SWITCH = 3;
	final int CAMERA = 4;
	final int SENSOR = 5;
	final int TV = 6;
	final int VT = 7;
	final int AV = 8;
	final int PHONE = 9;
	final int MUSIC_SERVER = 10;
	final int RADIATOR = 11;
	final int ROBOT = 12;
	final int ALARM = 13;
	final int BLIND = 14;
	
	String[] deviceSets = {"lights","sockets","appliances","switches","cameras","sensors", 
            "tvs", "vts", "avs", "phones", "musicservers", "radiators", "robots",
            "alarms","blinds"};

	final int LIGHTS = 0;
	final int SOCKETS = 1;
	final int APPLIANCES = 2;
	final int SWITCHES = 3;
	final int CAMERAS = 4;
	final int SENSORS = 5;
	final int TVS = 6;
	final int VTS = 7;
	final int AVS = 8;
	final int PHONES = 9;
	final int MUSIC_SERVERS = 10;
	final int RADIATORS = 11;
	final int ROBOTS = 12;
	final int ALARMS = 13;
	final int BLINDS = 14;
	
	String[] areas = {"floor", "room"};
	
	final int FLOOR = 0;
	final int ROOM = 1;
	
	String[] quantities = {"temperature", "humidity",  "lightlevel", "battery", 
			               "volume", "power", "occupied", "energy", "sound",
			               "outside_temperture", "outside_humidity", "weather",
			               "soil_moisture"};
	
	final int TEMPERATURE = 0;
	final int HUMIDITY = 1;
	final int LIGHT_LEVEL = 2;
	final int BATTERY = 3;
	final int VOLUME = 4;
	final int POWER = 5;
	final int OCCUPIED = 6;
	final int ENERGY = 7;
	final int SOUND = 8;
	final int OUTSIDE_TEMPERATURE = 9;
	final int OUTSIDE_HUMIDITY = 10;
	final int WEATHER = 11;
	final int SOIL_MOISTURE = 12;
	
	String[] floorNames, roomNames, sensorNames, lightNames, socketNames, applianceNames, cameraNames, switchNames;
	
	String[] actions = {"on","off","status","value","set", "play", "say", "mood", "channel"};
	
    final int ON = 0;
    final int OFF = 1;
    final int STATUS = 2;
    final int VALUE = 3;
    final int SET = 4;
	final int PLAY = 5;
	final int SAY = 6;
	final int MOOD = 7;
	final int CHANNEL = 8;
		
	String[] panActions = {"left","right","up","down"};
	
	final int LEFT = 0;
	final int RIGHT = 1;
	final int UP = 2;
	final int DOWN = 3;
	
	String[] tvActions = {"guide","home","shows","tv","ok"};
	
	final int GUIDE = 0;
	final int HOME = 1;
	final int SHOWS = 2;
	final int SHOWTV = 3;
	final int OK = 4;
	
	String[] avActions = {"volup","voldown","dvd","stb","bd","mute"};
	
	final int VOLUP = 0;
	final int VOLDOWN = 1;
	final int DVD = 2;
	final int STB = 3;
	final int BD = 5;
	final int MUTE = 6;
	
	String[] musicActions = {"stop","skip","shutdown"};
	
	final int STOP = 0;
	final int SKIP = 1;
	final int SHUT_DOWN = 2;
	
	BreakIterator boundary = BreakIterator.getWordInstance(Locale.UK);

	public Parser(String[] floorNames, String[] roomNames, String [] sensorNames, String[] lightNames,
			      String[] socketNames, String[] applianceNames, String[] cameraNames, String[] switchNames) {
		this.floorNames = floorNames;
		this.roomNames = roomNames;
		this.sensorNames = sensorNames;
		this.lightNames = lightNames;
		this.socketNames = socketNames;
		this.applianceNames = applianceNames;
		this.cameraNames = cameraNames;
		this.switchNames = switchNames;
	}
	
	final int COMMAND = 0;
	final int AREA = 1;
	final int SPACES = 2;
	final int DEVICE = 3;
	final int QUANTITY = 4;
	final int ACTION = 5;
	final int FLOOR_NAME = 6;
	final int ROOM_NAME = 7;
	final int SENSOR_NAME = 8;
	final int SOCKET_NAME = 9;
	final int LIGHT_NAME = 10;
	final int SWITCH_NAME = 11;
	final int APPLIANCE_NAME = 12;
	final int CAMERA_NAME = 13;
	final int NUMBER = 14;
	final int PAN_ACTION = 15;
	final int TV_ACTION = 16;
	final int AV_ACTION = 17;
	final int DEVICE_SET = 18;
	
	int getType(String token) {
		int type = -1;
		
		if (token.trim().isEmpty()) {
			return SPACES;
		}
		
		try {
			int n = Integer.parseInt(token);
			return NUMBER;
		} catch (Exception e) {}
		
		if (find(token,floorNames) >= 0) type = FLOOR;
		else if (find(token,roomNames) >= 0) type = ROOM;
		else if (find(token,devices) >= 0) type = DEVICE;
		else if (find(token,deviceSets) >= 0) type = DEVICE_SET;
		else if (find(token,quantities) >= 0) type = QUANTITY;
		else if (find(token,actions) >= 0) type = ACTION;
		else if (find(token,areas) >= 0) type = AREA;
		else if (find(token,tvActions) >= 0) type = TV_ACTION;
		else if (find(token,panActions) >= 0) type = PAN_ACTION;
		else if (find(token,avActions) >= 0) type = AV_ACTION;
		else if (find(token,sensorNames) >= 0) type = SENSOR_NAME;
		else if (find(token,socketNames) >= 0) type = SOCKET_NAME;
		else if (find(token,lightNames) >= 0) type = LIGHT_NAME;
		else if (find(token,applianceNames) >= 0) type = APPLIANCE_NAME;
		else if (find(token,cameraNames) >= 0) type = CAMERA_NAME;
		else if (find(token,switchNames) >= 0) type = SWITCH_NAME;
		
		return type;
	}
	
	final String ERROR = "error";
	final String GOOD = "ok";
	
	String parse(String source) {
		
		debug("Command is " + source);
		
		boundary.setText(source);
		int start = boundary.first();
		int end=boundary.next();
		
        if (end == BreakIterator.DONE) {
        	error("Empty command");
        	return ERROR;
        }
		
		String first = source.substring(start,end);
        int firstType = getType(first);
        
	    debug("First token = '" + first + "', type = "+ firstType);
        
	    start=end;
	    end=boundary.next();
        
        if (firstType == SPACES) {
        	debug("leading spaces");
        	
    	    start=end;
    	    end=boundary.next();
        	
            if (end == BreakIterator.DONE) {
            	error("Only spaces");
            	return ERROR;
            }

	        first = source.substring(start,end);
	        firstType = getType(first);  	
        }
        
        if (firstType == NUMBER) {
        	error("Invalid command starting with a number");
        	return ERROR;
        }
	    
	    String second = "", third = "", fourth = "", fifth = "", sixth = "", seventh = "";
	    int secondType = -1, thirdType = -1, fourthType = -1, fifthType = -1, sixthType = -1, seventhType = -1;
	    
        if (end == BreakIterator.DONE) {
        	debug("Only one token");
        } else {
	    
		    second = source.substring(start,end);
		    secondType = getType(second);
		    
		    debug("Second token = '" + second + "', type = " + secondType);
		    
		    start=end;
		    end=boundary.next();
		    
	        if (end == BreakIterator.DONE) {
	        	debug("Only two tokens");
	        } else {
	    	    third = source.substring(start,end);
	    	    thirdType = getType(third);
	    	    
	    	    debug("Third token = '" + third + "', type = " + thirdType);
	    	    
	    	    if (thirdType == NUMBER) {
	    	    	debug("Third token is a number");
	    	    }
	    	    
	    	    start=end;
	    	    end=boundary.next();
	    	    
	            if (end == BreakIterator.DONE) {
	            	debug("End of command - 3 tokens");
	            } else {
	            	fourth = source.substring(start,end);
	            	fourthType =  getType(fourth);
	            	
	            	debug("Fourth token = '" + fourth + "', type = " + fourthType);
	            	
	        	    start=end;
	        	    end=boundary.next();
	        	    
	        	    if (end == BreakIterator.DONE) {
	        	    	debug("End of command: 3 tokens with trailing spaces");
	        	    } else {
	                	fifth = source.substring(start,end);
	                	fifthType =  getType(fifth);
	                	
	                	debug("Fifth token = '" + fifth + "', type = " + fifthType);
	                	
	            	    start=end;
	            	    end=boundary.next();
	            	    
	            	    if (end == BreakIterator.DONE) {
	            	    	debug("End of command - five tokens");
	            	    } else {
	            	    	debug("More than 5 tokens");
	            	    	
	    	            	sixth = source.substring(start,end);
	    	            	sixthType =  getType(sixth);
	    	            	
	    	            	debug("Sixth token = '" + sixth + "', type = " + sixthType);
	    	            	
	    	        	    start=end;
	    	        	    end=boundary.next();
	    	        	    
	    	        	    if (end == BreakIterator.DONE) {
	    	        	    	debug("End of command: 5 tokens with trailing spaces");
	    	        	    } else {
	    	                	seventh = source.substring(start,end);
	    	                	seventhType =  getType(seventh);
	    	                	
	    	                	debug("Seventh token = '" + seventh + "', type = " + seventhType);
	    	                	
	    	            	    start=end;
	    	            	    end=boundary.next();
	    	            	    
	    	            	    if (end == BreakIterator.DONE) {
	    	            	    	debug("End of command - seven tokens");
	    	            	    } else {
	    	            	    	debug("More than 7 tokens");
	    	            	    }
	    	        	    }
	            	    }    	    	
	        	    }
	            }

	        }
        }
        
        // Execute command
        switch(firstType) {
        default:
        	error("Command not recognised");
        	return ERROR;
        case QUANTITY:
        	switch(thirdType) {
        	case NUMBER:
        		debug(first + " " + third + " is ....");
        		break;
        	case INVALID:
        		// Single word comand
        		debug(first + " is ...");
        		break;
        	default:
        		error("Invalid quantity command");
        		return ERROR;
        	}
        	break;
        case DEVICE:
        	// Its a device
        	debug("Device command");
        	
        	switch(thirdType) {
        	case ACTION:
        		switch (find(third, actions)) {
        		case ON:
        		case OFF:
        			if (firstType == SWITCH) {
        				error("Cannot turn switches on or off");
        				return ERROR;
        			} else {
        				
        				debug("Turning " + first + " " +  third);
        			}
        			break;
        		case STATUS:
        			debug(first + " status is ...");
        			break;
        		}
        		break;
        	case NUMBER:
        		debug("Device command with number");
        		int n = Integer.parseInt(third);
        		if (fifthType == ACTION) {
	        		switch(find(fifth,actions)) {
	        		case ON:
	        		case OFF:
	        			switch (find(first,devices)) {
	        			case TV:
	        				sendIRCode(TV_ON);
	        				break;
	        			case VT:
	        				break;
	        			case AV:
	        				break;
	        			case LIGHT:
	        				switchLight(n,false);
	        				break;
	        			case SOCKET:
	        				switchSocket(n,false);
	        				break;
	        			case APPLIANCE:
	        				break;
	        			case CAMERA:
	        				break;
	        			case SWITCH:
	        				error("Cannot turn window switches on or off");
	        				return ERROR;
	        			case SENSOR:
	        				break;	        				
	        			case PHONE:
	        				break;
	        			case MUSIC_SERVER:
	        				break;
	        			}
	        			debug("Turning " + first + " " +  third + " " + fifth);
	        			break;
	        		case STATUS:
	        			debug(first + " " + third + " status is ...");
	        			break;
	        		case VALUE:
	        			debug(first + " " + third + " value is ...");
	        			break;
	    			case SET:
	    				debug("Setting " +  first + " " + third + " to " + seventh);
	    				break;
	    			case PLAY:
	    				break;
	    			case SAY:
	    				break;
	    			case MOOD:
	    				break;
	        		}
        		} else if (fifthType == PAN_ACTION || fifthType == TV_ACTION  || fifthType == AV_ACTION) {
        			debug(first + " " + third + " send " + fifth);
        		} else if (fifthType == QUANTITY) {
        			debug(first + " " + third + " " + fifth + " is ....");
        		}
        		break;
        	}
        	break;
        case ACTION:
        	// Its a command without a device
	    	switch (find(first,actions)) {
	    	case PLAY:
	    		debug("play " + third);
	    		break;
	    	case SAY:
	    		debug(source);
	    		break;
	    	case MOOD:
	    		debug("Setting mood to " + third);
	    		break;
	    	}
	    	break;
        case AREA:
        	// Its an area query
        	switch (thirdType) {
    		case NUMBER:
    			debug("Area command with number");
    			int n = Integer.parseInt(third);
    			
    			if (find(first,areas) == FLOOR) {
	    			if (n < 1 || n >= Config.MAX_FLOORS) {
	    				error("Floor number out of range");
	    				return ERROR;
	    			}   				
    			} else {
	    			if (n < 1 || n >= Config.MAX_ROOMS) {
	    				error("Room number out of range");
	    				return ERROR;
	    			}
    			}
    			
    			if (fifthType == ACTION) {
	    			switch(find(fifth,actions)) {
	    			case ON:
	    			case OFF:
	    				error("Cannot switch an area on or off");
	    				return ERROR;
	    			case STATUS:
	    			case VALUE:
	    				error("Areas do not have a status or value");
	    				return ERROR;
	    			case OCCUPIED:
	    				debug(first + " " + third + " is ...");
	    				break;
	    			}
    			} else if (fifthType == QUANTITY) {
    				debug(first + " " + third + " " + fifth + " is ...");
    			} else if (fifthType == DEVICE_SET) {
    				if (seventhType == ACTION) {
    					switch(find(seventh,actions)) {
    					case ON:
    						debug("Switching " + first + " " + third + " " + fifth + " on");
    						break;
    					case OFF:
    						debug("Switching " + first + " " + third + " " + fifth + " off");
    						break;
    					}
    				}
    			}
    			break;
    		default:
    			error(first + " must be followed by a number");
    			return ERROR;
        	}
        	break;
        }
        return GOOD;
	}
	
	private static final String CONFIG_FILE = "src/house.xml";
	private static Config config = new Config(CONFIG_FILE);
	
	public static void main(String[] args) {
		
		Parser parser = new Parser(config.floorNames, config.roomNames, config.sensorNames, config.lightNames, 
				config.socketNames, config.applianceNames, config.cameraNames, config.switchNames);
		
		//parser.parse("tv  on");
		//parser.parse("vt on");
		//parser.parse("tv status");
		//parser.parse("light on");
		//parser.parse("light 3 on");
		//parser.parse("room 3 on");
		//parser.parse("room 3 occupied");
		parser.parse("temperature 1");
		parser.parse("temperature");
		parser.parse("humidity");
		parser.parse("lightlevel");
		parser.parse("volume");
		parser.parse("battery");
		parser.parse("play hiatt");
		parser.parse("say hello Lawrie");
		parser.parse("floor 3 occupied");
		parser.parse("room 3 occupied");
		parser.parse("room 3 temperature");
		parser.parse("power");
		parser.parse("tv 1 on");
		parser.parse("tv 1 left");
		parser.parse("tv 1 guide");
		parser.parse("appliance 1 value");
		parser.parse("mood party");
		parser.parse("light 3 set 50");
		parser.parse("sensor 3 temperature");
		parser.parse("room 2 lights off");
	}
	
	int find(String s, String[] t) {
		if (t == null) return -1;
		for(int i=0;i<t.length;i++) {
			if (s.equalsIgnoreCase(t[i])) return i;
		}
		return -1;
	}
	
	void debug(String msg) {
		System.out.println(msg);
	}
	
	void error(String msg) {
		System.out.println("*** ERROR *** " +  msg);
	}
	
  // IR remote codes
  private static final byte TV_ON = 100;
  private static final byte VT_HOME = 101;
  private static final byte AV_MUTE = 102;
  private static final byte VT_MN_OK = 103;
  private static final byte VT_MN_UP = 104;
  private static final byte VT_MN_DN = 105;
  private static final byte VT_MN_LT = 106;
  private static final byte VT_MN_RT = 107;
  private static final byte AV_VOL_UP = 108;
  private static final byte AV_VOL_DN = 109;
  private static final byte AV_SRC_DVD = 110;
  private static final byte AV_SRC_BD = 111;
  private static final byte VT_GUIDE = 112;
  private static final byte AV_SRC_STB = 113; 
	  
	void sendIRCode(byte cmd) {
		
	}
	
	void switchLight(int light, boolean on) {
		
	}
	
	void switchSocket(int socket, boolean on) {
		
	}
}
