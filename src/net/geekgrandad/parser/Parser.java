package net.geekgrandad.parser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.Reporter;

public class Parser {
	private BreakIterator boundary = BreakIterator.getWordInstance(Locale.UK);	
	
	public static final int AREA = 1;
	public static final int SPACES = 2;
	public static final int DEVICE = 3;
	public static final int QUANTITY = 4;
	public static final int ACTION = 5;
	public static final int FLOOR_NAME = 6;
	public static final int ROOM_NAME = 7;
	public static final int SENSOR_NAME = 8;
	public static final int SOCKET_NAME = 9;
	public static final int LIGHT_NAME = 10;
	public static final int SWITCH_NAME = 11;
	public static final int APPLIANCE_NAME = 12;
	public static final int CAMERA_NAME = 13;
	public static final int NUMBER = 14;
	public static final int PAN_ACTION = 15;
	public static final int VT_ACTION = 16;
	public static final int AV_ACTION = 17;
	public static final int DEVICE_SET = 18;
	public static final int MUSIC_ACTION = 19;
	public static final int ROBOT_ACTION = 20;
	public static final int DIGIT = 21;
	public static final int SERVICE = 22;
	public static final int COLOR = 23;
	public static final int DEFAULT = 24;
	public static final int MEDIA_NAME = 25;
	
	public static String[] areas = { "floor", "room" };

	public static final int FLOOR = 0;
	public static final int ROOM = 1;
	
	public static String[] devices = { "light", "socket", "appliance", "switch", "camera",
		"sensor", "phone", "media", "radiator",
		"robot", "alarm", "blind", "heating" };

	public static final int LIGHT = 0;
	public static final int SOCKET = 1;
	public static final int APPLIANCE = 2;
	public static final int SWITCH = 3;
	public static final int CAMERA = 4;
	public static final int SENSOR = 5;
	public static final int PHONE = 6;
	public static final int MEDIA = 7;
	public static final int RADIATOR = 8;
	public static final int ROBOT = 9;
	public static final int ALARM = 10;
	public static final int BLIND = 11;
	public static final int HEATING = 12;
	
	public static String[] deviceSets = { "lights", "sockets", "appliances", "switches",
		"cameras", "sensors", "phones",
		"radiators", "robots", "alarms", "blinds" };

	public static final int LIGHTS = 0;
	public static final int SOCKETS = 1;
	public static final int APPLIANCES = 2;
	public static final int SWITCHES = 3;
	public static final int CAMERAS = 4;
	public static final int SENSORS = 5;
	public static final int PHONES = 6;
	public static final int RADIATORS = 7;
	public static final int ROBOTS = 8;
	public static final int ALARMS = 9;
	public static final int BLINDS = 10;
	
	public static String[] quantities = { 
		"temperature", "humidity", "lightlevel", "battery",
		"power", "occupied", "energy", "sound",
		"soil_moisture" };

	public static final int TEMPERATURE = 0;
	public static final int HUMIDITY = 1;
	public static final int LIGHT_LEVEL = 2;
	public static final int BATTERY = 3;
	public static final int POWER = 4;
	public static final int OCCUPIED = 5;
	public static final int ENERGY = 6;
	public static final int SOUND = 7;
	public static final int SOIL_MOISTURE = 8;
	
	public static final String[] services = {
		"spotify", "itunes", "guide", "channel",
		"shows", "broadcast", "home", "catchup",
		"ondemand", "volume", "xbmc", "source",
		"iplayer", "4oD", "youtube",
		"tvxl", "movies", "music",
		"games", "rentals", "subtitles", "info",
		"itvplayer", "demand5", "settings", "help",
		"system", "parental", "sports", "search",
		"thumbs", "series", "wishlist", "planned",
		"browse", "apps", "purchase", "messages",
		"pin","payperview","lifestyle", "news", 
		"featured", "navix", "weather", "ted",
		"context", "pictures", "filemanager",
		"video", "skyondemand", "playlists",
		"dilbert"
	};
	
