package net.geekgrandad.plugin;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.Alerter;
import net.geekgrandad.interfaces.ApplianceControl;
import net.geekgrandad.interfaces.MQTT;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Quantity;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.rf.RFControl;

public class IAMControl implements ApplianceControl {
	private Config config;
	private Reporter reporter;
	private RFControl rfcIAM;
	private Alerter alerter;
	
	private boolean[] iamStarted = new boolean[Config.MAX_APPLIANCES];
	private boolean[] iamFinished = new boolean[Config.MAX_APPLIANCES];
	private long[] iamLast = new long[Config.MAX_APPLIANCES];
	private long[] iamLastPos = new long[Config.MAX_APPLIANCES];
	private boolean[] iamOn = new boolean[Config.MAX_APPLIANCES];
	private int[] iamValue = new int[Config.MAX_APPLIANCES];
	private double[] iamEnergy = new double[Config.MAX_APPLIANCES];
	private long[] iamIds;
	private Provider provider;
	private MQTT mqtt;
	
	private Thread inThreadIAM = new Thread(new ReadIAMInput());
	
	@Override
	public void setProvider(Provider provider) {
		this.provider = provider;
		config = provider.getConfig();
		alerter = provider.getAlerter();
		reporter = provider.getReporter();
		mqtt = provider.getMQTTControl();
		
		iamIds = config.iamCodes;
		
		// Connect to the IAM network, if transceiver port defined
		if (config.iamPort != null && config.iamPort.length() > 0) {
			try {
				rfcIAM = new RFControl(config.iamPort,115200, 200, "IAM");
			} catch (IOException e1) {
				reporter.error(e1.getMessage());
			}
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Start the IAM read thread, if required
		if (rfcIAM != null) {
			inThreadIAM.setDaemon(true);
			inThreadIAM.start();

			// Tell transceiver the devices to use
			try {
				// Only receive data from known devices
				sendRFCIAM('k');

				// add known devices
				for (int i = 0; i < iamIds.length; i++) {
					sendRFCIAM('N');
					sendIAMString("" + iamIds[i]);
					sendRFCIAM('\r');
				}
			} catch (IOException e) {
				reporter.error("Failed to talk to IAM transceiver:" + e.getMessage());
				System.exit(1);
			}
		}

	}

	@Override
	public void switchAppliance(int appliance, boolean on) throws IOException {
		sendRFCIAM((on ? '1' : '0'));
		sendIAMString("" + iamIds[appliance]);
		iamOn[appliance] = true;	
	}
	
	// Send a byte to IAM transceiver
	private void sendRFCIAM(char cmd) throws IOException {
		if (rfcIAM != null)
			rfcIAM.sendCmd((byte) cmd);
	}

	// Send a string to IAM transceiver
	public void sendIAMString(String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			sendRFCIAM(s.charAt(i));
		}
	}
	
	@Override
	public boolean getApplianceStatus(int appliance) {
		return iamOn[appliance];
	}

	@Override
	public int getAppliancePower(int appliance) {
		return iamValue[appliance];
	}
	
	// Thread to process IAM input
	class ReadIAMInput implements Runnable {
		StringBuilder buff = new StringBuilder();

		public void run() {
			for (;;) {
				DecimalFormat df = new DecimalFormat("#####.##");
				int b = -1;

				try {
					b = rfcIAM.readByte();
				} catch (IOException e1) {
					reporter.error("IOException in ReadIAMInput: " + e1.getMessage());
				}

				if (b == '\r') {
					long millis = System.currentTimeMillis();
					String s = buff.toString();
					reporter.debug(s);
					if (s.length() > 0 && s.charAt(0) == '{') {
						try {
							JSONObject obj = new JSONObject(s);
							long id = obj.getLong("id");
							int n = findIAMIndex(id);
							if (n >= 0) {
								String name = config.applianceNames[n];
								String key = name + ":" + Quantity.POWER.name().toLowerCase();
								String topic = config.mqttTopics.get(key);
								
								iamOn[n] = (obj.getInt("state") == 1);
								int val = ((JSONObject) obj.get("sensors"))
										.getInt("1");
								reporter.print("IAM " + (n + 1) + ": " + val + " watts");
								if (val > config.iamMinValue
										&& iamValue[n] < config.iamMinValue
										&& !iamStarted[n]) {
									iamStarted[n] = true;
									iamFinished[n] = false;
									reporter.print("*** " + name
											+ " started ***");
									alerter.say(name + " started");
									alerter.sendEmail(name
											+ " started", "Started at "
											+ (new Date()));
									iamEnergy[n] = 0;
								} else if (val < config.iamMinValue
										&& iamStarted[n]
										&& !iamFinished[n]
										&& iamLastPos[n] > 0
										&& millis - iamLastPos[n] > config.iamMinOffPeriod) {
									
									iamFinished[n] = true;
									iamStarted[n] = false;
									reporter.print("*** " + name 
											+ " finished ***");
									alerter.say(name + " finished");
									alerter.sendEmail(name
											+ " finished", "Finished at "
											+ (new Date()) + " and used "
											+ df.format(iamEnergy[n]) + "kwh");
								}
								iamValue[n] = val;
								// Publish to MQTT server
								reporter.print("Key: " + key + ", topic: " + (topic == null ? "null" : topic));
								if (mqtt == null) mqtt = provider.getMQTTControl();
								if (mqtt != null && topic != null) {
									mqtt.publish(topic, "" + val, 0);
									mqtt.publish(topic.replace("/power",  "/status"), (iamOn[n] ? "on" : "off"), 0);
								}
								// calculate energy usage
								if (iamLast[n] != 0) {
									int diff = (int) (millis - iamLast[n]);
									iamEnergy[n] += (diff * val) / 3600000000d; // from watt-milliseconds to kwh
									reporter.debug("IAM " + (n + 1) + " energy: "
											+ iamEnergy[n]);
								}
								iamLast[n] = millis;
								if (val > config.iamMinValue)
									iamLastPos[n] = millis;
							}
						} catch (JSONException e) {
							reporter.error("IAM JSON Exception: " + s);
						}
					}
					buff = new StringBuilder();
				} else if (b != '\n') {
					buff.append((char) b);
				}
			}
		}
	}

	// Search the array of IAM device IDs
	int findIAMIndex(long id) {
		for (int i = 0; i < iamIds.length; i++) {
			if (id == iamIds[i])
				return i;
		}
		reporter.error("*** Failed to find Id: " + id + " ***");
		return -1;
	}
}
