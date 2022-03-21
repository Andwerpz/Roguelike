package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import main.MainPanel;
import map.Map;
import state.GameManager;
import util.Point;
import util.Vector;

public class Hitbox {
	
	//just a rectangular box defined by 4 points
	
	public double length, width;
	public Vector offset;
	
	public Point upperLeft, upperRight, lowerLeft, lowerRight;
	
	public Point[] corners;	//so it's easy to loop through
	
	//center of hitbox is 0, 0
	public Hitbox(double w, double l) {
		this.length = l;
		this.width = w;
		this.offset = new Vector(0, 0);
		
		//just making some utility points. To check collision, we're only going to be looking at the corners.
		upperLeft = new Point(-w / 2, -l / 2);
		upperRight = new Point(w / 2, -l / 2);
		lowerLeft = new Point(-w / 2, l / 2);
		lowerRight = new Point(w / 2, l / 2);
		
		this.corners = new Point[] {upperLeft, upperRight, lowerRight, lowerLeft};
	}

	public boolean collision(Vector thisPos, Hitbox h, Vector hPos) {
		
		Rectangle2D rect1 = new Rectangle2D.Double((thisPos.x + this.offset.x + upperLeft.x), (thisPos.y + this.offset.y + upperLeft.y), this.width, this.length);
		Rectangle2D rect2 = new Rectangle2D.Double((hPos.x + h.offset.x + h.upperLeft.x), (hPos.y + h.offset.y + h.upperLeft.y), h.width, h.length);
		
		for(int i = 0; i < 4; i++) {
			Point p = corners[i];
			if(rect2.contains((p.x + thisPos.x + this.offset.x), (p.y + thisPos.y + this.offset.y))) {
				return true;
			}
		}
		
		for(int i = 0; i < 4; i++) {
			Point p = h.corners[i];
			if(rect1.contains((p.x + hPos.x + h.offset.x), (p.y + hPos.y + h.offset.y))) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public void draw(Graphics g, Vector pos) {
		for(int i = 0; i < 4; i++) {
			Vector a = new Vector(corners[i]); 
			Vector b = new Vector(corners[(i + 1) % 4]);
			a.addVector(pos);
			b.addVector(pos);
			a.addVector(offset);
			b.addVector(offset);
			Vector aScreen = Entity.convertVectorToScreen(a);
			Vector bScreen = Entity.convertVectorToScreen(b);
			//System.out.println(a.x + " " + a.y);
			g.setColor(Color.GREEN);
			g.drawLine((int) aScreen.x, (int) aScreen.y, (int) bScreen.x, (int) bScreen.y);
			//System.out.println("DRAW");
//			g.drawLine(
//					(int) ((a.x + pos.x + offset.x) * GameManager.tileSize - GameManager.cameraOffset.x), 
//					(int) ((a.y + pos.y + offset.y) * GameManager.tileSize - GameManager.cameraOffset.y), 
//					(int) ((b.x + pos.x + offset.x) * GameManager.tileSize - GameManager.cameraOffset.x), 
//					(int) ((b.y + pos.y + offset.y) * GameManager.tileSize - GameManager.cameraOffset.y));
		}

	}
}
