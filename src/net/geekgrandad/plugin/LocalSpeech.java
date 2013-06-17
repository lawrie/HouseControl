package net.geekgrandad.plugin;

import javax.speech.EngineManager;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;

import org.jvoicexml.jsapi2.jse.synthesis.freetts.FreeTTSEngineListFactory;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.SpeechControl;

public class LocalSpeech implements SpeechControl {
	private boolean speak = true;
	private Synthesizer synth;
	Config config;
	Reporter reporter;
	
	@Override
	public void say(int id, String msg) {
		if (synth != null && speak) {
			try {
				reporter.print("Really really saying " + msg);
				// Speak the message
				synth.speak(msg, null);

				// Wait till speaking is done
				synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setSpeech(int id, boolean on) {
		speak = on;		
	}

	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();
		reporter = provider.getReporter();
		
		//System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        try {
			EngineManager.registerEngineListFactory(FreeTTSEngineListFactory.class.getName());
			 // Create a synthesizer for the default Locale
            synth = (Synthesizer) EngineManager.createEngine(SynthesizerMode.DEFAULT);
            // Get it ready to speak
            synth.allocate();
            synth.resume();
            synth.waitEngineState(Synthesizer.RESUMED);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
