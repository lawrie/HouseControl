package net.geekgrandad.plugin;

import net.geekgrandad.interfaces.AlarmControl;
import net.geekgrandad.interfaces.Alerter;
import net.geekgrandad.interfaces.Provider;

public class DummyAlarm implements AlarmControl {
	private Alerter alerter;
	private int alarmTime;

	@Override
	public int getAlarmTime() {
		return (int) (alarmTime == 0 ? 0 : (alarmTime - System.currentTimeMillis()) / 1000);
	}

	@Override
	public boolean getAlarmStatus() {
		return (alarmTime > 0);
	}

	@Override
	public void setAlarmTime(int seconds) {
		alarmTime = seconds * 100;		
	}

	@Override
	public void clearAlarm() {
		alarmTime = 0;		
	}

	@Override
	public void setProvider(Provider provider) {
		alerter = provider.getAlerter();	
	}

	@Override
	public void soundAlarm() {
		alerter.say("Buzz buzz buzz, beep beep beep, buzz buzz buzz");
		
	}
}
