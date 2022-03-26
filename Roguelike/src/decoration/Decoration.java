package decoration;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;
import map.Map;

public abstract class Decoration extends Entity {
	
	//class to hold all decorations. 
	//Decorations can be interactive, such as chests, or exit doors
	//generally, decorations shouldn't move
	
	//I still don't like the name of this class
	
	public boolean interactive = false;
	
	public Decoration(Vector pos, double width, double height, HashMap<Integer, ArrayList<BufferedImage>> sprites) {
		super(pos, new Vector(0, 0), width, height, sprites);
		this.sprites = sprites;
	}
	
	public static void loadTextures() {
		Chest.loadTextures();
		ExitDoor.loadTextures();
	}
	
	public void onInteract() {
		
	}
	
	public void despawn() {
		GameManager.decorations.remove(this);
	}

}
