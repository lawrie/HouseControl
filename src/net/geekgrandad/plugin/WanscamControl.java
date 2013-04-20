package net.geekgrandad.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.geekgrandad.interfaces.CameraControl;
import net.geekgrandad.interfaces.Provider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class WanscamControl implements CameraControl {
	private HttpGet upUrl, downUrl, leftUrl, rightUrl;
	private HttpClient client = new DefaultHttpClient();
	private String host;
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setProvider(Provider provider) {
		host = provider.getConfig().cameraHost;
		upUrl = new HttpGet("http://" + host + "/decoder_control.cgi?onestep=1&command=0&user=admin&pwd=");
		downUrl = new HttpGet("http://" + host + "/decoder_control.cgi?onestep=1&command=1&user=admin&pwd=");
		leftUrl = new HttpGet("http://" + host + "/decoder_control.cgi?onestep=1&command=4&user=admin&pwd=");
		rightUrl = new HttpGet("http://" + host + "/decoder_control.cgi?onestep=1&command=6&user=admin&pwd=");
	}
	
	@Override
	public void panUp() throws IOException {
		HttpResponse r = client.execute(upUrl);
		print(getBody(r));
	}

	@Override
	public void panDown() throws IOException {
		HttpResponse r = client.execute(downUrl);
		print(getBody(r));
	}
	@Override
	public void panLeft() throws IOException {
		HttpResponse r = client.execute(leftUrl);
		print(getBody(r));
	}

	@Override
	public void panRight() throws IOException {
		HttpResponse r = client.execute(rightUrl);
		print(getBody(r));
	}
	
	public String getBody(HttpResponse response) throws IOException {
		StringBuilder builder = new StringBuilder();				
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		return builder.toString();		
	}
	
	public void print(String msg) {
		System.out.println(msg);
	}
	
	public static void main(String[] args) throws Exception {
		WanscamControl c = new WanscamControl();
		c.setHost("ipcam:99");
		
		Thread.sleep(5000);
		
		for(int i=0;i<10;i++) c.panUp();
		for(int i=0;i<10;i++) c.panDown();
		for(int i=0;i<10;i++) c.panLeft();
		for(int i=0;i<10;i++) c.panRight();
	}

}
