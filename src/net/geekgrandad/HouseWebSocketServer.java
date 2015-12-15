package net.geekgrandad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.NotYetConnectedException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class HouseWebSocketServer extends WebSocketServer {
	private HouseControl house;

	public HouseWebSocketServer( int port, HouseControl house ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		this.house = house;
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		System.out.println( conn + " disconnected" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println( conn + ": " + message );
		try {
			conn.send(house.parse(message));
		} catch (Exception e) {
			System.err.println("Web socket send error: " + e);
		}
	}

	@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		System.out.println( "received fragment: " + fragment );
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		System.err.println("Error from Websocket");
	}

}
