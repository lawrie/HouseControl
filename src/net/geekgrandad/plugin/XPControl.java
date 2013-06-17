package net.geekgrandad.plugin;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

import net.geekgrandad.interfaces.ComputerControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.util.ActivateWindow;

public class XPControl implements ComputerControl {
	private Reporter reporter;
	private Robot robot;
	
	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();	
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
    public void shutdown() {
	    String shutdownCommand = null;
	    String osName = System.getProperty("os.name");        
	    if (osName.startsWith("Win")) {
	      shutdownCommand = "shutdown.exe -s -t 0";
	    } else if (osName.startsWith("Linux") || osName.startsWith("Mac")) {
	      shutdownCommand = "shutdown -h now";
	    } else {
	      System.err.println("Shutdown unsupported operating system ...");
	    }
	    if (shutdownCommand != null)
	      try {
	        Runtime.getRuntime().exec(shutdownCommand);
	      } catch (IOException e) {}
	    System.exit(0);
	}

	@Override
	public void reboot() {
		// TODO Auto-generated method stub	
	}

	@Override
	public int getVolume() {	
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
					reporter.error("LineUnavailable exception in getVolume");
				}     
	        }  
	    }
	    return 0;
	}

	@Override
	public void setVolume(int vol) {	
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
					reporter.error("LineUnavailable exception in setVolume");
				}     
	        }  
	    }
	}

	@Override
	public int execute(String cmd) throws IOException  {
		Runtime.getRuntime().exec(cmd);
        return 0;
	}

	@Override
	public void sendKey(String program, int keyCode) {
		reporter.print("Activating " + program);
		String title = ActivateWindow.activate(program);
		reporter.print("Title is " + title);
		if (title == null) {
			reporter.error("Could not find program");
		} else {
	        robot.keyPress(keyCode);
	        robot.keyRelease(keyCode);
	        try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}
}
