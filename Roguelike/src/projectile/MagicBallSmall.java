package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import map.Map;
import util.GraphicsTools;
import util.Vector;

public class MagicBallSmall extends Projectile {
	
	//initially stays still and plays a forming animation, then it fires at the intended location
	
	public static ArrayList<BufferedImage> sprite;
	public static ArrayList<BufferedImage> spriteFormation;
	
	public Vector savedVel;
	public boolean formed = false;
	public int formationFrameInterval = 6;
	
	public double degPerFrame = 5;
	public double rotationRate;
	public int frameCounter = 0;

	public MagicBallSmall(Vector pos, Vector vel, int damage) {
		super(pos, new Vector(0, 0), 0.25, 0.25, damage, MagicBallSmall.sprite);
		savedVel = vel;
		this.rotationRate = (Math.random() - 0.5) * 2;
	}
	
	public static void loadTextures() {
		MagicBallSmall.sprite = GraphicsTools.loadAnimation("/magic_ball_small.png", 4, 4);
		MagicBallSmall.spriteFormation = GraphicsTools.loadAnimation("/magic_ball_small_formation.png", 10, 10);
	}
	
	@Override
	public void tick(Map map) {
		this.move(map);
		
		frameCounter ++;
		if(!this.formed) {
			if(frameCounter / formationFrameInterval >= MagicBallSmall.spriteFormation.size()) {
				formed = true;
				frameCounter = -1;
				this.vel = savedVel;
			}
		}
		else {
			//no need to reset frame counter since we always sample the first frame
		}
	}
	
	@Override
	public void draw(Graphics g) {
		if(formed) {
			this.drawRotatedSprite(MagicBallSmall.sprite.get(0), g, Math.toRadians(degPerFrame * frameCounter * rotationRate));
		}
		else {
			this.drawCenteredSprite(MagicBallSmall.spriteFormation.get(frameCounter / formationFrameInterval), g);
		}
	}

}
