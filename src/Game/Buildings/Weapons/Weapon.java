
/*
 * Weapon.java
 *
 * Dec 13, 2015
 */
package Game.Buildings.Weapons;

import Game.Buildings.Building;
import Game.Buildings.Building;
import Game.Buildings.Industry;
import Game.Camera;
import Game.Entities.Entity;
import Game.Interface;
import Game.Main;
import Game.Entities.Projectile;
import Game.Rect;
import static Game.Buildings.Industry.BUILDING_TYPE;
import static Game.Settings.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/* 
 * @author Per Eresund
 */
public abstract class Weapon extends Building {
	public static final int BUILDING_TYPE = 1; 
	
	protected abstract int getCooldown();
	protected abstract void shoot();
	protected abstract int getWeaponType();
	protected abstract void setTarget();
	
	protected int getShotX(Image img) {
		return (int)(getRect().getCenterX() + img.getWidth()/2d * Math.cos(getRotation()));
	}
	protected int getShotY(Image img) {
		return (int)(getRect().getCenterY() + img.getHeight()/2d * Math.sin(getRotation()));
	}
	
	protected int getShotX(int shotlen) {
		return (int)(getRect().getCenterX() + shotlen * Math.cos(getRotation()));
	}
	protected int getShotY(int shotlen) {
		return (int)(getRect().getCenterY() + shotlen * Math.sin(getRotation()));
	}
	
	protected long lastShot;
	protected Entity aimTarget;
	protected Rect reachRect;
	protected double rotation;
	
	public Weapon(String imgPath, Color c) {
		super(32, 32, true, imgPath, c);
	}
	
	
	@Override
	public int getBuildingType() {
		return BUILDING_TYPE;
	}
	
	protected double getRotation() {
		return rotation;
	}

	protected void setRotation(double rotation) {
		this.rotation = rotation % (Math.PI*2D);
	}
	
	@Override
	public double getDrawRotation() {
		if (rotation == 0) return super.getDrawRotation();
		return rotation + getImgRotation();
	}
	
	@Override
	public void update() {
		super.update();
		if(super.isConstructed()) {
			setTarget();
			long time = System.currentTimeMillis();
			if (lastShot+getCooldown() < time) {
				lastShot = time;// - (lastShot+getCooldown());
				shoot();
			}
		}
	}
	
	public static Weapon random(int player) {
		double rand = Math.random();
		
		if(rand < 0.5) return new Gatling(player);
		else return new MissileBattery(player);
	}
}