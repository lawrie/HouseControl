package net.geekgrandad.apps;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class Dashboard extends JFrame {
	private static final long serialVersionUID = 4427504253834299967L;
	private static final Color background = Color.WHITE;
	
	private Socket sock;
	private String host = "localhost";
	private JLabel power, temperature, light, occupied, dishWasher, bedMedia, xbox, dryer, washer;
	private JPanel panel;
	private Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);;

	public Dashboard() {
		super("Dashboard");
		setAlwaysOnTop( true );
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(200,600));
		panel.setBackground(background);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		setContentPane(panel);
		
		power = detail("Power");
		temperature = detail("Temperature");
		light = detail("Light level");
		occupied = detail("Occupied");
		dishWasher = detail("Dish washer");
		dryer = detail("Dryer");
		washer = detail("Washer");
		bedMedia = detail("Bed media");
		xbox = detail("XBox");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	// Nothing yet
	        }
	    });
	}
	
	public JLabel detail(String label) {
		JPanel p = new JPanel();
		p.setBorder(raisedEtched);
		p.setMaximumSize(new Dimension(150,30));
		JLabel l = new JLabel(label+":");
		p.add(l);
		JLabel v = new JLabel("n/a");
		p.add(v);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(p);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		return v;
	}
	
	public void run() {
		for(;;) {
			power.setText(get("power"));
			temperature.setText(get("temperature 3"));
			light.setText(get("lightlevel 2"));
			occupied.setText(get("occupied 3").equals("on") ? "yes" : "no");
			dishWasher.setText(get("Dishwasher value"));
			bedMedia.setText(get("Bedmedia value"));
			xbox.setText(get("Xbox value"));
			dryer.setText(get("Dryer value"));
			washer.setText(get("Washer value"));
			revalidate();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
	}
	
	public String get(String msg) {
		try {
			sock = new Socket(host, 50000);
		    PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		    out.println(msg);
		    String ret = in.readLine();
		    out.close();
		    in.close();
		    sock.close();
		    sock = null;
		    return ret;
		} catch (UnknownHostException e1) {
			System.err.println("Unknown host");
			return null;
		} catch (IOException e1) {
			System.err.println(e1);
			try {
				if (sock != null) sock.close();
			} catch (IOException e2) {
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		Dashboard dash = new Dashboard();
		dash.pack();
		dash.setVisible(true);
		dash.run();
	}
}
