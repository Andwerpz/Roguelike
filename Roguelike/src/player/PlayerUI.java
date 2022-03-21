package player;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import state.GameManager;
import util.GraphicsTools;

public class PlayerUI {
	
	//utility class for drawing the player UI
	
	//STAT BARS
	//0 1 2: empty; start, mid, end
	//3 4 5: full; start, mid end
	//6 7: single; empty, full
	//8: icon
	public static ArrayList<BufferedImage> healthBar;
	public static ArrayList<BufferedImage> shieldBar;
	
	public static void loadTextures() {
		PlayerUI.healthBar = GraphicsTools.loadAnimation("/health_bar_white.png", 16, 8);
		PlayerUI.shieldBar = GraphicsTools.loadAnimation("/shield_bar_white.png", 16, 8);
	}
	
	public static void drawHealthBar(int ox, int oy, Graphics g) {
		PlayerUI.drawStatBar(ox, oy, healthBar, GameManager.player.maxHealth, GameManager.player.health, g);
	}
	
	public static void drawShieldBar(int ox, int oy, Graphics g) {
		PlayerUI.drawStatBar(ox, oy, shieldBar, GameManager.player.maxShield, GameManager.player.shield, g);
	}
	
	public static void drawStatBar(int ox, int oy, ArrayList<BufferedImage> statBar, int max, int cur, Graphics g) {
		int tileWidth = statBar.get(0).getWidth() * GameManager.pixelSize;
		int tileHeight = statBar.get(0).getHeight() * GameManager.pixelSize;
		
		g.drawImage(statBar.get(8), ox, oy, tileWidth, tileHeight, null);	//drawing icon

		for(int i = 1; i <= max; i++) {
			int x = (i) * tileWidth + ox;
			int y = oy;
			
			BufferedImage nextImg = null;
			
			if(i == 1 && i == max) {
				//draw single
				if(cur >= i) {
					//full
					nextImg = statBar.get(7);
				}
				else {
					//empty
					nextImg = statBar.get(6);
				}
			}
			else if(i == 1) {
				//draw start
				if(cur >= i) {
					//full
					nextImg = statBar.get(3);
				}
				else {
					//empty
					nextImg = statBar.get(0);
				}
			}
			else if(i == max) {
				//draw end
				if(cur >= i) {
					//full
					nextImg = statBar.get(5);
				}
				else {
					//empty
					nextImg = statBar.get(2);
				}
			}
			else {
				//draw middle
				if(cur >= i) {
					//full
					nextImg = statBar.get(4);
				}
				else {
					//empty
					nextImg = statBar.get(1);
				}
			}
			
			g.drawImage(nextImg, x, y, tileWidth, tileHeight, null);
		}
	}
	
}
