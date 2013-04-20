package net.geekgrandad.plugin;

import java.text.DecimalFormat;

import Cosm.Cosm;
import Cosm.CosmException;
import Cosm.Datastream;
import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.DatalogControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class CosmUpdate implements DatalogControl {
	private Reporter reporter;
	private Config config;
	private Cosm c;
	private Datastream d;
	private int f;
	private DecimalFormat df = new DecimalFormat("#####.##");
	
	@Override
	public void updatePower(double power) {	
		try {
			d.setCurrentValue("" + ((int) power));
			c.updateDatastream(f, config.cosmPower, d);
		} catch (CosmException e) {
			reporter.error("COSM error updating power: " + e.getMessage());
		}		
	}

	@Override
	public void updateEnergy(double energy) {
		try {
			d.setCurrentValue(df.format(energy));
			c.updateDatastream(f, config.cosmEnergy, d);
		} catch (CosmException e) {
			reporter.error("COSM error updating energy: " + e.getMessage());
		}	
	}

	@Override
	public void setProvider(Provider provider) {
		config = provider.getConfig();
		reporter = provider.getReporter();
		
		c = new Cosm(config.cosmApiKey);
		d = new Datastream();
		f = Integer.parseInt(config.cosmFeed);
	}
}
