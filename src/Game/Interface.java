   /*
    *   Interface.java
    *
    *   Dec 13, 2015
    */
package Game;

import Game.AI.AI_masterDrone;
import Game.Entities.Planet;
import Game.Buildings.Building;
import static Game.Game.BUILDSLOTS;
import static Game.Settings.*;
import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

/**
 *
 * @author J
 */
public class Interface implements KeyListener {
	public static final int MODE_NONE = 0;
	public static final int MODE_MENU = 1;
	public static final int MODE_1PLAYER = 2;
	public static final int MODE_2PLAYERS = 3;
	
	public static int MODE = 0;
	
	public static boolean USE_BAD_PROJECTILE_PHYSICS = false;
	public static boolean showReachLines = false;
	
	private static AI_masterDrone AI;
	
	private static final int AI_B1 = 9999;
	private static final int AI_B2 = 10000;
	
	public static final byte BUTTON_COMMAND_INF = 1;
	public static final byte BUTTON_COMMAND_INF_CELL = 2;
	public static final byte BUTTON_COMMAND_INF_FACTORY = 3;
	
	public static final byte BUTTON_COMMAND_MIL = 4;
	public static final byte BUTTON_COMMAND_MIL_GATLING = 5;
	public static final byte BUTTON_COMMAND_MIL_MISSILE = 6;
	
	public static final byte BUTTON_P2_COMMAND_INF = 7;
	public static final byte BUTTON_P2_COMMAND_INF_CELL = 8;
	public static final byte BUTTON_P2_COMMAND_INF_FACTORY = 9;
	
	public static final byte BUTTON_P2_COMMAND_MIL = 10;
	public static final byte BUTTON_P2_COMMAND_MIL_GATLING = 11;
	public static final byte BUTTON_P2_COMMAND_MIL_MISSILE = 12;
	
	private static final float P1_BUTTON_POSITION = 0.25f;
	private static final float P2_BUTTON_POSITION = 0.75f;
	
	private static final float BUTTON_POSITION_Y = 0.80f;
	private static final int BUTTON_WIDTH = 80;
	private static final int BUTTON_MARGIN = 80;
	private static final int BUTTON_PRESS_OFFSET = 5;
	
	private static long p1_timeSinceAction;
	private static long p2_timeSinceAction;
	
	private static byte lastAction[] = new byte[BUILDSLOTS];
	private static Button[] buttons;
	
	private static final String[] MENUTEXT = new String[]{
		"One-player game",
		"Two-player game",
		"Highscore",
		"About",
		"Exit"
	};
	
	private static final String[] BUTTONTEXT = new String[]{
		"Infrastructure",
		"Powercell",
		"Missile factory",
		"Military",
		"Gatling turret",
		"Missile battery",
		"Next option",
		"Select"
	};
	
	private static int menu_selection = 0;
	static boolean tacticalControl = true;
	
	
	public static void setup(int newMode) {
		MODE = newMode;
		switch (MODE) {
			case MODE_NONE:
				buttons = null;
				break;
			case MODE_MENU:
				buttons = new Button[] {
					new Button("S", BUTTONTEXT[6], Screen.width()/2-BUTTON_WIDTH-BUTTON_MARGIN/2, (int)(Screen.height()*BUTTON_POSITION_Y)),
					new Button("D", BUTTONTEXT[7], Screen.width()/2+BUTTON_MARGIN/2, (int)(Screen.height()*BUTTON_POSITION_Y))
				};
				
				break;
			case MODE_1PLAYER:
			
				AI = new AI_masterDrone(AI_B1, AI_B2);
				
				int p1_b1_x = Screen.width()/2-BUTTON_WIDTH-BUTTON_MARGIN/2;
				int p1_b2_x = Screen.width()/2+BUTTON_MARGIN/2;
				int AI_b_x = -1000;
				
				makeButtons(p1_b1_x, p1_b2_x, AI_b_x, AI_b_x);
				break;
				
			case MODE_2PLAYERS:
				
				p1_b1_x = (int)(Screen.width()*P1_BUTTON_POSITION-BUTTON_WIDTH-BUTTON_MARGIN/2);
				p1_b2_x = (int)(Screen.width()*P1_BUTTON_POSITION+BUTTON_MARGIN/2);
				int p2_b1_x = (int)(Screen.width()*P2_BUTTON_POSITION-BUTTON_WIDTH-BUTTON_MARGIN/2);
				int p2_b2_x = (int)(Screen.width()*P2_BUTTON_POSITION+BUTTON_MARGIN/2);
				
				makeButtons(p1_b1_x, p1_b2_x, p2_b1_x, p2_b2_x);
				
				break;
		}
	}
	