	public static final int SPOTIFY = 0;
	public static final int ITUNES = 1;
	public static final int GUIDE = 2;
	public static final int CHANNEL = 3;
	public static final int SHOWS = 4;
	public static final int BROADCAST = 5;
	public static final int HOME = 6;
	public static final int CATCHUP = 7;
	public static final int ONDEMAND = 8;
	public static final int VOLUME = 9;
	public static final int XBMC = 10;
	public static final int SOURCE = 11;
	public static final int IPLAYER = 12;
	public static final int OD4 = 13;
	public static final int YOUTUBE = 14;
	public static final int TVXL = 15;
	public static final int MOVIES = 16;
	public static final int MUSIC = 17;
	public static final int GAMES = 18;
	public static final int RENTALS = 19;
	public static final int SUBTITLES = 20;
	public static final int INFO = 21;
	public static final int ITVPLAYER = 22;
	public static final int DEMAND5 = 23;
	public static final int SETTINGS = 24;
	public static final int HELP = 25;
	public static final int SYSTEM = 26;
	public static final int PARENTAL = 27;
	public static final int SPORTS = 28;
	public static final int SEARCH = 29;
	public static final int THUMBS = 30;
	public static final int SERIES = 31;
	public static final int WISHLIST = 32;
	public static final int PLANNED = 33;
	public static final int BROWSE = 34;
	public static final int APPS = 35;
	public static final int PURCHASE = 36;
	public static final int MESSAGES = 37;
	public static final int PIN = 38;
	public static final int PAY_PER_VIEW = 39;
	public static final int LIFESTYLE = 40;
	public static final int NEWS = 41;
	public static final int FEATURED = 42;
	public static final int NAVI_X = 43;
	public static final int WEATHER = 44;
	public static final int TED = 45;
	public static final int CONTEXT = 46;
	public static final int PICTURES = 47;
	public static final int FILE_MANAGER = 48;
	public static final int VIDEO = 49;
	public static final int SKY_ON_DEMAND = 50;
	public static final int PLAYLISTS = 51;
	public static final int DILBERT = 52;
	
	public static final String[] source = { "stb", "dvd", "blueray" };
	
	public static final int STB = 0;
	public static final int DVD = 1;
	public static final int BLUERAY = 2;
	
	public static final String[] actions = { 
		"on", "off", "status", "value", "set", 
		"mood", "email", "clear", "signal", 
		"back", "delete", "reboot", "shutdown" };

	public static final int ON = 0;
	public static final int OFF = 1;
	public static final int STATUS = 2;
	public static final int VALUE = 3;
	public static final int SET = 4;
	public static final int MOOD = 5;
	public static final int EMAIL = 6;
	public static final int CLEAR = 7;
	public static final int SIGNAL = 8;
	public static final int BACK = 9;
	public static final int DELETE = 10;
	public static final int REBOOT = 11;
	public static final int SHUT_DOWN = 12;

	public static String[] robotActions = {"fetch", "clean", "grab", "release"};
	
	public static final int FETCH = 0;
	public static final int CLEAN = 1;
	public static final int GRAB = 2;
	public static final int RELEASE = 3;

	public static String[] panActions = { "left", "right", "up", "down" };

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;

	public static String[] vtActions = { "ok", "send", "type", "lastch", "pageup", "pagedown" };

	public static final int OK = 0;
	public static final int SEND = 1;
	public static final int TYPE = 2;
	public static final int LAST_CH = 3;
	public static final int PAGE_UP = 4;
	public static final int PAGE_DOWN = 5;
	
	public static String[] avActions = { "volup", "voldown", "mute" };

	public static final int VOLUP = 0;
	public static final int VOLDOWN = 1;
	public static final int MUTE = 2;

	public static String[] musicActions = { "stop", "skip", "speak", "silent", 
		                                    "play", "say", "playlist", "pause", "ff",
		                                    "fb", "skipb", "slow", "record", "start",
		                                    "track", "artist", "album", "open", "player"};
 
