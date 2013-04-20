package net.geekgrandad.plugin;

import java.io.IOException;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.Alerter;
import net.geekgrandad.interfaces.HTTPControl;
import net.geekgrandad.interfaces.LightControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class HTTPRespond implements HTTPControl {
	private Reporter reporter;
	private Alerter alerter;
	private Config config;
	private LightControl lightControl;
	private boolean phoneConnected = false, phoneOn = false;
	
	// HTTP commands
	private static final String HTTP_PHONE_WAKE = "phonewake";
	private static final String HTTP_PHONE_SLEEP = "phonesleep";
	private static final String HTTP_TEXT_RECEIVED = "text";
	private static final String HTTP_CALL_RECEIVED = "call";
	private static final String HTTP_WIFI_CONNECTED = "wifion";
	private static final String HTTP_WIFI_DISCONNECTED = "wifioff";
	private static final String HTTP_LIGHT = "light";
	private static final String HTTP_DARK = "dark";
	
	private static final int LIVING_ROOM = 0;

	@Override
	public String httpCommand(String cmd) throws IOException {
		String req = cmd.substring(5);
		int n = req.indexOf(" ");
		req = req.substring(0, n);

		reporter.print("HTTP command: " + req);

		if (req.equals(HTTP_PHONE_WAKE)) {
			phoneOn = true;
			alerter.say("Phone woken up");
		} else if (req.equals(HTTP_PHONE_SLEEP)) {
			phoneOn = false;
			alerter.say("Phone gone to sleep");
		} else if (req.equals(HTTP_TEXT_RECEIVED)) {
			alerter.say("Text message received");
		} else if (req.equals(HTTP_CALL_RECEIVED)) {
			alerter.say("Phone call received");
		} else if (req.equals(HTTP_WIFI_CONNECTED)) {
			alerter.say("Phone connected to Wifi");
			phoneConnected = true;
		} else if (req.equals(HTTP_WIFI_DISCONNECTED)) {
			alerter.say("Phone disconnected from Wifi");
			phoneConnected = false;
		} else if (req.equals(HTTP_LIGHT)) {
			// Switch living room lights on
			for (int light : config.roomLights[LIVING_ROOM]) {
				lightControl.switchLight(light, true);
			}
		} else if (req.equals(HTTP_LIGHT)) {
			// Switch living room lights off
			for (int light : config.roomLights[LIVING_ROOM]) {
				lightControl.switchLight(light, false);
			}
		}
		
		return "HTTP/1.1 200 OK\r\n\r\nOK\r\n";
	}

	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();
		reporter = provider.getReporter();
		alerter = provider.getAlerter();
		lightControl = provider.getLightControl(0);
	}

	@Override
	public void setLightControl(LightControl lightControl) {
		this.lightControl = lightControl;	
	}
}
