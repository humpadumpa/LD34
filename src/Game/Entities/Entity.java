
package Game.Entities;

import Game.Camera;
import Game.Entities.Planet.Team;
import Game.Rect;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Per Eresund
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
