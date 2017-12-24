
/*
 * Main.java
 *
 * Dec 12, 2015
 */
package Game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

/* 
 * @author Per Eresund
 */
public class Main {
	private static AppGameContainer app;
	public static Game game;
	
	public static void main(String[] args) {
		startApp();
	}
	
	private static void startApp() {
		game = new Game();
		try {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", (Settings.FULLSCREEN ? "true" : "false"));
			app = new AppGameContainer(game);
			app.setUpdateOnlyWhenVisible(false);
			app.setAlwaysRender(true);
			app.setShowFPS(false);
			app.setVSync(true);
			app.setTargetFrameRate(0);
			app.setClearEachFrame(true);
			app.setMultiSample(0);
//			app.setIcon("barrel.png");
			Screen.set(app,	(Settings.FULLSCREEN ? Settings.SCREEN_WIDTH_FULLSCREEN  : Settings.SCREEN_WIDTH_WINDOW ),
							(Settings.FULLSCREEN ? Settings.SCREEN_HEIGHT_FULLSCREEN : Settings.SCREEN_HEIGHT_WINDOW), false);
			app.start();
		} catch (SlickException ex) {}
	}
}