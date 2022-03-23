package decoration;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import item.Coin;
import item.Item;
import map.Map;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class Chest extends Decoration {
	
	public static HashMap<Integer, HashMap<Integer, ArrayList<BufferedImage>>> sprites;
	
	public static final int TYPE_WOODEN = 0;
	
	public static final int CLOSED_STATE = 1;
	public static final int OPENING_STATE = 2;
	public static final int OPEN_STATE = 3;
	
	public int type;
	
	public ArrayList<Item> loot;

	public Chest(Vector pos, int type) {
		super(pos, 2, 1.5, Chest.sprites.get(type));
		this.state = CLOSED_STATE;
		this.type = type;
		
		this.generateLoot();
	}
	
	public static void loadTextures() {
		Chest.sprites = new HashMap<>();
		
		HashMap<Integer, ArrayList<BufferedImage>> wooden = new HashMap<>();
		wooden.put(CLOSED_STATE, GraphicsTools.loadAnimation("/wooden_chest.png", 32, 24));
		wooden.put(OPENING_STATE, GraphicsTools.loadAnimation("/wooden_chest_open.png", 32, 24));
		wooden.put(OPEN_STATE, GraphicsTools.loadAnimation("/wooden_chest_opened.png", 32, 24));
		
		Chest.sprites.put(TYPE_WOODEN, wooden);
	}
	
	public void generateLoot() {
		this.loot = new ArrayList<Item>();
		
		if(this.type == TYPE_WOODEN) {
			for(int i = 0; i < 10; i++) {
				this.loot.add(new Coin(this.pos));
			}
		}
	}
	
	@Override
	public void tick(Map map) {
		this.incrementFrameCounter();
		
		if(this.state == CLOSED_STATE) {
			//do nothing
		}
		else if(this.state == OPENING_STATE) {
			if(this.animationLooped) {
				this.changeToOpen();
			}
		}
		else if(this.state == OPEN_STATE) {
			//do nothing
		}
	}
	
	public void changeToOpening() {
		this.state = OPENING_STATE;
		this.frameCounter = 0;
	}
	
	public void changeToOpen() {
		this.state = OPEN_STATE;
		this.frameCounter = 0;
		
		//drop loot onto ground
		for(Item i : this.loot) {
			GameManager.items.add(i);
		}
	}
	
	public void onInteract() {
		if(this.state == CLOSED_STATE) {
			this.changeToOpening();
		}
	}

}
