package state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import game.GamePanel;
import main.MainPanel;
import map.Map;
import player.Player;
import player.PlayerUI;
import util.Vector;
import util.Point;
import util.TransitionManager;

public class GameState extends State{
	
	//this is the state when the player is "in the game"
	
	Map map;
	
	GamePanel gp;
	
	TransitionManager tm;
	public String nextScene;
	public boolean initialLoad = false;
	public boolean loaded = false;
	
	public int stageCounter = 1;
	
	public GameState(GameManager gsm) {
		super(gsm);
		
		this.map = new Map();
		this.gp = new GamePanel(this.map);
		this.tm = new TransitionManager();
		
		this.loadLobby();
		this.tm.changeToLoadingState();
		this.nextScene = "Lobby";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public void loadLobby() {
		this.tm.startTransition();
		this.nextScene = "Lobby";
		this.loaded = false;
	}
	
	public void loadNextStage() {
		this.tm.startTransition();
		this.nextScene = "Dungeon";
		this.loaded = false;
	}

	@Override
	public void tick(java.awt.Point mouse2) {
		if(this.initialLoad) {
			this.gp.tick();
		}
		
		this.tm.tick();
		
		if(this.tm.state == TransitionManager.LOADING_STATE && !this.loaded) {
			if(this.nextScene.equals("Dungeon")) {
				this.map.generateDungeon();
			}
			else if(this.nextScene.equals("Lobby")) {
				this.map.generateLobby();
			}
			
			GameManager.resetEntities(this.map);
			
			this.loaded = true;
			this.initialLoad = true;
			
			this.tm.endTransition();
		}
	}

	@Override
	public void draw(Graphics g) {
		if(this.initialLoad) {
			this.gp.draw(g);
		}
		
		this.tm.draw(g);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		this.gp.keyPressed(arg0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		this.gp.keyReleased(arg0);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		this.gp.mousePressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		this.gp.mouseReleased(arg0);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
