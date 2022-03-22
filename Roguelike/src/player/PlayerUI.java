package player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import state.GameManager;
import util.FontTools;
import util.GraphicsTools;

public class PlayerUI {
	
	//utility class for drawing the player UI
	
	//STAT BARS
	//0 1 2: empty; start, mid, end
	//3 4 5: full; start, mid end
	//6 7: single; empty, full
	public static ArrayList<BufferedImage> healthBar;
	public static BufferedImage healthBarIcon;
	public static ArrayList<BufferedImage> shieldBar;
	public static BufferedImage shieldBarIcon;
	
	public static BufferedImage coinIcon;
	public static Font coinDisplayFont;
	public static int coinDisplayFontSize = 16;
	
	public static void loadTextures() {
		PlayerUI.healthBar = GraphicsTools.loadAnimation("/health_bar.png", 16, 8);
		PlayerUI.healthBarIcon = GraphicsTools.loadImage("/health_bar_icon.png");
		PlayerUI.shieldBar = GraphicsTools.loadAnimation("/shield_bar.png", 16, 8);
		PlayerUI.shieldBarIcon = GraphicsTools.loadImage("/shield_bar_icon.png");
		
		PlayerUI.coinIcon = GraphicsTools.loadAnimation("/coin_rotate.png", 8, 8).get(0);
		PlayerUI.coinDisplayFont = FontTools.deriveSize(coinDisplayFontSize, FontTools.PressStart2P);
	}
	
	public static void drawCoinDisplay(int ox, int oy, Graphics g) {
		int coinWidth = PlayerUI.coinIcon.getWidth() * GameManager.pixelSize;
		int coinHeight = PlayerUI.coinIcon.getHeight() * GameManager.pixelSize;
		int gap = 10;
		
		int fontSize = 16;
		
		g.drawImage(PlayerUI.coinIcon, ox, oy, coinWidth, coinHeight, null);
		
		g.setColor(Color.WHITE);
		g.setFont(PlayerUI.coinDisplayFont);
		g.drawString(GameManager.player.gold + "", ox + coinWidth + gap, oy + coinHeight / 2 + fontSize / 2);
		
		//g.drawRect(ox + coinWidth + gap, oy + coinHeight / 2 + fontSize / 2, coinWidth, coinHeight);
		//g.drawRect(ox, oy, coinWidth, coinHeight);
		//g.drawLine(ox, oy + coinHeight / 2, ox + coinWidth + gap, oy + coinHeight / 2);
	}
	
	public static void drawHealthBar(int ox, int oy, Graphics g) {
		PlayerUI.drawStatBar(ox, oy, healthBar, healthBarIcon, GameManager.player.maxHealth, GameManager.player.health, g);
	}
	
	public static void drawShieldBar(int ox, int oy, Graphics g) {
		PlayerUI.drawStatBar(ox, oy, shieldBar, shieldBarIcon, GameManager.player.maxShield, GameManager.player.shield, g);
	}
	
	private static void drawStatBar(int ox, int oy, ArrayList<BufferedImage> statBar, BufferedImage icon, int max, int cur, Graphics g) {
		//drawing icon
		int iconWidth = icon.getWidth() * GameManager.pixelSize;
		int iconHeight = icon.getHeight() * GameManager.pixelSize;
		
		g.drawImage(icon, ox, oy, iconWidth, iconHeight, null);
		
		//drawing bar
		int tileWidth = statBar.get(0).getWidth() * GameManager.pixelSize;
		int tileHeight = statBar.get(0).getHeight() * GameManager.pixelSize;
		int gap = 10;	//gap between icon and bar

		for(int i = 1; i <= max; i++) {
			int x = (i - 1) * tileWidth + ox + iconWidth + gap;
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
