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
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;

	public ExitDoor(Vector pos) {
		super(pos, 3, 3.5, ExitDoor.sprites);
	}
	
	public static void loadTextures() {
		ExitDoor.sprites = Entity.loadIntoMap("/exit_door.png", 46, 57);
	}
	
	@Override
	public void onInteract() {
		GameManager.nextStage();
	}

}
