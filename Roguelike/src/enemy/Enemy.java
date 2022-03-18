package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entity.Entity;
import map.Map;
import projectile.Projectile;
import state.GameManager;
import util.Vector;

public abstract class Enemy extends Entity{
	
	public ArrayList<BufferedImage> sprite;
	public int health;
	
	public Enemy(Vector pos, double width, double height, int health) {
		super(pos, new Vector(0, 0), width, height);
		this.health = health;
	}
	
	public static void loadTextures() {
		Grunt.loadTextures();
	}
	
	public abstract void tick(Map map);	//should invoke move() method
	public abstract void draw(Graphics g);
	public abstract void onDeath(); 	//make some particles, spawn coins
	
	public boolean takeDamage(Projectile p) {
		if(p.playerProjectile && this.collision(p)) {
			this.health -= p.damage;
			Vector projectileVel = new Vector(p.vel);
			projectileVel.setMagnitude(0.1);
			this.vel = new Vector(projectileVel);
			return true;
		}
		return false;
	}
	
	public void despawn() {
		this.onDeath();
		GameManager.enemies.remove(this);
	}

}
