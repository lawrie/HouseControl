package net.geekgrandad.interfaces;

import java.util.Date;

public interface CalendarControl extends Controller {
	
	public String[] getEntries(Date start, Date end);
	
	public void addEntry(String text, Date start, int duration);
	
}
