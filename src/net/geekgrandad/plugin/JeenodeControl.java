package net.geekgrandad.plugin;

import java.io.IOException;
import java.text.DecimalFormat;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.DatalogControl;
import net.geekgrandad.interfaces.InfraredControl;
import net.geekgrandad.interfaces.PlantControl;
import net.geekgrandad.interfaces.PowerControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.SensorControl;
import net.geekgrandad.rf.RFControl;

/**
 * Class to connect to the Jeenode network, read data from sensors and send data to IR devices
 * 
 * @author Lawrie Griffiths
 *
 */
public class JeenodeControl implements SensorControl, PowerControl, PlantControl, InfraredControl {
	private Reporter reporter;
	private Config config;
	private RFControl rfc;
	private int sensor;
	private float power = 0;
	private long lastPower = 0;
	private double energy = 0;
	private double roundedEnergy;
	private DatalogControl datalogControl;
	private boolean[] batteryLow = new boolean[Config.MAX_SENSORS];
	private int[] humidity = new int[Config.MAX_SENSORS];
	private int[] temp = new int[Config.MAX_SENSORS];
	private int[] light = new int[Config.MAX_SENSORS];
	private long[] occupied = new long[Config.MAX_SENSORS];
	private boolean[] sensorOn = new boolean[Config.MAX_SENSORS];
	private Thread inThread = new Thread(new ReadInput());
	
	/**
	 * Set the datalog controller (e.g. COSM) to send power and energy data to
	 * 
	 * @param datalogControl the controller
	 */
	public void setDatalogControl(DatalogControl datalogControl) {
		this.datalogControl = datalogControl;
	}
	
	@Override
	public int getSoilMoisture(int plant) {
		return humidity[plant];
	}

	@Override
	public int getPower() {
		return (int) power;
	}

	@Override
	public int getEnergy() {
		return (int) energy;
	}

	@Override
	public boolean getSensorStatus(int sensor) {
		return sensorOn[sensor];
	}

	@Override
	public int getTemperature(int sensor) {
		return temp[sensor];
	}

	@Override
	public int getHumidity(int sensor) {
		return humidity[sensor];
	}

	@Override
	public int getLightLevel(int sensor) {
		return light[sensor];
	}

	@Override
	public boolean getMotion(int sensor) {
		return (occupied[sensor] != 0 && (System.currentTimeMillis() - occupied[sensor]) < config.occupiedInterval);
	}

	@Override
	public boolean getBatteryLow(int sensor) {
		return batteryLow[sensor];
	}
	
	// Send single byte infrared command via RFM12
	public void sendCommand(int id, int cmd) throws IOException {
		reporter.print("Sending IR code: " + cmd);
		if (rfc != null)
			rfc.sendCmd((byte) cmd);
	}
	
	// Thread to process RFM12 input
	class ReadInput implements Runnable {
		public void run() {
			DecimalFormat df = new DecimalFormat("#####.##");
			for (;;) {
				int b = -1;

				try {
					b = rfc.readByte();
					sensor = (b & 0x1f);
					sensorOn[sensor] = true;
					reporter.print("Room: " + sensor);
					if (b == config.getEmontxId()) { // Special value for emonTx
						power = (rfc.readByte() + (rfc.readByte() << 8));
						reporter.print("  Power: " + power + " watts");
						long millis = System.currentTimeMillis();
						if (lastPower != 0) {
							int diff = (int) (millis - lastPower);
							energy += (diff * power) / 3600000000d; // from
																	// watt-milliseconds
																	// to kwh
							roundedEnergy = Double.parseDouble(df.format(energy));
							if (datalogControl != null) {
								datalogControl.updateEnergy(energy);
							}
							reporter.print("  Energy: " + roundedEnergy + " kwh");
						}
						lastPower = millis;
						if (datalogControl != null) {
							datalogControl.updatePower(power);
						}
						rfc.readByte();
						rfc.readByte(); // Skip unused values
						rfc.readByte();
						rfc.readByte();
						int batt = (rfc.readByte() + (rfc.readByte() << 8));
						reporter.print("  Battery: " + batt);
						batteryLow[sensor] = (batt < config.emontxBatteryLow);
					} else { // Room node
						for (int i = 0; i < 4; i++) {
							b = rfc.readByte();
							if (i == 0) {
								light[sensor] = Math.round(b / 2.56f);
								reporter.print("  Light: " + b);
							} else if (i == 1) {
								humidity[sensor] = b >> 1;
								reporter.print("  Humidity: " + (b >> 1));
								if ((b & 1) == 1) {
									reporter.print("  *** MOTION ***");
									occupied[sensor] = System.currentTimeMillis();
								}
							} else if (i == 2) {
								temp[sensor] = Math.round(b / 10f);
								reporter.print("  Temperature: " + ((float) (b / 10f)));
							} else if (i == 3) {
								reporter.debug("Battery low: " + b);
								batteryLow[sensor] = (b == 0 ? false : true);
							}
						}
					}
				} catch (IOException e) {
					reporter.error("IOException in ReadInput: " + e.getMessage());
				}
			}
		}
	}

	// Set the configuration, connect to the device and start the thread
	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();
		reporter = provider.getReporter();
		datalogControl = provider.getDatalogControl();
		
		// Connect to the Jeenode network, if transceiver port defined
		if (config.rfm12Port != null && config.rfm12Port.length() > 0) {
			try {
				rfc = new RFControl(config.rfm12Port,57600,2000,"RFM12");
			} catch (IOException e1) {
				reporter.error(e1.getMessage());
			}
		}
		
		// Start the Jeenode read thread, if required
		if (rfc != null) {
			inThread.setDaemon(true);
			inThread.start();
		}
	}

	// Change the Virgin TiVo TV channel
	public void setChannel(String channel) throws IOException {
		for (int i = 0; i < 3; i++) {
			int code = (128 + channel.charAt(i) - '0');
			sendCommand(3, code);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}
