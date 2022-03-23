package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import map.Map;
import util.GraphicsTools;
import util.Vector;

public class LongBullet extends Projectile {
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;

	public LongBullet(Vector pos, Vector vel, int damage) {
		super(pos, vel, 1, 1, damage, LongBullet.sprites);
	}
	
	public static void loadTextures() {
		LongBullet.sprites = Entity.loadIntoMap("/long_bullet.png", 26, 5);
	}
}
