package weapon;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import projectile.LongBullet;
import state.GameManager;
import util.Vector;

public class PumpShotgun extends Weapon{
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;
	public String name = "Pump Shotgun";

	public PumpShotgun(Vector pos) {
		super(pos, PumpShotgun.sprites);
		numBullets = 5;
		bulletSpread = 15;
		bulletSpeed = 0.5;
		velocitySpread = 0.1;
		bulletDamage = 2;
	}

}
