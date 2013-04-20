package net.geekgrandad.apps;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class HouseChat extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	private JTextArea text = new JTextArea(20,20);
	private int row=0;
	private Socket sock;
	private StringBuilder sb = new StringBuilder();
	
	public HouseChat() {
		getContentPane().add(text,BorderLayout.CENTER);
		setTitle("House chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		text.addKeyListener(this);
		pack();
		setVisible(true);		
	}
	
	public static void main(String[] args) {
		new HouseChat();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// Do nothing	
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// Do nothing	
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		char key = e.getKeyChar();
		if (key == '\n') {
			if (++row >= 18) {
				text.setText("");
				row = 0;				
			}
			//System.out.println("Row: " + row);
			//System.out.println("Command: " + sb);
			try {
				sock = new Socket("localhost", 50000);
			    PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
			    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			    out.println(sb.toString());
			    String ret = in.readLine();
			    text.append("Reply: " + ret + "\n");
			    row++;
			    out.close();
			    in.close();
			    sock.close();
			    sock = null;
			} catch (UnknownHostException e1) {
				System.err.println("Unknown host");
			} catch (IOException e1) {
				System.err.println(e1);
				try {
					if (sock != null) sock.close();
				} catch (IOException e2) {
				}
			}
			
			sb = new StringBuilder();
		} else if (key == 8) {
			sb.setLength(sb.length() - 1);
		} else if (Character.isLetterOrDigit(key) || key == ' ') {
	        sb.append(key);
		}
		
	}
}