	private static void makeButtons(int p1_b1_x, int p1_b2_x, int p2_b1_x, int p2_b2_x) {
		
		buttons = new Button[] {
			new Button(
					"S", 
					BUTTONTEXT[0], 
					p1_b1_x, 
					(int)(Screen.height()*BUTTON_POSITION_Y)),
			new Button(
					"D", 
					BUTTONTEXT[3], 
					p1_b2_x, 
					(int)(Screen.height()*BUTTON_POSITION_Y)),
			new Button(
					"K",
					BUTTONTEXT[0], 
					p2_b1_x, 
					(int)(Screen.height()*BUTTON_POSITION_Y)),
			new Button(
					"L", 
					BUTTONTEXT[3], 
					p2_b2_x, 
					(int)(Screen.height()*BUTTON_POSITION_Y))
		};

		buttons[0].setOther(buttons[1]);
		buttons[1].setOther(buttons[0]);

		buttons[2].setOther(buttons[3]);
		buttons[3].setOther(buttons[2]);

		if(tacticalControl) {
			buttons[0].addSecondary(
					new Button(
							"S", 
							BUTTONTEXT[1], 
							p1_b1_x,
							(int)(Screen.height()*BUTTON_POSITION_Y), 
							BUTTON_COMMAND_INF_CELL, 
							buttons[0]),
					new Button(
							"S", 
							BUTTONTEXT[4], 
							p1_b1_x,
							(int)(Screen.height()*BUTTON_POSITION_Y), 
							BUTTON_COMMAND_MIL_GATLING,
							buttons[0])
			);

			buttons[1].addSecondary(
					new Button(
							"D", 
							BUTTONTEXT[5], 
							p1_b2_x,
							(int)(Screen.height()*BUTTON_POSITION_Y), 
							BUTTON_COMMAND_MIL_MISSILE, 
							buttons[1]),
					new Button(
							"D", 
							BUTTONTEXT[2], 
							p1_b2_x,
							(int)(Screen.height()*BUTTON_POSITION_Y),
							BUTTON_COMMAND_INF_FACTORY, 
							buttons[1])
			);

			buttons[2].addSecondary(
					new Button(
							"K",
							BUTTONTEXT[1], 
							p2_b1_x, 
							(int)(Screen.height()*BUTTON_POSITION_Y),
							BUTTON_P2_COMMAND_INF_CELL,
							buttons[2]),
					new Button(
							"K",
							BUTTONTEXT[4], 
							p2_b1_x, 
							(int)(Screen.height()*BUTTON_POSITION_Y),
							BUTTON_P2_COMMAND_MIL_GATLING,
							buttons[2])
			);

			buttons[3].addSecondary(
					new Button(
							"L", 
							BUTTONTEXT[5], 
							p2_b2_x, 
							(int)(Screen.height()*BUTTON_POSITION_Y),
							BUTTON_P2_COMMAND_MIL_MISSILE, 
							buttons[3]),
					new Button(
							"L", 
							BUTTONTEXT[2],
							p2_b2_x, 
							(int)(Screen.height()*BUTTON_POSITION_Y),
							BUTTON_P2_COMMAND_INF_FACTORY, 
							buttons[3])
			);


			buttons[0].secondaryButtonThis.setOther(buttons[1].secondaryButtonOther);
			buttons[0].secondaryButtonOther.setOther(buttons[1].secondaryButtonThis);

			buttons[1].secondaryButtonThis.setOther(buttons[0].secondaryButtonOther);
			buttons[1].secondaryButtonOther.setOther(buttons[0].secondaryButtonThis);

			buttons[2].secondaryButtonThis.setOther(buttons[3].secondaryButtonOther);
			buttons[2].secondaryButtonOther.setOther(buttons[3].secondaryButtonThis);

			buttons[3].secondaryButtonThis.setOther(buttons[2].secondaryButtonOther);
			buttons[3].secondaryButtonOther.setOther(buttons[2].secondaryButtonThis);
		}
		else {
			buttons[0].setCommand(BUTTON_COMMAND_INF);
			buttons[1].setCommand(BUTTON_COMMAND_MIL);
			buttons[2].setCommand(BUTTON_P2_COMMAND_INF);
			buttons[3].setCommand(BUTTON_P2_COMMAND_MIL);
		}
	}
	
