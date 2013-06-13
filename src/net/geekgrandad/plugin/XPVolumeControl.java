package net.geekgrandad.plugin;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.VolumeControl;

public class XPVolumeControl implements VolumeControl {
	private int volume = 0;
	
	@Override
	public void setProvider(Provider provider) {
		// nothing
	}

	@Override
	public void volumeUp(int id) throws IOException {
		setVolume(volume+1);
	}

	@Override
	public void volumeDown(int id) throws IOException {
		setVolume(volume-1);
	}

	@Override
	public void mute(int id) throws IOException {
		setVolume(0);
	}

	@Override
	public int getVolume(int id) throws IOException {
		volume = getVolume();
		return volume;
	}

	@Override
	public void setVolume(int id, int volume) throws IOException {
		setVolume(volume);
	}
	
	private int getVolume() {	
		Mixer.Info[] infos = AudioSystem.getMixerInfo();  
	    for (Mixer.Info info: infos)  
	    {  
	        Mixer mixer = AudioSystem.getMixer(info);  
	        if (mixer.isLineSupported(Port.Info.SPEAKER))  
	        {  
	            Port port;
				try {
					port = (Port)mixer.getLine(Port.Info.SPEAKER);
	                port.open();
	                if (port.isControlSupported(FloatControl.Type.VOLUME))  
	                {  
	                    FloatControl volume = (FloatControl)port.getControl(FloatControl.Type.VOLUME);  
	                    return (int) (volume.getValue() * 100f) ;
	                }  
	                port.close();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}     
	        }  
	    }
	    return 0;
	}
  
	private void setVolume(int vol) {
		volume = vol;
		Mixer.Info[] infos = AudioSystem.getMixerInfo();  
	    for (Mixer.Info info: infos)  
	    {  
	        Mixer mixer = AudioSystem.getMixer(info);  
	        if (mixer.isLineSupported(Port.Info.SPEAKER))  
	        {  
	            Port port;
				try {
					port = (Port)mixer.getLine(Port.Info.SPEAKER);
	                port.open();
	                if (port.isControlSupported(FloatControl.Type.VOLUME))  
	                {  
	                    FloatControl volume = (FloatControl)port.getControl(FloatControl.Type.VOLUME);  
	                    volume.setValue(vol/ 100f);
	                }  
	                port.close();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}     
	        }  
	    }
	}
}
