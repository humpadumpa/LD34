
/*
 * Planet.java
 *
 * Dec 12, 2015
 */
package Game.Entities;

import Game.Buildings.Industry;
import Game.Buildings.Weapons.Weapon;
import Game.Buildings.Building;
import static Game.Settings.*;
import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import Game.Buildings.Industry.*;
import Game.Buildings.Weapons.Gatling;
import Game.Buildings.Weapons.MissileBattery;
import Game.Buildings.Weapons.Weapon.*;
import Game.Buildings.Weapons.WeaponRing;
import Game.Camera;
import Game.Interface;
import Game.Main;
import Game.Rect;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

/* 
 * @author Per Eresund
 */
public class Planet extends Orbital {
	private Team team;
	
	private ArrayList<Industry> industries;
	private ArrayList<Weapon> weapons;
	
	private Industry[][] industryLayers;
	private WeaponRing weaponRing;
	
	private int industrySlots;
	
	private Rect rect;
	private Color c;
	private boolean isAlive = true;
	private int maxHp, hp;
	
	private int missileCap;
	private int missiles;
	private int missileProduction;
	
	private int energyCap;
	private int energyDrain;
	
	public Planet() {
		rect = new Rect(0, 0, 0, 0, true);
		team = new Team();
		maxHp = PLANET_SIZE_FACTOR*PLANET_INTEGRITY_FACTOR*8;
		hp = maxHp;
		weaponRing = new WeaponRing();
	}
	
	@Override
	public boolean exists() {
		return isAlive;
	}

	@Override
	public Rect getRect() {
		return rect;
	}
	
	public int getRadius() {
		return rect.w/2;
	}

	@Override
	public double getImgRotation() {
		return 0;
	}

	@Override
	public Team getTeam() {
		return team;
	}

	@Override
	public void setTeam(Team team) {
		this.team = team;
	}
	
	/**
	 * Radius must have been set for this to work as intended when using centered.
	 */
	@Override
	public void setPos(int x, int y, boolean centered) {
		setPos(x, y, getRadius(), centered);
	}
	
	public void setPos(int x, int y, int r, boolean centered) {
		rect.x = x;
		rect.y = y;
		rect.w = r*2;
		rect.h = r*2;
		if (centered) {
			rect.x -= r;
			rect.y -= r;
		}
	}
	
	public void setRadius(int r) {
		rect.w = r*2;
		rect.h = r*2;
		
		initBuildLayers(r);
	}
	
	public void setColor(Color c) {
		this.c = c;
	}
	
	public String getEnergyInfoS() {
		return ("Energy: " + energyDrain + "/" + energyCap);
	}
	public String getMissileInfoS() {
		return ("Missiles: " + missiles + "/" + missileCap + " (+" + missileProduction + ")");
	}
	public String getHpInfoS() {
		return ("Planet: " + hp + "/" + maxHp);
	}
	
	
	
	private void initBuildLayers(int r) {
		industries = new ArrayList<Industry>();
		weapons = new ArrayList<Weapon>();
		
		industryLayers = new Industry[(int)(Math.floor(r/32f))][];
		industryLayers[0] = new Industry[1];
		industrySlots = 1;
		
		for (int i = 1; i < industryLayers.length; i++) {
			industryLayers[i] = new Industry[i*6];
			industrySlots += i*6;
		}
		
//		for (int i = 0; i < industrySlots; i++) {
//			Industry ind = Industry.random(1);
//			addIndustry(ind);
//			Main.game.addEntity(ind);
//		}
//		for (int i = 0; i < industrySlots; i++) {
//			Weapon wep = Weapon.random(1);
//			addWeapon(wep);
//			Main.game.addEntity(wep);
//		}
	}
	
