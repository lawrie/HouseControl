package net.geekgrandad.plugin;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xively.client.XivelyService;
import com.xively.client.http.exception.HttpException;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;

import net.geekgrandad.config.Config;
import net.geekgrandad.interfaces.DatalogControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class XivelyUpdate implements DatalogControl {
	private Reporter reporter;
	private DecimalFormat df = new DecimalFormat("#####.##");
	DateFormat daf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	Feed feed;
	Datastream powerStream, energyStream;
	
	@Override
	public void setProvider(Provider provider) {
		reporter = provider.getReporter();	
		
		try {
			feed  = XivelyService.instance().feed().get(1059467473);
			
			for(Datastream s: feed.getDatastreams()) {
				if (s.getId().equals("power")) powerStream = s;
				if (s.getId().equals("energy")) energyStream = s;
			}
		} catch (HttpException e) {
			reporter.error("Exception gettng Xively feed:" + e);
		}
	}

	@Override
	public void updatePower(double power) {
		try {
			if (powerStream != null) {
				powerStream.setValue(df.format(power));
				powerStream.setUpdatedAt(daf.format(new Date()));
			}
			if (feed != null) XivelyService.instance().feed().update(feed);
		} catch (HttpException e) {
			reporter.error("Exception updating power data stream:" + e);
		}
	}

	@Override
	public void updateEnergy(double energy) {
		// Delay feed update until updatePower is called
		if (energyStream != null) {
			energyStream.setValue(df.format(energy));
			energyStream.setUpdatedAt(daf.format(new Date()));
		}
	}

}
