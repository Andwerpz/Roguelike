package particle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class ProjectileExplosionMedium extends Particle {
	
	public static ArrayList<BufferedImage> sprite;

	public ProjectileExplosionMedium(Vector pos) {
		super(pos, new Vector(0, 0), 0.5, 0.5, ProjectileExplosionMedium.sprite);
		this.despawnOnFinishedAnimation = true;
		this.doCollision = false;
		this.frameInterval = 3;
		
		//spawn some more particles
		for(int i = 0; i < 3; i++) {
			Vector particlePos = new Vector(this.pos);
			Vector offset = new Vector((Math.random() - 0.5) * 1, (Math.random() - 0.5) * 1);
			particlePos.addVector(offset);
			GameManager.particles.add(new ProjectileExplosionParticle(particlePos));
		}
	}
	
	public static void loadTextures() {
		ProjectileExplosionMedium.sprite = GraphicsTools.loadAnimation("/projectile_explosion_medium.png", 32, 32);
	}

}