	public boolean hasCapacity(byte buildCommand) {
		
		if ( buildCommand < 4 ) {
			if (industries.size() >= industrySlots) {
				return false;
			}
		}
		switch(buildCommand) {
			case Interface.BUTTON_COMMAND_INF:
			case Interface.BUTTON_COMMAND_INF_CELL:
				return true;
			case Interface.BUTTON_COMMAND_INF_FACTORY:
				if(energyCap-energyDrain >= ENERGY_DRAIN_FACTORY) return true;
				return false;
			case Interface.BUTTON_COMMAND_MIL:
			case Interface.BUTTON_COMMAND_MIL_MISSILE:
				if(energyCap-energyDrain >= ENERGY_DRAIN_BATTERY) return true;
				return false;
			case Interface.BUTTON_COMMAND_MIL_GATLING:
				if(energyCap-energyDrain >= ENERGY_DRAIN_GATLING) return true;
				return false;
				
		}
		return false;
	}
	
	public void constructionComplete(Building b) {
		if (b instanceof Industry) {
			if (b instanceof Powercell) {
				energyCap += ENERGY_CAP_POWERCELL;
			}
			else if (b instanceof Factory) {
				missileCap += MISSILE_CAP_FACTORY;
				missileProduction++;
			}
		}
		else if (b instanceof Weapon) {
			if (b instanceof Gatling) {
			}
			else if (b instanceof MissileBattery) {
				missileCap += MISSILE_CAP_BATTERY;
			}
		}
	}
	
	public void buildingDestroyed(Building b) {
		team.lostBuilding();
		Main.game.removeEntity(b);
		boolean found = false;
		for (int layer = 0; layer < industryLayers.length; layer++) {
			for (int n = 0; n < industryLayers[layer].length; n++) {
				if (industryLayers[layer][n] == b) {
					industryLayers[layer][n] = null;
					found = true;
					break;
				}
			}
			if (found) break;
		}
		
		boolean isConstructed = b.isConstructed();
		if (b.getBuildingType() == Industry.BUILDING_TYPE) {
			industries.remove((Industry)b);
			if (b instanceof Powercell && isConstructed) {
				energyCap -= ENERGY_CAP_POWERCELL;
			}
			else if (b instanceof Factory) {
				if (isConstructed) {
					missileCap -= MISSILE_CAP_FACTORY;
					if (missiles > missileCap) missiles = missileCap;
					missileProduction--;
				}
				energyDrain -= ENERGY_DRAIN_FACTORY;
			}
		}
		else if (b.getBuildingType() == Weapon.BUILDING_TYPE) {
			weaponRing.removeWeapon((Weapon)b);
			weapons.remove((Weapon)b);
			if (b instanceof Gatling) {
				energyDrain -= ENERGY_DRAIN_GATLING;
			}
			else if (b instanceof MissileBattery) {
				if (isConstructed) {
					missileCap -= MISSILE_CAP_BATTERY;
				}
				energyDrain -= ENERGY_DRAIN_BATTERY;
			}
		}
	}
	
	public void addWeapon(Weapon w) {
		double addRadians = 0;
		if (weapons.size() > 0) {
			Weapon referenceWeapon = weapons.get(0);
			double referenceRadians = weaponRing.getWeaponRadians(referenceWeapon);
			double currentRadians = referenceWeapon.getRadians();
			addRadians = currentRadians - referenceRadians;
		}
		
		w.setTeam(team);
		weapons.add(w);
		
		int[] pos = weaponRing.addWeapon(w);
		double radians = weaponRing.getWeaponRadians(pos[0], pos[1]) + addRadians;
		
		spawnWeapon(w, radians);
	}
	
	public void addIndustry(Industry i) {
		i.setTeam(team);
		industries.add(i);
		spawnIndustry(i);
	}
	
	private void spawnWeapon(Weapon w, double radians) {
		if(w instanceof Gatling) {
			energyDrain += ENERGY_DRAIN_GATLING;
		}
		else if (w instanceof MissileBattery) {
			energyDrain += ENERGY_DRAIN_BATTERY;
		}
		
		w.circleAround(
			this,
			getRadius() + w.getRadius(),
			BUILDING_ORBIT_SPEED,
			radians,
			BUILDING_ORBIT_CLOCKWISE,
			1D, 1D
		);
	}
	
