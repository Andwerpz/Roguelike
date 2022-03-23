package item;

import java.awt.Color;
import java.awt.Font;
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
import weapon.Weapon;

public abstract class Item extends entity.Entity {
	
	public boolean autoPickup = false;
	
	public boolean purchaseable = false;	//if true, then a certain amount of gold is required to pick this item up for the first time.
	public int itemCost;	//the amount of gold this item costs
	
	public Item(Vector pos, Vector vel, double width, double height, HashMap<Integer, ArrayList<BufferedImage>> sprites) {
		super(pos, vel, width, height, sprites);
	}
	
	//should usually implement despawn()
	public abstract void onPickup();
	
	public void tick(Map map) {
		this.move(map);
		this.incrementFrameCounter();
		
		if(this.autoPickup) {
			//if the player is close enough, start moving towards player
			double dist = MathTools.dist(this.pos.x, this.pos.y, GameManager.player.pos.x, GameManager.player.pos.y);
			if(dist <= 5) {
				Vector toPlayer = new Vector(GameManager.player.pos.x - this.pos.x, GameManager.player.pos.y - this.pos.y);
				toPlayer.setMagnitude(0.1);
				if(toPlayer.y < 0) {
					this.pos.y -= Entity.cushion * 2;
				}
				this.vel.addVector(toPlayer);
			}
			
			if(this.collision(GameManager.player)) {
				this.onPickup();
				this.despawn();
			}
		}
	}
	
	public static void loadTextures() {
		Coin.loadTextures();
		Weapon.loadTextures();
	}
	
	public void drawCostIndicator(Graphics g) {
		//draw cost indicator above item
		if(this.purchaseable) {
			String costString = this.itemCost + "g";
			Font font = new Font("Dialogue", Font.PLAIN, 12);
			int stringWidth = GraphicsTools.calculateTextWidth(costString, font);
			Vector labelPos = new Vector(this.pos);
			labelPos.y -= this.height / 2;
			Vector labelScreenPos = Entity.convertVectorToScreen(labelPos);
			labelScreenPos.y -= font.getSize();
			
			g.setFont(font);
			g.setColor(new Color(Integer.parseInt("fed752", 16)));
			
			g.drawString(costString, (int) (labelScreenPos.x - stringWidth / 2d), (int) labelScreenPos.y);
		}
	}
	
	public void despawn() {
		GameManager.items.remove(this);
	}

}
