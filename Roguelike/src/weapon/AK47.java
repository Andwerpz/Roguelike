package weapon;

import java.awt.image.BufferedImage;

import util.Vector;

public class AK47 extends Weapon{
	
	public static BufferedImage sprite;

	public AK47(Vector pos) {
		super(pos, AK47.sprite);
		numBullets = 1;
		bulletSpread = 5;
		bulletSpeed = 0.5;
		velocitySpread = 0.05;
		bulletDamage = 3;
	}

}
