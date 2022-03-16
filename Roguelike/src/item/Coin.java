package item;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entity.Entity;
import entity.Hitbox;
import map.Map;
import state.GameManager;
import util.GraphicsTools;
import util.MathTools;
import util.Vector;

public class Coin extends Item {
	
	public static ArrayList<BufferedImage> animation;
	public static int frameInterval = 8;
	public int animationFrame;
	
	public Coin(Vector pos) {
		super(new Vector(pos.x, pos.y), new Vector((Math.random() - 0.5) * 0.7, -(Math.random() - 0.5) * 0.7), 0.5, 0.5);
		
		this.animationFrame = 0;
		
		this.autoPickup = true;
		this.purchaseable = false;
	}

	@Override
	public void tick(Map map) {
		this.animationFrame ++;
		if(this.animationFrame / Coin.frameInterval >= Coin.animation.size()) {
			this.animationFrame = 0;
		}
		
		if(this.outOfBounds(map)) {
			this.pos = new Vector(Math.random() * map.map[0].length, 1);
			this.vel = new Vector(0, 0);
		}
		else {
			//if the player is close enough, start moving towards player
			double dist = MathTools.dist(this.pos.x, this.pos.y, GameManager.player.pos.x, GameManager.player.pos.y);
			if(dist <= 5) {
				Vector toPlayer = new Vector(GameManager.player.pos.x - this.pos.x, GameManager.player.pos.y - this.pos.y);
				toPlayer.setMagnitude(0.1);
				if(toPlayer.y < 0) {
					this.pos.y -= this.cushion * 2;
				}
				this.vel.addVector(toPlayer);
			}
			
			this.move(map);
		}
		
	}

	@Override
	public void draw(Graphics g) {
		this.drawSprite(animation.get(animationFrame / Coin.frameInterval), g);
	}

	@Override
	public void onPickup() {
		//GameManager.gold ++;
	}

}
