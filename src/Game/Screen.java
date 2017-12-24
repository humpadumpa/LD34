
/*
 * Screen.java
 *
 * Dec 12, 2015
 */
package Game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/* 
 * @author Per Eresund
 */
public class Screen {
	private static int w, h;
	private static boolean fullscreen;
	
	public static int width()			{ return w; }
	public static int height()			{ return h; }
	public static boolean fullscreen()	{ return fullscreen; }
	
	private static void resetDisplay(AppGameContainer app) throws SlickException {
		app.setDisplayMode(w, h, fullscreen);
	}
	public static void set(AppGameContainer app, int w, int h, boolean fullscreen) throws SlickException {
		Screen.w = w;
		Screen.h = h;
		Screen.fullscreen = fullscreen;
		resetDisplay(app);
	}
	
}