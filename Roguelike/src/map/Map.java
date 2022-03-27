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

import decoration.Chest;
import decoration.Decoration;
import decoration.ExitDoor;
import input.Button;
import input.InputManager;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class Map {
	//stores all relevant map info
	
	public static final int TILE_SIZE = 48;	//used for drawing the map texture
	
	public static final int TILE_TYPE_FIGHT = 1;
	public static final int TILE_TYPE_REWARD = 2;
	public static final int TILE_TYPE_EXIT = 3;
	public static final int TILE_TYPE_START = 4;
	
	//0 : air, 1 : floor, 2 : entrance / exit
	public int[][] map;	//storing tile info
	
	public static final int AIR = 0;
	public static final int FLOOR = 1;
	public static final int DOOR = 2;
	
	public double playerStartX;
	public double playerStartY;
	
	//manages enemy encounter tiles
	public ArrayList<EnemyEncounter> enemyEncounters;
	
	//decorations placed on map create
	public ArrayList<Decoration> mapDecorations;
	
	BufferedImage mapTexture;
	BufferedImage wallTexture;

	public int mapSize = 200;
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
	
	//generates starting lobby
	//TODO should probably load the lobby instead of generating it each time
	public void generateLobby() {
		this.map = new int[mapSize][mapSize];
		this.enemyEncounters = new ArrayList<EnemyEncounter>();
		this.mapDecorations = new ArrayList<Decoration>();
		
		for(int i = 10; i < this.mapSize - 10; i++) {
			for(int j = 10; j < this.mapSize - 10; j++) {
				this.map[i][j] = Map.FLOOR;
			}
		}
		
		this.mapDecorations.add(new ExitDoor(new Vector(mapSize / 2, mapSize / 2), ExitDoor.TYPE_RUINED));
		
		this.playerStartX = mapSize / 2;
		this.playerStartY = mapSize / 2 + 10;
		
		this.processTileTextures();
		this.processWallTextures();
	}
	
	//generates a dungeon TODO based off of stage and level
	public void generateDungeon() {
		
		this.map = new int[mapSize][mapSize];
		this.enemyEncounters = new ArrayList<EnemyEncounter>();
		this.mapDecorations = new ArrayList<Decoration>();
		
		int tileSize = 50;
		int innerTileSize = 30;
		int connectorSize = 4;
		int numTiles = mapSize / 50;
		
		ArrayList<int[]> drc = new ArrayList<>();
		drc.add(new int[] {-1, 0});
		drc.add(new int[] {1, 0});
		drc.add(new int[] {0, -1});
		drc.add(new int[] {0, 1});
		
		int numFightTiles = 2;
		
		//generate map layouts until one works
		while(true) {
			//Each fight tile should have 1 or more reward tiles associated with it
			//the key is that the reward tiles should only be accessible if the player has beaten their respective fight tile
			
			//initial generation should take the form of a tree like structure. The start tile is the root, 
			//and the first fight tile should stem from it. 
			
			//from there, the reward tiles follow.
			
			//the second fight tile has to be a child of the first fight tile
			
			//we can call the first fight tile from the 1st layer, and the second one from the 2nd layer. 
			//the key is that the fight tile of the nth layer can only be connected to the fight tile of the n - 1th layer, or it's
			//own reward tiles
			
			//exit should be connected to the fight tile of the final layer. This makes it so that there is a relatively short path from the start
			//to the end
			System.out.println("------start generation");
			
			int[][] tiles = new int[numTiles][numTiles];
			int[][] tileType = new int[numTiles][numTiles];
			
			int sr = (int) (Math.random() * (double) numTiles);
			int sc = (int) (Math.random() * (double) numTiles);
			
			tiles[sr][sc] = 1;
			tileType[sr][sc] = Map.TILE_TYPE_START;
			
			boolean isValid = true;
			
			int[] lastFightTile = new int[2];
			
			for(int i = 0; i < numFightTiles; i++) {
				System.out.println("ID: " + i);
				int curId = i + 2;
				//find tiles that belong to curId - 1
				int[] prevFightTile = new int[2];
				for(int r = 0; r < numTiles; r++) {
					for(int c = 0; c < numTiles; c++) {
						if(tiles[r][c] == curId - 1 && (tileType[r][c] == Map.TILE_TYPE_FIGHT || (r == sr && c == sc))) {
							prevFightTile = new int[] {r, c};
						}
					}
				}
				
				//pick prev fight tile to connect the next fight tile to
				boolean foundFightTile = false;
				int[] curFightTile = new int[2];
				Collections.shuffle(drc);
				for(int j = 0; j < 4; j++) {
					int nextR = prevFightTile[0] + drc.get(j)[0];
					int nextC = prevFightTile[1] + drc.get(j)[1];
					if(nextR < 0 || nextR >= numTiles || nextC < 0 || nextC >= numTiles) {
						continue;
					}
					if(tiles[nextR][nextC] == 0) {
						foundFightTile = true;
						curFightTile = new int[] {nextR, nextC};
						tiles[nextR][nextC] = curId;
						tileType[nextR][nextC] = Map.TILE_TYPE_FIGHT;
						break;
					}
				}
				if(!foundFightTile) {
					System.out.println("Didn't find fight tile");
					isValid = false;
					break;
				}
				
				//generate reward tiles that are connected to the fight tile
				int numRewardTiles = (int) (Math.random() * 2d) + 1;
				boolean foundRewardTile = true;
				for(int j = 0; j < numRewardTiles; j++) {
					foundRewardTile = false;
					Collections.shuffle(drc);
					for(int k = 0; k < 4; k++) {
						int nextR = curFightTile[0] + drc.get(k)[0];
						int nextC = curFightTile[1] + drc.get(k)[1];
						if(nextR < 0 || nextR >= numTiles || nextC < 0 || nextC >= numTiles) {
							continue;
						}
						if(tiles[nextR][nextC] == 0) {
							foundRewardTile = true;
							tiles[nextR][nextC] = curId;
							tileType[nextR][nextC] = Map.TILE_TYPE_REWARD;
							break;
						}
					}
					if(!foundRewardTile) {
						break;
					}
				}
				if(!foundRewardTile) {
					System.out.println("Didn't find reward tile");
					isValid = false;
					break;
				}
				
				//save last fight tile to connect the exit to
				if(i == numFightTiles - 1) {
					lastFightTile = curFightTile;
				}
			}
			
			boolean foundExitTile = false;
			Collections.shuffle(drc);
			for(int k = 0; k < 4; k++) {
				int nextR = lastFightTile[0] + drc.get(k)[0];
				int nextC = lastFightTile[1] + drc.get(k)[1];
				if(nextR < 0 || nextR >= numTiles || nextC < 0 || nextC >= numTiles) {
					continue;
				}
				if(tiles[nextR][nextC] == 0) {
					foundExitTile = true;
					tiles[nextR][nextC] = -1;
					tileType[nextR][nextC] = Map.TILE_TYPE_EXIT;
					break;
				}
			}
			if(!foundExitTile) {
				System.out.println("Didn't find exit tile");
				isValid = false;
			}
			
			if(isValid) {
				System.out.println("Generation Successful");
				//save the map layout
				int[][] newMap = new int[mapSize][mapSize];
				
				this.playerStartX = ((double) sc + 0.5) * tileSize;
				this.playerStartY = ((double) sr + 0.5) * tileSize;
				
				for(int i = 0; i < numTiles; i++) {
					for(int j = 0; j < numTiles; j++) {
						
						if(tileType[i][j] == 0) {	//empty tile
							continue;
						}
						
						int or = i * tileSize;
						int oc = j * tileSize;
						int gap = (tileSize - innerTileSize) / 2;
						
						if(tiles[i][j] != 0) {
							//fill in inner tile
							for(int r = or + gap; r < or + tileSize - gap; r++) {
								for(int c = oc + gap; c < oc + tileSize - gap; c++) {
									newMap[r][c] = 1;
								}
							}
						}
						
						if(tileType[i][j] == Map.TILE_TYPE_FIGHT) {
							//create new enemy encounter that covers the inner tile
							this.enemyEncounters.add(new EnemyEncounter(new Vector(oc + gap, or + gap), innerTileSize, innerTileSize));
						}
						
						else if(tileType[i][j] == Map.TILE_TYPE_EXIT) {
							//add exit door
							this.mapDecorations.add(new ExitDoor(new Vector(oc + tileSize / 2, or + tileSize / 2), ExitDoor.TYPE_INTACT));
						}
						
						else if(tileType[i][j] == Map.TILE_TYPE_REWARD) {
							this.mapDecorations.add(new Chest(new Vector(oc + tileSize / 2, or + tileSize / 2), Chest.TYPE_WOODEN));
						}
						
						else if(tileType[i][j] == Map.TILE_TYPE_START) {
							
						}
						
						//drawing connections
						for(int k = 0; k < 4; k++) {
							int nr = i + drc.get(k)[0];
							int nc = j + drc.get(k)[1];
							
							if(nr >= numTiles || nr < 0 || nc >= numTiles || nc < 0) {
								continue;
							}
							
							int nextTileType = tileType[nr][nc];
							
							boolean drawConnection = false;
							
							if(tileType[i][j] == Map.TILE_TYPE_START && 
									(nextTileType == Map.TILE_TYPE_FIGHT)) {
								drawConnection = true;
							}
							
							else if(tileType[i][j] == Map.TILE_TYPE_FIGHT && 
									(nextTileType == Map.TILE_TYPE_REWARD || 
									nextTileType == Map.TILE_TYPE_START || 
									nextTileType == Map.TILE_TYPE_EXIT ||
									nextTileType == Map.TILE_TYPE_FIGHT)) {
								drawConnection = true;
							}
							
							else if(tileType[i][j] == Map.TILE_TYPE_REWARD && 
									(nextTileType == Map.TILE_TYPE_REWARD || 
									nextTileType == Map.TILE_TYPE_FIGHT)) {
								drawConnection = true;
							}
							
							else if(tileType[i][j] == Map.TILE_TYPE_EXIT && 
									(nextTileType == Map.TILE_TYPE_FIGHT)) {
								drawConnection = true;
							}
							
							if(drawConnection) {
								this.drawConnection(newMap, tileSize, connectorSize, i, j, drc.get(k)[0], drc.get(k)[1]);
							}
						}
						
						System.out.print(tileType[i][j] + " ");
					}
					System.out.println();
				}
				
				//save map
				this.map = newMap;
				
				break;
			}
		}
		
		this.processTileTextures();
		this.processWallTextures();
		
		//generate enemies
		for(EnemyEncounter e : this.enemyEncounters) {
			e.generateWave(this);
		}
	}
	
	public void drawConnection(int[][] map, int tileSize, int connectorSize, int curR, int curC, int dr, int dc) {
		int or = curR * tileSize + tileSize / 2;
		int oc = curC * tileSize + tileSize / 2;
		
		if(dc == -1) {	//left
			for(int r = or - connectorSize / 2; r < or + connectorSize / 2; r++) {
				for(int c = oc; c > oc - tileSize; c--) {
					if(map[r][c] == Map.AIR) {
						map[r][c] = Map.DOOR;
					}
				}
			}
		}
		else if(dc == 1) {	//right
			for(int r = or - connectorSize / 2; r < or + connectorSize / 2; r++) {
				for(int c = oc; c < oc + tileSize; c++) {
					if(map[r][c] == Map.AIR) {
						map[r][c] = Map.DOOR;
					}
				}
			}
		}
		else if(dr == -1) {	//up
			for(int c = oc - connectorSize / 2; c < oc + connectorSize / 2; c++) {
				for(int r = or; r > or - tileSize; r--) {
					if(map[r][c] == Map.AIR) {
						map[r][c] = Map.DOOR;
					}
				}
			}
		}
		else if(dr == 1) {	//down
			for(int c = oc - connectorSize / 2; c < oc + connectorSize / 2; c++) {
				for(int r = or; r < or + tileSize; r++) {
					if(map[r][c] == Map.AIR) {
						map[r][c] = Map.DOOR;
					}
				}
			}
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
				if(this.map[i][j] != 0 && !v[i][j]) {
					
					int ox = j * GameManager.tileSize;
					int oy = i * GameManager.tileSize;
					
					boolean canPlaceLarge = true;
					if(i + 1 == this.mapSize || j + 1 == this.mapSize) {
						canPlaceLarge = false;
					}
					else if(
							this.map[i + 1][j] == 0 ||
							this.map[i + 1][j + 1] == 0 ||
							this.map[i][j + 1] == 0 ||
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
		
		//copy over map
		int[][] mapCopy = new int[mapSize][mapSize];
		for(int i = 0; i < mapSize; i++) {
			for(int j = 0; j < mapSize; j++) {
				mapCopy[i][j] = map[i][j] == 0? 0 : 1;
			}
		}
		
		//change map copy to suit needs
		for(int i = this.mapSize - 1; i > 0; i--) {
			for(int j = 0; j < this.mapSize; j++) {
				if(mapCopy[i - 1][j] != 0) {
					mapCopy[i][j] = 1;
				}
			}
		}
		
		this.wallTexture = new BufferedImage(mapSize * Map.TILE_SIZE, mapSize * Map.TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics gImg = this.wallTexture.getGraphics();
		
		BufferedImage[] wallTex = walls.get(0); 	//stone wall
		
		//first draw top wall textures
		for(int i = 0; i < this.mapSize - 1; i++) {
			for(int j = 0; j < this.mapSize; j++) {
				
				if(mapCopy[i][j] != 0) {
					continue;
				}
				
				boolean foundLower = false;
				
				for(int k = 0; k < dx.length; k++) {
					int x = j + dx[k];
					int y = i + dy[k];
					
					if(x >= 0 && y >= 0 && y < this.mapSize && x < this.mapSize && mapCopy[y][x] == 1) {
						foundLower = true;
						break;
					}
				}
				
				int x = j * Map.TILE_SIZE;
				int y = (i - 0) * Map.TILE_SIZE;
				
				if(foundLower) {
					//gImg.drawImage(wallTex[16], x, y, Map.TILE_SIZE, Map.TILE_SIZE, null);
				}
				
				if(mapCopy[i][j] == 0 && mapCopy[i + 1][j] != 0) {
					x = j * Map.TILE_SIZE;
					y = (i - 0) * Map.TILE_SIZE;
					
					gImg.drawImage(wallTex[15], x, y, Map.TILE_SIZE, Map.TILE_SIZE, null);
					
				}
			}
		}
		
		//draw the rest of the wall
		for(int i = 0; i < this.mapSize; i++) {
			for(int j = 0; j < this.mapSize; j++) {
				if(mapCopy[i][j] == 0) {
					
					if((i - 1) < 0) {
						continue;
					}
					
					BufferedImage img = this.setWallTile(wallTex, i, j, mapCopy);
					
					int x = j * Map.TILE_SIZE;
					int y = (i - 1) * Map.TILE_SIZE;
					
					
					
					gImg.drawImage(img, x, y, Map.TILE_SIZE, Map.TILE_SIZE, null);
				}
			}
		}
	}
	
	public BufferedImage setWallTile(BufferedImage[] wallTex, int row, int col, int[][] map) {
		
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
