package net.geekgrandad.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import net.geekgrandad.config.Config;
import net.geekgrandad.plugin.GoogleMail;
import net.geekgrandad.plugin.RFControl;
import net.geekgrandad.plugin.RFIAMControl;
import net.geekgrandad.plugin.RFM12Control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import Cosm.Cosm;
import Cosm.CosmException;
import Cosm.Datastream;

public class OldORControl {
	
  private static final String CONFIG_FILE = "src/house.xml";
	
  private static Config config = new Config(CONFIG_FILE);
	
  // Maximum number of devices
  private static final int MAX_SENSORS = 11;
  private static final int MAX_IAMS = 3;
  private static final int MAX_SOCKETS = 3;
  private static final int MAX_LIGHTS = 4;
  private static final int MAX_WINDOWS = 2;
  
  // Room nodes for house temperature and humidity
  private static final int LIVING_ROOM_TEMPERATURE_SENSOR = 2; 
  private static final int LIVING_ROOM_HUMIDITY_SENSOR = 2;
	
  // Ports for transceivers
  private static final String RFM12_PORT = "COM15";
  private static final String LWRF_PORT = "COM8";
  private static final String IAM_PORT = "COM20";
  
  // Set the voice to Kevin
  private static boolean localSpeech = true;
  private static String VOICE = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";
	
  /// Email data
  private static boolean email = true;
  private static final String EMAIL_USER = "lawrie.griffiths";
  private static final String EMAIL_PASSWORD = "basic1y1y";
  private static final String EMAIL_TARGET = "lawrie.griffiths@ntlworld.com";
  
  // OpenEnergyMonitor data
  private static final int EMONTX_NODE = 10;
  // Minimum emontx battery level in millivolts
  private static int BATTERY_LOW_LEVEL = 3000;
  
  // EDF IAM data
  static final int IAM_MIN_VALUE = 3;
  static final int IAM_MIN_OFF_PERIOD = 120000;
  // EDF IAM IDs
  private static long[] iamIds = {1884046140,1567559737,1077486631};
  
  // COSM data
  private boolean cosm = true; // set true to post energy data to COSM
  private static final String COSM_API_KEY = "5e8sM5Ls6RJhqilxUPtdq3LEF8ySAKxIbkVMS2RudjZiTT0g";
  private static final int COSM_FEED = 86697;
  private static String COSM_POWER_STREAM = "0";
  private static String COSM_ENERGY_STREAM = "1";
  
  // The name of the log4j2 logger used by this class
  private static final String LOGGER_NAME = "ORControl";
  
  // Mobile phone data
  private static final String MOBILE_PHONE_HOST_NAME = "phone";
  
  // The TCP/IP port to listen on
  private static final int PORT = 50000;
	
  // OpenRemote TCP/IP commands
  private static String CMD_TV_ON = "tvon";
  private static String CMD_AV_MUTE = "avmute";
  private static String CMD_VT_HOME = "vthome";
  private static String CMD_AV_VOL_UP = "avvolup";
  private static String CMD_AV_VOL_DN = "avvoldn";
  private static String CMD_VT_MN_OK = "vtmnok";
  private static String CMD_VT_MN_UP = "vtmnup";
  private static String CMD_VT_MN_DN = "vtmndn"; 
  private static String CMD_VT_MN_LT = "vtmnlt";
  private static String CMD_VT_MN_RT = "vtmnrt";
  private static String CMD_VT_GUIDE = "vtguide";
  private static String CMD_AV_SRC_STB = "avsrcstb";
  private static String CMD_AV_SRC_DVD = "avsrcdvd";
  private static String CMD_AV_SRC_BD = "avsrcbd";
  private static String CMD_PHONE_STATUS = "phonestat";
  private static String CMD_MUSIC_STATUS = "musicstat";
  private static String CMD_WINDOW_STATUS = "windowstat";
  private static String CMD_LIGHT_STATUS = "lightstat";
  private static String CMD_SOCKET_STATUS = "sockstat";
  private static String CMD_IAM_STATUS = "iamstat";
  private static String CMD_HEATING_STATUS = "heating";
  private static String CMD_ALARM_STATUS = "alarm";
  private static String CMD_BATTERY = "battery";
  private static String CMD_SOCKET_ON = "socketon";
  private static String CMD_SOCKET_OFF = "socketoff";
  private static String CMD_IAM_ON = "iamon";
  private static String CMD_IAM_OFF = "iamoff";
  private static String CMD_IAM_VALUE = "iamval";
  private static String CMD_LIGHT_ON = "lighton";
  private static String CMD_LIGHT_OFF = "lightoff";
  private static String CMD_LIGHT_VALUE = "lightval";
  private static String CMD_LIGHT_LEVEL = "light";
  private static String CMD_SET_LIGHT = "setlight";
  private static String CMD_HEATING_ON = "heaton";
  private static String CMD_HEATING_OFF = "heatoff";
  private static String CMD_VT = "vt";
  private static String CMD_ENERGY = "energy";
  private static String CMD_POWER = "power";
  private static String CMD_PLAY = "play";
  private static String CMD_PLAYLIST = "playlist";
  private static String CMD_TEMPERATURE = "temp";
  private static String CMD_HUMIDITY = "humid";
  private static String CMD_BEER = "beer";
  private static String CMD_CLEAN = "clean";
  private static String CMD_REQUIRED_TEMPERATURE = "reqtemp";
  private static String CMD_SET_TEMPERATURE = "settemp";
  private static String CMD_OCCUPIED = "occupied";
  private static String CMD_GET_VOLUME = "getvol";
  private static String CMD_SET_VOLUME = "setvol";
  private static String CMD_SAY = "say";
  private static String CMD_EMAIL = "email";
  private static String CMD_STOP = "stop";
  private static String CMD_SKIP = "skip";
  private static String CMD_PLANT = "plant";
  private static String CMD_SHUT_DOWN = "shutdown";
  private static String CMD_HIGH_POWER = "highpower";
  private static String CMD_SET_ALARM = "setalarm";
  private static String CMD_CLEAR_ALARM = "clearalarm";
  private static String CMD_SILENT = "silent";
  private static String CMD_NOISY = "noisy";
  private static String CMD_CHANNEL = "ch";
  private static String CMD_ROOM = "room";
  
