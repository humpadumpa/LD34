
/*
 * Weapon.java
 *
 * Dec 13, 2015
 */
package Game;

import static Game.Industry.BUILDING_TYPE;
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
	
	private int getShotX(Image img) {
		return (int)(getRect().getCenterX() + img.getWidth()/2d * Math.cos(getRadians()));
	}
	private int getShotY(Image img) {
		return (int)(getRect().getCenterY() + img.getHeight()/2d * Math.sin(getRadians()));
	}
	
//	private int getShotX(Image img) {
//		return (int)(getRect().getCenterX() + Math.sqrt((img.getWidth())*(img.getWidth()) + (img.getHeight())*(img.getHeight())) * Math.cos(super.getRadians()));
//	}
//	private int getShotY(Image img) {
//		return (int)(getRect().getCenterY() + Math.sqrt((img.getWidth())*(img.getWidth()) + (img.getHeight())*(img.getHeight())) * Math.sin(super.getRadians()));
//	}
	
	private int getShotX(int shotlen) {
		return (int)(getRect().getCenterX() + shotlen * Math.cos(getRadians()));
	}
	
	private int getShotY(int shotlen) {
		return (int)(getRect().getCenterY() + shotlen * Math.sin(getRadians()));
	}
	
	private long lastShot;
	
	public Weapon(String imgPath, Color c) {
		super(32, 32, true, imgPath, c);
	}
	
	@Override
	public int getBuildingType() {
		return BUILDING_TYPE;
	}
	
	protected abstract int getCooldown();
	protected abstract void shoot();
	protected abstract int getWeaponType();
	protected abstract void setTarget();
	
	
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
	
	public static class Gatling extends Weapon {
		public static final int TYPE = 0;
		
		private Entity aimTarget;
		private Rect reachRect;
		private double rotation;
	
		public Gatling(int player) {
			super(player == 0 ? GATLING_PATH : GATLING_P2_PATH, Color.white);
			reachRect = new Rect(0, 0, GATLING_REACH*2, GATLING_REACH*2, true);
		}
	
		@Override
		public double getRadians() {
			return rotation;
		}
	
		@Override
		public double getDrawRotation() {
			if (rotation == 0) return super.getDrawRotation();
			return rotation + getImgRotation();
		}

		private void setRotation(double rotation) {
			this.rotation = rotation % (Math.PI*2D);
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
						target = Main.game.findClosestProjectile(this, Battery.TYPE);
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
				setRotation(getRadians());
				return;
			}
			aimTarget = target;
			if (getRect().getDistance(aimTarget.getRect()) <= GATLING_REACH) {
				setRotation(reachRect.getAngleTo(aimTarget.getRect()));
			} else {
				aimTarget = null;
				setRotation(getRadians());
			}
		}
		
		private double getShotRotation() {
			return getDrawRotation() - getImgRotation() + (Math.random() * GATLING_SPREAD_RADIANS - GATLING_SPREAD_RADIANS / 2D);
		}
		
		@Override
		protected void shoot() {
			if (aimTarget == null) return;
			if (!aimTarget.exists()) {aimTarget = null; return;}
			if (getRect().getDistance(aimTarget.getRect()) > GATLING_REACH) {aimTarget = null; return;}
			
			int shotlen = 18;
			double shotRot = getShotRotation();
			double dxa = getDeltaX(), dxb = GATLING_PROJECTILE_SPEED * Math.cos(shotRot);
			double dya = getDeltaY(), dyb = GATLING_PROJECTILE_SPEED * Math.sin(shotRot);
			double dx = dxa+dxb;
			double dy = dya+dyb;
			double speed = Interface.USE_BAD_PROJECTILE_PHYSICS ? GATLING_PROJECTILE_SPEED : (Math.sqrt(dx*dx + dy*dy));
			double angle = Interface.USE_BAD_PROJECTILE_PHYSICS ? shotRot				   : (Rect.getAngle(dx, dy));
			Projectile p = new Projectile(	super.getShotX(shotlen),
											super.getShotY(shotlen),
											2,
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
//				System.out.println("proj");
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
//					g.setLineWidth(1);
//					g.setColor(Color.white);
//					g.drawLine(cam.getRenderX(getRect().getCenterX()),
//							   cam.getRenderY(getRect().getCenterY()),
//							   cam.getRenderX((int)(getRect().getCenterX() + 1000D * Math.cos(rotation))),
//							   cam.getRenderY((int)(getRect().getCenterY() + 1000D * Math.sin(rotation))));
	
	public static class Battery extends Weapon {
		public static final int TYPE = 1;
		
//		private static final int BATTERY_REACH = 1000, BATTERY_RANGE = BATTERY_REACH * 2;
//		private static final int BATTERY_COOLDOWN = 4000;
//		private static final int BATTERY_DAMAGE = 5;
//		private static final double BATTERY_SPREAD_RADIANS = Math.PI / 32D;
		
		private Entity aimTarget;
		private Rect reachRect;
		private double rotation;
		
		public Battery(int player) {
			super(player == 0 ? BATTERY_PATH : BATTERY_P2_PATH, Color.white);
			reachRect = new Rect(0, 0, BATTERY_REACH*2, BATTERY_REACH*2, true);
		}

		private void setRotation(double rotation) {
			this.rotation = rotation % (Math.PI*2D);
		}
	
		@Override
		public double getRadians() {
			return rotation;
		}
	
		@Override
		public double getDrawRotation() {
			if (rotation == 0) return super.getDrawRotation();
			return rotation + getImgRotation();
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
			int i = 0, choices = 1;
			while (target == null && i < choices) {
				switch(i) {
//					case 0:
//						target = Main.game.findClosestBuilding(this, Industry.BUILDING_TYPE);
//						break;
//					case 1:
//						target = Main.game.findClosestBuilding(this, Weapon.BUILDING_TYPE);
//						break;
					case 0:
						target = Main.game.findClosestPlanet(this);
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
			
			if (!super.getTarget().fireMissile()) return;
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
			
//			g.drawLine(cam.getRenderX(reachRect.getCenterX()),
//					   cam.getRenderY(reachRect.getCenterY()),
//					   cam.getRenderX(reachRect.getCenterX() + super.get),
//					   cam.getRenderY(;
		}
	}
	
	public static Weapon random(int player) {
		double rand = Math.random();
		
		if(rand < 0.5) return new Gatling(player);
		else return new Battery(player);
	}
}