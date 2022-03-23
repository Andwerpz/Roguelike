package weapon;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import util.Vector;

public class AK47 extends Weapon{
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;

	public AK47(Vector pos) {
		super(pos, AK47.sprites);
		numBullets = 1;
		bulletSpread = 5;
		bulletSpeed = 0.5;
		velocitySpread = 0.05;
		bulletDamage = 3;
	}

}