  // HTTP commands
  static final String HTTP_PHONE_WAKE = "phonewake";
  static final String HTTP_PHONE_SLEEP = "phonesleep";
  static final String HTTP_TEXT_RECEIVED = "text";
  static final String HTTP_CALL_RECEIVED = "call";
  static final String HTTP_WIFI_CONNECTED = "wifion";
  static final String HTTP_WIFI_DISCONNECTED = "wifioff";
  static final String HTTP_LIGHT = "light";
  static final String HTTP_DARK = "dark";
  
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
  
  // Start of byte codes for switching LightwaveRF sockets on or off
  private static final int SOCKET_BASE = 240;
  
  // Start of byte codes for switching lights on or off
  private static final int LIGHT_BASE = 246;
  
  // Default temperature in degrees C for heating
  private static final int DEFAULT_TEMP = 16;
  
  // Lighting numbers
  private static final int LIVING_ROOM_LIGHT_1 = 2;
  private static final int LIVING_ROOM_LIGHT_2 = 3;
  
  // Music server data
  private static final int MUSIC_SERVER_SOCKET_TIMEOUT = 5000;
  private static final String MUSIC_SERVER_HOST_NAME = "musicserver";
  
  // Spotify playlists
  private static final Map<String, String> playlists = config.getPlaylists();
  
  /*
  static {
      playlists.put("hiatt", "http://open.spotify.com/user/grabbas/playlist/7kqeUug8LFcc1G5x85Vs3u");
      playlists.put("bluegrass", "http://open.spotify.com/user/grabbas/playlist/1rNcNwvAEhkr8SpMMr544H");
      playlists.put("dylan", "http://open.spotify.com/user/grabbas/playlist/5bB3gAtMpGZf3ZchfLrA3C");
      playlists.put("marley", "http://open.spotify.com/user/grabbas/playlist/7yQ6qTXmUvAU2QWWIOZoSL");
      playlists.put("springsteen", "http://open.spotify.com/user/grabbas/playlist/280kYcQxqNwn8bR2bcen4M");
      playlists.put("country", "http://open.spotify.com/user/grabbas/playlist/22xSqobsRdCYndbOCSpQhm");
      playlists.put("cult", "http://open.spotify.com/user/grabbas/playlist/7KORIvehmmAaMelCEcfXeW");
      playlists.put("dire", "http://open.spotify.com/user/grabbas/playlist/4ux4XpOau6yDj8tMjyfAfP");
      playlists.put("dido", "http://open.spotify.com/user/grabbas/playlist/4PatpiTDxd2TiILksMKFXC");
      playlists.put("early", "http://open.spotify.com/user/grabbas/playlist/7CFyd9y1mc3BSciNUooydk");
      playlists.put("elvis", "http://open.spotify.com/user/grabbas/playlist/5hKu9DamfyD1okIOtqi6UU");
      playlists.put("folk", "http://open.spotify.com/user/grabbas/playlist/2iWMpiGgRMVhLtWkeo1Aql");
      playlists.put("glam", "http://open.spotify.com/user/grabbas/playlist/71qODECLpEXYqhd9sVet7D");
      playlists.put("jimi", "http://open.spotify.com/user/grabbas/playlist/54WN7FjYmGJDbfaLGTd4PF");
      playlists.put("norah", "http://open.spotify.com/user/grabbas/playlist/0aRVRQF9kSTDsHxpGrTZa6");
      playlists.put("brady", "http://open.spotify.com/user/grabbas/playlist/1WD5Vicc9xkGWNIJxJbKUU");
      playlists.put("simon", "http://open.spotify.com/user/grabbas/playlist/0Q837X7Okqk6Ybbj1Nered");
      playlists.put("protest", "http://open.spotify.com/user/grabbas/playlist/6gG1acgi7ANQB6qAsP6JLy");
      playlists.put("randy", "http://open.spotify.com/user/grabbas/playlist/72JlGtiC8x9abCYTDIZW8H");
      playlists.put("recent", "http://open.spotify.com/user/grabbas/playlist/5kBmntSOI1xgzRC2dXTMAv");
      playlists.put("thompson", "http://open.spotify.com/user/grabbas/playlist/2srsx92tpdgX6VZJ96q83P");
      playlists.put("stones", "http://open.spotify.com/user/grabbas/playlist/2i1Ke5fneT98RcGpq9vgax");
      playlists.put("ry", "http://open.spotify.com/user/grabbas/playlist/6kaiCnFvz6Um7nQGNKH1oJ");
      playlists.put("soul", "http://open.spotify.com/user/grabbas/playlist/69HA7z2wSZEJ1qgQOtAy5S");
      playlists.put("van", "http://open.spotify.com/user/grabbas/playlist/6pG6Q4ibxTzW1LaBb26aYB");
      playlists.put("willie", "http://open.spotify.com/user/grabbas/playlist/01BBRKHQ66JkkuQwcSrJjF");
      playlists.put("joni", "http://open.spotify.com/user/grabbas/playlist/2OqfjYeiZ1rtQH26o68gC3");
      playlists.put("young", "http://open.spotify.com/user/grabbas/playlist/2FOuoECPwBjmy8b9M7fQ7o");
      playlists.put("london", "http://open.spotify.com/user/grabbas/playlist/0MmGNWj60XzcTtk9uO4Ggc");
      playlists.put("waters", "http://open.spotify.com/user/grabbas/playlist/6Gmec5UnMTSAgNyppimCrP");
      playlists.put("van", "http://open.spotify.com/user/grabbas/playlist/6pG6Q4ibxTzW1LaBb26aYB");
      playlists.put("empty", "http://open.spotify.com/user/grabbas/playlist/4TdBstcStLezIg2v1GQFcb");
      playlists.put("alabama", "http://open.spotify.com/user/grabbas/playlist/4eihS66MISpy576BeulT0o");
      playlists.put("adele", "http://open.spotify.com/user/grabbas/playlist/3rXRJNLoXiprei7McncQ3u");
      playlists.put("rock", "http://open.spotify.com/user/grabbas/playlist/6KHoOpxsJcr7qcFkNkAfD4");
      playlists.put("sixties", "http://open.spotify.com/user/grabbas/playlist/3PukGEHOiiEzg5gY6Ypete");
      playlists.put("ska", "http://open.spotify.com/user/grabbas/playlist/6cT06jr6cx44wk2kLYczQs");
      playlists.put("lehrer", "http://open.spotify.com/user/grabbas/playlist/2xJEu8XJlMH8UvSQf69sBg");
      playlists.put("ballads", "http://open.spotify.com/user/grabbas/playlist/1NzqxaHqY4LzUYkNflTq7q");
      playlists.put("classical", "http://open.spotify.com/user/grabbas/playlist/3L2jwLXkWy8AT6RGuQTBkv");
      playlists.put("iver", "http://open.spotify.com/user/grabbas/playlist/44vtR9Np9QAZQJQhJYTk3r");
      playlists.put("women", "http://open.spotify.com/user/grabbas/playlist/0y1xQvg7wYi6z3aXOxjZ4V");
  }
  */
  
