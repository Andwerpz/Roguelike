package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import entity.Entity;
import item.Item;
import map.Map;
import projectile.Projectile;
import projectile.LongBullet;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Weapon extends Item{
	
	public static HashMap<String, BufferedImage> wepSprites;
	public String name;
	
	public int numBullets;
	public double bulletSpread;	//in degrees
	public double bulletSpeed;
	public double velocitySpread;
	public int bulletDamage;

	public Weapon(Vector pos, HashMap<Integer, ArrayList<BufferedImage>> sprites) {
		super(pos, new Vector(0, 0), 2, 2, sprites);
	}
	
	public static void loadTextures() {
		Weapon.wepSprites = new HashMap<>();
		ArrayList<BufferedImage> wepSprites = GraphicsTools.loadAnimation("/weapon sprites.png", 32, 32);

		PumpShotgun.sprites = new HashMap<Integer, ArrayList<BufferedImage>>();
		PumpShotgun.sprites.put(Entity.DEFAULT_STATE, new ArrayList<BufferedImage>(Arrays.asList(wepSprites.get(0))));
		
		AK47.sprites = new HashMap<Integer, ArrayList<BufferedImage>>();
		AK47.sprites.put(Entity.DEFAULT_STATE, new ArrayList<BufferedImage>(Arrays.asList(wepSprites.get(1))));
		
		M1911.sprites = new HashMap<Integer, ArrayList<BufferedImage>>();
		M1911.sprites.put(Entity.DEFAULT_STATE, new ArrayList<BufferedImage>(Arrays.asList(wepSprites.get(3))));
	}
	
	//generates projectiles to be put into the world
	public ArrayList<Projectile> generateProjectiles() {
		Vector mouseReal = new Vector(Entity.convertVectorToReal(GameManager.mouse));
		Vector wepToMouse = new Vector(this.pos, mouseReal);
		wepToMouse.normalize();
		Vector wepEnd = new Vector(this.pos);
		wepEnd.addVector(wepToMouse);
		
		ArrayList<Projectile> output = new ArrayList<Projectile>();
		
		for(int i = 0; i < this.numBullets; i++) {
			Vector finalVec = new Vector(wepToMouse);
			finalVec.rotateCounterClockwise(Math.toRadians((Math.random() - 0.5) * this.bulletSpread));
			finalVec.setMagnitude(this.bulletSpeed + (Math.random() - 0.5) * this.velocitySpread);
			output.add(new LongBullet(new Vector(wepEnd), finalVec, this.bulletDamage));
		}
		
		return output;
	}
	
	//marks projectiles as fired from player
	public void attack() {
		ArrayList<Projectile> projectiles = this.generateProjectiles();
		for(Projectile p : projectiles) {
			p.playerProjectile = true;
			GameManager.projectiles.add(p);
		}
	}

	@Override
	public void onPickup() {
		// TODO Auto-generated method stub
		
	}

}
