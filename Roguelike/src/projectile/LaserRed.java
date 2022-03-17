package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import map.Map;
import util.GraphicsTools;
import util.Vector;

public class LaserRed extends Projectile{
	
	public static ArrayList<BufferedImage> sprite;
	
	public LaserRed(Vector pos, Vector vel, int damage) {
		super(pos, vel, 1, 1, damage, LaserRed.sprite, 1);
	}
	
	public static void loadTextures() {
		LaserRed.sprite = GraphicsTools.loadAnimation("/laser_red.png", 26, 5);
	}

	@Override
	public void despawn() {
		// TODO Auto-generated method stub
		
	}

}
