
/*
 * Game.java
 *
 * Dec 12, 2015
 */
package Game;

import Game.AI.AI_masterDrone;
import Game.Entities.Entity;
import Game.Entities.Projectile;
import Game.Entities.Planet;
import Game.Buildings.Industry;
import Game.Buildings.Weapon;
import Game.Buildings.Building;
import static Game.Settings.*;
import Game.Entities.Planet.Team;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/* 
 * @author Per Eresund
 */
public class Game implements org.newdawn.slick.Game {
	
	public Planet center, sun, green, red;
	public double currentSpeedMultiple = 1D;
	private int maxdistance;
	
	private Camera greenCam, redCam, mapCam, fightCam;
	private boolean gameon, fiteon;
	private int mode, players;
	
	private ArrayList<Planet> planets;
	private ArrayList<Building> buildings;
	private ArrayList<Projectile> projectiles;
	private ArrayList<Entity> toAdd;
	private ArrayList<Entity> toRemove;
	
	private boolean closeRequested;
	
	public void requestClose() {
		closeRequested = true;
	}
	
	@Override
	public boolean closeRequested() {
		return true;
	}

	@Override
	public String getTitle() {
		return "Space Defense Command";
	}
	
	public void enterMode(int newMode) {
		mode = newMode;
		switch (newMode) {
			case Interface.MODE_1PLAYER:
				players = 1;
				gameon = true;
				spawnPlanets();
				setupCameras();
				setCamPoses();
				break;
			case Interface.MODE_2PLAYERS:
				players = 2;
				gameon = true;
				spawnPlanets();
				setupCameras();
				setCamPoses();
				break;
			default:
				gameon = false;
				players = 0;
				break;
		}
	}
	
	private void setupCameras() {
		if (players == 1) {
			greenCam = new Camera(Screen.width(), Screen.height(), 0, 0);
			redCam = null;
		
			int size = 256;
			mapCam = new Camera(size, size, 0, Screen.height()-size);
			mapCam.setScale(size/(float)(red.getOrbitRange()*2f + red.getRadius()*2f));
			mapCam.setPos(0, 0, true);
		} else if (players == 2) {
			greenCam = new Camera(Screen.width() / 2, Screen.height(), 0, 0);
			redCam = new Camera(Screen.width() / 2, Screen.height(), Screen.width() / 2, 0);
		
			int size = 128;
			mapCam = new Camera(size, size, Screen.width()/2-size/2, Screen.height()-size);
			mapCam.setScale(size/(float)(red.getOrbitRange()*2f + red.getRadius()*2f));
			mapCam.setPos(0, 0, true);
		}
		fightCam = new Camera(Screen.width(), Screen.height(), 0, 0);
	}
	
	private void setCamPoses() {
		if (fiteon) {
			float xScale, yScale;
			if (green.getRect().getCenterX() < red.getRect().getCenterX()) {
				xScale = red.getRect().getRightX() - green.getRect().getLeftX();
			} else {
				xScale = green.getRect().getRightX() - red.getRect().getLeftX();
			}
			if (green.getRect().getCenterY() < red.getRect().getCenterY()) {
				yScale = red.getRect().getBottomY() - green.getRect().getTopY();
			} else {
				yScale = green.getRect().getBottomY() - red.getRect().getTopY();
			}
			fightCam.scaleAfter((int)Math.abs(xScale), (int)Math.abs(yScale));
			fightCam.setPos(green.getRect().getMidpointX(red.getRect()), green.getRect().getMidpointY(red.getRect()), true);
		} else {
			fightCam.setPos(green.getRect(), true);
			if (players > 0) {
				greenCam.scaleAfter(green.getRect());
				greenCam.setPos(green.getRect(), true);
			}
			if (players == 2) {
				redCam.scaleAfter(red.getRect());
				redCam.setPos(red.getRect(), true);
			}
		}
		mapCam.setPos(sun.getRect(), true);
	}

