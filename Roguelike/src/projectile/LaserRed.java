package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import map.Map;
import util.GraphicsTools;
import util.Vector;

public class LaserRed extends Projectile{
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;
	
	public LaserRed(Vector pos, Vector vel, int damage) {
		super(pos, vel, 1, 1, damage, LaserRed.sprites);
	}
	
	public static void loadTextures() {
		LaserRed.sprites = Entity.loadIntoMap("/laser_red.png", 26, 5);
	}

}
