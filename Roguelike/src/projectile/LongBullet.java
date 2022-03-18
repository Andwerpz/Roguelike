package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import map.Map;
import util.GraphicsTools;
import util.Vector;

public class LongBullet extends Projectile {
	
	public static ArrayList<BufferedImage> sprite;

	public LongBullet(Vector pos, Vector vel, int damage) {
		super(pos, vel, 1, 1, damage, LongBullet.sprite);
		// TODO Auto-generated constructor stub
	}
	
	public static void loadTextures() {
		LongBullet.sprite = GraphicsTools.loadAnimation("/long_bullet.png", 26, 5);
	}
	
	@Override
	public void onDeath() {
		// TODO Auto-generated method stub
		
	}	
	
}
