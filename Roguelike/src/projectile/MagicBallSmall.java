package projectile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import map.Map;
import util.GraphicsTools;
import util.Vector;

public class MagicBallSmall extends Projectile {
	
	//initially stays still and plays a forming animation, then it fires at the intended location
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;
	public static ArrayList<BufferedImage> spriteFormation;
	
	public static final int IDLE_STATE = 1;
	public static final int FORMATION_STATE = 2;
	
	public Vector savedVel;
	public boolean formed = false;
	
	public double degPerFrame = 5;
	public double rotationRate;

	public MagicBallSmall(Vector pos, Vector vel, int damage) {
		super(pos, new Vector(0, 0), 0.25, 0.25, damage, MagicBallSmall.sprites);
		savedVel = vel;
		this.rotationRate = (Math.random() - 0.5) * 2;
		this.state = FORMATION_STATE;
	}
	
	public static void loadTextures() {
		MagicBallSmall.sprites = new HashMap<Integer, ArrayList<BufferedImage>>();
		MagicBallSmall.sprites.put(MagicBallSmall.IDLE_STATE, GraphicsTools.loadAnimation("/magic_ball_small.png", 4, 4));
		MagicBallSmall.sprites.put(MagicBallSmall.FORMATION_STATE, GraphicsTools.loadAnimation("/magic_ball_small_formation.png", 10, 10));
	}
	
	@Override
	public void tick(Map map) {
		this.move(map);
		this.incrementFrameCounter();
		
		if(this.state == FORMATION_STATE) {
			if(this.animationLooped) {
				this.state = IDLE_STATE;
				frameCounter = 0;
				this.vel = savedVel;
			}
		}
		
		if(this.envCollision) {
			this.despawn();
		}
	}
	
	@Override
	public void draw(Graphics g) {
		if(formed) {
			this.drawRotatedSprite(MagicBallSmall.sprites.get(this.state).get(0), g, Math.toRadians(degPerFrame * frameCounter * rotationRate));
		}
		else {
			this.drawCenteredSprite(MagicBallSmall.sprites.get(this.state).get(frameCounter / frameInterval), g);
		}
	}

}
