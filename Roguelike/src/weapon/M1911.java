package weapon;

import java.awt.image.BufferedImage;

import util.Vector;

public class M1911 extends Weapon{
	
	public static BufferedImage sprite;
	
	public M1911(Vector pos) {
		super(pos, M1911.sprite);
		numBullets = 1;
		bulletSpread = 8;
		bulletSpeed = 0.3;
		velocitySpread = 0.05;
		bulletDamage = 3;
	}

}
