package net.geekgrandad.interfaces;

public interface AlarmControl extends Controller {
	
	public int getAlarmTime();
	
	public boolean getAlarmStatus();
	
	public void setAlarmTime(int seconds);
	
	public void clearAlarm();
	
	public void soundAlarm();
	
	public void checkAlarm();
	
}
