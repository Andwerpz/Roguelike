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
import java.util.StringTokenizer;

import input.Button;
import input.InputManager;
import main.MainPanel;
import util.GraphicsTools;
import util.Vector;

public class Map {
	//stores all relevant map info
	
	public static final int TILE_SIZE = 32;
	
	//0 : air, 1 : floor, 2 : entrance / exit
	public int[][] map;
	BufferedImage mapTexture;
	BufferedImage wallTexture;

	int mapSize = 250;
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

	public void draw(Graphics g, int xOffset, int yOffset) {
		
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
		
		g.drawImage(mapTexture, xOffset, yOffset, Map.TILE_SIZE * mapSize, Map.TILE_SIZE * mapSize, null);
		g.drawImage(wallTexture, xOffset, yOffset, Map.TILE_SIZE * mapSize, Map.TILE_SIZE * mapSize, null);
	}
	
	int minConnectorLength = 5;
	int maxConnectorLength = 10;
	
	ArrayList<ArrayList<Integer>> tileOccupied;
	
	public void generateMap() {
		this.mapTexture = new BufferedImage(mapSize * Map.TILE_SIZE, mapSize * Map.TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		this.map = new int[mapSize][mapSize];
		this.tileOccupied = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < mapSize; i++) {
			this.tileOccupied.add(new ArrayList<Integer>());
			for(int j = 0; j < mapSize; j++) {
				this.tileOccupied.get(i).add(0);
			}
		}
		
		//pick random tile to start
		//Tile startTile = roomTiles.get((int) (Math.random() * (double) roomTiles.size()));
		Tile startTile = TilesetManager.startTiles.get((int) (Math.random() * (double) TilesetManager.startTiles.size()));
		
		Queue<int[]> exits = new ArrayDeque<int[]>();
		Queue<Vector> exitDir = new ArrayDeque<Vector>();
		
		int startX = mapSize / 2;
		int startY = mapSize / 2;
		
		for(int i = 0; i < startTile.height; i++) {
			for(int j = 0; j < startTile.width; j++) {
				int next = startTile.map.get(i).get(j);
				this.map[i + startY][j + startX] = next;
			}
		}
		
		for(int i = -2; i < startTile.height + 2; i++) {
			for(int j = -2; j < startTile.width + 2; j++) {
				int x = j + startX;
				int y = i + startY;
				if(x < 0 || x >= this.mapSize || y < 0 || y >= this.mapSize) {
					continue;
				}
				this.tileOccupied.get(i + startY).set(j + startX, 1);
			}
		}
		
		for(int[] e : startTile.exits) {
			exits.add(new int[] {e[0] + startX, e[1] + startY});
			
			Vector eDir = new Vector(0, 0);
			for(int i = 0; i < 4; i++) {
				int x = e[0] + dx[i];
				int y = e[1] + dy[i];
				
				if(
						x < 0 || x >= startTile.map.get(0).size() || 
						y < 0 || y >= startTile.map.size()) {
					eDir = new Vector(dx[i], dy[i]);
					break;
				}
				else if(startTile.map.get(e[1] + dy[i]).get(e[0] + dx[i]) == 1) {
					eDir = new Vector(-dx[i], -dy[i]);
					break;
				}
			}
			//System.out.println(eDir);
			exitDir.add(eDir);
		}
		
		Graphics gMap = this.mapTexture.getGraphics();
		gMap.drawImage(startTile.texture, startX * Map.TILE_SIZE, startY * Map.TILE_SIZE, null);
		
		int roomCounter = 0;
		
		while(exits.size() != 0) {
			if(roomCounter > 10000) {
				break;
			}
			
			//System.out.println(exitDir);
			
			if(this.addTileToMap(TilesetManager.roomTiles, exits, exitDir) != -1) {
				roomCounter ++;
			}
		}
		
		this.processWallTextures();
	}
	
