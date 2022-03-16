package state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import game.GamePanel;
import main.MainPanel;
import map.Map;
import player.Player;
import util.Vector;
import util.Point;

public class GameState extends State{
	
	Map map;
	GamePanel gp;
	
	public GameState(GameManager gsm) {
		super(gsm);
		
		GameManager.player = new Player(new Vector(23, 23));
		this.map = new Map();
		this.gp = new GamePanel(this.map);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
