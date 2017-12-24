/*
 * Rect.java
 *
 * Dec 14, 2015
 */
package Game;

import org.newdawn.slick.Graphics;

/* 
 * @author Per Eresund
 */
public class Rect {
	public int x, y, w, h;
	public boolean isCircle;
	
	public Rect(int x, int y, int w, int h, boolean isCircle) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.isCircle = isCircle;
	}
	
	public Rect(int x, int y, int w, int h, boolean isCircle, boolean centered) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.isCircle = isCircle;
		if (centered) {
			this.x -= this.w/2;
			this.y -= this.h/2;
		}
	}
	
	public int getLongestSide() {
		return Math.max(w, h);
	}
	
	public int getLeftX()	{ return x;		}
	public int getCenterX()	{ return x+w/2;	}
	public int getRightX()	{ return x+w;	}
	
	public int getTopY()	{ return y;		}
	public int getCenterY()	{ return y+h/2;	}
	public int getBottomY()	{ return y+h;	}
	
	public int getMidpointX(Rect b) {
		return (this.getCenterX() + b.getCenterX()) / 2;
	}
	public int getMidpointY(Rect b) {
		return (this.getCenterY() + b.getCenterY()) / 2;
	}
	
    public boolean intersectsRect(Rect b) {
		if (isCircle) return b.intersectsCircle(this);
		
		if (getLeftX()		>	b.getRightX())	return false;
		if (getRightX()		<	b.getLeftX())	return false;
		if (getBottomY()	<	b.getTopY())	return false;
		if (getTopY()		>	b.getBottomY()) return false;
		
        return true;
//		return intersectsRect(b.x, b.y, b.w, b.h);
    }
	
    public boolean intersectsRect(int x, int y, int w, int h) {
        if (this.x + this.w < x) return false;
        if (x + w < this.x) return false;
        if (this.y + this.h < y) return false;
        if (y + h < this.y) return false;
        
        return true;
    }
    
    public boolean intersectsPos(int x, int y) {
        if (getRightX()		<	x) return false;
        if (getLeftX()		>	x) return false;
        if (getTopY()		>	y) return false;
        if (getBottomY()	<	y) return false;
        
        return true;
    }
	
    public boolean intersectsCircle(Rect c) {
		if (isCircle) {
			int radii = this.w + c.w;
			return (getDistance(c) <= radii * radii);
		}
		if (!intersectsRect(c)) return false;
		
        return intersectsCircleAdv(c);
	}
	
    private boolean intersectsCircleAdv(Rect c) {
		double overlapSquared = getCornerOverlapSquared(c);
        return overlapSquared < 1d;
	}
	
	public double getRadius() {
		if (isCircle) return w/2d;
		return Math.sqrt((w/2d)*(w/2d) + (h/2d)*(h/2d));
	}
	
	/**
	 * Calculates the distance between the middle points of two bodies.
	 *
	 * @param b The body.
	 *
	 * @return The distance between the middle points of the bodies. Can only be
	 *         positive or zero.
	 */
	public double getDistance(Rect b) {
		long dx = Math.abs(getCenterX() - b.getCenterX());
		long dy = Math.abs(getCenterY() - b.getCenterY());
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Calculates the square of the distance between the middle points of two
	 * bodies. Used instead of getDistance() to save performance when only
	 * distance comparisons are necessary.
	 *
	 * @param b The body.
	 *
	 * @return The square of the distance between the middle points of the
	 *         bodies. Can only be positive or zero.
	 */
	public double getDistanceSquared(Rect b) {
		long dx = Math.abs(getCenterX() - b.getCenterX());
		long dy = Math.abs(getCenterY() - b.getCenterY());
		return dx*dx + dy*dy;
	}

	/**
	 * Calculates the square of the sum of the radii of the rect and the circle.
	 * Used instead of getRadiusDistance() to save performance when only
	 * distance comparisons are necessary (no need to use square root).
	 *
	 * @param c The circle.
	 *
	 * @return The square of the sum of the radii of the rect and the circle.
	 *		   Can only be positive or zero.
	 */
	public double getRadiusDistanceSquared(Rect c) {
		double rectRadius = getRadius();
		double circleRadius = c.getRadius();
		return (rectRadius + circleRadius)*(rectRadius + circleRadius);
	}
	
	/**
	 * Creates a big circle from the rect's centre to check if the circle is
	 * outside any of corners of the rect, which will mean there is no overlap.
	 *
	 * @param c Circle
	 *
	 * @return Overlap distance (negative for no overlap).
	 */
	public double getCornerOverlapSquared(Rect c) {
		double distSquare = getDistanceSquared(c);
		double radiusDistSquare = getRadiusDistanceSquared(c);
		return distSquare - radiusDistSquare;
	}
	
	public static double getAngle(double dx, double dy) {
		double angle;
		if (dx == 0) {
			angle = (dy < 0 ? Math.PI/2D : Math.PI*3D/2D);
		} else {
			angle = dy/dx;   //0 is straight to the right and PI/2 is straight down.
			angle = Math.atan(angle);
	//		if (angle != angle) {
	//			angle = 0;
	//		}
			if (dx < 0) {
				angle += Math.PI;
			}
		}
		return angle;
	}

	
	private static final double[] temp = new double[2];
	public static double[] addVectors(double radian0, double force0, double radian1, double force1) {
		double vx0 = Math.cos(radian0) * force0;
		double vy0 = Math.sin(radian0) * force0;
		double vx1 = Math.cos(radian1) * force1;
		double vy1 = Math.sin(radian1) * force1;
		double finalvx = vx0 + vx1;
		double finalvy = vy0 + vy1;
		double finalRadian = getAngle(finalvx, finalvy);
		double finalForce = Math.sqrt(finalvx*finalvx+finalvy*finalvy);
		temp[0] = finalRadian;
		temp[1] = finalForce;
		return temp;
	}
	
	public double getAngleTo(Rect b) {
		return getAngleTo(b.getCenterX(), b.getCenterY());
	}
	
	public double getAngleTo(int centerX, int centerY) {
		double dx = centerX - this.getCenterX();
		double dy = centerY - this.getCenterY();
		return getAngle(dx, dy);
	}
	
	public void draw(Graphics g, Camera cam) {
		if (isCircle) {
			g.drawOval(cam.getRenderX(x), cam.getRenderY(y), w, h, 360);
		} else {
			g.drawRect(cam.getRenderX(x), cam.getRenderY(y), w, h);
		}
	}
	
	public void fill(Graphics g, Camera cam) {
		if (isCircle) {
			g.fillOval(cam.getRenderX(x), cam.getRenderY(y), w, h, 360);
		} else {
			g.fillOval(cam.getRenderX(x), cam.getRenderY(y), w, h);
		}
	}
}