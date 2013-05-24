package net.geekgrandad.music;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;


public class ORMusicControl {
	private ServerSocket ss;
	private Socket s;
	private InputStream is;
	private OutputStream os;
	private Robot robot;
	private Synthesizer synth;
	
	public void run() {	
		try {
			ss = new ServerSocket(50000);
		} catch (IOException e) {
			System.err.println("Failed to create server socket:" + e.getMessage());
			System.exit(1);
		}
		
		try {
      robot = new Robot();
    } catch (AWTException e1) {
      e1.printStackTrace();
    }
		
    // Create a speak synthesizer and start it
    try {  
      System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
      // Create a synthesizer for English
      synth = Central.createSynthesizer(new SynthesizerModeDesc(Locale.ENGLISH));
      synth.allocate();
      synth.resume();
    } catch (EngineException e1) {
      e1.printStackTrace();
    } catch (EngineStateError e1) {
      e1.printStackTrace();
    } catch (AudioException e) {
      e.printStackTrace();
    }
		
		while(true) {			
			try {
				s = ss.accept();
				is = s.getInputStream();
				os = s.getOutputStream();
				String cmd = getCmd();
				System.out.println("cmd: " + cmd);
				
				if (cmd != null) {
				
  			  if (cmd.startsWith("play")) {
  					java.awt.Desktop.getDesktop().browse(java.net.URI.create(cmd.substring(5)));
  					success();
  				} else if (cmd.startsWith("say")) {
  				  say(cmd.substring(4));
  				  success();
  				} else if (cmd.equals("getvol")) {
  					int vol = getVolume();
  					writeInt(vol);
  				} else if ((cmd.startsWith("setvol"))) {
  					int vol = Integer.parseInt(cmd.substring(7));
  					setVolume(vol);
  					success();						
  				} else if (cmd.equals("stop")) {
  				  if (robot != null) {
  				    System.out.println(ActivateWindow.activate("Spotify"));
  				    System.out.println("Sending space keypress");
  				    robot.keyPress(KeyEvent.VK_SPACE);
  				    robot.keyRelease(KeyEvent.VK_SPACE);
  				  }
  				  success();
  				} else if (cmd.equals("skip")) {
            if (robot != null) {
              ActivateWindow.activate("Spotify");
              System.out.println("Sending next keypress");
              robot.keyPress(KeyEvent.VK_CONTROL);
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {}
              robot.keyPress(KeyEvent.VK_RIGHT);
              robot.keyRelease(KeyEvent.VK_RIGHT);
              robot.keyRelease(KeyEvent.VK_CONTROL);
            }
            success();
          }else if (cmd.equals("shutdown")) {
  				  success();
  				  try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {}
  				  shutdown();
          } else {
  				  System.out.println("Unknown command: " + cmd);
  				  success();
  				} 
				}
				is.close();
				os.close();
				s.close();
			} catch (IOException e) {
				System.err.println("IO error:" + e.getMessage());
			}
		}
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
	
	private void writeString(String s) throws IOException {
		os.write(s.getBytes());
	}
	
	private void success() throws IOException {
		os.write('0');
	}
	
	private void writeInt(int n) throws IOException {
		String s = Integer.toString(n, 10);
		writeString(s);
	}

  private String getCmd() {
    StringBuilder s = new StringBuilder();
      try {
        while(true) {
          int b = is.read();
          
          if (b == '\r') return s.toString();
          else s.append((char) b);
        }
      } catch (IOException e) {
        System.err.println("IOException reading command");
      }
      return null;
  }
  
  private void say(String msg) {
    try {  
      // Speak the message
      synth.speakPlainText(msg, null);
  
      // Wait till speaking is done
      synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void shutdown() {
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

	
	public static void main(String[] args) {
		(new ORMusicControl()).run();
	}
}
