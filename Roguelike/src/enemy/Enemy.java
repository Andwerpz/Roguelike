package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import map.Map;
import particle.DamageNumber;
import projectile.Projectile;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Enemy extends Entity{
	
	public int health;
	
	public boolean facingLeft = false;
	
	public Enemy(Vector pos, double width, double height, int health, HashMap<Integer, ArrayList<BufferedImage>> sprites) {
		super(pos, new Vector(0, 0), width, height, sprites);
		this.health = health;
	}
	
	public static void loadTextures() {
		Grunt.loadTextures();
		DarkKnight.loadTextures();
	}
	
	//do all state transitions and frame counter incrementing in here
	//should invoke move(), incrementFrameCounter(), checkForDeath()
	public abstract void tick(Map map);	
	
	//make some particles, spawn coins
	public abstract void onDeath(); 
	
	//gets current sprite based on current state and frameCounter
	//flips it if facingLeft == true
	public void draw(Graphics g) {
		BufferedImage curSprite = sprites.get(state).get(frameCounter / frameInterval);
		if(this.facingLeft) {
			curSprite = GraphicsTools.flipImageHorizontal(curSprite);
		}
		this.drawCenteredSprite(curSprite, g);
	}
	
	public boolean takeDamage(Projectile p) {
		if(p.playerProjectile && this.collision(p)) {
			this.health -= p.damage;
			
			Vector projectileVel = new Vector(p.vel);
			projectileVel.setMagnitude(0.1);
			this.vel = new Vector(projectileVel);
			
			GameManager.particles.add(new DamageNumber(this.pos, p.damage));
			
			return true;
		}
		return false;
	}
	
	public void checkForDeath() {
		if(this.health <= 0) {
			this.despawn();
		}
	}
	
	public void despawn() {
		this.onDeath();
		GameManager.enemies.remove(this);
	}

}
