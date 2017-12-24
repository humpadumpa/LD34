   /*
    *   AI_masterDrone.java
    *
    *   Dec 15, 2015
    */
package Game.AI;

import static Game.Settings.*;
import Game.Interface;
import org.newdawn.slick.KeyListener;

/**
 *
 * @author J
 */
public class AI_masterDrone {

	private static KeyListener interfaceKeyListener;

	private int AI_B1, AI_B2;
	
	public AI_masterDrone(int AI_B1, int AI_B2) {
		this.AI_B1 = AI_B1;
		this.AI_B2 = AI_B2;
	}
	
	public void update() {
		if(Math.random() < AI_INDUSTRY_PREFERENCE) {
			interfaceKeyListener.keyReleased(AI_B1, 'a');
		}
		else {
			interfaceKeyListener.keyReleased(AI_B2, 'b');
		}
	}
	
	public static void init(KeyListener interfaceHaxxor) {
		interfaceKeyListener = interfaceHaxxor;
	}
}
