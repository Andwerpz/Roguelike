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

public class GameState extends State{
	
	//this is the state when the player is "in the game"
	
	Map map;
	GamePanel gp;
	
	public int stageCounter = 1;
	
	public GameState(GameManager gsm) {
		super(gsm);
		
		this.map = new Map();
		this.gp = new GamePanel(this.map);
		
		this.nextStage();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public void nextStage() {
		this.map.generateMap();
		GameManager.player.pos = new Vector(this.map.playerStartX, this.map.playerStartY);
		
		GameManager.items = new ArrayList<>();
		GameManager.enemies = new ArrayList<>();
		GameManager.particles = new ArrayList<>();
		GameManager.projectiles = new ArrayList<>();
		GameManager.decorations = new ArrayList<>();
		
		PlayerUI.resetMinimap(map);
		
		GameManager.decorations.addAll(this.map.mapDecorations);
	}

	@Override
	public void tick(java.awt.Point mouse2) {
		this.gp.tick();
	}

	@Override
	public void draw(Graphics g) {
		this.gp.draw(g);
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
