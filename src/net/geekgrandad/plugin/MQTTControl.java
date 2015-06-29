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

	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();
		config = provider.getConfig();
		
		MemoryPersistence persistence = new MemoryPersistence();
		String broker = provider.getConfig().mqttServer;
		String clientId = "housecontrol";
		Collection<String> topics = config.mqttTopics.values();
		
        try {
            client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            reporter.print("Connecting to MQTT broker: "+ broker);
            client.connect(connOpts);
            reporter.print("Connected to MQTT broker");
	        client.setCallback(this);
	        for(String topic: topics) {
	        	subscribe(topic);
	        }
	        
	        
        } catch(MqttException me) {
        	reporter.error("Failed to connect to MQTT broker");
        }
	}

	@Override
	public void publish(String topic, String content, int qos) {
        reporter.debug("Publishing message: " + content);
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
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		reporter.debug("MQTT delivery of " + token + " complete");	
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		reporter.print("MQTT message arrived: " + topic + ":" + message);
		values.put(topic, message.toString());
		lastMessage = System.currentTimeMillis();
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