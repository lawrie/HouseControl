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

import net.geekgrandad.interfaces.MQTT;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class MQTTControl implements MQTT, MqttCallback {
	private Reporter reporter;
	private MqttClient client;
	private HashMap<String,String> values = new HashMap<String,String>();

	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();
		
		MemoryPersistence persistence = new MemoryPersistence();
		String broker = provider.getConfig().mqttServer;
		String clientId = "housecontrol";
		Collection<String> topics = provider.getConfig().mqttTopics.values();
		
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
	}

	@Override
	public String getValue(String topic) {
		reporter.print("Getting value for " + topic + " returned " + values.get(topic));
		return values.get(topic);
	}
}
