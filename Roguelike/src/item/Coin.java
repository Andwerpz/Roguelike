package item;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import entity.Hitbox;
import map.Map;
import state.GameManager;
import util.GraphicsTools;
import util.MathTools;
import util.Vector;

public class Coin extends Item {
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;
	
	public Coin(Vector pos) {
		super(new Vector(pos.x, pos.y), new Vector((Math.random() - 0.5) * 0.7, -(Math.random() - 0.5) * 0.7), 0.5, 0.5, Coin.sprites);
		
		this.frameCounter = (int) (Math.random() * (sprites.get(Entity.DEFAULT_STATE).size() * frameInterval));
		
		this.autoPickup = true;
		this.purchaseable = false;
	}
	
	public static void loadTextures() {
		Coin.sprites = new HashMap<>();
		sprites.put(Entity.DEFAULT_STATE, GraphicsTools.loadAnimation("/coin_rotate.png", 8, 8));
	}

	@Override
	public void onPickup() {
		GameManager.player.gold ++;
		despawn();
	}

}
