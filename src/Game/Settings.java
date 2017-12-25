   /*
    *   Settings.java
    *
    *   Dec 17, 2015
    */
package Game;

/**
 *
 * @author J
 */
public class Settings {
	
	// -----  Windows Settings  ----- \\
	public static final int SCREEN_WIDTH_WINDOW = 1280;
	public static final int SCREEN_HEIGHT_WINDOW = 720;
	public static final int SCREEN_WIDTH_FULLSCREEN = 1920;
	public static final int SCREEN_HEIGHT_FULLSCREEN = 1080;
	public static final boolean FULLSCREEN = true;
	
	
	// -----  Key controls  ----- \\
	public static final int P1_B1 = 31; // = Input.KEY_S
	public static final int P1_B2 = 32; // = Input.KEY_D
	
	public static final int P2_B1 = 37; // = Input.KEY_K
	public static final int P2_B2 = 38; // = Input.KEY_L
	
	public static final long ACTION_COOLDOWN_MILLIS = 500;
	
	public static final double AI_INDUSTRY_PREFERENCE = 0.4;
	
	
	// -----  Planet sizes  ----- \\
	public static final int SUN_SIZE_FACTOR = 50;
	public static final int PLANET_SIZE_FACTOR = 15;

	public static final int PLANET_SUN_RANGE = 1000;
	public static final int INTERPLANETARY_RANGE = 150;
	
	public static final int PLANET_INTEGRITY_FACTOR = 20;
	
	
	// -----  Orbit speeds  ----- \\
	public static final double BUILDING_ORBIT_SPEED = 100;
	public static final boolean BUILDING_ORBIT_CLOCKWISE = false;
	
	
	// -----  Planet orbit speeds  ----- \\
	public static final double SUN_SPEED = 0.1;
	public static final double GREEN_SPEED = 2;
	public static final double RED_SPEED = 2.7777777;
	public static final double SPEED_MULTIPLIER = 150;
	public static final double SLOWMO_SPEED = 2;
	
	
	
	// -----  Buildings general speccs  ----- \\
	public static final int MAXHP = 50;
	public static final int BUILD_HP = 1;
	public static final int BUILD_TIME = 5000;
	public static final int BUILD_INTERVAL = (int)((double)BUILD_TIME * ((double)BUILD_HP/(double)MAXHP));
	
	public static final long ENERGY_DRAIN_GATLING = 10;
	public static final long ENERGY_DRAIN_BATTERY = 5;
	public static final long ENERGY_DRAIN_FACTORY = 20;
	
	public static final long ENERGY_CAP_POWERCELL = 40;
	
	public static final long MISSILE_CAP_BATTERY = 3;
	public static final long MISSILE_CAP_FACTORY = 5;
	public static final int MISSILE_PRODUCTION_TIME = 2222;
	
	
	// -----  Weapon speccs  ----- \\
	public static final int GATLING_REACH = 555;
	public static final int GATLING_RANGE = GATLING_REACH * 3;
	public static final int GATLING_COOLDOWN = MISSILE_PRODUCTION_TIME / 3;
	public static final int GATLING_DAMAGE = 1;
	public static final double GATLING_SPREAD_RADIANS = Math.PI / 8D;
	public static final double GATLING_PROJECTILE_SPEED = 25D;
	
	public static final int BATTERY_REACH = 1515;
	public static final int BATTERY_RANGE = BATTERY_REACH * 3;
	public static final int BATTERY_COOLDOWN = MISSILE_PRODUCTION_TIME;
	public static final int BATTERY_DAMAGE = 5;
	public static final double BATTERY_SPREAD_RADIANS = Math.PI / 32D;
	public static final double BATTERY_PROJECTILE_SPEED = 10D;
	
	
	
	// -----    ----- \\
	
	// -----    ----- \\
	
}