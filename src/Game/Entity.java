/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Game.Planet.Team;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Humpadumpa
 */
public interface Entity {
	public Rect getRect();
	public boolean exists();
	public boolean collideWith(int damage);
	public void setPos(int x, int y, boolean centered);
	public void update();
	public void render(Graphics g, Camera cam);
	public Team getTeam();
	public void setTeam(Team team);
	public double getDeltaX();
	public double getDeltaY();
}
