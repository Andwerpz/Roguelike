package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tile {
	//0 : air;
	//1 : wall / enclosed space
	//2 : exit / connector
	
	ArrayList<ArrayList<Integer>> map;
	ArrayList<int[]> exits;
	
	int width;
	int height;
	
	BufferedImage texture;
	
	int gridTileSize = 32;
	
	public Tile() {
		map = new ArrayList<ArrayList<Integer>>();
	}
	
	public Tile(ArrayList<ArrayList<Integer>> tile, BufferedImage img) {
		this.map = tile;
		this.locateExits();
		
		this.width = map.get(0).size();
		this.height = map.size();
		
		this.texture = img;
	}
	
	public void locateExits() {
		exits = new ArrayList<int[]>();
		for(int i = 0; i < this.map.size(); i++) {
			for(int j = 0; j < this.map.get(i).size(); j++) {
				if(this.map.get(i).get(j) == 2) {
					exits.add(new int[] {j, i});
				}
			}
		}
	}
	
	public void draw(Graphics g, double xOffset, double yOffset) {
		g.drawImage(this.texture, (int) xOffset, (int) yOffset, null);
	}
	
	public void drawGrid(Graphics g, double xOffset, double yOffset, boolean drawGrid) {
		for (int i = 0; i < map.size(); i++) {
			for (int j = 0; j < map.get(i).size(); j++) {
				int x = (int) xOffset + j * gridTileSize;
				int y = (int) yOffset + i * gridTileSize;

				

				if (map.get(i).get(j) == 1) {
					g.setColor(Color.BLACK);
					g.fillRect(x, y, gridTileSize, gridTileSize);
				} else if (map.get(i).get(j) == 2) {
					g.setColor(Color.GREEN);
					g.fillRect(x, y, gridTileSize, gridTileSize);
				}
				
				if (drawGrid) {
					g.setColor(Color.BLACK);
					g.drawRect(x, y, gridTileSize, gridTileSize);
				}

			}
		}
	}
	
	public String toString() {
		String out = "";
		for(ArrayList<Integer> a : map) {
			for(Integer b : a) {
				out += b + " ";
			}
			out += "\n";
		}
		return out;
	}
}
