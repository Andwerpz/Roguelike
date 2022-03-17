package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import map.Map;
import util.GraphicsTools;
import util.Vector;

public class LongBullet extends Projectile {
	
	public static BufferedImage sprite;

	public LongBullet(Vector pos, Vector vel, double width, double height, int damage) {
		super(pos, vel, width, height, damage);
		// TODO Auto-generated constructor stub
	}
	
	public static void loadTextures() {
		LongBullet.sprite = GraphicsTools.loadImage("/long_bullet.png");
	}
	
	@Override
	public void despawn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		this.drawPointAtSprite(LongBullet.sprite, g, this.vel);
	}

	
	
}
