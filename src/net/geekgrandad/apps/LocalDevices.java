package net.geekgrandad.apps;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

public class LocalDevices {

	public static void main(String[] args) throws UnknownHostException, IOException {
		checkHosts("192.168.0");

	}
	
	public static void checkHosts(String subnet) throws UnknownHostException, IOException{
	   int timeout=2000;
	   for (int i=1;i<254;i++){
	       String host=subnet + "." + i;
	       //System.out.println("Checking " + host);
	       if (InetAddress.getByName(host).isReachable(timeout)){
	           System.out.println(host + " mac: " + getMACString(getMACAddress(host)));
	       }
	   }
	}
	
	public static byte[] getMACAddress(String host) {
		try {
			InetAddress address = InetAddress.getByName(host);
			NetworkInterface ni = NetworkInterface.getByInetAddress(address);
			return ni.getHardwareAddress();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getMACString(byte[] mac) {
		if (mac == null) return "n/a";
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<mac.length;i++) {
			String hex=Integer.toHexString(mac[i]);
			if (hex.length() == 8) hex = hex.substring(6);
			sb.append(hex);
			if (i < mac.length-1) sb.append('.');
		}
		
		return sb.toString();
	}

}
