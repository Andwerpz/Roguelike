package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import map.Map;
import projectile.Projectile;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Enemy extends Entity{
	
	public HashMap<Integer, ArrayList<BufferedImage>> sprites;
	public int health;
	
	public int frameCounter = 0;	//managing frames
	public int frameInterval = 10;	//6 fps
	
	public boolean facingLeft = false;
	public int state;
	
	public Enemy(Vector pos, double width, double height, int health) {
		super(pos, new Vector(0, 0), width, height);
		this.sprites = new HashMap<Integer, ArrayList<BufferedImage>>();
		this.health = health;
	}
	
	public static void loadTextures() {
		Grunt.loadTextures();
		DarkKnight.loadTextures();
	}
	
	//do all state transitions and frame counter incrementing in here
	//should invoke move() method
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
	
	public void incrementFrameCounter() {
		this.frameCounter ++;
		if(frameCounter / frameInterval >= this.sprites.get(this.state).size()) {
			this.frameCounter = 0;
		}
	}
	
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