	public int addTileToMap(ArrayList<Tile> tiles, Queue<int[]> exits, Queue<Vector> exitDir) {
		if(exits.size() == 0) {
			return -1;
		}
		
		int[] dx = {-1, 1, 0, 0, 0};
		int[] dy = {0, 0, -1, 1, 0};
		
		int[] nextExit = exits.poll();
		Vector nextExitDir = exitDir.poll();
		
		//try every tile in random order, until you get one
		
		//TODO implement some sort of rare room system
		Collections.shuffle(tiles);
		
		for(Tile t : tiles) {
			for(int[] e : t.exits) {
				
				//determine if both exits are facing the same direction
				if(
						e[0] + nextExitDir.x < 0 || e[0] + nextExitDir.x >= t.map.get(0).size() ||
						e[1] + nextExitDir.y < 0 || e[1] + nextExitDir.y >= t.map.size() ||
						t.map.get(e[1] + (int) nextExitDir.y).get(e[0] + (int) nextExitDir.x) == 0) {
					continue;
				}
				
				//the exits are compatible
				//place the tile, and move it back if placement is invalid
				
				for(int con = minConnectorLength; con <= maxConnectorLength; con++) {
				
					int ox = nextExit[0] + (int) nextExitDir.x * con;	//origin x
					int oy = nextExit[1] + (int) nextExitDir.y * con;
					
					int ex = e[0];
					int ey = e[1];
					
					int minX = ox - ex;
					int maxX = ox - ex + t.width;
					int minY = oy - ey;
					int maxY = oy - ey + t.height;
					
					if(minX < 0 || minY < 0 || maxX >= mapSize || maxY >= mapSize) {	//tile out of bounds
						continue;
					}
					
					//check whether exit placement is valid
					//just check against bounding boxes
					boolean isValid = true;
					
					outer:
					for(int i = 0; i < t.height; i++) {
						for(int j = 0; j < t.width; j++) {
							int x = j + ox - ex;
							int y = i + oy - ey;
							
							for(int k = 0; k < this.dx.length; k++) {
								
								if(x + this.dx[k] < 0 || y + this.dy[k] < 0 || x + this.dx[k] >= this.mapSize || y + this.dy[k] >= this.mapSize) {
									continue;
								}
								
								if(this.tileOccupied.get(y + this.dy[k]).get(x + this.dx[k]) == 1) {
									isValid = false;
									break outer;
								}
							}
							
							/*
							int mapVal = map.get(y).get(x);
							int tileVal = t.map.get(i).get(j);
							
							
							if(tileVal == 2) {
								if(mapVal != 2 && mapVal != 0) {
									isValid = false;
									break outer;
								}
							}
							else if(tileVal == 0) {
								//do nothing
							}
							else {
								//we know the tile is a floor tile
								//check this tile, and all adjacent tiles
								
								for(int k = 0; k < dx.length; k++) {
									int nx = x + dx[k];
									int ny = y + dy[k];
									
									if(ny < 0 || nx < 0 || ny >= map.size() || nx >= map.get(0).size() ||
											i + dy[k] < 0 || j + dx[k] < 0 || i + dy[k] >= t.map.get(0).size() || j + dx[k] >= t.map.get(0).size()) {	//out of bounds
										
										continue;
									}
									
									if(map.get(ny).get(nx) == 2 && t.map.get(i + dy[k]).get(j + dx[k]) == 2) {
										continue;
									}
									
									else if(map.get(ny).get(nx) != 0) {
										isValid = false;
										break outer;
									}
								}
							}
							*/
						}
					}
				
					if(!isValid) {
						continue;
					}
					
					for(int i = 0; i < t.height; i++) {
						for(int j = 0; j < t.width; j++) {
							int x = j + ox - ex;
							int y = i + oy - ey;
							
							int mapVal = map[y][x];
							int tileVal = t.map.get(i).get(j);
							
							if(t.map.get(i).get(j) != 2) {
								map[y][x] = t.map.get(i).get(j);
							}
							
							//this.tileOccupied.get(y).set(x, 1);
						}
					}
					
					for(int i = -2; i < t.height + 2; i++) {
						for(int j = -2; j < t.width + 2; j++) {
							int x = j + ox - ex;
							int y = i + oy - ey;
							if(x < 0 || x >= this.mapSize || y < 0 || y >= this.mapSize) {
								continue;
							}
							this.tileOccupied.get(y).set(x, 1);
						}
					}
					
					//draw tile texture onto map
					Graphics gMap = this.mapTexture.getGraphics();
					gMap.drawImage(t.texture, minX * Map.TILE_SIZE, minY * Map.TILE_SIZE, null);
					
					//set current exit
					map[oy][ox] = 2;
					map[nextExit[1]][nextExit[0]] = 2;
					
					//set pathway between the two
					int px = nextExit[0] + (int) nextExitDir.x;
					int py = nextExit[1] + (int) nextExitDir.y;
					
					//System.out.println("[" + ox + ", " + oy + "], [" + px + ", " + py + "]");
					
					while(px != ox || py != oy) {
						map[py][px] = 1;
						
						gMap.drawImage(tileSpritesheet.get(1), px * Map.TILE_SIZE, py * Map.TILE_SIZE, Map.TILE_SIZE, Map.TILE_SIZE, null);
						
						px += (int) nextExitDir.x;
						py += (int) nextExitDir.y;
					}
					
					gMap.drawImage(tileSpritesheet.get(1), ox * Map.TILE_SIZE, oy * Map.TILE_SIZE, Map.TILE_SIZE, Map.TILE_SIZE, null);
					gMap.drawImage(tileSpritesheet.get(1), nextExit[0] * Map.TILE_SIZE, nextExit[1] * Map.TILE_SIZE, Map.TILE_SIZE, Map.TILE_SIZE, null);
					
					//add exits to stack
					for(int[] exit : t.exits) {
						exits.add(new int[] {exit[0] + minX, exit[1] + minY});
						
						Vector eDir = new Vector(0, 0);
						for(int i = 0; i < 4; i++) {
							int x = exit[0] + dx[i];
							int y = exit[1] + dy[i];
							
							if(
									x < 0 || x >= t.map.get(0).size() || 
									y < 0 || y >= t.map.size()) {
								eDir = new Vector(dx[i], dy[i]);
								break;
							}
							else if(t.map.get(exit[1] + dy[i]).get(exit[0] + dx[i]) == 1) {
								eDir = new Vector(-dx[i], -dy[i]);
								break;
							}
						}
						//System.out.println(eDir);
						exitDir.add(eDir);
					}
					
					
					
					//System.out.println(nextExitDir);
					
					return 1;
				}
			}

		}
		return -1;
	}
	
	//includes out of tile textures - now it's just a black background
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
		for(int i = this.mapSize - 1; i > 0; i--) {
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
