package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import util.GraphicsTools;
import util.Vector;

public class MagicBallSmall extends Projectile {
	
	//initially stays still and plays a forming animation, then it fires at the intended location
	
	public static ArrayList<BufferedImage> sprite;
	public static ArrayList<BufferedImage> spriteFormation;
	
	public Vector savedVel;
	public boolean formed = false;
	public int formationFrameInterval = 6;
	
	public int degPerFrame = 5;
	public int frameCounter = 0;

	public MagicBallSmall(Vector pos, Vector vel, int damage) {
		super(pos, new Vector(0, 0), 0.25, 0.25, damage, MagicBallSmall.sprite);
		savedVel = vel;
	}
	
	public static void loadTextures() {
		MagicBallSmall.sprite = GraphicsTools.loadAnimation("/magic_ball_small.png", 4, 4);
		MagicBallSmall.spriteFormation = GraphicsTools.loadAnimation("/magic_ball_small_formation.png", 10, 10);
	}
	
	public void draw(Graphics g) {
		if(formed) {
			this.drawRotatedSprite(MagicBallSmall.sprite.get(0), g, Math.toRadians(degPerFrame * frameCounter));
		}
		else {
			this.drawCenteredSprite(MagicBallSmall.spriteFormation.get(frameCounter / formationFrameInterval), g);
		}
		frameCounter ++;
		if(frameCounter / formationFrameInterval >= MagicBallSmall.spriteFormation.size()) {
			formed = true;
			frameCounter = -1;
			this.vel = savedVel;
		}
	}

	@Override
	public void onDeath() {
		
	}

}
