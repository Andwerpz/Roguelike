package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Stack;

import enemy.Enemy;
import item.Item;
import particle.Particle;
import player.Player;
import projectile.Projectile;
import util.Vector;
import weapon.Weapon;

public class GameManager {
	
	public static util.Point mouse;

	public Stack<State> states;
	
	public static Vector cameraOffset = new Vector(0, 0);
	public static Player player = new Player(new Vector(0, 0));
	public static final int tileSize = 48;	//screen pixel size of each tile
	public static final int pixelSize = 3;	//size of each pixel in screen pixels
	
	public static ArrayList<Item> items;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<Particle> particles;
	public static ArrayList<Projectile> projectiles;
	
	public GameManager() {
		
		Player.loadTextures();
		Item.loadTextures();
		Projectile.loadTextures();
		Weapon.loadTextures();
		
		states = new Stack<State>();
		states.push(new GameState(this));
		
	}
	
	public void tick(Point mouse2) {
		GameManager.mouse = new util.Point(mouse2.x, mouse2.y);
		states.peek().tick(mouse2);
	}
	
	public void draw(Graphics g) {
		
		states.peek().draw(g);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Dialogue", Font.PLAIN, 12));
		g.drawString((int) (mouse.x) + "", (int) (mouse.x - 30), (int) (mouse.y - 10));
		g.drawString((int) (mouse.y) + "", (int) (mouse.x), (int) (mouse.y - 10));
		
	}
	
	public void keyPressed(KeyEvent arg0) {
		states.peek().keyPressed(arg0);
	}
	
	public void keyReleased(KeyEvent arg0) {
		states.peek().keyReleased(arg0);
	}
	
	public void keyTyped(KeyEvent arg0) {
		states.peek().keyTyped(arg0);
	}
	
	public void mouseClicked(MouseEvent arg0) {
		states.peek().mouseClicked(arg0);
	}

	public void mouseEntered(MouseEvent arg0) {
		states.peek().mouseEntered(arg0);
	}

	public void mouseExited(MouseEvent arg0) {
		states.peek().mouseExited(arg0);
	}

	public void mousePressed(MouseEvent arg0) {
		states.peek().mousePressed(arg0);
	}

	public void mouseReleased(MouseEvent arg0) {
		states.peek().mouseReleased(arg0);
	}
	
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		states.peek().mouseWheelMoved(arg0);
	}
	
}
