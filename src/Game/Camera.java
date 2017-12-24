/*
 * Camera.java
 *
 * Dec 14, 2015
 */
package Game;

import org.newdawn.slick.Graphics;

/* 
 * @author Per Eresund
 */
public class Camera {
	private final Rect rect;				//Position of camera in the world and size of camera on the screen
	private final int screenX, screenY;		//Position of camera on the screen
	private double xbuf, ybuf;				//Position buffers
	private float scale;					//Scaling applied to rendering
	
	public Camera(int w, int h, int screenX, int screenY) {
		rect = new Rect(0, 0, w, h, false);
		this.screenX = screenX;
		this.screenY = screenY;
		this.scale = 1f;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void scale(Graphics g) {
		g.scale(scale, scale);
	}
	
	public void unscale(Graphics g) {
		g.scale(1f/scale, 1f/scale);
	}
	
	public void scaleAfter(Rect b) {
		scaleAfter(b.w, b.h);
	}
	
	public void scaleAfter(int w, int h) {
		float xScale, yScale;
		xScale = (float)rect.w / (float)(w+64);
		yScale = (float)rect.h / (float)(h+64);

		setScale(Math.min(1.0f, Math.min(xScale, yScale)));
	}
	
	public boolean contains(Entity e) {
		if (true) return true;
		Rect b = e.getRect();
		return b.intersectsRect((int)(((rect.x + rect.w/2f - (rect.w/2f) / scale))*scale), (int)(((rect.y + rect.h/2f - (rect.h/2f) / scale))*scale), (int)(rect.w*scale), (int)(rect.h*scale));
	}
	
	public Rect getRect() {
		return rect;
	}
	
	public void setClip(Graphics g) {
		g.setClip(screenX, screenY, rect.w, rect.h);
	}
	
	public void followPos(int x, int y, boolean centered) {
		int dx = x - rect.x, dy = y - rect.y;
		if (centered) {
			dx -= rect.w/2;
			dy -= rect.h/2;
		}
		if (Math.abs(dx) < 8 && Math.abs(dy) < 8) {
//			System.out.println("no");
			setPos(x, y, centered);
			return;
		}
		double speedX = dx / 50D + 2D*Math.signum(dx);
		double speedY = dy / 50D + 2D*Math.signum(dy);
		xbuf += speedX;
		ybuf += speedY;
		int newx = (centered ? rect.getCenterX() : rect.x) + (int)xbuf, newy = (centered ? rect.getCenterY() : rect.y) + (int)ybuf;
		setPos(newx, newy, centered);
		xbuf %= 1;
		ybuf %= 1;
	}
	
	public void setPos(int x, int y, boolean centered) {
		rect.x = x;
		rect.y = y;
		if (centered) {
			rect.x -= rect.w/2;
			rect.y -= rect.h/2;
		}
	}
	
	public void setPos(Rect b, boolean centered) {
		if (centered) {
			rect.x = b.getCenterX();
			rect.y = b.getCenterY();
			rect.x -= rect.w/2;
			rect.y -= rect.h/2;
		} else {
			rect.x = b.x;
			rect.y = b.y;
		}
	}
	
	public void setSize(int w, int h) {
		rect.w = w;
		rect.h = h;
	}
	
	public int getInterfaceX(int origX) {
		return origX + screenX;
	}
	
	public int getInterfaceY(int origY) {
		return origY + screenY;
	}
	
	public int getRenderX(int origX) {
//		return origX - rect.x + screenX;
		return (int)(origX - (rect.x + rect.w/2f - (rect.w/2f + screenX) / scale));
	}
	
	public int getRenderY(int origY) {
//		return origY - rect.y + screenY;
		return (int)(origY - (rect.y + rect.h/2f - (rect.h/2f + screenY) / scale));
	}
}