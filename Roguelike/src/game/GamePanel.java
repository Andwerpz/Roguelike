package game;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import map.Map;
import particle.Particle;
import projectile.Projectile;
import state.GameManager;
import util.Point;
import util.Vector;
import item.Coin;
import item.Item;
import main.MainPanel;

import java.util.ArrayList;

import enemy.Enemy;

public class GamePanel {
	
	//updating entity position and drawing to screen
	
	public Map map;
	
	public ArrayList<Item> items;
	public ArrayList<Enemy> enemies;
	public ArrayList<Particle> particles;
	public ArrayList<Projectile> projectiles;
	
	public GamePanel(Map map) {
		this.map = map;
		
		this.items = new ArrayList<>();
		this.enemies = new ArrayList<>();
		this.particles = new ArrayList<>();
		this.projectiles = new ArrayList<>();
		
	}
	
	public void tick() {
		if(this.items.size() != 10) {
			this.items.add(new Coin(new Vector(30, 30)));
		}
		
		GameManager.player.tick(map);
		
		//calculate new camera offset
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
		
		for(int index = 0; index < items.size(); index ++) {
			Item i = items.get(index);
			i.tick(map);
			if(i.collision(GameManager.player) && i.autoPickup) {
				i.onPickup();
				items.remove(i);
				index--;
			}
		}
		
		for(Enemy e : enemies) {
			e.tick(map);
		}
		
		for(Particle p : particles) {
			p.tick(map);
		}
		
		for(Projectile p : projectiles) {
			p.tick(map);
		}
		
	}
	
	public void draw(Graphics g) {
		this.map.drawBackground(g);
		this.map.drawFloor(g);
		
		//draw shadows first
		for(Item i : items) {
			i.drawShadow(g);
		}
		for(Enemy e : enemies) {
			e.drawShadow(g);
		}
		for(Particle p : particles) {
			p.drawShadow(g);
		}
		for(Projectile p : projectiles) {
			p.drawShadow(g);
		}
		GameManager.player.drawShadow(g);
		
		this.map.drawWalls(g);
		
		//draw sprites
		for(Item i : items) {
			i.draw(g);
		}
		for(Enemy e : enemies) {
			e.draw(g);
		}
		for(Particle p : particles) {
			p.draw(g);
		}
		for(Projectile p : projectiles) {
			p.draw(g);
		}
		GameManager.player.draw(g);
	}
	
	public void keyPressed(KeyEvent arg0) {
		GameManager.player.keyPressed(arg0.getKeyCode());
	}

	public void keyReleased(KeyEvent arg0) {
		GameManager.player.keyReleased(arg0.getKeyCode());
	}

}
