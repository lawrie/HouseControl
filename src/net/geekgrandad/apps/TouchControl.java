package net.geekgrandad.apps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class TouchControl extends JFrame {
	private static final long serialVersionUID = 4081752910623037903L;
	
	private Socket sock;
	private String host;
	private JTabbedPane tabs = new JTabbedPane();
	private JPanel rooms = new JPanel(),
			info = new JPanel(),
			control = new JPanel(),
			electricity = new JPanel(),
			tv =  new JPanel(),
			sockets = new JPanel(),
			lights = new JPanel(),
			livingRoom = new JPanel(), diningRoom = new JPanel(),
			kitchen = new JPanel(), hall = new JPanel(),
			bathroom = new JPanel(),
			landing = new JPanel(), utilityRoom = new JPanel(),
			masterBedroom = new JPanel(), secondBedroom = new JPanel();
	private Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	private ArrayList<String> commands = new ArrayList<String>();
	private ArrayList<String> suffices = new ArrayList<String>();
	private ArrayList<JLabel> details = new ArrayList<JLabel>();
	private String[] channels = {"BBC 1", "BBC 2", "ITV 1", "Channel 4", "Five", "BBC 3", "BBC 4", "BBC 1 HD", "Sky 1 HD", "Sky 1",
			"Sky Liv HD", "Sky Living", "ITV HD", "ITV + 1", "ITV 2", "ITV 2 + 1", "ITV 3", "ITV 4", "ITV Be", "ITV Be + 1",
			"Sky 2", "Sky Arts", "Pick", "Watch", "Watch +1", "Gold", "Gold + 1", "Dave", "Dave ja vu", "Alibi", "Alibi + 1",
			"Comedy Central", "Comedy Central + 1", "MTV", "Syfy", "Syfy + 1", "Universal", "Universal + 1", "Challenge",
			"Sky Liv + 1", "4 HD", "C4 + 1", "E4", "E4 HD", "E4 + 1", "More 4", "CBS Reality", "Horror", "Chan 5 HD", "5*",
			"Sky Sports News HQ", "Sky Sports 1", "Sky Sports 2", "Sky Sports 3", "Sky Sports 4", "Sky Sports 5",
			"Sky Sports F1", "Euro Sports HD", "Euro Sports 2 HD", "Euro Sports", "Euro Sports 2", "Extreme",
			"BT Sports HD"};
	private int[] channelNumbers = {101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115,
			116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135,
			136, 137, 138, 139, 140, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 511, 512, 513, 514, 515,
			516, 517, 521, 522, 523, 524, 525, 527};
	private JComboBox<String> programs = new JComboBox<String>(channels);

	public TouchControl(String host) {
		super("Touch Control");
		//setAlwaysOnTop( true );
		this.host = host;
		tabs.setPreferredSize(new Dimension(780, 400));
		tabs.addTab("Control", control);
		tabs.addTab("Information", info);
		tabs.addTab("Rooms", rooms);
		setContentPane(tabs);
		
		TitledBorder lvBorder = BorderFactory.createTitledBorder("living room");
		lvBorder.setTitleJustification(TitledBorder.CENTER);
		livingRoom.setBorder(lvBorder);
		livingRoom.setPreferredSize(new Dimension(150, 130));
		rooms.add(livingRoom);
		
		details.add(detail("Temperature", "temperature 3", " C", livingRoom));
		details.add(detail("Light level", "lightlevel 3", " %", livingRoom));
		details.add(detail("Occupied", "occupied 3", "", livingRoom));
		
		TitledBorder kitchenBorder = BorderFactory.createTitledBorder("kitchen");
		kitchenBorder.setTitleJustification(TitledBorder.CENTER);
		kitchen.setBorder(kitchenBorder);
		kitchen.setPreferredSize(new Dimension(150, 130));
		rooms.add(kitchen);
		
		details.add(detail("Temperature", "temperature 2", " C", kitchen));
		details.add(detail("Light level", "lightlevel 2", " %", kitchen));
		details.add(detail("Occupied", "occupied 2", "", kitchen));
		
		TitledBorder hallBorder = BorderFactory.createTitledBorder("hall");
		hallBorder.setTitleJustification(TitledBorder.CENTER);
		hall.setBorder(hallBorder);
		hall.setPreferredSize(new Dimension(150, 130));
		rooms.add(hall);
		
		details.add(detail("Temperature", "temperature 1", " C", hall));
		details.add(detail("Light level", "lightlevel 1", " %", hall));
		details.add(detail("Occupied", "occupied 1", "", hall));
		
		TitledBorder landingBorder = BorderFactory.createTitledBorder("landing");
		landingBorder.setTitleJustification(TitledBorder.CENTER);
		landing.setBorder(landingBorder);
		landing.setPreferredSize(new Dimension(150, 130));
		rooms.add(landing);
		
		details.add(detail("Temperature", "temperature 1", " C", landing));
		details.add(detail("Light level", "lightlevel 1", " %", landing));
		details.add(detail("Occupied", "occupied 1", "", landing));
		
		TitledBorder masterBorder = BorderFactory.createTitledBorder("master bedroom");
		masterBorder.setTitleJustification(TitledBorder.CENTER);
		masterBedroom.setBorder(masterBorder);
		masterBedroom.setPreferredSize(new Dimension(150, 130));
		rooms.add(masterBedroom);
		
		details.add(detail("Temperature", "radio temperature", " C", masterBedroom));
		details.add(detail("Light level", "radio lightlevel", " %", masterBedroom));
		
		TitledBorder secondBorder = BorderFactory.createTitledBorder("second bedroom");
		secondBorder.setTitleJustification(TitledBorder.CENTER);
		secondBedroom.setBorder(secondBorder);
		secondBedroom.setPreferredSize(new Dimension(150, 130));
		rooms.add(secondBedroom);
		
		details.add(detail("Temperature", "espsecond temperature", " C", secondBedroom));
		details.add(detail("Humidity", "espsecond humidity", " %", secondBedroom));
		
		TitledBorder utilityBorder = BorderFactory.createTitledBorder("utility room");
		utilityBorder.setTitleJustification(TitledBorder.CENTER);
		utilityRoom.setBorder(utilityBorder);
		utilityRoom.setPreferredSize(new Dimension(150, 130));
		rooms.add(utilityRoom);
		
		details.add(detail("Temperature", "esputil temperature", " C", utilityRoom));
		details.add(detail("Pressure", "esputil pressure", "", utilityRoom));
		
		TitledBorder bathroomBorder = BorderFactory.createTitledBorder("bathroom");
		bathroomBorder.setTitleJustification(TitledBorder.CENTER);
		bathroom.setBorder(bathroomBorder);
		bathroom.setPreferredSize(new Dimension(150, 130));
		rooms.add(bathroom);
		
		details.add(detail("Temperature", "enoceant temperature", " C", bathroom));
		
		TitledBorder diningBorder = BorderFactory.createTitledBorder("dining room");
		diningBorder.setTitleJustification(TitledBorder.CENTER);
		diningRoom.setBorder(diningBorder);
		diningRoom.setPreferredSize(new Dimension(150, 130));
		rooms.add(diningRoom);
		
		TitledBorder elecBorder = BorderFactory.createTitledBorder("electricity");
		elecBorder.setTitleJustification(TitledBorder.CENTER);
		electricity.setBorder(elecBorder);
		electricity.setPreferredSize(new Dimension(240, 360));
		info.add(electricity);
		
		details.add(detail("Power", "power", "watts", electricity));
		details.add(detail("Dish washer", "dishwasher value", " watts", electricity));
		details.add(detail("Dryer", "dryer value", " watts", electricity));
		details.add(detail("Washer", "washer value", " watts", electricity));
		details.add(detail("Bedroom", "bedmedia value", " watts", electricity));
		details.add(detail("Games", "xbox value", " watts", electricity));
		details.add(detail("Media", "fridge value", " watts", electricity));
		
		TitledBorder tvBorder = BorderFactory.createTitledBorder("tv");
		tvBorder.setTitleJustification(TitledBorder.CENTER);
		tv.setBorder(tvBorder);
		tv.setPreferredSize(new Dimension(240, 360));
		control.add(tv);
		
		tv.add(button("TV On/off", "tv on"));
		tv.add(button("TiVO On/off", "vt on"));
		tv.add(button("AV On/off", "av on"));
		tv.add(button("Mute On/off", "av mute"));
		tv.add(button("Volup", "volume up"));
		tv.add(button("Vol down", "volume down"));
		tv.add(button("Chan up", "channel up"));
		tv.add(button("Chan down", "channel down"));
		
		programs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				get("channel " + channelNumbers[programs.getSelectedIndex()]);	
			}
		});
		tv.add(new JLabel("Channel:"));
		tv.add(programs);
		
		tv.add(button("Home", "vt home"));
		tv.add(button("/\\", "vt up"));
		tv.add(button("My shows", "vt shows"));
		tv.add(button("<", "vt left"));
		tv.add(button("OK", "vt ok"));
		tv.add(button(">", "vt right"));
		tv.add(button("Play", "vt play"));
		tv.add(button("Pause", "vt pause"));
		tv.add(button("\\/", "vt down"));
		tv.add(button("Record", "vt record"));
		tv.add(button("Stop", "vt stop"));
		tv.add(button("Back to TV", "vt broadcast"));
		tv.add(button("Last Ch", "vt lastch"));
		tv.add(button("FF", "vt ff"));
		tv.add(button("FB", "vt fb"));
		tv.add(button("Clear", "vt clear"));
		
		TitledBorder socketsBorder = BorderFactory.createTitledBorder("sockets");
		socketsBorder.setTitleJustification(TitledBorder.CENTER);
		sockets.setBorder(socketsBorder);
		sockets.setPreferredSize(new Dimension(240, 360));
		control.add(sockets);
		
		sockets.add(button("Fan on", "fan on"));
		sockets.add(button("Fan off", "fan off"));
		sockets.add(button("Camera on", "spy on"));
		sockets.add(button("Camera off", "spy off"));
		sockets.add(button("Guitar on", "guitar on"));
		sockets.add(button("Guitar off", "guitar off"));
		sockets.add(button("Banjolele on", "banjolele on"));
		sockets.add(button("Banjolele off", "banjolele off"));
		sockets.add(button("Desktop on", "print on"));
		sockets.add(button("Desktop off", "print off"));
		sockets.add(button("Table on", "coffee on"));
		sockets.add(button("Table off", "coffee off"));
		sockets.add(button("Bench on", "bench on"));
		sockets.add(button("Bench off", "bench off"));
		sockets.add(button("Spare on", "trans on"));
		sockets.add(button("Spare off", "trans off"));
		sockets.add(button("Games on", "xbox on"));
		sockets.add(button("Games off", "xbox off"));
		sockets.add(button("Bedroom on", "bedmedia on"));
		sockets.add(button("Bedroom off", "bedmedia off"));
		
		TitledBorder lightsBorder = BorderFactory.createTitledBorder("lights");
		lightsBorder.setTitleJustification(TitledBorder.CENTER);
		lights.setBorder(lightsBorder);
		lights.setPreferredSize(new Dimension(240, 360));
		control.add(lights);
		
		lights.add(button("Lights on", "lights on"));
		lights.add(button("Lights off", "lights off"));
		lights.add(button("front on", "pollock on"));
		lights.add(button("front off", "pollock off"));
		lights.add(button("back on", "picasso on"));
		lights.add(button("back off", "picasso off"));
		lights.add(button("bedroom left on", "world on"));
		lights.add(button("bedroom left off", "world off"));
		lights.add(button("bedroom right on", "periodic on"));
		lights.add(button("bedroom right off", "periodic off"));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	// Nothing yet
	        }
	    });
	}
	
	public JLabel detail(String label, String cmd, String suffix, JPanel panel) {
		JPanel p = new JPanel();
		p.setBackground(new Color(0x32C9D1));
		p.setBorder(raisedEtched);
		p.setMaximumSize(new Dimension(200,30));
		JLabel l = new JLabel(label+":");
		p.add(l);
		JLabel v = new JLabel("n/a");
		v.setForeground(new Color(0xFA8507));
		p.add(v);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(p);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		commands.add(cmd);
		suffices.add(suffix);
		return v;
	}
	
	public JButton button(String label, final String cmd) {
		JPanel p = new JPanel();
		p.setBackground(new Color(0x32C9D1));
		p.setBorder(raisedEtched);
		p.setMaximumSize(new Dimension(200,30));
		JButton b = new JButton(label);
		p.add(b);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Result from " + cmd + " is " + get(cmd));
			}
		});
		return b;
	}
	
	public void run() {
		for(;;) {
			try {
				for(int i=0;i<commands.size();i++) {
					String s = get(commands.get(i)) + suffices.get(i);
					details.get(i).setText(s);
				}
				revalidate();
				Thread.sleep(3000);
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
	
	public String get(String msg) {
		DecimalFormat df = new DecimalFormat("0.0");
		try {
			sock = new Socket(host, 50000);
		    PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		    out.println(msg);
		    out.flush();
		    String ret = in.readLine();
			try {
				Double d  = Double.parseDouble(ret);
				ret = df.format(d);
			} catch (NumberFormatException e) {
				if (ret.equals("on")) ret = "yes";
				if (ret.equals("off")) ret = "no";
			}
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
		TouchControl touch = new TouchControl(args.length > 0 ? args[0] : "localhost");
		touch.pack();
		touch.setVisible(true);
		touch.run();
	}
}
