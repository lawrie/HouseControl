package net.geekgrandad.config;

public class Step {
	private String cmd;
	private int delay;
	
	public Step(String cmd, int delay) {
		this.cmd = cmd;
		this.delay = delay;
	}
	
	public String getCmd() {
		return cmd;
	}
	
	public int getDelay() {
		return delay;
	}
}