	public static final int STOP = 0;
	public static final int SKIP = 1;
	public static final int SPEAK = 2;
	public static final int SILENT = 3;
	public static final int PLAY = 4;
	public static final int SAY = 5;
	public static final int PLAYLIST = 6;
	public static final int PAUSE = 7;
	public static final int FF = 8;
	public static final int FB = 9;
	public static final int SKIPB = 10;
	public static final int SLOW = 11;
	public static final int RECORD = 12;
	public static final int START = 13;
	public static final int TRACK = 14;
	public static final int ARTIST = 15;
	public static final int ALBUM = 16;
	public static final int OPEN = 17;
	public static final int PLAYER = 18;
	
	public static String[] digits = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
	
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int NINE = 9;
	
	public static String[] colors = {"red", "green", "yellow", "blue"};
	
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int YELLOW = 2;
	public static final int BLUE = 3;
	
	public static String[] defaults = {"location", "device", "service"};
	
	public static final int DEFAULT_LOCATION = 0;
	public static final int DEFAULT_DEVICE = 1;
	public static final int DEFAULT_SERVICE = 2;
	
	private Reporter reporter;
	private Config config;
	
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}
	
	public Token[] parse(String source) {
		int numTokens = 0;
		reporter.debug("Command is " + source);
		ArrayList<Token> tokens = new ArrayList<Token>();

		boundary.setText(source);
		int start = boundary.first();
		int end = boundary.next();

		if (end == BreakIterator.DONE) {
			reporter.error("Empty command");
			return null;
		}

		String first = source.substring(start, end);
		int firstType = getType(first);

		reporter.debug("First token = '" + first + "', type = " + firstType);

		start = end;
		end = boundary.next();
		numTokens = 1;

		if (firstType == SPACES) {
			reporter.debug("leading spaces");

			start = end;
			end = boundary.next();

			if (end == BreakIterator.DONE) {
				reporter.error("Only spaces");
				return null;
			}

			first = source.substring(start, end);
			firstType = getType(first);
		}
		
		tokens.add(new Token(first,firstType,-1));

		if (firstType == NUMBER) {
			reporter.error("Invalid command starting with a number");
			return null;
		}

		String second = "", third = "", fourth = "", fifth = "", sixth = "", seventh = "", eighth="", ninth = "";
		int secondType = -1, thirdType = -1, fourthType = -1, fifthType = -1, sixthType = -1, seventhType = -1;
		int eighthType, ninthType;

		if (end == BreakIterator.DONE) {
			reporter.debug("Only one token");
		} else {

			second = source.substring(start, end);
			secondType = getType(second);

			reporter.debug("Second token = '" + second + "', type = " + secondType);

			start = end;
			end = boundary.next();

			if (end == BreakIterator.DONE) {
				reporter.debug("Only two tokens");
			} else {
				third = source.substring(start, end);
				thirdType = getType(third);
				numTokens = 2;

				reporter.debug("Third token = '" + third + "', type = " + thirdType);
				
				tokens.add(new Token(third,thirdType,-1));

				if (thirdType == NUMBER) {
					reporter.debug("Third token is a number");
				}

				start = end;
				end = boundary.next();

				if (end == BreakIterator.DONE) {
					reporter.debug("End of command - 3 tokens");
				} else {
					fourth = source.substring(start, end);
					fourthType = getType(fourth);

					reporter.debug("Fourth token = '" + fourth + "', type = "
							+ fourthType);

					start = end;
					end = boundary.next();

					if (end == BreakIterator.DONE) {
						reporter.debug("End of command: 3 tokens with trailing spaces");
					} else {
						fifth = source.substring(start, end);
						fifthType = getType(fifth);
						numTokens = 3;

						reporter.debug("Fifth token = '" + fifth + "', type = "
								+ fifthType);
						
						tokens.add(new Token(fifth,fifthType,-1));

						start = end;
						end = boundary.next();

						if (end == BreakIterator.DONE) {
							reporter.debug("End of command - five tokens");
						} else {
							reporter.debug("More than 5 tokens");

							sixth = source.substring(start, end);
							sixthType = getType(sixth);

							reporter.debug("Sixth token = '" + sixth + "', type = "
									+ sixthType);

							start = end;
							end = boundary.next();

							if (end == BreakIterator.DONE) {
								reporter.debug("End of command: 5 tokens with trailing spaces");
							} else {
								seventh = source.substring(start, end);
								seventhType = getType(seventh);
								numTokens = 4;

								reporter.debug("Seventh token = '" + seventh
										+ "', type = " + seventhType);
								
								tokens.add(new Token(seventh, seventhType, -1));

								start = end;
								end = boundary.next();

								if (end == BreakIterator.DONE) {
									reporter.debug("End of command - seven tokens");
								} else {
									reporter.debug("More than 7 tokens");
									
									eighth = source.substring(start, end);
									eighthType = getType(eighth);

									start = end;
									end = boundary.next();

									if (end == BreakIterator.DONE) {
										reporter.debug("Seven tokens with trailing spaces");
									} else {
										ninth = source.substring(start, end);
										ninthType = getType(seventh);
										numTokens = 5;

										reporter.debug("Ninth token = '" + ninth
												+ "', type = " + ninthType);
										
										tokens.add(new Token(ninth, ninthType, -1));
									}
								}
							}
						}
					}
				}
			}
		}
		return tokens.toArray(new Token[0]);
	}
	
	int getType(String token) {
		int type = -1;

		if (token.trim().isEmpty()) {
			return SPACES;
		}

		try {
			Integer.parseInt(token);
			return NUMBER;
		} catch (Exception e) {
		}

		if (find(token, config.floorNames) >= 0)
			type = FLOOR;
		else if (find(token, config.roomNames) >= 0)
			type = ROOM;
		else if (find(token, devices) >= 0)
			type = DEVICE;
		else if (find(token, deviceSets) >= 0)
			type = DEVICE_SET;
		else if (find(token, quantities) >= 0)
			type = QUANTITY;
		else if (find(token, actions) >= 0)
			type = ACTION;
		else if (find(token, areas) >= 0)
			type = AREA;
		else if (find(token, services) >= 0)
			type = SERVICE;
		else if (find(token, digits) >= 0)
			type = DIGIT;
		else if (find(token, colors) >= 0)
			type = COLOR;
		else if (find(token, defaults) >= 0)
			type = DEFAULT;
		else if (find(token, vtActions) >= 0)
			type = VT_ACTION;
		else if (find(token, panActions) >= 0)
			type = PAN_ACTION;
		else if (find(token, avActions) >= 0)
			type = AV_ACTION;
		else if (find(token, musicActions) >= 0)
			type = MUSIC_ACTION;
		else if (find(token, robotActions) >= 0)
			type = ROBOT_ACTION;
		else if (find(token, config.sensorNames) >= 0)
			type = SENSOR_NAME;
		else if (find(token, config.socketNames) >= 0)
			type = SOCKET_NAME;
		else if (find(token, config.lightNames) >= 0)
			type = LIGHT_NAME;
		else if (find(token, config.applianceNames) >= 0)
			type = APPLIANCE_NAME;
		else if (find(token, config.cameraNames) >= 0)
			type = CAMERA_NAME;
		else if (find(token, config.switchNames) >= 0)
			type = SWITCH_NAME;
		else if (find(token, config.mediaNames) >= 0)
			type = MEDIA_NAME;

		return type;
	}
	
	public int find(String s, String[] t) {
		if (t == null)
			return -1;
		for (int i = 0; i < t.length; i++) {
			if (s.equalsIgnoreCase(t[i]))
				return i;
		}
		return -1;
	}
	
	static class DummyReporter implements Reporter {

		@Override
		public void debug(String msg) {
			System.out.println(msg);		
		}

		@Override
		public void print(String msg) {
			System.out.println(msg);
		}

		@Override
		public void error(String msg) {
			System.err.println(msg);		
		}
		
	}
	
	public static void main(String[] args) {
		Reporter r = new DummyReporter();
		Config c = new Config("src/house.xml");
		Parser parser = new Parser();
		parser.setReporter(r);
		parser.setConfig(c);
		
		Token[] tokens = parser.parse("lights on");
		
		r.print("Number of tokens:" + tokens.length);
		
		for(Token t: tokens) {
			r.print("Token: " + t.getValue());
			r.print("Type: " + t.getType());
			r.print("");
		}
	}
}
