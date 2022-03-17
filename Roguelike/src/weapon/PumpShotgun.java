package weapon;

import java.awt.image.BufferedImage;

import entity.Entity;
import projectile.LongBullet;
import state.GameManager;
import util.Vector;

public class PumpShotgun extends Weapon{
	
	public static BufferedImage sprite;
	public String name = "Pump Shotgun";
	
	public int numBullets = 5;
	public double bulletSpread = 15;	//in degrees
	public double bulletSpeed = 0.5;
	public double velocitySpread = 0.1;
	public int bulletDamage = 2;

	public PumpShotgun(Vector pos) {
		super(pos, PumpShotgun.sprite);
	}

	@Override
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

}
