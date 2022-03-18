package weapon;

import java.awt.image.BufferedImage;

import entity.Entity;
import projectile.LongBullet;
import state.GameManager;
import util.Vector;

public class PumpShotgun extends Weapon{
	
	public static BufferedImage sprite;
	public String name = "Pump Shotgun";

	public PumpShotgun(Vector pos) {
		super(pos, PumpShotgun.sprite);
		numBullets = 5;
		bulletSpread = 15;
		bulletSpeed = 0.5;
		velocitySpread = 0.1;
		bulletDamage = 2;
	}

}
