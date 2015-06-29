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
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class WemoControl implements ApplianceControl {
	private Reporter reporter;
    private SOAPConnectionFactory soapConnectionFactory;
    private SOAPConnection soapConnection;
    private String basicEventUrl;
    private String insightUrl;
    private Config config;
    private URLStreamHandler handler;
    private int port = 49152;
    
	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();
		config = provider.getConfig();
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
	}

	@Override
	public void switchAppliance(int socket, boolean on) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getApplianceStatus(int appliance) {
		for(int i=0;i<3;i++) {
			basicEventUrl = "http://" + config.applianceHostNames[appliance] + ":" + port + "/upnp/control/basicevent1";
			try {
				SOAPMessage soapResponse = soapConnection.call(createBinaryStateRequest(), new URL(null, basicEventUrl, handler));
				SOAPBody msg = soapResponse.getSOAPBody();
		        int val = Integer.parseInt(msg.getTextContent().trim());
		        return (val == 1);
			} catch (Exception e) {
				if (e.getCause().getCause() instanceof SocketTimeoutException) {
					if (++port > 49154) port = 49152;
				} else reporter.error(e.getMessage());
			}
		}
		return false;
	}

	@Override
	public int getAppliancePower(int appliance) {
		for(int i=0;i<3;i++) {
			insightUrl = "http://" + config.applianceHostNames[appliance] + ":" + port + "/upnp/control/insight1";
			System.out.println("Trying + " + insightUrl);
			try {
				SOAPMessage soapResponse = soapConnection.call(createInsightParamsRequest(), new URL(null, insightUrl, handler));
				SOAPBody msg = soapResponse.getSOAPBody();
		        String value = msg.getTextContent().trim();
		        String[] array = value.split("\\|"); 
		        
		        String power = array[7];
		        return  (int) (Math.round(Integer.parseInt(power)/1000.0));
			} catch (Exception e) {
				if (e.getCause().getCause() instanceof SocketTimeoutException) {
					if (++port > 49154) port = 49152;
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
}
