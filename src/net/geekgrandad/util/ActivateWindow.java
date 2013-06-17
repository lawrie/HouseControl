package net.geekgrandad.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.platform.win32.User32;

public class ActivateWindow {
  private static HWND wind;
  public static String title;
  
  public static HWND findWind(final String name) {
	  final User32 user32 = User32.INSTANCE;
	  title=null;
	  wind = null;
	  user32.EnumWindows(new WNDENUMPROC() {
	      public boolean callback(HWND hWnd, Pointer arg1) {
	          char[] windowText = new char[512];
	          user32.GetWindowText(hWnd, windowText, 512);
	          String wText = Native.toString(windowText);
	          if (wText.contains(name)) {
	        	  title = wText;
	        	  wind = hWnd;
	        	  return false;
	          }
	          return true;
	      }
	  }, null);
	  return wind;
  }

  public static String activate(String app) {
    User32 user32 = User32.INSTANCE;
    HWND hWnd = findWind(app);
    user32.ShowWindow(hWnd, User32.SW_SHOWMAXIMIZED);
    user32.SetForegroundWindow(hWnd);
    return title;
  }
  
  public static String getTitle(String app) {
    findWind(app);
    return title;
  }
  
  public static void main(String[] args) {
	  System.out.println(activate("Spotify"));
  }
}

