package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import enemy.Enemy;
import entity.Entity;
import map.Map;
import particle.Particle;
import particle.ProjectileExplosionMedium;
import state.GameManager;
import util.Point;
import util.Vector;

public abstract class Projectile extends Entity{
	
	//usually for projectiles, there is no state change, so to make it easier, 
	//you just have to pass a pointer from the static sprite animation back to the parent
	//projectile class to draw.
	
	public int damage;
	
	public boolean playerProjectile = false;	//true if projectile fired by player
	
	public boolean despawnOnEnvCollision = true;
	
	public Projectile(Vector pos, Vector vel, double width, double height, int damage, HashMap<Integer, ArrayList<BufferedImage>> sprites) {
		super(pos, vel, width, height, sprites);
		this.friction = 0;
		this.damage = damage;
	}
	
	public static void loadTextures() {
		LongBullet.loadTextures();
		LaserRed.loadTextures();
		MagicBallSmall.loadTextures();
		MagicBallPointed.loadTextures();
	}
	
	@Override
	public void tick(Map map) {
		this.move(map);
		this.incrementFrameCounter();
		this.checkCollision();
		if(this.envCollision && this.despawnOnEnvCollision) {
			this.despawn();
		}
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawPointAtSprite(this.sprites.get(this.state).get(frameCounter / frameInterval), g, this.vel);
	}
	
	//checks for collision against the player, or any enemy
	public void checkCollision() {
		if(!this.playerProjectile) {
			if(GameManager.player.takeDamage(this)) {
				this.despawn();
			}
		}
		else if(this.playerProjectile) {
			for(Enemy e : GameManager.enemies) {
				if(e.takeDamage(this)) {
					this.despawn();
					return;
				}
			}
		}
		
	}
	
	public void onDeath() {
		if(!this.playerProjectile) {	//enemy fired projectiles will usually be red
			GameManager.particles.add(new ProjectileExplosionMedium(this.pos, Particle.RED));
		}
		else if(this.playerProjectile) {
			GameManager.particles.add(new ProjectileExplosionMedium(this.pos, Particle.YELLOW));
		}
	}
	
	public void despawn() {
		this.onDeath();
		GameManager.projectiles.remove(this);
	}

}
