package net.geekgrandad.plugin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.ApplianceControl;
import net.geekgrandad.interfaces.MQTT;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Quantity;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.plugin.JeenodeControl.ReadInput;

public class WemoControl implements ApplianceControl {
	private Reporter reporter;
    private SOAPConnectionFactory soapConnectionFactory;
    private SOAPConnection soapConnection;
    private String basicEventUrl;
    private String insightUrl;
    private Config config;
    private URLStreamHandler handler;
    private static final int START_PORT = 49152;
    private static final int END_PORT = 49154;
    private static final int POLL_INTERVAL = 30;
    private MQTT mqtt;
    private Thread publishThread = new Thread(new Publish());
    private int[] ports = new int[Config.MAX_APPLIANCES];
    
	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();
		config = provider.getConfig();
		mqtt = provider.getMQTTControl();
		for(int i=0;i<Config.MAX_APPLIANCES;i++) ports[i] = START_PORT;
		try {
			soapConnectionFactory = SOAPConnectionFactory.newInstance();
			soapConnection = soapConnectionFactory.createConnection();
			handler = new URLStreamHandler() {
	    		@Override
	    		protected URLConnection openConnection(URL url) throws IOException {
		    		URL clone_url = new URL(url.toString());
		    		HttpURLConnection clone_urlconnection = (HttpURLConnection) clone_url.openConnection();
		    		clone_urlconnection.setConnectTimeout(1000);
		    		clone_urlconnection.setReadTimeout(1000);
		    		return(clone_urlconnection);
	    		}
		};
		} catch (UnsupportedOperationException e) {
			reporter.error(e.getMessage());
		} catch (SOAPException e) {
			reporter.error(e.getMessage());
		}
		publishThread.setDaemon(true);
		publishThread.start();
	}

	@Override
	public void switchAppliance(int socket, boolean on) throws IOException {
		reporter.error("Switching Wemo device not yet supported");	
	}

	@Override
	public boolean getApplianceStatus(int appliance) {
		for(int i=0;i<(END_PORT-START_PORT) + 1;i++) {
			basicEventUrl = "http://" + config.applianceHostNames[appliance] + ":" + ports[appliance] + "/upnp/control/basicevent1";
			try {
				SOAPMessage soapResponse = soapConnection.call(createBinaryStateRequest(), new URL(null, basicEventUrl, handler));
				SOAPBody msg = soapResponse.getSOAPBody();
		        int val = Integer.parseInt(msg.getTextContent().trim());
		        return (val == 1);
			} catch (Exception e) {
				if (e.getCause().getCause() instanceof SocketTimeoutException) {
					if (++ports[appliance] > END_PORT) ports[appliance] = START_PORT;
				} else reporter.error(e.getMessage());
			}
		}
		return false;
	}

	@Override
	public int getAppliancePower(int appliance) {
		for(int i=0;i<3;i++) {
			insightUrl = "http://" + config.applianceHostNames[appliance] + ":" + ports[appliance] + "/upnp/control/insight1";
			//System.out.println("Trying " + insightUrl);
			try {
				SOAPMessage soapResponse = soapConnection.call(createInsightParamsRequest(), new URL(null, insightUrl, handler));
				SOAPBody msg = soapResponse.getSOAPBody();
		        String value = msg.getTextContent().trim();
		        String[] array = value.split("\\|"); 
		        
		        String power = array[7];
		        return  (int) (Math.round(Integer.parseInt(power)/1000.0));
			} catch (Exception e) {
				if (++ports[appliance] > END_PORT) ports[appliance] = START_PORT;
				if (e.getCause() instanceof SocketTimeoutException || e.getCause().getCause() instanceof SocketTimeoutException) {
					reporter.error("Wemo on port " +  ports[appliance]  + " timed out");		
				} else reporter.error(e.getMessage());
			}
		}
		return 0;
	}
	
    private SOAPMessage createBinaryStateRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "urn:Belkin:service:basicevent:1";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("u", serverURI);

        SOAPBody soapBody = envelope.getBody();
        soapBody.addChildElement("GetBinaryState", "u");

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "\"urn:Belkin:service:basicevent:1#GetBinaryState\"");

        soapMessage.saveChanges();

        return soapMessage;
    }
	
    private SOAPMessage createInsightParamsRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "urn:Belkin:service:insight:1";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("u", serverURI);

        SOAPBody soapBody = envelope.getBody();
        soapBody.addChildElement("GetInsightParams", "u");

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "\"urn:Belkin:service:insight:1#GetInsightParams\"");

        soapMessage.saveChanges();

        return soapMessage;
    }
    
    class Publish implements Runnable {

		@Override
		public void run() {
			// Poll for power 
			for(;;) {
				for(int i=0;i<Config.MAX_APPLIANCES;i++) {
					if (config.applianceTypes[i].equals("Wemo")) {
						String name = config.applianceNames[i];
						String key = name + ":" + Quantity.POWER.name().toLowerCase();
						String topic = config.mqttTopics.get(key);
						int power = getAppliancePower(i);
						reporter.print("Wemo: publishing Name: " + name + " , topic : " + topic + " , power: " + power);
						//System.out.println("Wemo: publishing Name: " + name + " , topic : " + topic + " , power: " + power);
						if (topic != null )
							mqtt.publish(topic, "" + power, 0);						
					}
				}
				try {
					Thread.sleep(POLL_INTERVAL * 1000);
				} catch (InterruptedException e) {}
			}
			
		}
    	
    }
}
