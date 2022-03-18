package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entity.Entity;
import map.Map;
import util.Vector;

public abstract class Enemy extends Entity{
	
	public ArrayList<BufferedImage> sprite;
	
	public Enemy(Vector pos, double width, double height) {
		super(pos, new Vector(0, 0), width, height);
	}
	
	public static void loadTextures() {
		Grunt.loadTextures();
	}
	
	public abstract void tick(Map map);	//should invoke move() method
	public abstract void draw(Graphics g);

}
