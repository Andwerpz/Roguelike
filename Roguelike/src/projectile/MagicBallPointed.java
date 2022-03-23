package projectile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import util.GraphicsTools;
import util.Vector;

public class MagicBallPointed extends Projectile{
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;

	public MagicBallPointed(Vector pos, Vector vel, int damage) {
		super(pos, vel, 0.7, 0.7, damage, MagicBallPointed.sprites);
	}
	
	public static void loadTextures() {
		MagicBallPointed.sprites = Entity.loadIntoMap("/magic_ball_pointed.png", 19, 9);
	}

}
