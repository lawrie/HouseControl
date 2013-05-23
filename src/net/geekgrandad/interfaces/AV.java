package net.geekgrandad.interfaces;

import java.io.IOException;

public interface AV {
	
	public static final int ON = 100;
	public static final int OFF = 100;
	public static final int HOME = 101;
	public static final int MUTE = 102;
	public static final int MENU_OK = 103;
	public static final int MENU_UP = 104;
	public static final int MENU_DOWN = 105;
	public static final int MENU_LEFT = 106;
	public static final int MENU_RIGHT = 107;
	public static final int CHANNEL_UP = 0xa0;
	public static final int CHANNEL_DOWN = 0xa1;
	public static final int VOLUME_UP = 108;
	public static final int VOLUME_DOWN = 109;
	public static final int SOURCE_DVD = 110;
	public static final int SOURCE_BLUERAY = 111;
	public static final int GUIDE = 112;
	public static final int SOURCE_STB = 113;
	public static final int RECEIVER_ON = 114;
	public static final int TV = 0xa2;
	public static final int SHOWS = 0x95;
	public static final int RED = 115;
	public static final int YELLOW = 117;
	public static final int BLUE = 118;
	public static final int GREEN = 116;
	public static final int INFO = 0xaf;
	public static final int BACK = 0xaa;
	public static final int LAST_CHANNEL = 0x94;
	public static final int DELETE = 0x99;
	public static final int DIGIT_0 = 0x80;
	public static final int DIGIT_1 = 0x81;
	public static final int DIGIT_2 = 0x82;
	public static final int DIGIT_3 = 0x83;
	public static final int DIGIT_4 = 0x84;
	public static final int DIGIT_5 = 0x85;
	public static final int DIGIT_6 = 0x86;
	public static final int DIGIT_7 = 0x87;
	public static final int DIGIT_8 = 0x88;
	public static final int DIGIT_9 = 0x89;
	public static final int SLOW = 0x98;
	public static final int RECORD = 0xb7;
	public static final int PLAY = 0xb0;
	public static final int PAUSE = 0x93;
	public static final int STOP = 0xb6;
	public static final int FAST_FORWARD = 0xb4;
	public static final int FAST_BACKWARDS = 0xb2;
	public static final int SKIP_FORWARDS = 0xab;
	public static final int SKIP_BACKWARDS = 0xaa;
	public static final int THUMBS_UP = 0x97;
	public static final int THUMBS_DOWN = 0x96;
	public static final int SUBTITLES = 0x9a;
	public static final int BROADCAST = 119;

	/**
	 * Send an audio video command to a device
	 * 
	 * @param id the device id
	 * @param cmd the command code
	 */
	public void sendCommand(int id, int cmd) throws IOException;
	
}