	private void spawnPlanets() {
		int range = 0;
		int centerX = 0;
		int centerY = 0;
		int centerRadius = 32*1000;
		center = new Planet();
		center.setPos(centerX, centerY, centerRadius, true);
		center.setColor(Color.white);
		planets.add(center);
		
//		int sunX = 0;
//		int sunY = 0;
//		int sunRadius = 32*SUN_SIZE_FACTOR;
//		sun = new Planet();
//		sun.setPos(sunX, sunY, sunRadius, true);
//		sun.setColor(Color.yellow);
//		planets.add(sun);
		int sunRadius = 32*SUN_SIZE_FACTOR;
		int sunRange = 100000;
		range = range + centerRadius + sunRadius + sunRange;
		sun = new Planet();
		sun.setRadius(sunRadius);
		sun.circleAround(center, range, SUN_SPEED, (Math.random() * Math.PI*2D), true, 2D, 1D);
		sun.setColor(Color.yellow);
		planets.add(sun);
		
		int greenRadius = (int)(32*PLANET_SIZE_FACTOR), greenRange = PLANET_SUN_RANGE;
		range = range - (centerRadius+sunRange) + greenRadius + greenRange;
		green = new Planet();
		green.setRadius(greenRadius);
		green.circleAround(sun, range, GREEN_SPEED, (Math.random() * Math.PI*2D), true, 1D, 1D);
		green.setColor(new Color(12, 172, 104));
		planets.add(green);
		
		int redRadius = greenRadius, redRange = INTERPLANETARY_RANGE;
		range = range + greenRadius + redRadius + redRange;
		red = new Planet();
		red.setRadius(redRadius);
		red.circleAround(sun, range, RED_SPEED, (Math.random() * Math.PI*2D), true, 1D, 1D);
		red.setColor(new Color(172, 65, 12));
		planets.add(red);
		
//		for (int i = 0; i < 0; i++) {
//			Planet p = new Planet();
//			int radius = (int)(Math.random() * sunRadius/50D + 32D*2D);
//			int range =(int)(sunRadius + Math.random() * (greenRange - radius));
//			double speed = Math.random() * 150 + 5;
//			p.setPos(0, 0, radius, true);
//			p.circleAround(sun, range, speed, (Math.random() * Math.PI*2D), (Math.random() < 0.5) ? true : false, 1D, 1D);
//			p.setColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
//			planets.add(p);
//		}
	
		maxdistance = sunRadius + 100 + greenRadius*2 + redRadius*2 + 64;
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		planets = new ArrayList();
		buildings = new ArrayList();
		projectiles = new ArrayList();
		toAdd = new ArrayList();
		toRemove = new ArrayList();
		
		Interface.setup(Interface.MODE_MENU);
		
		Interface keyListener = new Interface();
		
		container.getInput().addKeyListener(keyListener);
		AI_masterDrone.init(keyListener);
//		spawnPlanets();
//		setupCameras();
	}
	
	public void addEntity(Entity e) {
		toAdd.add(e);
	}
	
	public void removeEntity(Entity e) {
		toRemove.add(e);
	}
	
	public Entity getCollision(Entity e) {
		for(Entity i : planets) {
			if (i.getTeam() == e.getTeam()) continue;
			Rect rect = i.getRect();
			Rect collision = new Rect(rect.getCenterX(), rect.getCenterY(), 16, 16, true, true);
			if (collision.intersectsRect(e.getRect())) {
				return i;
			}
		}
		for(Entity i : buildings) {
			if (!i.exists() || i.getTeam() == e.getTeam()) continue;
			if (i.getRect().intersectsRect(e.getRect())) {
				return i;
			}
		}
		for(Entity i : projectiles) {
			if (!i.exists() || i.getTeam() == e.getTeam()) continue;
			if (i.getRect().intersectsRect(e.getRect())) {
				return i;
			}
		}
		return null;
	}
	
	public Planet findClosestPlanet(Weapon w) {
		double min = Double.MAX_VALUE;
		Planet minP = null;
		for (Planet i : planets) {
			if (!i.exists()) continue;
			if (i != sun && i.getTeam() != w.getTeam()) {
				double dist = i.getRect().getDistanceSquared(w.getRect());
				if (dist < min) {
					min = dist;
					minP = i;
				}
			}
		}
		return minP;
	}
	
	public Building findClosestBuilding(Weapon w, int type) {
		double min = Double.MAX_VALUE;
		Building minB = null;
		for (Building i : buildings) {
			if (!i.exists()) continue;
			if (i.getBuildingType() == type && i.getTeam() != w.getTeam()) {
				double dist = i.getRect().getDistanceSquared(w.getRect());
				if (dist < min) {
					min = dist;
					minB = i;
				}
			}
		}
		return minB;
	}
	
