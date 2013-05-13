package net.geekgrandad.plugin;

import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

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
	public void say(String msg) {
		if (synth != null && speak) {
			try {
				// Speak the message
				synth.speakPlainText(msg, null);

				// Wait till speaking is done
				synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setSpeech(Boolean on) {
		speak = on;		
	}

	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();
		reporter = provider.getReporter();
		
		// Create a speech synthesizer and start it
		try {
			// Create a synthesizer for English
			System.setProperty("freetts.voices", config.speechVoice);
			synth = Central.createSynthesizer(new SynthesizerModeDesc(
					Locale.ENGLISH));
			synth.allocate();
			synth.resume();
		} catch (Exception e) {
			reporter.error("Failed to create speak synthesizer");
		}
	}
}
