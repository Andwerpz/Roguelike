package projectile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import util.GraphicsTools;
import util.Vector;

public class MagicBallPointed extends Projectile{
	
	public static ArrayList<BufferedImage> sprite;

	public MagicBallPointed(Vector pos, Vector vel, int damage) {
		super(pos, vel, 0.7, 0.7, damage, MagicBallPointed.sprite);
	}
	
	public static void loadTextures() {
		MagicBallPointed.sprite = GraphicsTools.loadAnimation("/magic_ball_pointed.png", 19, 9);
	}

}
