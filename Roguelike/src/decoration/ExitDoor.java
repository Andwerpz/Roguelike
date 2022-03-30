package decoration;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import map.Map;
import state.GameManager;
import util.Vector;

public class ExitDoor extends Decoration {
	
	public static final int TYPE_INTACT = 1;
	public static final int TYPE_RUINED = 2;
	
	public static HashMap<Integer, HashMap<Integer, ArrayList<BufferedImage>>> sprites;

	public ExitDoor(Vector pos, int type) {
		super(pos, 3, 3.5, ExitDoor.sprites.get(type));
	}
	
	public static void loadTextures() {
		ExitDoor.sprites = new HashMap<>();
		ExitDoor.sprites.put(ExitDoor.TYPE_INTACT, Entity.loadIntoMap("/exit_door_intact.png", 46, 57));
		ExitDoor.sprites.put(ExitDoor.TYPE_RUINED, Entity.loadIntoMap("/exit_door_ruined.png", 46, 57));
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawSprite(super.sprites.get(this.state).get(frameCounter / frameInterval), g);
	}
	
	@Override
	public void onInteract() {
		GameManager.loadNextStage();
	}

}
