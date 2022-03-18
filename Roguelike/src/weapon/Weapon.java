package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import item.Item;
import map.Map;
import projectile.LongBullet;
import projectile.MagicBallSmall;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Weapon extends Item{
	
	public static HashMap<String, BufferedImage> sprites;
	public BufferedImage sprite;
	public String name;
	
	public int numBullets;
	public double bulletSpread;	//in degrees
	public double bulletSpeed;
	public double velocitySpread;
	public int bulletDamage;

	public Weapon(Vector pos, BufferedImage sprite) {
		super(pos, new Vector(0, 0), 2, 2);
		this.sprite = sprite;
	}
	
	public static void loadTextures() {
		Weapon.sprites = new HashMap<>();
		ArrayList<BufferedImage> wepSprites = GraphicsTools.loadAnimation("/weapon sprites.png", 32, 32);

		PumpShotgun.sprite = wepSprites.get(0);
		AK47.sprite = wepSprites.get(1);
		M1911.sprite = wepSprites.get(3);
	}
	
	public void attack() {
		Vector mouseReal = new Vector(Entity.convertPointToReal(GameManager.mouse));
		Vector wepToMouse = new Vector(this.pos, mouseReal);
		wepToMouse.normalize();
		Vector wepEnd = new Vector(this.pos);
		wepEnd.addVector(wepToMouse);
		
		for(int i = 0; i < this.numBullets; i++) {
			Vector finalVec = new Vector(wepToMouse);
			finalVec.rotateCounterClockwise(Math.toRadians((Math.random() - 0.5) * this.bulletSpread));
			finalVec.setMagnitude(this.bulletSpeed + (Math.random() - 0.5) * this.velocitySpread);
			GameManager.projectiles.add(new LongBullet(new Vector(wepEnd), finalVec, this.bulletDamage));
		}
	}

	@Override
	public void onPickup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(Map map) {}

	@Override
	public void draw(Graphics g) {
		this.drawSprite(Weapon.sprites.get(this.name), g);
	}

}
