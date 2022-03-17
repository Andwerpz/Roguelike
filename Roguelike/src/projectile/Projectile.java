package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entity.Entity;
import map.Map;
import util.Point;
import util.Vector;

public abstract class Projectile extends Entity{
	
	public ArrayList<BufferedImage> sprite;
	public int damage;
	public int frameInterval;
	public int frameCounter = 0;
	
	public Projectile(Vector pos, Vector vel, double width, double height, int damage, ArrayList<BufferedImage> sprite, int frameInterval) {
		super(pos, vel, width, height);
		this.frameInterval = frameInterval;
		this.sprite = sprite;
		this.friction = 0;
		this.damage = damage;
	}
	
	public Projectile(Vector pos, Vector vel, double width, double height, int damage, ArrayList<BufferedImage> sprite) {
		super(pos, vel, width, height);
		this.frameInterval = 1;
		this.sprite = sprite;
		this.friction = 0;
		this.damage = damage;
	}
	
	public static void loadTextures() {
		LongBullet.loadTextures();
		LaserRed.loadTextures();
	}
	
	public void tick(Map map) {
		this.move(map);
		
		frameCounter ++;
		if(frameCounter / frameInterval >= sprite.size()) {
			frameCounter = 0;
		}
	}
	
	public void draw(Graphics g) {
		this.drawPointAtSprite(this.sprite.get(frameCounter / frameInterval), g, this.vel);
	}
	
	public abstract void despawn();

}
