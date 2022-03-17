package projectile;

import java.awt.Graphics;

import entity.Entity;
import map.Map;
import util.Point;
import util.Vector;

public abstract class Projectile extends Entity{
	
	public Projectile(Vector pos, Vector vel, double width, double height) {
		super(pos, vel, width, height);
	}

	@Override
	public void tick(Map map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}
	
	public abstract void despawn();

}
