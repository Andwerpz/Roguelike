package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.StringTokenizer;

import util.GraphicsTools;

public class TilesetManager {
	//manages the tileset
	
	static ArrayList<Tile> roomTiles = new ArrayList<Tile>();	//fight rooms
	static ArrayList<Tile> startTiles = new ArrayList<Tile>();	//spawn tiles
	static ArrayList<Tile> hallwayTiles = new ArrayList<Tile>();	//only placed if a fight room can't be placed
	static ArrayList<Tile> lootTiles = new ArrayList<Tile>();	//chests, shops, ect
	
	public static void loadTiles() {
		
	}
}
