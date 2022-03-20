package particle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import util.GraphicsTools;
import util.Vector;

public class ProjectileExplosionParticle extends Particle {
	
	public static ArrayList<BufferedImage> sprite;

	public ProjectileExplosionParticle(Vector pos) {
		super(pos, new Vector(0, 0), 0.3, 0.3, ProjectileExplosionParticle.sprite);
		this.despawnOnFinishedAnimation = true;
		this.doCollision = false;
		this.frameInterval = 3;
	}
	
	public static void loadTextures() {
		ProjectileExplosionParticle.sprite = GraphicsTools.loadAnimation("/projectile_explosion_particle.png", 32, 32);
	}

}
