package net.geekgrandad.config;

public class Device {
	private String technology;
	private String name;
	private String code;
	private int id;
	private int channel;
	private int type;
	
	public static final int SOCKET = 0;
	public static final int LIGHT = 1;
	public static final int SWITCH = 2;
	public static final int APPLIANCE = 3;
	public static final int MUSIC_SERVER = 4;
	public static final int MEDIA= 5;
	
	public Device(String technology, String name, String code, int id, int channel, int type) {
		this.name = name;
		this.technology = technology;
		this.code = code;
		this.id = id;
		this.channel = channel;
		this.type = type;
	}
	
	public String getTechnology() {
		return technology;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
	
	public int getId() {
		return id;
	}
	
	public int getChannel() {
		return channel;
	}
	
	public int getType() {
		return type;
	}
}
