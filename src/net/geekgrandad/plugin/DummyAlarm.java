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

	@Override
	public void checkAlarm() {
		if (alarmTime != 0 && alarmTime < System.currentTimeMillis()) {
			// Save the current volume
			/*
			int vol = volume;
			// Stop the music
			try {
				mediaControl[defaultMusicDevice-1].pause(defaultMusicDevice);
			} catch (IOException e) {
				musicOn = false;
			}
			// Set the volume to max
			try {
				mediaControl[defaultMusicDevice-1].setVolume(defaultMusicDevice, 100);
			} catch (IOException e) {
				musicOn = false;
			}
			// Sound the alarm
			alarmControl.soundAlarm();
			// Set the volume back
			try {
				mediaControl[defaultMusicDevice-1].setVolume(defaultMusicDevice, vol);
			} catch (IOException e) {
				musicOn = false;
			}
			// Restart the music
			try {
				mediaControl[defaultMusicDevice-1].play(defaultMusicDevice);
			} catch (IOException e) {
				musicOn = false;
			}
			*/
		}
		
	}
}
