package game;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import map.EnemyEncounter;
import map.Map;
import particle.Particle;
import player.PlayerUI;
import projectile.LongBullet;
import projectile.Projectile;
import state.GameManager;
import util.Point;
import util.Vector;
import item.Coin;
import item.Item;
import main.MainPanel;

import java.util.ArrayList;

import decoration.Chest;
import decoration.Decoration;
import enemy.DarkKnight;
import enemy.Enemy;
import enemy.Grunt;
import entity.Entity;

public class GamePanel {
	
	//updating entity position and drawing to screen
	
	public Map map;
	
	public EnemyEncounter activeEncounter = null;
	
	public GamePanel(Map map) {
		this.map = map;	
	}
	
	public void tick() {
		
		if(this.activeEncounter == null) {
			//if no active encounter, look if the player has triggered another one
			for(EnemyEncounter e : this.map.enemyEncounters) {
				if(e.contains(GameManager.player)) {
					this.activeEncounter = e;
					break;
				}
			}
			this.map.enemyEncounters.remove(this.activeEncounter);
			
			//remove door tile from list of collidable tiles
			Entity.wallTiles.remove(2);
		}
		else {
			//handle the current encounter
			if(GameManager.enemies.size() == 0) {
				if(this.activeEncounter.hasMoreEnemies()) {
					this.activeEncounter.spawnEnemies();
				}
				else {
					this.activeEncounter = null;
				}
			}
			
			//add door tile to list of collidable tiles
			Entity.wallTiles.add(2);
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
		
		for(int index = GameManager.items.size() - 1; index >= 0; index --) {
			Item i = GameManager.items.get(index);
			i.tick(map);
		}
		
		for(int index = GameManager.enemies.size() - 1; index >= 0; index --) {
			Enemy e = GameManager.enemies.get(index);
			e.tick(map);
		}
		
		for(int index = GameManager.particles.size() - 1; index >= 0; index --) {
			Particle p = GameManager.particles.get(index);
			p.tick(map);
		}
		
		for(int index = GameManager.projectiles.size() - 1; index >= 0; index --) {
			Projectile p = GameManager.projectiles.get(index);
			p.tick(map);
		}
		
		for(int index = GameManager.decorations.size() - 1; index >= 0; index --) {
			Decoration d = GameManager.decorations.get(index);
			d.tick(map);
		}
		
		//check if player died
		if(GameManager.player.health <= 0) {
			GameManager.loadLobby();
			GameManager.player.reset();
		}
	}
	
	public void draw(Graphics g) {
		this.map.drawBackground(g);
		this.map.drawFloor(g);
		this.map.drawWalls(g);
		
		//draw shadows first
		for(int index = GameManager.items.size() - 1; index >= 0; index--) {
			if(index >= GameManager.items.size()) {
				continue;
			}
			Item i = GameManager.items.get(index);
			i.drawShadow(g);
		}
		for(int index = GameManager.enemies.size() - 1; index >= 0; index --) {
			if(index >= GameManager.enemies.size()) {
				continue;
			}
			Enemy e = GameManager.enemies.get(index);
			e.drawShadow(g);
		}
		for(int index = GameManager.decorations.size() - 1; index >= 0; index--) {
			if(index >= GameManager.decorations.size()) {
				continue;
			}
			Decoration d = GameManager.decorations.get(index);
			d.drawShadow(g);
		}
		GameManager.player.drawShadow(g);
		
		ArrayList<Entity> drawnEntities = new ArrayList<Entity>();
		drawnEntities.addAll(GameManager.decorations);
		drawnEntities.addAll(GameManager.items);
		drawnEntities.addAll(GameManager.enemies);
		drawnEntities.add(GameManager.player);
		
		drawnEntities.sort((a, b) -> Double.compare(a.pos.y + a.height / 2, b.pos.y + b.height / 2));
		
		//draw sprites
		for(int index = 0; index < drawnEntities.size(); index++) {
			Entity e = drawnEntities.get(index);
			e.draw(g);
		}
		
		for(int index = GameManager.particles.size() - 1; index >= 0; index --) {
			if(index >= GameManager.particles.size()) {
				continue;
			}
			Particle p = GameManager.particles.get(index);
			p.draw(g);
		}
		for(int index = GameManager.projectiles.size() - 1; index >= 0; index--) {
			if(index >= GameManager.projectiles.size()) {
				continue;
			}
			Projectile p = GameManager.projectiles.get(index);
			p.draw(g);
		}
		
		//Player UI
		PlayerUI.drawHealthBar(20, 20, g);
		PlayerUI.drawShieldBar(20, 50, g);
		PlayerUI.drawCoinDisplay(20, 80, g);
		
		PlayerUI.updateMinimap(this.map);
		PlayerUI.drawMinimap(g, 1600, 20);
		
		for(EnemyEncounter e : map.enemyEncounters) {
			e.draw(g);
		}
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
