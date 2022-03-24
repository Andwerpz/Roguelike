package map;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Stack;

import enemy.DarkKnight;
import enemy.Enemy;
import enemy.Grunt;
import entity.Entity;
import entity.Hitbox;
import state.GameManager;
import util.Point;
import util.Vector;

public class EnemyEncounter {
	
	//rectangle that should contain a room
	//handles all enemy encounter related stuff
	
	public Stack<ArrayList<Enemy>> waves;
	
	public Vector pos;	//upper left corner
	public double width, height;

	public EnemyEncounter(Vector pos, double width, double height) {
		this.pos = new Vector(pos);
		this.width = width;
		this.height = height;
		
		this.waves = new Stack<>();
		this.waves.add(new ArrayList<>());
	}
	
	public boolean contains(Entity e) {
		double minX = pos.x;
		double minY = pos.y;
		double maxX = minX + this.width;
		double maxY = minY + this.height;
		
		boolean isValid = true;
		for(Point p : e.envHitbox.corners) {
			double x = p.x + e.pos.x;
			double y = p.y + e.pos.y;
			
			if(x < minX || x > maxX || y < minY || y > maxY) {
				isValid = false;
				break;
			}
		}
		return isValid;
	}
	
	//generates a random vector within the bounding box
	public Vector generateRandomVector() {
		return new Vector(pos.x + (Math.random() * this.width), pos.y + (Math.random() * this.height));
	}
	
	public void generateWave(Map map) {
		ArrayList<Enemy> nextWave = new ArrayList<>();
		
		//first, make a new enemy at a random point inside the bounding box, then check if it collides with the environment
		for(int i = 0; i < 10; i++) {
			Enemy e = null;
			
			if(Math.random() <= 0.2) {
				e = new DarkKnight(generateRandomVector());
			}
			else {
				e = new Grunt(generateRandomVector());
			}
			e.vel = new Vector(0.000001, 0);
			while(true) {
				e.move(map);
				if(e.envCollision) {
					e.pos = generateRandomVector();
				}
				else {
					break;
				}
			}
			
			nextWave.add(e);
		}
		
		this.waves.add(nextWave);
	}
	
	public void spawnEnemies() {
		if(this.waves.size() == 0) {
			return;
		}
		
		ArrayList<Enemy> nextWave = this.waves.pop();
		for(Enemy e : nextWave) {
			GameManager.enemies.add(e);
		}
	}
	
	public boolean hasMoreEnemies() {
		return this.waves.size() != 0;
	}

	public void tick(Map map) {}
	
	public void draw(Graphics g) {
		Hitbox box = new Hitbox(this.width, this.height);
		box.draw(g, new Vector(this.pos.x + this.width / 2, this.pos.y + this.width / 2));
	}
}
