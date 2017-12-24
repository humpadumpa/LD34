   /*
    *   Industry.java
    *
    *   Dec 13, 2015
    */
package Game.Buildings;

import Game.Buildings.Building;
import static Game.Settings.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author J
 */
public abstract class Industry extends Building {
	public static final int BUILDING_TYPE = 0; 
	
	
	public Industry(String imgPath, Color c) {
		super(32, 32, false, imgPath, c);
	}
	
	@Override
	public int getBuildingType() {
		return BUILDING_TYPE;
	}
	
	public static Industry random(int player) {
		double rand = Math.random();
		
		if (rand < 0.5) return new Powercell(player);
		
		else return new Factory(player);
		
	}
	
	public static class Powercell extends Industry {
		public Powercell(int player) {
			super(player == 0 ? POWERCELL_PATH : POWERCELL_P2_PATH, Color.white);
		}

		@Override
		public double getImgRotation() {
			return 0;
		}

	}
	
	public static class Factory extends Industry {
		
		private long timeLastProd;
		
		public Factory(int player) {
			super(player == 0 ? FACTORY_PATH : FACTORY_P2_PATH, Color.white);
			timeLastProd = System.currentTimeMillis();
		}

		@Override
		public void update() {
			if (timeLastProd + MISSILE_PRODUCTION_TIME < System.currentTimeMillis()) {
				if (getTarget().produceMissile()) {
					timeLastProd = System.currentTimeMillis();
				}
			}
			super.update(); //To change body of generated methods, choose Tools | Templates.
		}

		
		@Override
		public double getImgRotation() {
			return 0;
		}
		
	}

}