  // The time since last motion for room to be considered unoccupied
  private static final int OCCUPIED_INTERVAL = 300000; // 5 minutes
  
  // The time in milliseconds to wait for a ping to check for phone or computer
  private static final int PING_TIMEOUT = 5000;
  
  // The time in microseconds to wait between executing background actions
  // such as checking for the phone and getting the music volume
  private static final int BACKGROUND_DELAY = 10000;

  // Variable data
  private ServerSocket ss;
  private Socket s;
  private InputStream is;
  private OutputStream os;
  private RFM12Control rfc;
  private RFControl rfc434;
  private RFIAMControl rfcIAM;
  private Thread inThread = new Thread(new ReadInput());
  private Thread inThread434 = new Thread(new ReadLWRFInput());
  private Thread inThreadIAM= new Thread(new ReadIAMInput());
  private Thread backGround = new Thread(new Background());
  private int[] temp = new int[MAX_SENSORS];
  private int[] light = new int[MAX_SENSORS];
  private boolean[] batteryLow = new boolean[MAX_SENSORS];
  private float power = 0;
  private int sensor;
  private int[] humidity = new int[MAX_SENSORS];
  private boolean[] socketOn = new boolean[MAX_SOCKETS]; 
  private boolean[] iamOn = new boolean[MAX_IAMS];
  private int[] iamValue = new int[MAX_IAMS];
  private double[] iamEnergy = new double[MAX_IAMS];
  private boolean[] lightOn = new boolean[MAX_LIGHTS];
  private int[] lightValue = new int[MAX_LIGHTS];
  private boolean[] windowOpen = new boolean[MAX_WINDOWS];
  private long[] occupied = new long[MAX_SENSORS];
  private boolean[] iamStarted = new boolean[MAX_IAMS];
  private boolean[] iamFinished = new boolean[MAX_IAMS];
  private long[] iamLast = new long[MAX_IAMS];
  private long[] iamLastPos = new long[MAX_IAMS];
  private int reqtemp = DEFAULT_TEMP;
  private boolean heating = false;
  private long lastPower = 0;
  private double energy = 0;
  private double roundedEnergy;
  private boolean phoneConnected = false;
  private boolean phoneOn = false;
  private boolean musicOn = true;
  private int volume;
  private Synthesizer synth;
  private static int numCmds = 0;
  private static int state = 0;
  private String currentPlaylist = " ";
  private String cmd;
  private long alarmTime = 0;
  private boolean noisy = false;
  private Logger logger = LogManager.getLogger(LOGGER_NAME);
 