	public Projectile findClosestProjectile(Weapon w, int type) {
		double min = Double.MAX_VALUE;
		Projectile minP = null;
		for (Projectile i : projectiles) {
			if (!i.exists()) continue;
			if (i.getType() == type && i.getTeam() != w.getTeam()) {
				double dist = i.getRect().getDistanceSquared(w.getRect());
				if (dist < min) {
					min = dist;
					minP = i;
				}
			}
		}
		return minP;
	}
	
	private void renderEntity(Graphics g, Entity e, Camera cam) {
//		if (customCam != null) {
//			e.render(g, customCam);
//		} else {
//			if (fiteon) {
//				fightCam.setClip(g);
//				fightCam.scale(g);
//				e.render(g, fightCam);
//				fightCam.unscale(g);
//				return;
//			}
//			greenCam.setClip(g);
//			greenCam.scale(g);
//			e.render(g, greenCam);
//			greenCam.unscale(g);
//			if (players == 2) {
//				redCam.setClip(g);
//				greenCam.scale(g);
//				e.render(g, redCam);
//				greenCam.unscale(g);
//			}
//		}
		if (!cam.contains(e)) return;
		cam.setClip(g);
		cam.scale(g);
		e.render(g, cam);
		cam.unscale(g);
		return;
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
//		long t0 = System.nanoTime();
		for (Entity i : planets) {
			if (fiteon) {
				renderEntity(g, i, fightCam);
			} else {
				renderEntity(g, i, greenCam);
				if (players == 2) {
					renderEntity(g, i, redCam);
				}
			}
		}
		for (Entity i : buildings) {
			if (fiteon) {
				renderEntity(g, i, fightCam);
			} else {
				renderEntity(g, i, greenCam);
				if (players == 2) {
					renderEntity(g, i, redCam);
				}
			}
		}
		for (Entity i : projectiles) {
			if (fiteon) {
				renderEntity(g, i, fightCam);
			} else {
				renderEntity(g, i, greenCam);
				if (players == 2) {
					renderEntity(g, i, redCam);
				}
			}
		}

		g.clearClip();
		if (!fiteon && players==2) {
			int width = 5;
			g.setLineWidth(width);
			g.setColor(Color.yellow);
			g.drawLine(Screen.width()/2-width/2, 0, Screen.width()/2-width/2, Screen.height());
		}

		if (mapCam != null) {
			g.clearClip();
			g.setColor(Color.black);
			g.fillRect(mapCam.getInterfaceX(0), mapCam.getInterfaceY(0), mapCam.getRect().w, mapCam.getRect().h);
			for (Entity i : planets) {
				renderEntity(g, i, mapCam);
			}
		}
		
		g.clearClip();
		Interface.render(g);
//		System.out.println("Render: " + ((float)(System.nanoTime() - t0) / 1000000f) + " ms.");
		
		if (gameon) {
			g.drawString("X: " + fightCam.getRect().x, Screen.width() - 128, 16);
			g.drawString("Y: " + fightCam.getRect().y, Screen.width() - 128, 48);
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
//		long t0 = System.nanoTime();
		if (closeRequested || container.getInput().isKeyPressed(Input.KEY_ESCAPE)) container.exit();
		if (container.getInput().isKeyPressed(Input.KEY_P)) gameon = !gameon;
		
		if (Interface.MODE != mode) {
			enterMode(Interface.MODE);
		}
		
		if (gameon) {
			performActions();
			
			if (Interface.MODE == Interface.MODE_1PLAYER) {
				Interface.updateAI();
			}
			
			int distance = 400;
			if (green.getRect().getDistance(red.getRect()) < green.getRadius()+red.getRadius() + distance) {	//Slowmo
				fiteon = true;
				currentSpeedMultiple = SLOWMO_SPEED;
				green.setSpeed(GREEN_SPEED*currentSpeedMultiple);
				red.setSpeed(RED_SPEED*currentSpeedMultiple);
			} else {	//Speedup
				fiteon = false;
				currentSpeedMultiple = SPEED_MULTIPLIER * green.getRect().getDistance(red.getRect()) / (double)maxdistance + 5D;
				green.setSpeed(GREEN_SPEED*currentSpeedMultiple);
				red.setSpeed(RED_SPEED*currentSpeedMultiple);
				
//				currentSpeedMultiple = speedMultiple;
//				green.setSpeed(greenSpeed * speedMultiple);
//				red.setSpeed(redSpeed * speedMultiple);
			}
			
			for (Entity e : planets) {
				e.update();
			}
			for (Entity e : buildings) {
				e.update();
			}
			for (Entity e : projectiles) {
				e.update();
			}
			for (Entity e : toRemove) {
				if (e instanceof Planet) {
					planets.remove((Planet)e);
				} else if (e instanceof Building) {
					buildings.remove((Building)e);
				} else if (e instanceof Projectile) {
					projectiles.remove((Projectile)e);
				}
			}
			toRemove.clear();
			for (Entity e : toAdd) {
				if (e instanceof Planet) {
					planets.add((Planet)e);
				} else if (e instanceof Building) {
					buildings.add((Building)e);
				} else if (e instanceof Projectile) {
					projectiles.add((Projectile)e);
				}
			}
			toAdd.clear();
			updateConstruction();
			
			setCamPoses();
		}
//		System.out.println("Update: " + ((float)(System.nanoTime() - t0) / 1000000f) + " ms.");
	}
	
	public static final byte BUILDSLOTS = 2;
	
	private ArrayList<Building> p1_construction = new ArrayList();
	private ArrayList<Building> p2_construction = new ArrayList();
	
	private void performActions() {
		byte[] actions = Interface.getAction();
		
		for (int a = 0; a < BUILDSLOTS; a++) {
			if (actions[a] != 0) {
				Building newConstruction = null;

				switch (actions[a]) {

					case Interface.BUTTON_COMMAND_INF:
						newConstruction = Industry.random(a);
						break;
					case Interface.BUTTON_COMMAND_INF_CELL:
						newConstruction = new Industry.Powercell(a);
						break;
					case Interface.BUTTON_COMMAND_INF_FACTORY:
						newConstruction = new Industry.Factory(a);
						break;
					case Interface.BUTTON_COMMAND_MIL:
						newConstruction = Weapon.random(a);
						break;
					case Interface.BUTTON_COMMAND_MIL_GATLING:
						newConstruction = new Weapon.Gatling(a);
						break;
					case Interface.BUTTON_COMMAND_MIL_MISSILE:
						newConstruction = new Weapon.Battery(a);
						break;
				}

				if (a == 0) {
					if (p1_construction.size() < BUILDSLOTS) {
						p1_construction.add(newConstruction);
						addEntity(newConstruction);
						
						if(newConstruction instanceof Weapon) {
							green.addWeapon((Weapon)newConstruction);
						}
						else if(newConstruction instanceof Industry) {
							green.addIndustry((Industry)newConstruction);
						}
					}
				}
				else {
					if (p2_construction.size() < BUILDSLOTS) {
						p2_construction.add(newConstruction);
						addEntity(newConstruction);
						
						if(newConstruction instanceof Weapon) {
							red.addWeapon((Weapon)newConstruction);
						}
						else if(newConstruction instanceof Industry) {
							red.addIndustry((Industry)newConstruction);
						}
					}
				}
			}
		}
		
		Interface.clearAction();
	}
	
	public ArrayList getBuildSlots(int player) {
		return player == 1 ? p1_construction : player == 2 ? p2_construction : null;
	}
	
	private void updateConstruction() {
		for (Building b : p1_construction) {
			if (b.isConstructed() || !b.exists()) {
				p1_construction.remove(b);
				break;
			}
		}
		for (Building b : p2_construction) {
			if (b.isConstructed() || !b.exists()) {
				p2_construction.remove(b);
				break;
			}
		}
	}
	
	public boolean isActionValid(byte command) {
		
		if(command < 7) {
			if(!planets.contains(green)) {
				return false;
			}
			if(p1_construction.size() == BUILDSLOTS) {
				return false;
			}
			if(green.hasCapacity(command)) {
				return true;
			}
		}
		else if (command < 13) {
			if(!planets.contains(red)) {
				return false;
			}
			if(p2_construction.size() == BUILDSLOTS) {
				return false;
			}
			if(red.hasCapacity((byte)(command-6))) {
				return true;
			}
		}
		
		return false;
	}
}