	public static void updateAI() {
		if (MODE == MODE_1PLAYER) {
			if (p2_timeSinceAction + ACTION_COOLDOWN_MILLIS < System.currentTimeMillis()) {
				AI.update();
			}
		}
	}
	public static void render(Graphics g) {
		switch(MODE) {
			case MODE_NONE:
				return;
			case MODE_MENU:
				for(int i = 0; i < MENUTEXT.length; i++) {
					if(i == menu_selection) {
						g.setColor(Color.orange);
					}
					else {
						g.setColor(Color.gray);
					}
					g.fillRect(Screen.width()/2-100, 200+i*70, 200, 50);
					
					g.setColor(Color.white);
					g.drawString(MENUTEXT[i], Screen.width()/2-g.getFont().getWidth(MENUTEXT[i])/2, 200+i*70+10);
				}
				
				for (int i = 0; i < 2; i++) {
					buttons[i].render(g);
				}
				break;
				
				
			case MODE_1PLAYER:
				
				drawResourceOverview(g, 1);
				drawBuildSlots(g, 1);
				
				for (int i = 0; i < 2; i++) {
					buttons[i].render(g);
				}
				break;
				
				
			case MODE_2PLAYERS:
				
				drawResourceOverview(g, 2);
				drawBuildSlots(g, 2);
				
				for (int i = 0; i < 4; i++) {
					buttons[i].render(g);
				}
				break;
		}
	}
	
	private static void drawResourceOverview(Graphics g, int players) {
		for (int i = 0; i < players; i++) {
			float xFactor = i*(Screen.width()-350);
			
			g.setColor(Color.gray);
			g.fillRoundRect(50+xFactor, 50, 250, 150, 32);
			
			Planet currentHome = i == 0 ? Main.game.green : Main.game.red;
			
			g.setColor(Color.yellow);
			g.drawString(currentHome.getHpInfoS(), 70+xFactor, 70);
			g.drawString(currentHome.getEnergyInfoS(), 70+xFactor, 110);
			g.drawString(currentHome.getMissileInfoS(), 70+xFactor, 150);

		}
	}
	
	private static void drawBuildSlots(Graphics g, int players) {
		
		float xFactor = 0.25f/players;
		
		for (int i = 0; i < players; i++) {
			ArrayList<Building> slots = Main.game.getBuildSlots(i+1);
			for (int j = 0; j < slots.size(); j++) {
				
				float xPos = i*0.75f*Screen.width()+Screen.width()*xFactor-25f;
				float yPos = Screen.height()*BUTTON_POSITION_Y+(j*60f);
				
				g.setColor(Color.yellow);
				g.fillRoundRect(xPos-2, yPos-2, 50, 50, 12);

				g.setColor(new Color(192, 220, 192));
				g.fillRoundRect(xPos, yPos, 46, 46, 12);
				
				g.setColor(Color.white);
				g.fillOval(xPos+3, yPos+3, 40, 40);
				Building b = slots.get(j);
				float progress = (float)(b.getHp()+Settings.BUILD_HP)/(float)Settings.MAXHP;
				g.drawImage(b.getImage(), xPos+7, yPos+7, (float)xPos+7f+32f*progress, yPos+7+32, 0, 0, 32f*progress, 32);
				
			}
		}
	}
	
	private static void menu(int key) {
		if(key == P1_B1) {
			menu_selection++;
			if(menu_selection >= MENUTEXT.length) {
				menu_selection = 0;
			}
		}
		else if(key == P1_B2) {
			switch(menu_selection) {
				case 0:
					setup(MODE_1PLAYER);
					return;
				case 1:
					setup(MODE_2PLAYERS);
					return;
				case 2:
					
					break;
				case 3:
					
					break;
				case 4:
					Main.game.requestClose();
			}
		}
	}

	private static void clickButton(byte buttonCommand) {
		if(buttonCommand < 1) {
			// nope
		}
		else if(buttonCommand < 7) {
			if(lastAction[0] == 0) {
				lastAction[0] = buttonCommand;
				p1_timeSinceAction = System.currentTimeMillis();
			}
		}
		else if(buttonCommand < 13) {
			if(lastAction[1] == 0) {
				lastAction[1] = (byte)(buttonCommand-6);
				p2_timeSinceAction = System.currentTimeMillis();
			}
		}
		
	}
	
	static byte[] getAction() {
		return lastAction;
	}
	
	static void clearAction() {
		lastAction = new byte[BUILDSLOTS];
	}

