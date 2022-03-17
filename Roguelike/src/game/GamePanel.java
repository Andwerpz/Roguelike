package game;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import map.Map;
import particle.Particle;
import projectile.LongBullet;
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
	
	public GamePanel(Map map) {
		this.map = map;
		
		GameManager.items = new ArrayList<>();
		GameManager.enemies = new ArrayList<>();
		GameManager.particles = new ArrayList<>();
		GameManager.projectiles = new ArrayList<>();
		
	}
	
	public void tick() {
		if(GameManager.items.size() != 10) {
			GameManager.items.add(new Coin(new Vector(30, 30)));
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
		
		for(int index = 0; index < GameManager.items.size(); index ++) {
			Item i = GameManager.items.get(index);
			i.tick(map);
			if(i.collision(GameManager.player) && i.autoPickup) {
				i.onPickup();
				GameManager.items.remove(i);
				index--;
			}
		}
		
		for(Enemy e : GameManager.enemies) {
			e.tick(map);
		}
		
		for(Particle p : GameManager.particles) {
			p.tick(map);
		}
		
		for(int index = 0; index < GameManager.projectiles.size(); index++) {
			Projectile p = GameManager.projectiles.get(index);
			p.tick(map);
			if(p.envCollision) {
				p.despawn();
				GameManager.projectiles.remove(p);
				index --;
			}
		}
		
	}
	
	public void draw(Graphics g) {
		this.map.drawBackground(g);
		this.map.drawFloor(g);
		
		//draw shadows first
		for(Item i : GameManager.items) {
			i.drawShadow(g);
		}
		for(Enemy e : GameManager.enemies) {
			e.drawShadow(g);
		}
		for(Particle p : GameManager.particles) {
			p.drawShadow(g);
		}
		for(Projectile p : GameManager.projectiles) {
			p.drawShadow(g);
		}
		GameManager.player.drawShadow(g);
		
		this.map.drawWalls(g);
		
		//draw sprites
		for(Item i : GameManager.items) {
			i.draw(g);
		}
		for(Enemy e : GameManager.enemies) {
			e.draw(g);
		}
		for(Particle p : GameManager.particles) {
			p.draw(g);
		}
		for(Projectile p : GameManager.projectiles) {
			p.draw(g);
		}
		GameManager.player.draw(g);
	}
	
	public void mousePressed(MouseEvent arg0) {
		GameManager.player.mousePressed(arg0);
	}
	
	public void mouseReleased(MouseEvent arg0) {
		GameManager.player.mouseReleased(arg0);
	}
	
	public void keyPressed(KeyEvent arg0) {
		GameManager.player.keyPressed(arg0.getKeyCode());
	}

	public void keyReleased(KeyEvent arg0) {
		GameManager.player.keyReleased(arg0.getKeyCode());
	}

}
