/*
 * Gatling.java
 *
 * Dec 25, 2017
 */
package Game.Buildings.Weapons;

import static Game.Buildings.Building.GATLING_P2_PATH;
import static Game.Buildings.Building.GATLING_PATH;
import Game.Buildings.Industry;
import Game.Camera;
import Game.Entities.Entity;
import Game.Entities.Projectile;
import Game.Interface;
import Game.Main;
import Game.Rect;
import static Game.Settings.GATLING_COOLDOWN;
import static Game.Settings.GATLING_DAMAGE;
import static Game.Settings.GATLING_PROJECTILE_SPEED;
import static Game.Settings.GATLING_RANGE;
import static Game.Settings.GATLING_REACH;
import static Game.Settings.GATLING_SPREAD_RADIANS;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * 
 * @author Per Eresund
 */
public class Gatling extends Weapon {
	public static final int TYPE = 0;

	public Gatling(int player) {
		super(player == 0 ? GATLING_PATH : GATLING_P2_PATH, Color.white);
		reachRect = new Rect(0, 0, GATLING_REACH*2, GATLING_REACH*2, true);
	}

	@Override
	public double getImgRotation() {
		return Math.PI/2D;
	}

	@Override
	protected int getCooldown() {
		return GATLING_COOLDOWN;
	}

	@Override
	protected int getWeaponType() {
		return TYPE;
	}

	@Override
	protected void setTarget() {
		reachRect.x = super.getRect().getCenterX() - reachRect.w/2;
		reachRect.y = super.getRect().getCenterY() - reachRect.h/2;
		Entity target = null;
		int i = 0, choices = 4;
		while (target == null && i < choices) {
			switch(i) {
				case 0:
					target = Main.game.findClosestProjectile(this, MissileBattery.TYPE);
					break;
				case 1:
					target = Main.game.findClosestBuilding(this, Weapon.BUILDING_TYPE);
					break;
				case 2:
					target = Main.game.findClosestBuilding(this, Industry.BUILDING_TYPE);
					break;
				case 3:
					target = Main.game.findClosestPlanet(this);
					break;
			}
			i++;
		}
		if (target == null) {
			aimTarget = null;
			setRotation(getRotation());
			return;
		}
		aimTarget = target;
		if (getRect().getDistance(aimTarget.getRect()) <= GATLING_REACH) {
			setRotation(reachRect.getAngleTo(aimTarget.getRect()));
		} else {
			aimTarget = null;
			setRotation(getRotation());
		}
	}

	protected double getShotRotation() {
		return getDrawRotation() - getImgRotation() + (Math.random() * GATLING_SPREAD_RADIANS - GATLING_SPREAD_RADIANS / 2D);
	}

	@Override
	protected void shoot() {
		if (aimTarget == null) return;
		if (!aimTarget.exists()) {aimTarget = null; return;}
		if (getRect().getDistance(aimTarget.getRect()) > GATLING_REACH) {aimTarget = null; return;}

		int shotlen = 18, shotwidth = 2;
		double shotRot = getShotRotation();
		double dxa = getDeltaX(), dxb = GATLING_PROJECTILE_SPEED * Math.cos(shotRot);
		double dya = getDeltaY(), dyb = GATLING_PROJECTILE_SPEED * Math.sin(shotRot);
		double dx = dxa+dxb;
		double dy = dya+dyb;
		double speed = Interface.USE_BAD_PROJECTILE_PHYSICS ? GATLING_PROJECTILE_SPEED : (Math.sqrt(dx*dx + dy*dy));
		double angle = Interface.USE_BAD_PROJECTILE_PHYSICS ? shotRot				   : (Rect.getAngle(dx, dy));
		Projectile p = new Projectile(	super.getShotX(shotlen),
										super.getShotY(shotlen),
										shotwidth,
										shotlen,
										GATLING_PROJECTILE_SPEED,
										speed,
										angle,
										c,
										null,
										shotRot,
										getImgRotation(),
										GATLING_DAMAGE,
										TYPE,
										super.getTeam(),
										GATLING_RANGE);
		if (aimTarget instanceof Projectile) {
			p.setHoming(aimTarget);
		}
		Main.game.addEntity(p);
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