  // Run the main thread
  public void run() {
	  
    // Connect to the Jeenode network, if transceiver port defined
	if (RFM12_PORT != null && RFM12_PORT.length() > 0) {
	    try {
	      rfc = new RFM12Control(RFM12_PORT);
	    } catch (IOException e1) {
	    	error(e1.getMessage());
	    }
	}
	
    // Connect to the IAM network, if transceiver port defined
	if (IAM_PORT != null && IAM_PORT.length() > 0) {
	    try {
	      rfcIAM = new RFIAMControl(IAM_PORT);
	    } catch (IOException e1) {
	    	error(e1.getMessage());
	    }
	}
	    
    // Connect to the LightwaveRF network, if transceiver port defined
	if (LWRF_PORT != null && LWRF_PORT.length() > 0) {
	    try {
	      rfc434= new RFControl(LWRF_PORT);
	    } catch (IOException e1) {
	    	error(e1.getMessage());
	    }
	}
    
    // Start background thread
    backGround.setDaemon(true);
    backGround.start();
    
    // Start the Jeenode read thread, if required
    if (rfc != null) {
      inThread.setDaemon(true);
      inThread.start();
    }
    
    // Start the LightwaveRF thread, if required
    if (rfc434 != null) {
      inThread434.setDaemon(true);
      inThread434.start();
    }
    
    // Start the IAM read thread, if required
    if (rfcIAM != null) {
      inThreadIAM.setDaemon(true);
      inThreadIAM.start();
      
      // Tell tranceiver the devices to use
      try {
      // Only receive data from known devices
	      sendRFCIAM('k');
	      sendRFCIAM('\r');
	      
	      // add known devices
	      for(int i=0;i<iamIds.length;i++) {
	        sendRFCIAM('N');
	        sendIAMString("" + iamIds[i]);
	        sendRFCIAM('\r');
	      }
      } catch (IOException e) {
          error("Failed to talk to IAM transceiver:" + e.getMessage());
          System.exit(1);
      }
    }
    
    // Start the server socket
    try {
      ss = new ServerSocket(PORT);
    } catch (IOException e) {
      error("Failed to create server socket:" + e.getMessage());
      System.exit(1);
    }

    // Create a speech synthesizer and start it
    if (localSpeech) {
	    try {  
	      // Create a synthesizer for English
	      System.setProperty("freetts.voices", VOICE);
	      synth = Central.createSynthesizer(new SynthesizerModeDesc(Locale.ENGLISH));
	      synth.allocate();
	      synth.resume();
	    } catch (Exception e) {
	      error("Failed to create speak synthesizer");
	    }
    }
    
    // Main loop
    while(true) {     
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
          } catch (IOException e) {}
        if (os != null)
          try {
            os.close();
          } catch (IOException e) {}
        if (s != null)
          try {
            s.close();
          } catch (IOException e) {}
      }
    }
  }
  
  // Execute an OpenRemote TCP/IP command
  private void execute(String cmd) throws IOException {
    // Check for each possible command
    if (cmd.equals(CMD_TV_ON)) {
      debug("Turning TV on");
      sendIRCode(TV_ON);
      success();
    } else if (cmd.equals(CMD_VT_HOME)) {
      // TiVo home
      sendIRCode(VT_HOME);
      success();
    } else if (cmd.equals(CMD_AV_MUTE)) {
      // AV Receiver mute
      sendIRCode(AV_MUTE);
      success();
    } else if (cmd.equals(CMD_AV_VOL_UP)) {
      /// AV Receiver volume up
      sendIRCode(AV_VOL_UP);
      success();
    } else if (cmd.equals(CMD_AV_VOL_DN)) {
      // Volume down
      sendIRCode(AV_VOL_DN);
      success();
    } else if (cmd.equals(CMD_VT_MN_OK)) {
      // TiVo OK button
      sendIRCode(VT_MN_OK);
      success();
    } else if (cmd.equals(CMD_VT_MN_UP)) {
      // TiVO menu up
      sendIRCode(VT_MN_UP);
      success();
    } else if (cmd.equals(CMD_VT_MN_DN)) {
      // TiVo menu down
      sendIRCode(VT_MN_DN);
      success();
    } else if (cmd.equals(CMD_VT_MN_LT)) {
      // TiVo menu left
      sendIRCode(VT_MN_LT);
      success();
    } else if (cmd.equals(CMD_VT_MN_RT)) {
      // TiVo menu right
      sendIRCode(VT_MN_RT);
      success();
    } else if (cmd.equals(CMD_AV_SRC_STB)) {
      // AV Receiver source STB
      sendIRCode(AV_SRC_STB);
      success();
    } else if (cmd.equals(CMD_AV_SRC_BD)) {
      // AV Receiver source Blueray Disk
      sendIRCode(AV_SRC_BD);
      success();
    } else if (cmd.equals(CMD_AV_SRC_DVD)) {
      // AV Receiver source DVD
      sendIRCode(AV_SRC_DVD);
      success();
    } else if (cmd.equals(CMD_VT_GUIDE)) {
      // TiVo Guide
      sendIRCode(VT_GUIDE);
      success();
    } else if (cmd.startsWith(CMD_CHANNEL)) {
        // TiVo channel
    	print("Channel command: " + cmd);
        for(int i=0;i<cmd.length()-CMD_CHANNEL.length();i++) {
        	byte code = (byte) (128 +  cmd.charAt(CMD_CHANNEL.length()+i) - '0');
        	print("Sending IR code: " + (code & 0xFF));
        	sendIRCode(code);
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
        }
        success();
      } else if (cmd.equals(CMD_PHONE_STATUS)) {
      // Check if mobile phone is reachable over Wifi
      debug("Phone reachable: " + phoneConnected);
      writeSwitch(phoneConnected);
    } else if (cmd.equals(CMD_MUSIC_STATUS)) {
      // Check if music server is reachable over Wifi
      debug("Music server reachable: " + musicOn);
      writeSwitch(musicOn);
    } else if (cmd.startsWith(CMD_BATTERY)) {
      // Get battery level
      int n = getInt(cmd,CMD_BATTERY.length());
      writeSwitch(!batteryLow[n]);
    } else if (cmd.startsWith(CMD_WINDOW_STATUS)) {
      // Get status of window switch
      int n = getInt(cmd,CMD_WINDOW_STATUS.length()) - 1;
      writeSwitch(windowOpen[n]);
    } else if (cmd.startsWith(CMD_SOCKET_STATUS)) {
      // Get status of LightwaveRF socket
      int n = getInt(cmd,CMD_SOCKET_STATUS.length()) - 1;
      writeSwitch(socketOn[n]);
    } else if (cmd.startsWith(CMD_SOCKET_ON)) {
      // Switch LightwaveRF socket on
      int n = getInt(cmd,CMD_SOCKET_ON.length()) - 1;
      switchSocket(n, true);
      success();;
    } else if (cmd.startsWith(CMD_SOCKET_OFF)) {
      // Switch LightwaveRF socket off
      int n = getInt(cmd,CMD_SOCKET_OFF.length()) -1;
      switchSocket(n, false);
      success();
    } else if (cmd.startsWith(CMD_IAM_ON)) {
      // Switch IAM socket on
      int n = getInt(cmd,CMD_IAM_ON.length()) -1;
      switchIAMSocket(n, true);
      success();
    } else if (cmd.startsWith(CMD_IAM_OFF)) {
      // Switch IAM socket off
      int n = getInt(cmd,CMD_IAM_OFF.length()) -1;
      switchIAMSocket(n, false);
      success();
    } else if (cmd.startsWith(CMD_IAM_STATUS)) {
      // Get status of IAM socket
      int n = getInt(cmd,CMD_IAM_STATUS.length()) -1;
      writeSwitch(iamOn[n]);
    } else if (cmd.startsWith(CMD_IAM_VALUE)) {
      // Get power usage from IAM socket
      int n = getInt(cmd,CMD_IAM_VALUE.length()) -1;
      writeString("" + iamValue[n]);
    } else if (cmd.startsWith(CMD_LIGHT_ON)) {
      // Switch LightwaveRF light switch on
      int n = getInt(cmd,CMD_LIGHT_ON.length()) -1;
      switchLight(n,true);
      success();
    } else if (cmd.startsWith(CMD_LIGHT_OFF)) {
      // Switch LightwaveRF light switch off
      int n = getInt(cmd,CMD_LIGHT_OFF.length()) -1;
      switchLight(n,false);
      success();
    } else if (cmd.startsWith(CMD_LIGHT_STATUS)) {
      // Get status of LightwaveRF light switch
      int n = getInt(cmd,CMD_LIGHT_STATUS.length()) -1;
      writeSwitch(lightOn[n]);
    } else if (cmd.startsWith(CMD_LIGHT_VALUE)) {
      // Get dimmer level from LightwaveRF light switch
      int n = Integer.parseInt(cmd.substring(CMD_LIGHT_VALUE.length())) -1;
      writeString("" + lightValue[n]);
    } else if (cmd.startsWith(CMD_SET_LIGHT)) {
      // Set dimmer level of LightwaveRF light switch
      int n = getInt(cmd.substring(0,CMD_SET_LIGHT.length() + 1),CMD_SET_LIGHT.length()) -1;
      int v = getInt(cmd,CMD_SET_LIGHT.length() + 2) -1;
      dimLight(n,v);
      success();
    } else if (cmd.equals(CMD_HEATING_STATUS)) {
      // Get heating on/off status
      writeSwitch(temp[LIVING_ROOM_TEMPERATURE_SENSOR] < reqtemp);
    } else if (cmd.equals(CMD_HEATING_ON)) {
      // Turn the heating on
      heating = true;
      success();
    } else if (cmd.equals(CMD_HEATING_OFF)) {
      // Turn the heating off
      heating = false;
      success();
    }  else if (cmd.equals(CMD_BEER)) {
      // Tell robot to fetch a beer
      say("I'm sorry, Lawrie, I'm afraid I can't do that");
      success();
    } else if (cmd.equals(CMD_CLEAN)) {
      // Tell the robot to clean the carpet
    	say("I'm sorry, Elliot, I'm afraid I can't do that");
      success();
    } else if (cmd.startsWith(CMD_TEMPERATURE)) {
      // Get the temperature from a room node
      int n = getInt(cmd,CMD_TEMPERATURE.length());
      writeInt(temp[n]);
    } else if (cmd.equals(CMD_REQUIRED_TEMPERATURE)) {
      // Get the required heating temperature
      writeInt(reqtemp);
    } else if (cmd.startsWith(CMD_HUMIDITY)) {
      // Get the humidity of soil moisture level from a room node
      int n = getInt(cmd,CMD_HUMIDITY.length());
      writeInt(humidity[n]);
    } else if (cmd.startsWith(CMD_LIGHT_LEVEL)) {
      // Get the light level from a room node
      int n = getInt(cmd,CMD_LIGHT_LEVEL.length());
      writeInt(light[n]);
    } else if (cmd.startsWith(CMD_SET_TEMPERATURE)) {
      // Set the required temperature for the heating
      reqtemp = getInt(cmd,CMD_REQUIRED_TEMPERATURE.length());
      success();
    } else if (cmd.equals(CMD_POWER)) {
      // Get the power from emonTx
      writeInt((int) power);              
    } else if (cmd.equals(CMD_ENERGY)) {
      // Get the cumulative energy from emonTx readings
      writeInt((int) Math.round(energy));              
    } else if (cmd.startsWith(CMD_OCCUPIED)) {
      // Calculate occupied status using room node motion sensor
      int n = getInt(cmd,CMD_OCCUPIED.length());
      writeSwitch(System.currentTimeMillis() - occupied[n] < OCCUPIED_INTERVAL);
    } else if (cmd.startsWith(CMD_VT)) {
      // Send a code to the TiVo
      int code = Integer.parseInt(cmd.substring(CMD_VT.length()),16);
      print("Sending TiVo code " + code);
      sendIRCode((byte) code);
      success();
    } else if (cmd.equals(CMD_PLAYLIST)) {
      // Get the current playlist
      writeString(currentPlaylist);
    } else if (cmd.startsWith(CMD_PLAY)) {
      // Start a music playlist
      currentPlaylist = cmd.substring(CMD_PLAY.length() + 1);
      writeString(play(currentPlaylist));
    } else if (cmd.equals(CMD_GET_VOLUME)) {
      // Get the music volume
      writeInt(volume);
    } else if ((cmd.startsWith(CMD_SET_VOLUME))) {
      // Set the music volume
      int vol = getInt(cmd,CMD_SET_VOLUME.length() + 1);
      setVolume(vol);
      success();            
    } else if (cmd.startsWith(CMD_SAY)) {
      print("*** " + cmd + " *** ");
      if (cmd.length() > CMD_SAY.length() + 1) say(cmd.substring(CMD_SAY.length() + 1));
      success();
    } else if (cmd.startsWith(CMD_EMAIL)) {
        print("*** " + cmd + " *** ");
        if (cmd.length() > CMD_EMAIL.length() + 1) sendEmail("Home automation email", cmd.substring(CMD_EMAIL.length() + 1));
        success();
    } else if (cmd.startsWith(CMD_PLANT)) {
        print("*** " + cmd + " *** ");
        say("Your plant needs watering");
        sendEmail("Your plant needs watering", "Its soil moisture is " + (100 - humidity[LIVING_ROOM_HUMIDITY_SENSOR]) + "%");
        success();
      } else if (cmd.equals(CMD_STOP)) {
      print("Stopping music");
      sendMusicCmd("stop",false);
      success();
    } else if (cmd.equals(CMD_SKIP)) {
      print("Skipping music");
      sendMusicCmd("skip",false);
      success();
    } else if (cmd.equals(CMD_SHUT_DOWN)) {
      print("Shutting down music computer");
      sendMusicCmd("shutdown",false);
      success();
    } else if (cmd.equals(CMD_HIGH_POWER)) {
      say("You are using " + ((int) power) + " watts of electricity");
      success();
    } else if (cmd.startsWith(CMD_SET_ALARM)) {
    	int n = Integer.parseInt(cmd.substring(CMD_SET_ALARM.length() + 1));
    	print("Set alarm to " + n + " seconds");
        alarmTime = System.currentTimeMillis() + n * 1000;
        success();
    } else if (cmd.equals(CMD_ALARM_STATUS)) {
    	int togo = (int) ((alarmTime - System.currentTimeMillis())/1000);
        writeInt(togo < 0 ? 0 : togo);
    } else if (cmd.equals(CMD_CLEAR_ALARM)) {
    	alarmTime = 0;
    	success();
    } else if (cmd.equals(CMD_SILENT)) {
    	noisy = false;
    	success();
    } else if (cmd.equals(CMD_NOISY)) {
    	noisy = true;
    	success();
    } else if (cmd.startsWith(CMD_ROOM)) {
    	int n = getInt(cmd,CMD_ROOM.length()) - 1;
    	writeString(config.roomNames[n]);
    } else if (cmd.startsWith("GET")) {
    	httpCommand(cmd);
    } else {
      print("Unknown command: " + cmd);
      success();
    }
  }
  
  // Check if host is reachable
  public boolean isReachableByPing(String host) {
    try {
      return InetAddress.getByName(host).isReachable(PING_TIMEOUT);
    } catch (Exception e) {
      return false;
    }
  }

  // main method
  public static void main(String[] args) {
    (new OldORControl()).run();
  }

  // Thread to process RFM12 input
  class ReadInput implements Runnable {
    public void run() {
      Cosm c = new Cosm(COSM_API_KEY);
      Datastream d = new Datastream();
      DecimalFormat df = new DecimalFormat("#####.##");
      for(;;) {
        int b = -1;
        
        try {
	        b = rfc.readByte();
	        sensor = (b & 0x1f);
	        print("Room: " + sensor);
	        if (b == EMONTX_NODE) { // Special value for emonTx
	          power = (rfc.readByte() + (rfc.readByte() << 8));
	          print("  Power: " + power + " watts");
	          long millis = System.currentTimeMillis();
	          if (lastPower != 0) {
	            int diff = (int) (millis - lastPower);
	            energy += (diff * power) / 3600000000d; // from watt-milliseconds to kwh
	            roundedEnergy = Double.parseDouble(df.format(energy));
	            if (cosm) {
	              d.setCurrentValue(df.format(energy));
	              try {
	                c.updateDatastream(COSM_FEED, COSM_ENERGY_STREAM, d);
	              } catch (CosmException e) {
	            	e.printStackTrace();
	                error("COSM error updating energy: "  + e.getMessage());
	              }
	            }
	            print("  Energy: " + roundedEnergy + " kwh");
	          }
	          lastPower = millis;
	          if (cosm) {
	            d.setCurrentValue("" + ((int) power));
	            try {
	              c.updateDatastream(COSM_FEED, COSM_POWER_STREAM, d);
	            } catch (CosmException e) {
	              e.printStackTrace();
	              error("COSM error updating power: " + e.getMessage());
	            }
	          }
	          rfc.readByte();rfc.readByte(); // Skip unused values
	          rfc.readByte();rfc.readByte();
	          int batt = (rfc.readByte() + (rfc.readByte() << 8));
	          print("  Battery: " + batt);
	          batteryLow[sensor] = (batt < BATTERY_LOW_LEVEL);
	        } else { // Room node
	          for(int i=0;i<4;i++) {
	            b = rfc.readByte();
	            if (i==0) {
	              light[sensor] = Math.round(b / 2.56f);
	              print("  Light: " + b);
	            } else if (i==1) {
	              humidity[sensor] = b >> 1;
	              print("  Humidity: " + (b >> 1));
	              if ((b & 1) == 1) {
	                print("  *** MOTION ***");
	                occupied[sensor] = System.currentTimeMillis();
	              }
	            } else if (i ==2) {
	              temp[sensor] = Math.round(b/10f);
	              print("  Temperature: " + ((float) (b/10f)));
	            } else if (i == 3) {
	              debug("Battery low: " + b);
	              batteryLow[sensor] = (b == 0 ? false : true);
	            }
	          }
	        }
        } catch(IOException e) {
	    	error("IOException in ReadInput: " + e.getMessage());
        }
      }
    }
  }
  
  
  // Thread to process LightwaveRF input
  class ReadLWRFInput implements Runnable {
    public void run() {
      for(;;) {    
        try {
			int b = rfc434.readByte();
			print("Read LightwaveRF: " + b);
			
			if (b < MAX_SOCKETS*2) {
				socketOn[b/2] = (b % 2 == 0); // Even on, odd off 
			} else if (b < (MAX_SOCKETS+MAX_WINDOWS)*2) {
				windowOpen[(b-(MAX_SOCKETS*2))/2] = (b % 2 == 0);
			} else if (b < (MAX_SOCKETS+MAX_WINDOWS+MAX_LIGHTS)*2) {
				lightOn[(b-(MAX_SOCKETS+MAX_WINDOWS)*2)/2] = (b % 2 == 0);
			}
		} catch (IOException e) {
			error("IOException in ReadLWRFInput");
		}
      }
    }
  }
 
  // Thread to process IAM input
  class ReadIAMInput implements Runnable {
    StringBuilder buff = new StringBuilder();
    public void run() {
      for(;;) {
        int b = -1;
        
        try {
			b = rfcIAM.readByte();
		} catch (IOException e1) {
			error("IOException in ReadIAMInput: " + e1.getMessage());
		}
        
        if (b == '\r') {
          long millis = System.currentTimeMillis();
          String s = buff.toString();
          debug(s);
          if (s.length() > 0 && s.charAt(0) == '{') {
            try {
              JSONObject obj = new JSONObject(s);
              long id = obj.getLong("id");
              int n = findIAMIndex(id);
              if (n >=0) {
                iamOn[n] = (obj.getInt("state") == 1);
                int val = ((JSONObject) obj.get("sensors")).getInt("1");
                print("IAM " + (n+1) + ": " + val + " watts");
                if (val > IAM_MIN_VALUE && iamValue[n] < IAM_MIN_VALUE && !iamStarted[n]) {
                  iamStarted[n] = true;
                  iamFinished[n] = false;
                  print("*** Appliance " + (n+1) + " started ***");
                  say("Appliance " + (n+1) + " started");
                  sendEmail("Appliance " + (n+1) + " started","Started at " + (new Date()));
                  iamEnergy[n] = 0;
                } else if (val < IAM_MIN_VALUE && iamStarted[n] && !iamFinished[n] &&iamLastPos[n] > 0 && millis - iamLastPos[n] > IAM_MIN_OFF_PERIOD) {
                  iamFinished[n] = true;
                  iamStarted[n] = false;
                  print("*** Appliance " + (n+1) + " finished ***");
                  say("Appliance " + (n+1) + " finished");
                  sendEmail("Appliance " + (n+1) + " finished","Finished at " + (new Date()) + " and used " + iamEnergy[n] + "kwh");
                }
                iamValue[n] = val;
                // calculate energy usage
                if (iamLast[n] != 0) {
                  int diff = (int) (millis - iamLast[n]);
                 iamEnergy[n] += (diff * val) / 3600000000d; // from watt-milliseconds to kwh
                 debug("IAM " + (n+1) + " energy: " + iamEnergy[n]);
                }
                iamLast[n] = millis;
                if (val > IAM_MIN_VALUE) iamLastPos[n] = millis;
              }
            } catch (JSONException e) {
              error("IAM JSON Exception: " + s);
            }
          }
          buff = new StringBuilder();
        } else if (b != '\n') {
          buff.append((char) b);
        }
      }  
    }
  }
  
  // Thread for implementing time consuming background tasks, and doing some periodic checks
  class Background implements Runnable {
    @Override
    public void run() {
      for(;;) {
    	if (alarmTime != 0 && alarmTime < System.currentTimeMillis()) {
    		say("Beep beep beep buzz buzz beep");
    	}
        volume = getVolume();
        phoneConnected = isReachableByPing(MOBILE_PHONE_HOST_NAME);
        print("Music on: " + musicOn);
        try {
          Thread.sleep((BACKGROUND_DELAY));
        } catch (InterruptedException e) {
        }
        print("Number of commands: " + numCmds + " state: " + state + " command: " + cmd);
      }  
    }
  }

  // Send string reply
  private void writeString(String s) throws IOException {
    os.write(s.getBytes());
  }

  // Send success reply
  private void success() throws IOException {
    os.write('0');
  }
  
  // Send on or off switch reply
  private void writeSwitch(boolean sw) throws IOException {
    writeString(sw ? "on" : "off");
  }

  // Send integer reply
  private void writeInt(int n) throws IOException {
    String s = Integer.toString(n, 10);
    writeString(s);
  }
  
  // Get an integer parameter
  private int getInt(String cmd, int i) {
    return Integer.parseInt(cmd.substring(i));
  }
  
  // Send single byte infrared command via RFM12
  private void sendIRCode(byte cmd) throws IOException {
	print("Sending IR code: " + (cmd & 0xFF));
    if (rfc != null) rfc.sendCmd(cmd);
  }
  
  // Send a byte to LightWaveRF transceiver
  private void sendLWRF(byte cmd) throws IOException {
    if (rfc434 != null) rfc434.sendCmd(cmd);
  }
  
  // Send a LightwaveRF dim level command
  private void sendLevel(byte channel, byte level) throws IOException {
    sendLWRF((byte) ((channel << 5) | level));
  }
  
  // Send a byte to IAM transceiver
  private void sendRFCIAM(char cmd) throws IOException {
    if (rfc434 != null) rfcIAM.sendCmd((byte) cmd);
  }
  
  // Send a string to IAM transceiver
  private void sendIAMString(String s) throws IOException {
    for(int i=0;i<s.length();i++) {
      sendRFCIAM(s.charAt(i));      
    }
  }
  
  // Print a message on the console
  private void print(String s) {
    logger.info(s);
  }
  
  // Print a message on the console
  private void debug(String s) {
    logger.debug(s);
  }
  
  // Print an error message
  private void error(String s) {
    logger.error(s);
  }
  
  // Get the command from the socket
  private String getCmd() {
    numCmds++;
    StringBuilder s = new StringBuilder();
      try {
        while(true) {
          int b = is.read();
          if (b < 0) break;
          if (b == '\r') {
            return s.toString();
          }
          else s.append((char) b);
        }
      } catch (IOException e) {
        error("IOException reading command");
      }
      return null;
  }
  
  // Send a playlist request to the music computer
  private String play(String playlist) {
    String list = playlists.get(playlist);
    if (list != null) {
      return sendMusicCmd("play " + list + "?autoplay=true",false);
    } else {
      error("No such playlist: " + playlist);
      return "Error";
    }
  }
  
  // Send a playlist request to the music computer
  private synchronized String sendMusicCmd(String cmd, boolean force) {
    if (!force && !musicOn) return "Error";
    try {
      Socket sock = new Socket(MUSIC_SERVER_HOST_NAME, PORT);
      sock.setSoTimeout(MUSIC_SERVER_SOCKET_TIMEOUT);
      PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
      BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      out.println(cmd);
      print("Sending " + cmd + " to music server");
      String ret = in.readLine();
      print("Music server: " + ret);
      out.close();
      in.close();
      sock.close();
      return ret;
    } catch (UnknownHostException e) {
      error("Unknown host in sendCmd");
      return("Error");
    } catch (IOException e) {
      error("IO Exception in sendCmd");
      return "Error";
    }
  }
  
  // Send a get volume request to the music computer
  private int getVolume() {
     try {
       int r =  Integer.parseInt(sendMusicCmd(CMD_GET_VOLUME, true));
       musicOn = true;
       return r;
     } catch (Exception e) {
       musicOn = false;
       return volume;
     }
  }
  
  // Send a set volume command too the music computer
  private void setVolume(int vol) {
     sendMusicCmd(CMD_SET_VOLUME + " " + vol, false);   
  }
  
  // Search the array of IAM device IDs
  int findIAMIndex(long id) {
    for(int i=0;i<iamIds.length;i++) {
      if  (id == iamIds[i]) return i;
    }
    error("*** Failed to find Id: " + id + " ***");
    return -1;
  }
  
  // Speak a message on this computer
  private void sayLocal(String msg) {
	if (synth != null) {
	    try {  
	      // Speak the message
	      synth.speakPlainText(msg, null);
	  
	      // Wait till speaking is done
	      synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	}
  }
  
  // Speak a message on the music server or locally, if music server not available
  private void say(String msg) {
	print("Saying " + msg);
	if (!noisy) return;
    if (musicOn) sendMusicCmd("say " + msg, false);
    else sayLocal(msg);
  }
  
  // Send an email
  private void sendEmail(String title, String msg) {
	if (email) {
	    try {
	      GoogleMail.send(EMAIL_USER, EMAIL_PASSWORD, EMAIL_TARGET, title, msg);
	    } catch (AddressException e) {
	      e.printStackTrace();
	    } catch (MessagingException e) {
	      e.printStackTrace();
	    }
	}
  }
  
  // Execute an HTTP command
  private void httpCommand(String cmd) throws IOException {
	String req = cmd.substring(5);
	int n = req.indexOf(" ");
	req = req.substring(0, n);
	
	print("HTTP command: " + req);
	
	if (req.equals(HTTP_PHONE_WAKE)) {
		phoneOn = true;
		say("Phone woken up");
	} else if (req.equals(HTTP_PHONE_SLEEP)) {
		phoneOn = false;
		say("Phone gone to sleep");
	} else if (req.equals(HTTP_TEXT_RECEIVED)) {
		say("Text message received");
	} else if (req.equals(HTTP_CALL_RECEIVED)) {
		say("Phone call received");
	} else if (req.equals(HTTP_WIFI_CONNECTED)) {
		say("Phone connected to Wifi");
		phoneConnected = true;
	} else if (req.equals(HTTP_WIFI_DISCONNECTED)) {
		say("Phone disconnected from Wifi");
		phoneConnected = false;
	} else if (req.equals(HTTP_LIGHT)) {
		// Switch living room lights on
		switchLight(LIVING_ROOM_LIGHT_1,true);
		switchLight(LIVING_ROOM_LIGHT_2,true);
	} else if (req.equals(HTTP_LIGHT)) {
		// Switch living room lights off
		switchLight(LIVING_ROOM_LIGHT_1,false);
		switchLight(LIVING_ROOM_LIGHT_2,false);
	}

	writeString("HTTP/1.1 200 OK\r\n\r\nOK\r\n");
  }
  
  // Switch a light on or off
  private void switchLight(int light, boolean on) throws IOException {
      lightOn[light] = on;
      print("Switching light " + (light+1) + " " + (on ? "on" : "off"));
	  sendLWRF((byte) ((LIGHT_BASE + light*2) + (on ? 0 : 1)));
  }
  
  // Set the dim level on a LightwaveRF light
  private void dimLight(int light, int level) throws IOException {
      if (level < 0) level = 0;
      lightValue[light] = level;
      lightOn[light] = true;
      sendLevel((byte) light,(byte) (level * 32/100));
  }
  
  // Switch LightwaveRF socket on or off
  private void switchSocket(int socket, boolean on) throws IOException {
      socketOn[socket] = on;
      print("Switching socket " + (socket+1) + " "  + (on ? "on" : "off"));
	  sendLWRF((byte) (SOCKET_BASE + socket*2 + (on ? 0 : 1)));
  }
  
  // Switch EDF IAM socket on or off
  private void switchIAMSocket(int socket, boolean on) throws IOException {
      sendRFCIAM((on ? '1' : '0'));
      sendIAMString("" + iamIds[socket]);
      iamOn[socket] = true;
  }
}

