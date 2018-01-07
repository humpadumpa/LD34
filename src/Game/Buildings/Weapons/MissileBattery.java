/*
 * MissileBattery.java
 *
 * Dec 25, 2017
 */
package Game.Buildings.Weapons;

import static Game.Buildings.Building.BATTERY_P2_PATH;
import static Game.Buildings.Building.BATTERY_PATH;
import Game.Buildings.Industry;
import Game.Camera;
import Game.Entities.Entity;
import Game.Entities.Projectile;
import Game.Interface;
import Game.Main;
import Game.Rect;
import static Game.Settings.BATTERY_COOLDOWN;
import static Game.Settings.BATTERY_DAMAGE;
import static Game.Settings.BATTERY_PROJECTILE_SPEED;
import static Game.Settings.BATTERY_RANGE;
import static Game.Settings.BATTERY_REACH;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * 
 * @author Per Eresund
 */
public class MissileBattery extends Weapon {
	public static final int TYPE = 1;

	public MissileBattery(int player) {
		super(player == 0 ? BATTERY_PATH : BATTERY_P2_PATH, Color.white);
		reachRect = new Rect(0, 0, BATTERY_REACH*2, BATTERY_REACH*2, true);
	}

	@Override
	public double getImgRotation() {
		return Math.PI/4D;
	}

	@Override
	public int getCooldown() {
		return BATTERY_COOLDOWN;
	}

	@Override
	public int getWeaponType() {
		return TYPE;
	}

	@Override
	protected void setTarget() {
		reachRect.x = super.getRect().getCenterX() - reachRect.w/2;
		reachRect.y = super.getRect().getCenterY() - reachRect.h/2;
		
		Entity target = null;
		int i = 0, choices = 3;
		while (target == null && i < choices) {
			switch(i) {
				case 0:
					target = Main.game.findClosestPlanet(this);
					break;
				case 1:
					target = Main.game.findClosestBuilding(this, Industry.BUILDING_TYPE);
					break;
				case 2:
					target = Main.game.findClosestBuilding(this, Weapon.BUILDING_TYPE);
					break;
			}
			i++;
		}
		
		if (target == null) {
			aimTarget = null;
			setRotation(getRadians());
			return;
		}
		
		aimTarget = target;
		if (getRect().getDistance(aimTarget.getRect()) <= BATTERY_REACH) {
			setRotation(reachRect.getAngleTo(aimTarget.getRect()));
		} else {
			aimTarget = null;
			setRotation(getRadians());
		}
	}

	private double getShotRotation() {
		return getDrawRotation() - getImgRotation();// + (Math.random() * SPREAD_RADIUS - SPREAD_RADIUS / 2D);
	}

	@Override
	protected void shoot() {
		if (aimTarget == null) return;
		if (!aimTarget.exists()) {aimTarget = null; return;}
		if (getRect().getDistance(aimTarget.getRect()) > BATTERY_REACH) {aimTarget = null; return;}

		if (!super.getTarget().missilesAvailable()) return;
		super.getTarget().fireMissile();
		try {
			Image missile = new Image("src/res/img/interplanetaryMissile.png");
			double shotRot = getShotRotation();
			double dxa = getTarget().getDeltaX(), dxb = BATTERY_PROJECTILE_SPEED * Math.cos(shotRot);
			double dya = getTarget().getDeltaY(), dyb = BATTERY_PROJECTILE_SPEED * Math.sin(shotRot);
			double dx = dxa+dxb;
			double dy = dya+dyb;
			double speed = Interface.USE_BAD_PROJECTILE_PHYSICS ? BATTERY_PROJECTILE_SPEED : (Math.sqrt(dx*dx + dy*dy));
			double angle = Interface.USE_BAD_PROJECTILE_PHYSICS ? shotRot				   : (Rect.getAngle(dx, dy));
			Projectile p = new Projectile(	super.getShotX(missile),
											super.getShotY(missile),
											missile.getWidth(),
											missile.getHeight(),
											BATTERY_PROJECTILE_SPEED,
											speed,
											angle,
											c,
											missile,
											shotRot,
											getImgRotation(),
											BATTERY_DAMAGE,
											TYPE,
											super.getTeam(),
											BATTERY_RANGE);
			p.setHoming(aimTarget);
			Main.game.addEntity(p);
		} catch (SlickException ex) {
			System.out.println("Failed loading image src/res/img/interplanetaryMissile.png");
		}
	}

	@Override
	public void render(Graphics g, Camera cam) {
		super.render(g, cam);
		if (Main.game.green.getTeam() != this.getTeam()) return;

		g.setLineWidth(1);
		if (aimTarget != null) {
			g.setColor(Color.white);
			if (Interface.showReachLines) {
				g.drawLine(cam.getRenderX(reachRect.getCenterX()),
						   cam.getRenderY(reachRect.getCenterY()),
						   cam.getRenderX(aimTarget.getRect().getCenterX()),
						   cam.getRenderY(aimTarget.getRect().getCenterY()));
			}
		} else {
			g.setColor(Color.red);
		}
		if (Interface.USE_BAD_PROJECTILE_PHYSICS) {
			reachRect.draw(g, cam);
		}
	}
}