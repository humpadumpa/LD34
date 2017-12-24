
/*
 * Orbital.java
 *
 * Dec 12, 2015
 */
package Game.Entities;

import Game.Entities.Planet;

/* 
 * @author Per Eresund
 */
public abstract class Orbital implements Entity {
	public abstract double getImgRotation();
	
	private boolean clockwise;
	private boolean origClockwise;
	private int range;
	private long moveT0, moveT1;
	private double speed, radians, startRadians, endRadians, width, height;
	private Planet target;
	
	public void circleAround(Planet target, int range, double speed, double radians, boolean clockwise, double width, double height) {
		this.target = target;
		this.range = range;
		this.speed = speed;
		this.origClockwise = this.clockwise = clockwise;
		this.width = width;
		this.height = height;
		setRadians(radians);
		
		this.update();	//Check if causing bugs when planet-update is finished
	}
	
	public void setRadians(double radians) {
		while (radians < 0) radians += Math.PI*2D;
		this.radians = radians % (Math.PI*2D);
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public double getRadians() {
		return radians;
	}
	
	public double getDrawRotation() {
		return radians + getImgRotation();
	}
	
	public Planet getTarget() {
		return target;
	}
	
	public int getOrbitRange() {
		return range;
	}
	
	@Override
	public double getDeltaX() {
		double rad0 = radians - (clockwise ? speed : -speed) / 5000D;
		double x0 = (target.getRect().getCenterX() + (range * Math.cos(rad0)));
		double dx = getRect().getCenterX() - x0;
		return dx;
//		Orbital trg = target;
//		if (target == null) return 0;
//		double rad0 = trg.radians - (trg.clockwise ? trg.speed : -trg.speed) / 5000D;
//		int x0 = (int)(trg.target.getRect().getCenterX() + (trg.range * Math.cos(rad0)));
//		int dx = trg.getRect().getCenterX() - x0;
//		return dx;
	}
	
	@Override
	public double getDeltaY() {
		double rad0 = radians - (clockwise ? speed : -speed) / 5000D;
		double y0 = (target.getRect().getCenterY() + (range * Math.sin(rad0)));
		double dy = getRect().getCenterY() - y0;
		return dy;
//		Orbital trg = target;
//		if (target == null) return 0;
//		Orbital trg = target;
//		double rad0 = trg.radians - (trg.clockwise ? trg.speed : -trg.speed) / 5000D;
//		int y0 = (int)(trg.target.getRect().getCenterY() + (trg.range * Math.sin(rad0)));
//		int dy = trg.getRect().getCenterY() - y0;
//		return dy;
	}
	
	private boolean isMoving() {
		if (moveT1 == 0) return false;
		if (System.currentTimeMillis() >= moveT1) {
			moveT1 = 0;
			radians = endRadians + (clockwise ? speed : -speed) / 5000D;
//			if (radians < 0) radians = Math.PI*2 - radians;
			while (radians < 0) radians += Math.PI*2D;
		}
		return moveT1 != 0;
	}
	
	public void moveTowardsRadians(double radians, long time) {
		startRadians = this.radians;
		endRadians = radians;
		clockwise = ((endRadians - startRadians) > (Math.PI + (startRadians - endRadians)));
		moveT0 = System.currentTimeMillis();
		moveT1 = time;
	}
	
	@Override
	public void update() {
		if (target == null) return;
		double addRad = (clockwise ? speed : -speed) / 5000D;
		if (speed == 0) {
		} else if (isMoving()) {
			endRadians = endRadians + addRad;
//			if (clockwise) {
				setRadians(startRadians + (endRadians - startRadians) * ((double)(System.currentTimeMillis() - moveT0) / (double)(moveT1 - moveT0)));
//			} else {
//				setRadians(startRadians + (endRadians - startRadians) * ((double)(moveT1 - moveT0) / (double)(System.currentTimeMillis() - moveT0)));
//			}
		} else {
			clockwise = origClockwise;
			addRad = (clockwise ? speed : -speed) / 5000D;
			double newRad = radians + addRad;
			setRadians(newRad);
		}
		double range = this.range;
		double rangeX = (double)range * Math.cos(radians)*width;
		double rangeY = (double)range * Math.sin(radians)*height;
//		double dx = range * Math.cos(radians);
//		double dy = range * Math.sin(radians);
		setPos(	target.getRect().getCenterX() + (int)rangeX,
				target.getRect().getCenterY() + (int)rangeY,
				true);
	}
}