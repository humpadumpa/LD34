
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
	private double speed, radians, startRadians, targetRadians, width, height;
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
	
	public static double getRadians(double radians) {
		if (radians != radians) return 0;
		
		while (radians < 0) {
			radians += Math.PI*2D;
		}
		radians = radians % (Math.PI*2D);
		
		return radians;
	}
	
	public void setRadians(double radians) {
		this.radians = getRadians(radians);
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
			double radians = targetRadians + (clockwise ? speed : -speed) / 5000D;
//			if (radians < 0) radians = Math.PI*2 - radians;
			setRadians(radians);
		}
		return moveT1 != 0;
	}
	
	private void determineDirection() {
		
		double clockwiseDist = getRadians(Math.PI*2D - (targetRadians - startRadians));
		double counterClockwiseDist = getRadians(targetRadians - startRadians);
		
		clockwise = clockwiseDist < counterClockwiseDist;
		
		System.out.println("R0: " + getRadians(startRadians) + ", R1: " + getRadians(targetRadians));
		System.out.println("Direction: " + (clockwise ? "clockwise" : "counter-clockwise"));
	}
	
	public void moveTowardsRadians(double radians, long time) {
		startRadians = this.radians;
		targetRadians = radians;
		
		moveT0 = System.currentTimeMillis();
		moveT1 = moveT0 + time;
		
		determineDirection();
	}
	
	private void updateRadians() {
		double addRad = (origClockwise ? speed : -speed) / 5000D;
		if (speed == 0) {
		} else if (isMoving()) {
			targetRadians = targetRadians + addRad;
//			long timeLeft = moveT1 - System.currentTimeMillis();
//			long totTime = moveT1 - moveT0;
			
//			if (clockwise) {
				setRadians(startRadians + (targetRadians - startRadians) * ((double)(System.currentTimeMillis() - moveT0) / (double)(moveT1 - moveT0)));
//			} else {
//				setRadians(startRadians + (targetRadians - startRadians) * ((double)(moveT1 - moveT0) / (double)(System.currentTimeMillis() - moveT0)));
//			}
		} else {
			clockwise = origClockwise;
			addRad = (clockwise ? speed : -speed) / 5000D;
			double newRad = radians + addRad;
			setRadians(newRad);
		}
	}
	
	private void moveToPosition() {
		double range = this.range;
		double rangeX = range * Math.cos(radians)*width;
		double rangeY = range * Math.sin(radians)*height;
		
		setPos(	target.getRect().getCenterX() + (int)rangeX,
				target.getRect().getCenterY() + (int)rangeY,
				true);
	}
	
	@Override
	public void update() {
		if (target == null) return;
		
		updateRadians();
		moveToPosition();
	}
}