	private void spawnIndustry(Industry i) {
		if(i instanceof Factory) {
			energyDrain += ENERGY_DRAIN_FACTORY;
		}
		for (int layer = 0; layer < industryLayers.length; layer++) {
			for (int n = 0; n < industryLayers[layer].length; n++) {
				
				if (industryLayers[layer][n] == null) {
					i.circleAround(this, layer * i.getRadius()*2, BUILDING_ORBIT_SPEED, getRadians(layer)+n*Math.PI*2D/industryLayers[layer].length, true, 1D, 1D);
					industryLayers[layer][n] = i;
					return;
				}
			}
		}
	}
	
	public boolean missilesAvailable()	{ return missiles > 0; }
	public void fireMissile()			{ missiles--; }
	
	public boolean produceMissile() {
		if(missiles < missileCap) {
			missiles++;
			return true;
		}
		return false;
	}
	
	private double getRadians(int layer) {
		
		for (int i = 0; i < industryLayers[layer].length; i++) {
			if (industryLayers[layer][i] != null) {
				return industryLayers[layer][i].getRadians() - i / industryLayers[layer].length * Math.PI*2D;
			}
		}
		return 0;
	}

	@Override
	public boolean collideWith(int damage) {
		hp -= damage;
		if (hp <= 0) {
			Main.game.removeEntity(this);
			isAlive = false;
		}
		return isAlive;
	}
	
	@Override
	public void update() {
		if (!isAlive) return;
		super.update();
	}
	
	private void renderCircle(SGL gl, Camera cam) {
		gl.glColor4f(c.r, c.g, c.b, c.a);
		gl.glVertex2f(cam.getRenderX(rect.getCenterX()), cam.getRenderY(rect.getCenterY()));

		gl.glColor4f(c.r*0.1f, c.g*0.1f, c.b*0.1f, c.a);

		int max = 361;
		float incr = (float)(2D * Math.PI / max);
		for(int i = 0; i < max; i++)
		{
			  float radii = incr * i;

			  float x = (float) Math.cos(radii) * getRadius() + rect.getCenterX();
			  float y = (float) Math.sin(radii) * getRadius() + rect.getCenterY();

			  gl.glVertex2f(cam.getRenderX((int)x), cam.getRenderY((int)y));
		}

		gl.glVertex2f(cam.getRenderX(rect.getCenterX() + getRadius()), cam.getRenderY(rect.getCenterY()));
	}
	
	@Override
	public void render(Graphics g, Camera cam) {
		if (!isAlive) return;
		if (getRadius() == 0 || c == null) {
			System.out.println("Planet was not renderable!");
			return;
		}
		
		c.a = Math.min(1f, (float)hp/(float)maxHp + 0.2f);
		g.setColor(c);
		SGL gl = Renderer.get();
		
		gl.glDisable(SGL.GL_TEXTURE_2D);
		gl.glBegin(SGL.GL_TRIANGLE_FAN);
		renderCircle(gl, cam);
		gl.glEnd();
		gl.glEnable(SGL.GL_TEXTURE_2D);
		
		c.a = 1f;
	}
	
	public static class Team {
		private int planetaryAnnihilations, industryKills, weaponKills, projectilesHit, buildingsLost;
		
		public void earnKill(Entity e) {
			if (e instanceof Planet) {
				planetaryAnnihilations++;
			} else if (e instanceof Building) {
				killedBuilding((Building)e);
			} else if (e instanceof Projectile) {
				projectileHit();
			}
//			printStats();
		}
		
		public void killedBuilding(Building b) {
			int type = b.getBuildingType();
			switch(type) {
				case Industry.BUILDING_TYPE:
					industryKills++;
					break;
				case Weapon.BUILDING_TYPE:
					weaponKills++;
					break;
			}
		}
		
		public void projectileHit() {
			projectilesHit++;
		}
		
		public void lostBuilding() {
			buildingsLost++;
			printStats();
		}
		
		public void printStats() {
			System.out.println("Planetary Annihilations: " + planetaryAnnihilations);
			System.out.println("Industry Kills: " + industryKills);
			System.out.println("Weapon Kills: " + weaponKills);
			System.out.println("Projectiles Hit: " + projectilesHit);
			System.out.println("Buildings Lost: " + buildingsLost);
		}
	}
}