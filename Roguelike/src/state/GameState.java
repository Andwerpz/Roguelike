package state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import main.MainPanel;
import map.Map;
import player.Player;
import util.Vector;
import util.Point;

public class GameState extends State{
	
	Map map;
	
	public GameState(GameManager gsm) {
		super(gsm);
		
		GameManager.player = new Player(new Vector(132, 132));
		this.map = new Map();
	
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(java.awt.Point mouse2) {
		
		//calculate new camera offset
		GameManager.player.mouse = mouse2;
		GameManager.player.tick(map);
		Point playerPos = new Point((GameManager.player.pos.x + GameManager.player.envHitbox.width / 2) * GameManager.tileSize, (GameManager.player.pos.y + GameManager.player.envHitbox.length / 2) * GameManager.tileSize);	
		Vector nextCameraOffset = new Vector(new Point(0, 0), playerPos);
		nextCameraOffset.subtractVector(new Vector(MainPanel.WIDTH / 2, MainPanel.HEIGHT / 2));
		Vector oldToNew = new Vector(new Point(GameManager.cameraOffset), new Point(nextCameraOffset));
		oldToNew.multiply(0.15);	//the tracking speed of the camera
		GameManager.cameraOffset.addVector(oldToNew);
		//the point at where the camera will be centered around
		//enable this if you want the camera to lock onto the player
		//GameManager.cameraOffset = new Vector(new Point(0, 0), playerPos);	//vector from player location to center of screen
		
		//System.out.println(GameManager.cameraOffset.x + " " + GameManager.cameraOffset.y);
		//System.out.println(GameManager.player.pos.x + " " + GameManager.player.pos.y);
	}

	@Override
	public void draw(Graphics g) {
		
		this.map.draw(g);
		GameManager.player.draw(g);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		GameManager.player.keyPressed(arg0.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		GameManager.player.keyReleased(arg0.getKeyCode());
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
