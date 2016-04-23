package net.geekgrandad.apps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
  
import java.net.Socket;
import java.net.UnknownHostException;

import javax.bluetooth.*;
import javax.microedition.io.*;
  
/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
public class SPPServer {
    
    //start server
    private void startServer() throws IOException{
  
        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //Create the service url
        String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
        
        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
        
        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        
        for(;;) {
	        StreamConnection connection=streamConnNotifier.acceptAndOpen();
	  
	        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
	        System.out.println("Remote device address: "+dev.getBluetoothAddress());
	        System.out.println("Remote device name: "+dev.getFriendlyName(true));
	        
	        //read string from spp client
	        InputStream inStream=connection.openInputStream();
	        BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
	        
	        OutputStream outStream=connection.openOutputStream();
	        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
	        boolean connected = true;
	        
	        while (connected) {
		        String lineRead=bReader.readLine();
		        if (lineRead == null) {
		        	System.out.println("Connection terminated");
		        	break;
		        }
		        System.out.println(lineRead);
		        Socket sock = null;
		        String host = "192.168.0.100";
		        String ret = "No reply";
		        
				try {
					sock = new Socket(host, 50000);
				    PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
				    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				    out.println(lineRead);
				    ret = in.readLine();
				    System.out.println("Reply is " + ret);
				    out.close();
				    in.close();
				    sock.close();
				    sock = null;
				} catch (UnknownHostException e1) {
					System.err.println("Unknown host");
					connected = false;
				} catch (IOException e1) {
					connected = false;
					System.err.println(e1);
					try {
						if (sock != null) sock.close();
					} catch (IOException e2) {
					}
				}
	        
		        //send response to spp client
		
		        pWriter.write(ret);
		        pWriter.flush();
	        }
		  
	        pWriter.close();	        
        } 
    }
  
  
    public static void main(String[] args) throws IOException {
        
        //display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: "+localDevice.getBluetoothAddress());
        System.out.println("Name: "+localDevice.getFriendlyName());
        
        SPPServer sampleSPPServer=new SPPServer();
        sampleSPPServer.startServer();
        
    }
}
