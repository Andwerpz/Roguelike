package weapon;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import util.Vector;

public class M1911 extends Weapon{
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;
	
	public M1911(Vector pos) {
		super(pos, M1911.sprites);
		numBullets = 1;
		bulletSpread = 8;
		bulletSpeed = 0.3;
		velocitySpread = 0.05;
		bulletDamage = 3;
	}

}
