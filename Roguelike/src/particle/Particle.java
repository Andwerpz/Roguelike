package particle;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import map.Map;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Particle extends Entity{
	
	public static int RED = 0;
	public static int YELLOW = 1;
	
	public boolean despawnOnFinishedAnimation = true;	//despawn this particle after the animation runs all of it's frames
	
	public Particle(Vector pos, Vector vel, double width, double height, HashMap<Integer, ArrayList<BufferedImage>> sprites) {
		super(pos, vel, width, height, sprites);
		
		this.doCollision = false;
	}
	
	public static void loadTextures() {
		ProjectileExplosionMedium.loadTextures();
		ProjectileExplosionParticle.loadTextures();
		DamageNumber.loadTextures();
	}

	@Override
	public void tick(Map map) {
		this.move(map);
		this.incrementFrameCounter();
	}
	
	public void incrementFrameCounter() {
		this.frameCounter ++;
		if(this.frameCounter / this.frameInterval >= this.sprites.get(state).size()) {
			if(this.despawnOnFinishedAnimation) {
				this.despawn();
				return;
			}
			this.frameCounter = 0;
		}
	}

	@Override
	public void draw(Graphics g) {
		this.drawCenteredSprite(this.sprites.get(state).get(frameCounter / frameInterval), g);
	}
	
	public void despawn() {
		GameManager.particles.remove(this);
	}

}
