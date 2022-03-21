package particle;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entity.Entity;
import map.Map;
import state.GameManager;
import util.Vector;

public abstract class Particle extends Entity{
	
	public ArrayList<BufferedImage> sprite;
	public int frameCounter = 0;
	public int frameInterval = 6;
	
	public boolean despawnOnFinishedAnimation = true;	//despawn this particle after the animation runs all of it's frames
	
	public Particle(Vector pos, Vector vel, double width, double height, ArrayList<BufferedImage> sprite) {
		super(pos, vel, width, height);
		this.sprite = sprite;
		
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
		if(this.frameCounter / this.frameInterval >= this.sprite.size()) {
			if(this.despawnOnFinishedAnimation) {
				this.despawn();
				return;
			}
			this.frameCounter = 0;
		}
	}

	@Override
	public void draw(Graphics g) {
		this.drawCenteredSprite(this.sprite.get(frameCounter / frameInterval), g);
	}
	
	public void despawn() {
		GameManager.particles.remove(this);
	}

}
