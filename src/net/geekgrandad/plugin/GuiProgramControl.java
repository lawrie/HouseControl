package net.geekgrandad.plugin;

import java.awt.AWTException;
import java.awt.Robot;

import net.geekgrandad.interfaces.ProgramControl;
import net.geekgrandad.interfaces.Provider;
import net.geekgrandad.interfaces.Reporter;

public class GuiProgramControl implements ProgramControl {
	private Robot robot;
	private Reporter reporter;
	
	@Override
	public void setProvider(Provider provider) {
		this.reporter = provider.getReporter();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void activate(int id, String program) {
		reporter.print("Activating " + program);
		reporter.print("Title is " + ActivateWindow.activate(program));
	}

	@Override
	public void sendKey(int id, int keyCode) {
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
	}
}
