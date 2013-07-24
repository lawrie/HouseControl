package net.geekgrandad.plugin;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;
import net.geekgrandad.interfaces.WeatherControl;

public class WundergroundWeather implements WeatherControl {
	
	private HttpClient client = new DefaultHttpClient();
	private Reporter reporter;
	
	@Override
	public void setProvider(Provider provider) {
		this.reporter = provider.getReporter();		
	}

	@Override
	public float getTemperature() {
		return Float.parseFloat(getData("//current_observation/tempc"));
	}

	@Override
	public float getHumidity() {
		return Float.parseFloat(getData("//current_observation/relative_humidity"));
	}

	@Override
	public String getWeather() {
		return getData("//current_observation/weather");
	}
	
	private String getData(String xpath) {
		HttpGet get = new HttpGet("http://api.wunderground.com/api/c6787d227a0718e7/conditions/q/UK/Manchester.xml");
		HttpResponse response;
		String expression = xpath;
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String result = null;
		try {
			response = client.execute(get);
			String responseString = EntityUtils.toString(response.getEntity());
			reporter.debug("Weather response: " + responseString);
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();	
			ByteArrayInputStream stream = new ByteArrayInputStream(responseString.getBytes("UTF-8"));
			Document document = builder.parse(stream);
			result = xPath.compile(expression).evaluate(document);
			reporter.print("Weather result is " + result);
		} catch (Exception e) {
			reporter.error("Exception in Wunderground Weather: " + e);
		} 
		return result;		
	}
}
