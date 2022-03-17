package projectile;

import java.awt.Graphics;

import entity.Entity;
import map.Map;
import util.Point;
import util.Vector;

public abstract class Projectile extends Entity{
	
	public int damage;
	
	public Projectile(Vector pos, Vector vel, double width, double height, int damage) {
		super(pos, vel, width, height);
		
		this.friction = 0;
		this.damage = damage;
	}
	
	public static void loadTextures() {
		LongBullet.loadTextures();
	}
	
	public void tick(Map map) {
		this.move(map);
	}
	
	public abstract void draw(Graphics g);
	public abstract void despawn();

}
