package net.geekgrandad.plugin;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.ApplianceControl;
import net.geekgrandad.interfaces.MQTT;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Quantity;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.SensorControl;
import net.geekgrandad.interfaces.SwitchControl;

public class MQTTControl implements MQTT, MqttCallback, SensorControl, SwitchControl {
	private Reporter reporter;
	private MqttClient client;
	private HashMap<String,String> values = new HashMap<String,String>();
	private long lastMessage = 0;
	private Config config;
	private Provider provider;
	private boolean connected = false;
	private MqttConnectOptions connOpts = new MqttConnectOptions();
	private String clientId = "housecontrol";
	private MemoryPersistence persistence = new MemoryPersistence();
	String broker;
	
	private void connect() {
		Collection<String> topics = config.mqttTopics.values();
		
        try {
                        
            connOpts.setCleanSession(true);
            reporter.print("Connecting to MQTT broker: "+ broker);
            client.connect(connOpts);
            reporter.print("Connected to MQTT broker");
            connected = true;
	        client.setCallback(this);
	        
	        /* Subscribe to all topics */
	        
	        for(String topic: topics) {
	        	subscribe(topic);
	        }
	        
	        if (config.mqttCommandTopic != null) subscribe(config.mqttCommandTopic);
	        if (config.mqttIamControlTopic != null) subscribe(config.mqttIamControlTopic);
	                
        } catch(MqttException me) {
        	reporter.error("Failed to connect to MQTT broker");
        }
	}

	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();
		config = provider.getConfig();
		this.provider = provider;
		broker = provider.getConfig().mqttServer;
		try {
			client = new MqttClient(broker, clientId, persistence);
		} catch (MqttException e) {
			reporter.error("Failed to create MQTT client");
		}
	}

	@Override
	public void publish(String topic, String content, int qos) {
        reporter.debug("Publishing message: " + content);
        if (!connected) connect();

        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        try {
			client.publish(topic, message);
		} catch (MqttException e) {
			reporter.error("Failed to publish MQTT message");
		}
        reporter.debug("MQTT message published");
	}

	@Override
	public void subscribe(String topic) {
		try {
			reporter.print("MQTT subscribing to " + topic);
			client.subscribe(topic);
		} catch (MqttException e) {
			reporter.error("Failed to subscibe to " + topic + " : " + e);
		}	
	}

	@Override
	public void connectionLost(Throwable arg0) {
		reporter.debug("MQTT connection lost");
		connected = false;
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		reporter.debug("MQTT delivery of " + token + " complete");	
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		reporter.print("MQTT message arrived: " + topic + ":" + message);
		if (topic.equals(config.mqttCommandTopic)) {
			reporter.print("Result from "  + message + " : " +provider.parse(message.toString()));
		} else if (topic.equals(config.mqttIamControlTopic)) {
			ApplianceControl ac = provider.getApplianceControl(1);
			if (ac != null && ac instanceof IAMControl) {
				reporter.print("Sending " + message.toString() + " to IAMControl");
				((IAMControl) ac).sendIAMString(message.toString());
			}
		} else {
			values.put(topic, message.toString());
			lastMessage = System.currentTimeMillis();
		}
	}

	@Override
	public String getValue(String topic) {
		reporter.print("Getting value for " + topic + " returned " + values.get(topic));
		return values.get(topic);
	}

	@Override
	public boolean getSensorStatus(int sensor) {
		return (System.currentTimeMillis() - lastMessage < 60000); // message in last minute
	}

	@Override
	public float getQuantity(int sensor, Quantity q) {
		String name = config.sensorNames[sensor-1];
		String key = name + ":" + q.name().toLowerCase();
		String topic = config.mqttTopics.get(key);
		//System.out.println("Key is " + key + ", topic is " + topic);
		if (topic == null) return Float.NaN;
		String s = values.get(topic);
		if (s == null) return Float.NaN;
		float value = Float.parseFloat(s);
		if (q == Quantity.TEMPERATURE) value /= 10;
		else if (q == Quantity.ATMOSPHERIC_PRESSURE) value /= 100;
		else if (q == Quantity.RELATIVE_HUMIDITY) value /= 10;
		else if (q == Quantity.ILLUMINANCE) value /= 10;
		return value;
	}

	@Override
	public boolean getSwitchStatus(int id) {
		System.out.println("Getting status for id " + id);
		String name = config.switchNames[id];
		String key = name + ":" + Quantity.SWITCH.name().toLowerCase();
		String topic = config.mqttTopics.get(key);
		System.out.println("Key is " + key + ", topic is " + topic);
		if (topic == null) return false;
		String s = values.get(topic);
		if (s == null) return false;
		float value = Float.parseFloat(s);
		System.out.println("Value is " + value);
		return value > 0;
	}
}
