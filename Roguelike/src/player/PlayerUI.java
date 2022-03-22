package player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;

import map.Map;
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
	public static ArrayList<BufferedImage> shieldBar;
	public static BufferedImage healthBarIcon;
	public static BufferedImage shieldBarIcon;
	
	//COIN DISPLAY
	public static BufferedImage coinIcon;
	public static Font coinDisplayFont;
	public static int coinDisplayFontSize = 16;
	
	//MINIMAP
	//0 1 2 3: corners; top left, top right, bottom right, bottom left
	//4 5 6 7: sides; top, left, bottom, right
	public static ArrayList<BufferedImage> minimapBorder;
	public static BufferedImage minimapImg;
	public static int minimapTileSize = 2; //how big in pixels is a tile in the minimap
	public static boolean[][] minimapVisited;
	
	public static BufferedImage playerIcon;
	
	public static void loadTextures() {
		PlayerUI.healthBar = GraphicsTools.loadAnimation("/health_bar.png", 16, 8);
		PlayerUI.healthBarIcon = GraphicsTools.loadImage("/health_bar_icon.png");
		PlayerUI.shieldBar = GraphicsTools.loadAnimation("/shield_bar.png", 16, 8);
		PlayerUI.shieldBarIcon = GraphicsTools.loadImage("/shield_bar_icon.png");
		
		PlayerUI.coinIcon = GraphicsTools.loadAnimation("/coin_rotate.png", 8, 8).get(0);
		PlayerUI.coinDisplayFont = FontTools.deriveSize(coinDisplayFontSize, FontTools.PressStart2P);
		
		PlayerUI.minimapBorder = GraphicsTools.loadAnimation("/minimap_border.png", 16, 16);
		
		PlayerUI.playerIcon = GraphicsTools.loadImage("/knight_icon.png");
	}
	
	//do floodfill on player's current location, based on the type of tile the player is currently on. 
	//use different tile types to differentiate between different rooms on the map
	public static void updateMinimap(Map map) {
		int pr = (int) GameManager.player.pos.y;
		int pc = (int) GameManager.player.pos.x;
		Graphics gImg = PlayerUI.minimapImg.getGraphics();
		
		//check if player is already in a discovered area
		if(PlayerUI.minimapVisited[pr][pc]) {
			return;
		}
		
		//check if player is somehow not on a floor tile
		if(map.map[pr][pc] == 0) {
			return;
		}
		
		int tileId = map.map[pr][pc];
		int[] dr = new int[] {-1, 1, 0, 0};
		int[] dc = new int[] {0, 0, -1, 1};
		Stack<int[]> s = new Stack<>();
		s.add(new int[] {pr, pc});
		while(s.size() != 0) {
			int[] cur = s.pop();
			boolean airAdjacent = false;
			for(int i = 0; i < 4; i++) {
				int nextR = cur[0] + dr[i];
				int nextC = cur[1] + dc[i];
				if(nextR < 0 || nextR >= map.mapSize || nextC < 0 || nextC >= map.mapSize) {
					continue;
				}
				if(!PlayerUI.minimapVisited[nextR][nextC] && map.map[nextR][nextC] == tileId && map.map[nextR][nextC] != 0) {
					PlayerUI.minimapVisited[nextR][nextC] = true;
					s.add(new int[] {nextR, nextC});
				}
				else if(map.map[nextR][nextC] == 0) {
					airAdjacent = true;
				}
			}
			
			//color in the tile
			Color curColor = Color.black;
			if(airAdjacent) {
				curColor = Color.white;
			}
			
			int x = cur[1] * PlayerUI.minimapTileSize;
			int y = cur[0] * PlayerUI.minimapTileSize;
			
			gImg.setColor(curColor);
			gImg.fillRect(x, y, PlayerUI.minimapTileSize, PlayerUI.minimapTileSize);
		}
		
		
	}
	
	public static void resetMinimap(Map map) {
		PlayerUI.minimapImg = new BufferedImage(map.mapSize * PlayerUI.minimapTileSize, map.mapSize * PlayerUI.minimapTileSize, BufferedImage.TYPE_INT_ARGB);
		PlayerUI.minimapVisited = new boolean[map.mapSize][map.mapSize];
	}
	
	public static void drawMinimap(Graphics g, int ox, int oy) {
		int mapSize = 6;	//width and height of map in tiles
		int tileWidth = PlayerUI.minimapBorder.get(0).getWidth() * GameManager.pixelSize;
		int tileHeight = PlayerUI.minimapBorder.get(0).getHeight() * GameManager.pixelSize;
		
		BufferedImage drawnMap = new BufferedImage(mapSize * tileWidth, mapSize * tileHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics gImg = drawnMap.getGraphics();
		
		//draw contents
		Graphics2D g2 = (Graphics2D) gImg;
		g2.setComposite(GraphicsTools.makeComposite(0.25));
		gImg.setColor(Color.WHITE);
		gImg.fillRect(0, 0, drawnMap.getWidth(), drawnMap.getHeight());
		g2.setComposite(GraphicsTools.makeComposite(1));
		
		int pr = (int) GameManager.player.pos.y;
		int pc = (int) GameManager.player.pos.x;
		
		int offsetX = -pc * PlayerUI.minimapTileSize + drawnMap.getWidth() / 2;
		int offsetY = -pr * PlayerUI.minimapTileSize + drawnMap.getHeight() / 2;
		
		gImg.drawImage(PlayerUI.minimapImg, offsetX, offsetY, null);
		
		int iconWidth = PlayerUI.playerIcon.getWidth();
		int iconHeight = PlayerUI.playerIcon.getHeight();
		
		gImg.drawImage(PlayerUI.playerIcon, drawnMap.getWidth() / 2 - iconWidth / 2, drawnMap.getHeight() / 2 - iconHeight / 2, null);
		
		//draw frame
		//corners
		gImg.drawImage(PlayerUI.minimapBorder.get(0), 0, 0, tileWidth, tileHeight, null);
		gImg.drawImage(PlayerUI.minimapBorder.get(1), (mapSize - 1) * tileWidth, 0, tileWidth, tileHeight, null);
		gImg.drawImage(PlayerUI.minimapBorder.get(2), (mapSize - 1) * tileWidth, (mapSize - 1) * tileHeight, tileWidth, tileHeight, null);
		gImg.drawImage(PlayerUI.minimapBorder.get(3), 0, (mapSize - 1) * tileHeight, tileWidth, tileHeight, null);
		
		//sides
		for(int i = 1; i < mapSize - 1; i++) {
			//top and bottom
			int x = i * tileWidth;
			gImg.drawImage(PlayerUI.minimapBorder.get(4), x, 0, tileWidth, tileHeight, null);
			gImg.drawImage(PlayerUI.minimapBorder.get(6), x, (mapSize - 1) * tileHeight, tileWidth, tileHeight, null);
			
			//left and right
			int y = i * tileHeight;
			gImg.drawImage(PlayerUI.minimapBorder.get(7), 0, y, tileWidth, tileHeight, null);
			gImg.drawImage(PlayerUI.minimapBorder.get(5), (mapSize - 1) * tileWidth, y, tileWidth, tileHeight, null);
		}
		
		//draw map onto screen
		g.drawImage(drawnMap, ox, oy, null);
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
