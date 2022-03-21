package particle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import util.GraphicsTools;
import util.Vector;

public class ProjectileExplosionParticle extends Particle {
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;

	public ProjectileExplosionParticle(Vector pos, int color) {
		super(pos, new Vector(0, 0), 0.3, 0.3, ProjectileExplosionParticle.sprites.get(color));
		this.despawnOnFinishedAnimation = true;
		this.doCollision = false;
		this.frameInterval = 3;
	}
	
	public static void loadTextures() {
		ProjectileExplosionParticle.sprites = new HashMap<>();
		ProjectileExplosionParticle.sprites.put(Particle.RED, GraphicsTools.loadAnimation("/projectile_explosion_particle_red.png", 32, 32));
		ProjectileExplosionParticle.sprites.put(Particle.YELLOW, GraphicsTools.loadAnimation("/projectile_explosion_particle_yellow.png", 32, 32));
	}

}
