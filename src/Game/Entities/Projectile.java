
/*
 * Projectile.java
 *
 * Dec 13, 2015
 */
package Game.Entities;

import Game.Camera;
import Game.Interface;
import Game.Main;
import Game.Entities.Planet.Team;
import Game.Rect;
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
	private int x, y, width, length;
	private int damage, type, range;
	private Color c;
	private Image img;
	private boolean isAlive;
	
	private boolean isHoming;
	private Entity target;
	
	public Projectile(int x, int y, int w, int h, double force, double speed, double radians, Color c, Image img, double shotRotation, double imgRotation, int damage, int type, Team team, int range) {
		this.rect = new Rect(x, y, w, h, false);
		this.x = x;
		this.y = y;
		this.width = w;
		this.length = Math.max(w, h);
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
		this.adjustRect();
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
	
	@Override
	public void setTeam(Team team) {
		this.team = team;
	}
	
	@Override
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
		return x;
	}

	private int getY0() {
		return y;
	}
	
	private int getX1() {
		return (int)(x + length * Math.cos(radians));
	}

	private int getY1() {
		return (int)(y + length * Math.sin(radians));
	}
	
	private double getDrawRotation() {
		return shotRotation + imgRotation;
	}
	
	private void adjustRect() {
		int x0 = getX0();
		int x1 = getX1();
		int y0 = getY0();
		int y1 = getY1();
		
		rect.x = Math.min(x0, x1);
		rect.y = Math.min(y0, y1);
		rect.w = Math.abs(x1-x0);
		rect.h = Math.abs(y1-y0);
	}
	
	@Override
	public void setPos(int x, int y, boolean centered) {
		this.x = x;
		this.y = y;
		if (centered) {
			this.x += rect.w / 2 * Math.cos(radians);
			this.y += rect.h / 2 * Math.sin(radians);
		}
		adjustRect();
	}
	
	private void move() {
		double maxSpeed = force*1D;
		double angle = radians;
		double dist = maxSpeed;
		double currentForce = force;
		if (target != null) {
			if (target.exists()) {
				angle = rect.getAngleTo(target.getRect());
				dist = rect.getDistance(target.getRect());
				
				if (img != null) {
					double targetdx = target.getDeltaX();
					double targetdy = target.getDeltaY();
					double targetSpeed = Math.sqrt(targetdx*targetdx + targetdy*targetdy);
					maxSpeed += targetSpeed;
					currentForce = maxSpeed;
				}
			} else {
				target = null;
			}
		}
		double[] temp = Rect.addVectors(radians, speed, angle, currentForce);
		double change = temp[0] - radians;
		radians = temp[0];
		shotRotation += change;
		if (target != null) {
			shotRotation = angle;
		}
		speed = Math.min(maxSpeed, Math.min(dist, temp[1]));
		
		System.out.println("\nSpeed: " + speed);
		System.out.println("Addx: " + speed*Math.cos(radians));
		System.out.println("Addy: " + speed*Math.sin(radians));
		xbuf += speed * Math.cos(radians);
		ybuf += speed * Math.sin(radians);
		if (xbuf >= 1f || xbuf <= -1f) {
			x += (int)xbuf;
			xbuf -= (int)xbuf;
		}
		if (ybuf >= 1f || ybuf <= -1f) {
			y += (int)ybuf;
			ybuf -= (int)ybuf;
		}
		adjustRect();
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
		
//		g.setColor(Color.white);
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
		c.a = 1f;
	}
}