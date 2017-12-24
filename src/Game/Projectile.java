
/*
 * Shot.java
 *
 * Dec 13, 2015
 */
package Game;

import Game.Planet.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

/* 
 * @author Per Eresund
 */
public class Projectile implements Entity {
	private Team team;
	private Rect rect;
	private double xbuf, ybuf;
	
	private double speed, radians, force, shotRotation, imgRotation, travelled;
	private int damage, type, range;
	private Color c;
	private Image img;
	private boolean isAlive;
	
	private boolean isHoming;
	private Entity target;
	
	public Projectile(int x, int y, int w, int h, double force, double speed, double radians, Color c, Image img, double shotRotation, double imgRotation, int damage, int type, Team team, int range) {
		this.rect = new Rect(x, y, w, h, false);
		this.force = force;
		this.speed = speed;
		this.radians = radians;
		this.c = c;
		this.img = img;
		this.shotRotation = shotRotation;
		this.imgRotation = imgRotation;
		this.damage = damage;
		this.type = type;
		this.team = team;
		this.range = range;
		this.travelled = 0;
		this.isAlive = true;
		this.adjustSize();
	}

	@Override
	public double getDeltaX() {
		return (speed * Math.cos(radians));
	}

	@Override
	public double getDeltaY() {
		return (speed * Math.sin(radians));
	}
	
	public void setHoming(Entity target) {
		isHoming = true;
		this.target = target;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public boolean exists() {
		return isAlive;
	}

	@Override
	public Rect getRect() {
		return rect;
	}
	
	private int getX0() {
		return rect.x;
	}

	private int getY0() {
		return rect.y;
	}
	
	private int getLength() {
		return Math.max(rect.w, rect.h);
	}
	
	private int getX1() {
		return (int)(rect.x + getLength() * Math.cos(radians));
	}

	private int getY1() {
		return (int)(rect.y + getLength() * Math.sin(radians));
	}
	
	private double getDrawRotation() {
//		return radians + imgRotation;
		return shotRotation + imgRotation;
	}
	
	private void adjustSize() {
		if (rect.w == rect.h) return;
		int w = rect.w, h = rect.h;
		if (shotRotation >= 7D/4D * Math.PI || shotRotation < 1D/4D * Math.PI) {
			rect.w = Math.max(w, h);
			rect.h = Math.min(w, h);
		} else if (shotRotation >= 3D/4D * Math.PI && shotRotation < 5D/4D * Math.PI) {
			rect.w = Math.max(w, h);
			rect.h = Math.min(w, h);
			rect.x = rect.x-rect.w;
		} else if (shotRotation >= 5D/4D * Math.PI && shotRotation < 7D/4D * Math.PI) {
			rect.w = Math.min(w, h);
			rect.h = Math.max(w, h);
			rect.y = rect.y-rect.h;
		}
	}
	
	@Override
	public void setPos(int x, int y, boolean centered) {
		rect.x = x;
		rect.y = y;
		if (centered) {
			rect.x += rect.w / 2 * Math.cos(radians);
			rect.y += rect.h / 2 * Math.sin(radians);
		}
	}
	
	private void move() {
		double maxSpeed = force*1D;
		double angle = radians;
		double dist = maxSpeed;
		if (target != null) {
			if (target.exists()) {
//				double dstX;
//				dstX = target.getRect().getCenterX() - getRect().x;
//				dstX = (target.getDeltaX()*(dstX/maxSpeed)) + target.getRect().getCenterX();
//				double dstY;
//				dstY = target.getRect().getCenterY() - getRect().y;
//				dstY = (target.getDeltaY()*(dstY/maxSpeed)) + target.getRect().getCenterY();
//				angle = rect.getAngleTo((int)dstX, (int)dstY);
				angle = rect.getAngleTo(target.getRect());
				dist = rect.getDistance(target.getRect());
			} else {
				target = null;
			}
		} else {
		}
		double[] temp = Rect.addVectors(radians, speed, angle, force);
		double change = temp[0] - radians;
		radians = temp[0];
		shotRotation += change;
		speed = Math.min(maxSpeed, Math.min(dist, temp[1]));
		xbuf += speed * Math.cos(radians);// * Main.game.currentSpeedMultiple;
		ybuf += speed * Math.sin(radians);// * Main.game.currentSpeedMultiple;
//		xbuf += speed * Math.cos(radians);// * Main.game.currentSpeedMultiple;
//		ybuf += speed * Math.sin(radians);// * Main.game.currentSpeedMultiple;];
		if (xbuf >= 1f || xbuf <= -1f) {
			rect.x += (int)xbuf;
			xbuf -= (int)xbuf;
		}
		if (ybuf >= 1f || ybuf <= -1f) {
			rect.y += (int)ybuf;
			ybuf -= (int)ybuf;
		}
	}
	
	@Override
	public boolean collideWith(int damage) {
		Main.game.removeEntity(this);
		return (isAlive = false);
	}
	
	private void checkCollision() {
		Entity collision = Main.game.getCollision(this);
		if (collision != null) {
			boolean alive = collision.collideWith(damage);
			if (!alive) {
				team.earnKill(collision);
			}
			Main.game.removeEntity(this);
			isAlive = false;
		}
	}
	
	@Override
	public void update() {
		if (!isAlive) return;
		
		if (isHoming) {
			if (target.exists()) {
//				double change = rect.getAngleTo(target.getRect()) - radians;
//				if (change < -Math.PI || change > Math.PI) change *= -1;
//				double maxChange = 0.02D;
//				if (Math.abs(change) > maxChange) change = maxChange*Math.signum(change);
//				radians = (radians + change) % (Math.PI*2D);
//				shotRotation = (shotRotation + change) % (Math.PI*2D);
			} else {
				isHoming = false;
				target = null;
			}
		}
		
		move();
		checkCollision();
		travelled += speed;
		
		if (travelled >= range) {
			Main.game.removeEntity(this);
			isAlive = false;
		}
	}

	@Override
	public void render(Graphics g, Camera cam) {
		if (!isAlive) return;
		
//		rect.draw(g, cam); //How they are physically (what the collisions are based on).
		c.a = (float)range/(float)travelled - 0.9f;
		if (img == null) {
			g.setColor(c);
//			g.drawLine(cam.getRenderX(getX0()), cam.getRenderY(getY0()), cam.getRenderX(getX1()), cam.getRenderY(getY1()));
			g.drawLine(cam.getRenderX(getX0()), cam.getRenderY(getY0()), cam.getRenderX(getX1()), cam.getRenderY(getY1()));
		} else {
			img.setRotation((float)(Math.toDegrees(getDrawRotation())));
			g.drawImage(img, cam.getRenderX(getX0()), cam.getRenderY(getY0()), c);		//Is a bit off because of rotation, but what ever.
//			g.setColor(Color.white);
			if (Interface.showReachLines && target != null) {
				g.drawLine(cam.getRenderX(getRect().getCenterX()),
						   cam.getRenderY(getRect().getCenterY()),
						   cam.getRenderX(target.getRect().getCenterX()),
						   cam.getRenderY(target.getRect().getCenterY()));
			}
		}
		c.a=1f;
	}
}