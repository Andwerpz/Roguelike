package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;

import input.Button;
import input.InputManager;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class Map {
	//stores all relevant map info
	
	public static final int TILE_SIZE = 48;	//used for drawing the map texture
	
	//0 : air, 1 : floor, 2 : entrance / exit
	public int[][] map;	//storing tile info
	
	//manages enemy encounter tiles
	public ArrayList<EnemyEncounter> enemyEncounters;
	
	BufferedImage mapTexture;
	BufferedImage wallTexture;

	int mapSize = 200;
	boolean drawGrid = false;
	
	static ArrayList<BufferedImage[]> walls;
	static ArrayList<BufferedImage> tileSpritesheet;
	
	//4 0 5
	//2 # 3
	//6 1 7
	
	int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
	int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};
	
	//Path Tiles / Wall Tile decoder:
	
	//for paths, 1 means same as path texture, 0 means other texture
	//for walls, 1 means lower elevation, zero means higher or equal elevation
	
	//1: 
	//1 1
	//1 1
	
	//2:   3:   4:   5:
	//1 0  0 1  0 0  0 0
	//0 0  0 0  0 1  1 0
	
	//6:   7:   8:   9:
	//1 1  1 1  0 1  1 0
	//1 0  0 1  1 1  1 1
	
	//10:  11:  12:  13:
	//1 1  0 1  0 0  1 0
	//0 0  0 1  1 1  1 0
	
	//14:  15:
	//1 0  0 1
	//0 1  1 0
	
	//walls only: 
	//16: base vertical wall
	//17: tileable vertical wall
	
	static ArrayList<Integer> oo = new ArrayList<Integer>(Arrays.asList(1, 1));
	static ArrayList<Integer> oz = new ArrayList<Integer>(Arrays.asList(1, 0));
	static ArrayList<Integer> zo = new ArrayList<Integer>(Arrays.asList(0, 1));
	static ArrayList<Integer> zz = new ArrayList<Integer>(Arrays.asList(0, 0));
	
	static HashMap<ArrayList<ArrayList<Integer>>, Integer> keyToPath = new HashMap<ArrayList<ArrayList<Integer>>, Integer>() {{
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oo, oo)), 0);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oz, zz)), 1);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(zo, zz)), 2);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(zz, zo)), 3);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(zz, oz)), 4);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oo, oz)), 5);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oo, zo)), 6);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(zo, oo)), 7);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oz, oo)), 8);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oo, zz)), 9);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(zo, zo)), 10);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(zz, oo)), 11);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oz, oz)), 12);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(oz, zo)), 13);
		put(new ArrayList<ArrayList<Integer>>(Arrays.asList(zo, oz)), 14);
	}};
	
	
	
	public Map() {
		
		TilesetManager.loadTiles();
		
		loadWallTextures();
		tileSpritesheet = GraphicsTools.loadAnimation("/tile floor.png", 16, 16);
		
		this.map = new int[mapSize][mapSize];
		
		this.generateMap();
		
//		for(ArrayList<Integer> a : this.map) {
//			for(Integer i : a) {
//				System.out.print(i + " ");
//			}
//			System.out.println();
//		}
	}
	
	
	
	public static void loadWallTextures() {
		
		walls = new ArrayList<BufferedImage[]>();
		
		String[] paths = {"/stone wall 9.png"};
		
		for(String s : paths) {
			ArrayList<BufferedImage> animation = GraphicsTools.loadAnimation(s, 16, 16);
			
			BufferedImage[] next = new BufferedImage[17];
			
			for(int i = 0; i < 17; i++) {
				next[i] = animation.get(i);
			}
			
			walls.add(next);
		}
		
	}

	public void tick(Point mouse2) {}

	public void draw(Graphics g) {
		
		//drawing tiles
		/*
		for (int i = 0; i < map.size(); i++) {
			for (int j = 0; j < map.get(i).size(); j++) {
				int x = (int) offset.x + j * Map.TILE_SIZE;
				int y = (int) offset.y + i * Map.TILE_SIZE;

				if (map.get(i).get(j) == 1) {
					g.setColor(Color.BLACK);
					g.fillRect(x, y, Map.TILE_SIZE, Map.TILE_SIZE);
				} else if (map.get(i).get(j) == 2) {
					g.setColor(Color.GREEN);
					g.fillRect(x, y, Map.TILE_SIZE, Map.TILE_SIZE);
				}
				
				if (drawGrid) {
					g.setColor(Color.BLACK);
					g.drawRect(x, y, Map.TILE_SIZE, Map.TILE_SIZE);
				}

			}
		}
		*/
		
		//background color
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
		
		g.drawImage(mapTexture, (int) -GameManager.cameraOffset.x, (int) -GameManager.cameraOffset.y, this.mapSize * GameManager.tileSize, this.mapSize * GameManager.tileSize, null);
		g.drawImage(wallTexture, (int) -GameManager.cameraOffset.x, (int) -GameManager.cameraOffset.y, this.mapSize * GameManager.tileSize, this.mapSize * GameManager.tileSize, null);
	}
	
	public void drawBackground(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
	}
	
	public void drawWalls(Graphics g) {
		g.drawImage(wallTexture, (int) -GameManager.cameraOffset.x, (int) -GameManager.cameraOffset.y, this.mapSize * GameManager.tileSize, this.mapSize * GameManager.tileSize, null);
	}
	
	public void drawFloor(Graphics g) {
		g.drawImage(mapTexture, (int) -GameManager.cameraOffset.x, (int) -GameManager.cameraOffset.y, this.mapSize * GameManager.tileSize, this.mapSize * GameManager.tileSize, null);
	}
	
	int minConnectorLength = 5;
	int maxConnectorLength = 10;
	
	ArrayList<ArrayList<Integer>> tileOccupied;
	
	public void generateMap() {
		
		this.map = new int[mapSize][mapSize];
		this.enemyEncounters = new ArrayList<EnemyEncounter>();
		
		int tileSize = 50;
		int innerTileSize = 30;
		int connectorSize = 4;
		int numTiles = mapSize / 50;
		
		//laying out the 30x30 squares
		for(int i = 0; i < numTiles; i++) {
			for(int j = 0; j < numTiles; j++) {
				int or = i * tileSize;
				int oc = j * tileSize;
				int gap = (tileSize - innerTileSize) / 2;
				for(int r = or + gap; r < or + tileSize - gap; r++) {
					for(int c = oc + gap; c < oc + tileSize - gap; c++) {
						this.map[r][c] = 1;
					}
				}
				
				if(i != 0 || j != 0) {
					//create new enemy encounter that covers the inner tile
					this.enemyEncounters.add(new EnemyEncounter(new Vector(or + gap, oc + gap), innerTileSize, innerTileSize));
				}
			}
		}

		//generating connections with stack based maze structure
		boolean[][] v = new boolean[numTiles][numTiles];
		Stack<int[]> s = new Stack<int[]>();
		s.add(new int[] {0, 0});
		v[0][0] = true;
		
		int[] dr = new int[] {-1, 1, 0, 0};
		int[] dc = new int[] {0, 0, -1, 1};
		
		outer:
		while(s.size() != 0) {
			int[] cur = s.peek();
			while(true) {
				boolean curValid = false;
				//check for an available adjacent tile
				for(int i = 0; i < 4; i++) {
					int rNext = cur[0] + dr[i];
					int cNext = cur[1] + dc[i];
					if(rNext < 0 || rNext >= numTiles || cNext < 0 || cNext >= numTiles) {
						continue;
					}
					if(!v[rNext][cNext]) {
						curValid = true;
						break;
					}
				}
				
				if(curValid) {	//available adjacent tile found
					break;
				}
				
				//no available adjacent tiles
				s.pop();
				if(s.size() == 0) {	//no more tiles in stack
					break outer;
				}
			}

			while(true) {
				int i = (int) (Math.random() * 4);
				
				int rNext = cur[0] + dr[i];
				int cNext = cur[1] + dc[i];
				if(rNext < 0 || rNext >= numTiles || cNext < 0 || cNext >= numTiles) {
					continue;
				}
				if(!v[rNext][cNext]) {	//found available tile
					
					v[rNext][cNext] = true;
					s.add(new int[] {rNext, cNext});
					
					int or = cur[0] * tileSize + tileSize / 2;
					int oc = cur[1] * tileSize + tileSize / 2;
					
					//System.out.println(rNext + " " + cNext + " " + dr[i] + " " + dc[i] + " " + or + " " + oc);
					
					//fill in path to the next tile
					if(dc[i] == -1) {	//left
						for(int r = or - connectorSize / 2; r < or + connectorSize / 2; r++) {
							for(int c = oc; c > oc - tileSize; c--) {
								this.map[r][c] = 1;
							}
						}
					}
					else if(dc[i] == 1) {	//right
						for(int r = or - connectorSize / 2; r < or + connectorSize / 2; r++) {
							for(int c = oc; c < oc + tileSize; c++) {
								this.map[r][c] = 1;
							}
						}
					}
					else if(dr[i] == -1) {	//up
						for(int c = oc - connectorSize / 2; c < oc + connectorSize / 2; c++) {
							for(int r = or; r > or - tileSize; r--) {
								this.map[r][c] = 1;
							}
						}
					}
					else if(dr[i] == 1) {	//down
						for(int c = oc - connectorSize / 2; c < oc + connectorSize / 2; c++) {
							for(int r = or; r < or + tileSize; r++) {
								this.map[r][c] = 1;
							}
						}
					}
					break;
				}
			}
		}
		
		this.processTileTextures();
		this.processWallTextures();
		
		//generate enemies
		for(EnemyEncounter e : this.enemyEncounters) {
			e.generateWave(this);
		}
	}
	
	//run after the map has been generated
	//tiles the floor with the given tileset.
	public void processTileTextures() {
		
		this.mapTexture = new BufferedImage(mapSize * Map.TILE_SIZE, mapSize * Map.TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics gImg = this.mapTexture.getGraphics();
		
		boolean[][] v = new boolean[this.mapSize][this.mapSize];
		
		for(int i = 0; i < this.mapSize; i++) {
			for(int j = 0; j < this.mapSize; j++) {
				if(this.map[i][j] == 1 && !v[i][j]) {
					
					int ox = j * GameManager.tileSize;
					int oy = i * GameManager.tileSize;
					
					boolean canPlaceLarge = true;
					if(i + 1 == this.mapSize || j + 1 == this.mapSize) {
						canPlaceLarge = false;
					}
					else if(
							this.map[i + 1][j] != 1 ||
							this.map[i + 1][j + 1] != 1 ||
							this.map[i][j + 1] != 1 ||
							v[i + 1][j] ||
							v[i + 1][j + 1] ||
							v[i][j + 1]){
						canPlaceLarge = false;
					}
					
					if(canPlaceLarge && Math.random() > 0.95) {	//large 2x2 tile
						v[i + 1][j] = true;
						v[i + 1][j + 1] = true;
						v[i][j + 1] = true;
						v[i][j] = true;
						
						gImg.drawImage(Map.tileSpritesheet.get(2), ox, oy, GameManager.tileSize, GameManager.tileSize, null);
						gImg.drawImage(Map.tileSpritesheet.get(3), ox + GameManager.tileSize, oy, GameManager.tileSize, GameManager.tileSize, null);
						gImg.drawImage(Map.tileSpritesheet.get(4), ox + GameManager.tileSize, oy + GameManager.tileSize, GameManager.tileSize, GameManager.tileSize, null);
						gImg.drawImage(Map.tileSpritesheet.get(5), ox, oy + GameManager.tileSize, GameManager.tileSize, GameManager.tileSize, null);
					}
					else if(Math.random() > 0.8) {	//small 1/2 tiles
						v[i][j] = true;
						gImg.drawImage(Map.tileSpritesheet.get(0), ox, oy, GameManager.tileSize, GameManager.tileSize, null);
					}
					else {	//regular tile
						v[i][j] = true;
						gImg.drawImage(Map.tileSpritesheet.get(1), ox, oy, GameManager.tileSize, GameManager.tileSize, null);
					}
				}
			}
		}
		
	}
	
	//run after map has been generated
	//creates wall textures
	public void processWallTextures() {
		
		//change map to suit needs
		for(int i = this.mapSize - 1; i > 0; i--) {
			for(int j = 0; j < this.mapSize; j++) {
				if(this.map[i - 1][j] != 0) {
					this.map[i][j] = 1;
				}
			}
		}
		
		this.wallTexture = new BufferedImage(mapSize * Map.TILE_SIZE, mapSize * Map.TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics gImg = this.wallTexture.getGraphics();
		
		BufferedImage[] wallTex = walls.get(0); 	//stone wall
		
		//first draw vertical textures
		for(int i = 0; i < this.mapSize - 1; i++) {
			for(int j = 0; j < this.mapSize; j++) {
				
				if(this.map[i][j] != 0) {
					continue;
				}
				
				boolean foundLower = false;
				
				for(int k = 0; k < dx.length; k++) {
					int x = j + dx[k];
					int y = i + dy[k];
					
					if(x >= 0 && y >= 0 && y < this.mapSize && x < this.mapSize && this.map[y][x] == 1) {
						foundLower = true;
						break;
					}
				}
				
				int x = j * Map.TILE_SIZE;
				int y = (i - 0) * Map.TILE_SIZE;
				
				if(foundLower) {
					//gImg.drawImage(wallTex[16], x, y, Map.TILE_SIZE, Map.TILE_SIZE, null);
				}
				
				if(this.map[i][j] == 0 && this.map[i + 1][j] != 0) {
					x = j * Map.TILE_SIZE;
					y = (i - 0) * Map.TILE_SIZE;
					
					gImg.drawImage(wallTex[15], x, y, Map.TILE_SIZE, Map.TILE_SIZE, null);
					
				}
			}
		}
		
		//draw floor textures now
		for(int i = 0; i < this.mapSize; i++) {
			for(int j = 0; j < this.mapSize; j++) {
				if(this.map[i][j] == 0) {
					
					if((i - 1) < 0) {
						continue;
					}
					
					BufferedImage img = this.setWallTile(wallTex, i, j);
					
					int x = j * Map.TILE_SIZE;
					int y = (i - 1) * Map.TILE_SIZE;
					
					
					
					gImg.drawImage(img, x, y, Map.TILE_SIZE, Map.TILE_SIZE, null);
				}
			}
		}
		
		//change back map
		for(int i = 0; i < this.mapSize; i++) {
			for(int j = 0; j < this.mapSize; j++) {
				if(i + 1 == this.mapSize) {
					if(this.map[i][j] == 1) {
						this.map[i][j] = 0;
					}
				}
				else {
					if(this.map[i + 1][j] == 0) {
						this.map[i][j] = 0;
					}
				}
			}
		}
	}
	
	public BufferedImage setWallTile(BufferedImage[] wallTex, int row, int col) {
		
		boolean[] isLower = new boolean[8];
		
		for(int i = 0; i < dx.length; i++) {
			int x = row + dx[i];
			int y = col + dy[i];
			
			if(x < 0 || y < 0 || x >= map.length || y >= map[0].length) {
				continue;
			}
			
			if(map[x][y] != 0) {
				isLower[i] = true;
			}
		}
		
		//have an array of 2x2 associated with each path tile texture. 
		//use the booleans to set the key for this specific path tile
		
		//4 0 5
		//2 # 3
		//6 1 7
		
		ArrayList<ArrayList<Integer>> key = new ArrayList<ArrayList<Integer>>();
		
		key.add(new ArrayList<Integer>(Arrays.asList(0, 0)));
		key.add(new ArrayList<Integer>(Arrays.asList(0, 0)));
		
		if(isLower[2] || isLower[4] || isLower[0]) {
			key.get(0).set(0, 1);
		}
		if(isLower[0] || isLower[5] || isLower[3]) {
			key.get(0).set(1, 1);
		}
		if(isLower[2] || isLower[6] || isLower[1]) {
			key.get(1).set(0, 1);
		}
		if(isLower[1] || isLower[7] || isLower[3]) {
			key.get(1).set(1, 1);
		}
		
		if(keyToPath.containsKey(key)) {
			return wallTex[keyToPath.get(key)];
		}
		
		//return tileSpritesheet.get(17);	//green grass
		BufferedImage blackground = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		return blackground;
		
	}
}
