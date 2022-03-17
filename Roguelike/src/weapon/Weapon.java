package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import item.Item;
import map.Map;
import util.GraphicsTools;
import util.Vector;

public abstract class Weapon extends Item{
	
	public static HashMap<String, BufferedImage> sprites;
	public String name;

	public Weapon(Vector pos) {
		super(pos, new Vector(0, 0), 2, 2);
	}
	
	public static void loadTextures() {
		Weapon.sprites = new HashMap<>();
		ArrayList<BufferedImage> wepSprites = GraphicsTools.loadAnimation("/weapon sprites.png", 32, 32);
		sprites.put("Pump Shotgun", wepSprites.get(0));
	}
	
	public abstract void attack();

	@Override
	public void onPickup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(Map map) {}

	@Override
	public void draw(Graphics g) {
		this.drawSprite(Weapon.sprites.get(this.name), g);
	}

}
