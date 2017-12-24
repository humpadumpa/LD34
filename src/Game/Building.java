
/*
 * Building.java
 *
 * Dec 13, 2015
 */
package Game;

import static Game.Settings.*;
import Game.Planet.Team;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/* 
 * @author Per Eresund
 */
public abstract class Building extends Orbital {
	private static final Color drawColor = new Color(1f, 1f, 1f, 1f);
	
	public static final String GATLING_PATH = "/src/res/img/gatlingTurret.png";
	public static final String BATTERY_PATH = "/src/res/img/missileBattery.png";
	public static final String POWERCELL_PATH = "/src/res/img/powercell.png";
	public static final String FACTORY_PATH = "/src/res/img/missileFactory.png";
	
	public static final String GATLING_P2_PATH = "/src/res/img/gatlingTurretRed.png";
	public static final String BATTERY_P2_PATH = "/src/res/img/missileBatteryRed.png";
	public static final String POWERCELL_P2_PATH = "/src/res/img/powercellRed.png";
	public static final String FACTORY_P2_PATH = "/src/res/img/missileFactoryRed.png";
	
//	private static final int MAXHP = 10;
//	private static final int BUILD_HP = 1;
//	private static final int BUILD_TIME = 10000;
//	private static final int BUILD_INTERVAL = (int)((double)BUILD_TIME * ((double)BUILD_HP/(double)MAXHP));
//	
//	public static final long MISSILE_PRODUCTION_TIME = 2000;
	
	private Team team;
	private Rect rect;
	
	public Image img;
	public Color c;
	
	private int hp;
	private boolean isConstructed;
	private double timeAtLastHPtick;
	private boolean isAlive;
	
	public Building(int w, int h, boolean isCircle, String imgPath, Color c) {
		try {
			this.img = new Image(imgPath);
		} catch (SlickException ex) {
			Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
		}
		isAlive = true;
		
		if (c == null) this.c = new Color(1f, 1f, 1f, 1f);
		else this.c = c;
		
		hp = 0;
		timeAtLastHPtick = System.currentTimeMillis();
		isConstructed = false;
		rect = new Rect(0, 0, w, h, isCircle);
		setSize(w, h);
	}
	
	public Building() {
		rect = new Rect(0, 0, 0, 0, false);
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public abstract int getBuildingType();
	
	@Override
	public boolean exists() {
		return isAlive;
	}
	
	@Override
	public boolean collideWith(int damage) {
		return takeDamage(damage);
	}
	
	public void setImage(Image img, Color c) {
		setImage(img, c, img.getWidth(), img.getHeight());
	}
	
	public void setImage(Image img, Color c, int w, int h) {
		this.img = img;
		this.c = c;
		setSize(w, h);
	}
	
	public Image getImage() {
		return img;
	}
	
	public int getRadius() {
		return rect.w/2;
	}
	
	@Override
	public Rect getRect() {
		return rect;
	}
	
	/**
	 * Width & height must have been set for this to work as intended when using centered.
	 */
	@Override
	public void setPos(int x, int y, boolean centered) {
		setPos(x, y, rect.w, rect.h, centered);
	}

	public void setPos(int x, int y, int w, int h, boolean centered) {
		rect.x = x;
		rect.y = y;
		rect.w = w;
		rect.h = h;
		if (centered) {
			rect.x -= rect.w/2;
			rect.y -= rect.h/2;
		}
	}
	
	public void setSize(int w, int h) {
		rect.w = w;
		rect.h = h;
		img = img.getScaledCopy(w, h);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(!isConstructed) {
			if(construction()) {
				isConstructed = true;
				getTarget().constructionComplete(this);
			}
		}
	}

	@Override
	public void render(Graphics g, Camera cam) {
//		drawColor.r = (float)hp / (float)MAXHP;
		drawColor.g = (float)hp / (float)MAXHP;
		drawColor.b = (float)hp / (float)MAXHP;
		img.setRotation((float)(Math.toDegrees(getDrawRotation())));
		g.drawImage(img, cam.getRenderX(rect.x), cam.getRenderY(rect.y), drawColor);
		
		
//		try {
//			Image missile = new Image("src/res/img/interplanetaryMissile.png");
//			Projectile p = new Projectile(cam.getRect().x + (int)(Math.random() * cam.getRect().w), cam.getRect().y + (int)(Math.random() * cam.getRect().h), missile.getWidth(), missile.getHeight(), 5, super.getRadians(), c, missile, Math.PI/4D, 1, Projectile.TYPE_MISSILE, new Team());
//			Main.game.addEntity(p);
//		} catch (SlickException ex) {
//			System.out.println("Failed loading image src/res/img/interplanetaryMissile.png");
//		}
	}
	
	public boolean isConstructed() {
		return isConstructed;
	}
	
	private boolean construction() {
		if(timeAtLastHPtick + BUILD_INTERVAL <= System.currentTimeMillis()) {
			hp += BUILD_HP;
			timeAtLastHPtick = System.currentTimeMillis();
		}
		return hp == MAXHP;
	}
	
	public boolean takeDamage(int damage) {
		hp -= damage;
		if(hp <= 0) {
			isAlive = false;
			getTarget().buildingDestroyed(this);
		}
		return isAlive;
	}
	
	public int getHp() {
		return hp;
	}
}