	@Override
	public void keyPressed(int key, char c) {
		switch(MODE) {
			case MODE_MENU:
				if(key == P1_B1) {
					buttons[0].buttonHold();
				}
				else if(key == P1_B2) {
					buttons[1].buttonHold();
				}
				break;
			case MODE_2PLAYERS:
				if(key == P2_B1 && buttons[2].isActionValid()) {
					buttons[2].buttonHold();
				}
				else if(key == P2_B2 && buttons[3].isActionValid()) {
					buttons[3].buttonHold();
				}
			case MODE_1PLAYER:
				if(key == P1_B1 && buttons[0].isActionValid()) {
					buttons[0].buttonHold();
				}
				else if(key == P1_B2 && buttons[1].isActionValid()) {
					buttons[1].buttonHold();
				}
				break;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		switch(MODE) {
			case MODE_MENU:
				if(key == P1_B1 || key == P1_B2) {
					buttons[0].resetHold();
					buttons[1].resetHold();
					menu(key);
				}
				break;
			case MODE_2PLAYERS:
				if(key == P2_B1 && buttons[2].isActionValid()) {
					buttons[2].activate();
				}
				else if(key == P2_B2 && buttons[3].isActionValid()) {
					buttons[3].activate();
				}
			case MODE_1PLAYER:
				if (key == P1_B1 && buttons[0].isActionValid()) {
					buttons[0].activate();
				}
				else if (key == P1_B2 && buttons[1].isActionValid()) {
					buttons[1].activate();
				}
				else if (key == AI_B1 && buttons[2].isActionValid()) {
					buttons[2].activate();
				}
				else if (key == AI_B2 && buttons[3].isActionValid()) {
					buttons[3].activate();
				}
				else if (key == Input.KEY_I) {
					USE_BAD_PROJECTILE_PHYSICS = !USE_BAD_PROJECTILE_PHYSICS;
					System.out.println("Set showReachCircles to " + USE_BAD_PROJECTILE_PHYSICS);
				}
				else if (key == Input.KEY_O) {
					showReachLines = !showReachLines;
					System.out.println("Set showReachLines to " + showReachLines);
				}
				break;
		}
	}

	@Override
	public void setInput(Input input) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void inputEnded() {
		
	}

	@Override
	public void inputStarted() {
		
	}
	
	
	
	private static class Button {
		private final String key;
		private final String text;
		
		private int positionX, positionY;
		private boolean buttonDown = false;
		
		private byte buttonCommand;
		
		private Button otherButton;
		public Button secondaryButtonThis;
		public Button secondaryButtonOther;
		
		Button(String key, String text, int positionX, int positionY) {
			this.key = key;
			this.text = text;
			this.positionX = positionX;
			this.positionY = positionY;
		}
		Button(String key, String text, int positionX, int positionY, byte command, Button parent) {
			this.key = key;
			this.text = text;
			this.positionX = positionX;
			this.positionY = positionY;
			buttonCommand = command;
			secondaryButtonOther = secondaryButtonThis = parent;
		}
		
		public void setCommand(byte command) {
			buttonCommand = command;
		}
		public void setOther(Button other) {
			otherButton = other;
		}
		public void addSecondary(Button sbt, Button sbo) {
			secondaryButtonThis = sbt;
			secondaryButtonOther = sbo;
		}
		
		public void updatePosition(int positionX, int positionY) {
			this.positionX = positionX;
			this.positionY = positionY;
		}
		
		public void buttonHold() {
			buttonDown = true;
		}
		public void resetHold() {
			buttonDown = false;
		}
		
		public void activate() {
			resetHold();
			if(tacticalControl) {
				for(int i = 0; i < buttons.length; i++) {
					if(buttons[i] == this) {
						buttons[i] = secondaryButtonThis;
						break;
					}
				}
			}
			otherButton.otherActivate();
			
			Interface.clickButton(buttonCommand);
		}
		
		public void otherActivate() {
			resetHold();
			if(tacticalControl) {
				for(int i = 0; i < buttons.length; i++) {
					if(buttons[i] == this) {
						buttons[i] = secondaryButtonOther;
						break;
					}
				}
			}
		}
		
		private boolean isActionValid() {
			long timeAtPlayerAction;
			if (buttonCommand == 0) {
				if (secondaryButtonThis.buttonCommand < 7) {
					timeAtPlayerAction = p1_timeSinceAction;
				}
				else {
					timeAtPlayerAction = p2_timeSinceAction;
				}
			}
			else if (buttonCommand < 7) {
				timeAtPlayerAction = p1_timeSinceAction;
			}
			else {
				timeAtPlayerAction = p2_timeSinceAction;
			}
			
			if (System.currentTimeMillis()-ACTION_COOLDOWN_MILLIS > timeAtPlayerAction) {
				return Main.game.isActionValid(buttonCommand == 0 ? secondaryButtonThis.buttonCommand : buttonCommand);
			}
			return false;
		}
		
		public void render(Graphics g) {
			if(MODE != MODE_MENU && !isActionValid()) {
				g.setColor(Color.red);
				g.fillRoundRect(positionX, positionY, BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_WIDTH/5);
			}
			else if(buttonDown) {
				g.setColor(Color.darkGray);
				g.fillRoundRect(positionX+BUTTON_PRESS_OFFSET, positionY+BUTTON_PRESS_OFFSET, BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_WIDTH/5);
			}
			else {
				g.setColor(Color.gray);
				g.fillRoundRect(positionX, positionY, BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_WIDTH/5);
			}
			g.setColor(Color.yellow);
			g.drawString(key, positionX+30, positionY+30);
			g.drawString(text, positionX+BUTTON_WIDTH/2-g.getFont().getWidth(text)/2, positionY-20);
		}
	}
}