package particle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entity.Entity;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class ProjectileExplosionMedium extends Particle {
	
	public static HashMap<Integer, HashMap<Integer, ArrayList<BufferedImage>>> sprites;
	
	public int color;

	public ProjectileExplosionMedium(Vector pos, int color) {
		super(pos, new Vector(0, 0), 0.5, 0.5, ProjectileExplosionMedium.sprites.get(color));
		this.despawnOnFinishedAnimation = true;
		this.doCollision = false;
		this.frameInterval = 3;
		this.color = color;
		
		//spawn some more particles
		for(int i = 0; i < 3; i++) {
			Vector particlePos = new Vector(this.pos);
			Vector offset = new Vector((Math.random() - 0.5) * 1, (Math.random() - 0.5) * 1);
			particlePos.addVector(offset);
			GameManager.particles.add(new ProjectileExplosionParticle(particlePos, this.color));
		}
	}
	
	public static void loadTextures() {
		ProjectileExplosionMedium.sprites = new HashMap<>();
		ProjectileExplosionMedium.sprites.put(Particle.RED, Entity.loadIntoMap("/projectile_explosion_medium_red.png", 32, 32));
		ProjectileExplosionMedium.sprites.put(Particle.YELLOW, Entity.loadIntoMap("/projectile_explosion_medium_yellow.png", 32, 32));
	}

}
