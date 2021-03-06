package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import main.MainPanel;
import map.Map;
import state.GameManager;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public abstract class Entity {
	public static HashSet<Integer> wallTiles = new HashSet<>();	//which tiles in the map should register for env collision
	
	//PHYSICAL ATTRIBUTES
	public Hitbox envHitbox;	//the environment hitbox is going to just be 1 rectangle. Multiple rectangles are too tedious, and not worth the debugging
	
	public double width, height;
	
	public Vector vel;
	public Vector pos;
	
	public static double cushion = 0.001;	//to facilitate smooth movement
	
	public double friction = 0.25;	//how much speed is leaked between frames.
	
	public boolean doCollision = true;	//if false, this entity will phase through walls
	public boolean envCollision = false;	//is true if entity collided with environment on the last tick
	
	//ANIMATION
	public HashMap<Integer, ArrayList<BufferedImage>> sprites;
	public int frameCounter = 0;
	public int frameInterval = 6;
	public boolean animationLooped = false;
	
	public static final int DEFAULT_STATE = 0;
	public int state = Entity.DEFAULT_STATE;
	
	public Entity() {
		this.vel = new Vector(0, 0);
		this.pos = new Vector(0, 0);
		this.envHitbox = new Hitbox(1, 1);
		this.sprites = null;
	}
	
	public Entity(Vector pos, Vector vel, double width, double height, HashMap<Integer, ArrayList<BufferedImage>> sprites) {
		this.pos = new Vector(pos);
		this.vel = new Vector(vel);
		this.width = width;
		this.height = height;
		this.envHitbox = new Hitbox(width, height);
		this.sprites = sprites;
	}
	
	public static void init() {
		Entity.wallTiles.add(0);
	}
	
	//loads a single animation into it's own hashmap
	public static HashMap<Integer, ArrayList<BufferedImage>> loadIntoMap(String filepath, int width, int height) {
		HashMap<Integer, ArrayList<BufferedImage>> ans = new HashMap<>();
		ans.put(Entity.DEFAULT_STATE, GraphicsTools.loadAnimation(filepath, width, height));
		return ans;
	}
	
	public void tick(Map map) {
		this.move(map);
		this.incrementFrameCounter();
	}
	
	public void draw(Graphics g) {
		this.drawCenteredSprite(this.sprites.get(this.state).get(frameCounter / frameInterval), g);
	}
	
	public void incrementFrameCounter() {
		this.frameCounter ++;
		if(frameCounter / frameInterval >= this.sprites.get(this.state).size()) {
			this.frameCounter = 0;
			this.animationLooped = true;
		}
		else {
			this.animationLooped = false;
		}
	}
	
	public boolean collision(Entity e) {
		return this.envHitbox.collision(this.pos, e.envHitbox, e.pos);
	}
	
	public static Vector convertVectorToScreen(Vector real) {
		Vector ans = new Vector(real);
		ans.x = (ans.x * GameManager.tileSize - GameManager.cameraOffset.x);
		ans.y = (ans.y * GameManager.tileSize - GameManager.cameraOffset.y);
		return ans;
	}
	
	public static Vector convertVectorToReal(Vector screen) {
		Vector ans = new Vector(screen);
		ans.x = (ans.x + GameManager.cameraOffset.x) / GameManager.tileSize; 
		ans.y = (ans.y + GameManager.cameraOffset.y) / GameManager.tileSize; 
		return ans;
	}
	
	//draws shadow centered around bottom middle of hitbox
	public void drawShadow(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(GraphicsTools.makeComposite(0.5));
		g.setColor(Color.BLACK);
	
		Vector screen = new Vector(this.pos);
		screen.addVector(new Vector(-width / 2, height / 4));
		screen = Entity.convertVectorToScreen(screen);
		
		g.fillOval((int) screen.x, (int) screen.y, (int) (this.width * (double) GameManager.tileSize), (int) (this.height / 2 * (double) GameManager.tileSize));
		
		g2.setComposite(GraphicsTools.makeComposite(1));
	}
	
	//draws sprite so that the center of the sprite is aligned to the center of the hitbox
	public void drawCenteredSprite(BufferedImage sprite, Graphics g) {
		double width = (double) sprite.getWidth() / (double) (GameManager.tileSize / GameManager.pixelSize);
		double height = (double) sprite.getHeight() / (double) (GameManager.tileSize / GameManager.pixelSize);
		g.drawImage(sprite, 
				(int) ((this.pos.x - width / 2) * GameManager.tileSize - GameManager.cameraOffset.x), 
				(int) ((this.pos.y - height / 2) * GameManager.tileSize - GameManager.cameraOffset.y), 
				(int) (width * GameManager.tileSize), 
				(int) (height * GameManager.tileSize), null);
	}
	
	//draws the sprite so that the bottom of the sprite is aligned to the bottom of the env hitbox
	public void drawSprite(BufferedImage sprite, Graphics g) {
		double width = (double) sprite.getWidth() / (double) (GameManager.tileSize / GameManager.pixelSize);
		double height = (double) sprite.getHeight() / (double) (GameManager.tileSize / GameManager.pixelSize);
		g.drawImage(sprite, 
				(int) ((this.pos.x - width / 2) * GameManager.tileSize - GameManager.cameraOffset.x), 
				(int) ((this.pos.y - height + this.height / 2) * GameManager.tileSize - GameManager.cameraOffset.y), 
				(int) (width * GameManager.tileSize), 
				(int) (height * GameManager.tileSize), null);
	}
	
	public void drawHorizontalMirroredSprite(BufferedImage sprite, Graphics g) {
		BufferedImage mirroredSprite = GraphicsTools.flipImageHorizontal(sprite);
		double width = (double) sprite.getWidth() / (double) (GameManager.tileSize / GameManager.pixelSize);
		double height = (double) sprite.getHeight() / (double) (GameManager.tileSize / GameManager.pixelSize);
		g.drawImage(mirroredSprite, 
				(int) ((this.pos.x - width / 2) * GameManager.tileSize - GameManager.cameraOffset.x), 
				(int) ((this.pos.y - height + this.height / 2) * GameManager.tileSize - GameManager.cameraOffset.y), 
				(int) (width * GameManager.tileSize), 
				(int) (height * GameManager.tileSize), null);
	}
	
	public void drawSprite(BufferedImage sprite, Graphics g, double width, double height) {
		g.drawImage(sprite, 
				(int) ((this.pos.x - width / 2) * GameManager.tileSize - GameManager.cameraOffset.x), 
				(int) ((this.pos.y - height / 2) * GameManager.tileSize - GameManager.cameraOffset.y), 
				(int) (width * GameManager.tileSize), 
				(int) (height * GameManager.tileSize), null);
	}
	
	//rotates image before drawing
	//it first enlarges the image to full resolution, then it rotates it to minimize pixel displacement
	public void drawRotatedSprite(BufferedImage sprite, Graphics g, double rads) {
		BufferedImage newImg = new BufferedImage(sprite.getWidth() * GameManager.pixelSize, sprite.getHeight() * GameManager.pixelSize, BufferedImage.TYPE_INT_ARGB);
		Graphics gImg = newImg.getGraphics();
		gImg.drawImage(sprite, 0, 0, sprite.getWidth() * GameManager.pixelSize, sprite.getHeight() * GameManager.pixelSize, null);
		BufferedImage rotatedImg = GraphicsTools.rotateImageByDegrees(newImg, Math.toDegrees((rads)));
		
		double width = (double) rotatedImg.getWidth() / (double) (GameManager.tileSize);
		double height = (double) rotatedImg.getHeight() / (double) (GameManager.tileSize);
		g.drawImage(rotatedImg, 
				(int) ((this.pos.x - width / 2) * GameManager.tileSize - GameManager.cameraOffset.x), 
				(int) ((this.pos.y - height + this.height / 2) * GameManager.tileSize - GameManager.cameraOffset.y), 
				(int) (width * (double) GameManager.tileSize), 
				(int) (height * (double) GameManager.tileSize), null);
	}
	
	//don't use this one
	public void drawRotatedSprite(BufferedImage sprite, Graphics g, double rads, double width, double height) {
		BufferedImage rotatedImg = GraphicsTools.rotateImageByDegrees(sprite, Math.toDegrees((rads)));
		
		double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
		double w = width;
	    double h = height;
	    double newWidth = w * cos + h * sin;
	    double newHeight = h * cos + w * sin;

		this.drawSprite(rotatedImg, g, newWidth, newHeight);
	}
	
	//draws sprite so that it faces the direction of the point at vector
	public void drawPointAtSprite(BufferedImage sprite, Graphics g, Vector pointAt) {
		double rads = Math.atan2(pointAt.y, pointAt.x);
		this.drawRotatedSprite(sprite, g, rads);
	}
	
	//draws sprite so that it points at a real space point
	public void drawPointAtSprite(BufferedImage sprite, Graphics g, Point realPoint) {
		this.drawPointAtSprite(sprite, g, new Vector(realPoint.x - this.pos.x, realPoint.y - this.pos.y));
	}
	
	//don't use this one
	public void drawPointAtSprite(BufferedImage sprite, Graphics g, Vector pointAt, double width, double height) {
		double rads = Math.atan2(pointAt.y, pointAt.x);
		this.drawRotatedSprite(sprite, g, rads, width, height);
	}
	
	public void drawHitboxes(Graphics g) {
		g.setColor(Color.green);
		envHitbox.draw(g, new Vector(this.pos));
	}
	
	public boolean outOfBounds(Map map) {
		//out of bounds check
		//for now, just teleport the entity to the top of the map
		for(int i = 0; i < 4; i++) {
			Point p = new Point(this.envHitbox.corners[i]);
			p.addVector(new Vector(pos));
			if(p.x < 0 || p.x > map.map[0].length - 1 || p.y < 0 || p.y > map.map.length - 1) {
				this.pos = new Vector(Math.random() * 100, 1);
				this.vel = new Vector(0, 0);
				return true;
			}
		}
		return false;
	}
	
	// given the list of hitboxes and the velocity vector, find out how far the entity will move, first considering horizontal movement, then vertical movement.
	public void move(Map map) {
		
		this.envCollision = false;
		
		this.outOfBounds(map);
		
		//friction
		this.vel.multiply(1d - this.friction);
		
		if(!doCollision) {	//no collision for this entity
			this.pos.addVector(this.vel);
			return;
		}
		
		//movement collision checks
		//leftward movement
		if(this.vel.x < 0) {
			Point lowerLeft = new Point(envHitbox.lowerLeft);
			Point upperLeft = new Point(envHitbox.upperLeft);
			
			lowerLeft.addVector(new Vector(pos));
			upperLeft.addVector(new Vector(pos));
			
			double nextX = lowerLeft.x + this.vel.x;
			
			boolean collision = false;	//stop movement if collision
			
			double collisionX = 0;
			
			for(double i = upperLeft.y; i < lowerLeft.y; i++) {
				int tileX = (int) (nextX - cushion);
				int tileY = (int) (i);
				
				for(int id : Entity.wallTiles) {
					if(map.map[tileY][tileX] == id) {
						collision = true;
						collisionX = (double) tileX + cushion + 1d;
						break;
					}
				}
				
			}
			
			int tileX = (int) (nextX - cushion);
			int tileY = (int) (lowerLeft.y);
			
			for(int id : Entity.wallTiles) {
				if(map.map[tileY][tileX] == id) {
					collision = true;
					collisionX = (double) tileX + cushion + 1d;
					break;
				}
			}
			
			if(collision) {
				this.envCollision = true;
				this.pos.x += (collisionX - lowerLeft.x);
				this.vel.x = 0;
			}
			else {
				this.pos.x += this.vel.x;
			}
			
		}
		//rightward movement
		else if(this.vel.x > 0){
			Point lowerRight = new Point(envHitbox.lowerRight);
			Point upperRight = new Point(envHitbox.upperRight);
			
			lowerRight.addVector(new Vector(pos));
			upperRight.addVector(new Vector(pos));
			
			double nextX = lowerRight.x + this.vel.x;
			
			boolean collision = false;	//stop movement if collision
			
			double collisionX = 0;
			
			for(double i = upperRight.y; i < lowerRight.y; i++) {
				int tileX = (int) (nextX - cushion);
				int tileY = (int) (i);
				
				for(int id : Entity.wallTiles) {
					if(map.map[tileY][tileX] == id) {
						collision = true;
						collisionX = (double) tileX - cushion;
						break;
					}
				}
			}
			
			int tileX = (int) (nextX - cushion);
			int tileY = (int) (lowerRight.y);
			
			for(int id : Entity.wallTiles) {
				if(map.map[tileY][tileX] == id) {
					collision = true;
					collisionX = (double) tileX - cushion;
					break;
				}
			}
			
			if(collision) {
				this.envCollision = true;
				this.pos.x += (collisionX - lowerRight.x);
				this.vel.x = 0;
			}
			else {
				this.pos.x += this.vel.x;
			}
		}
		
		//downward movement
		if(this.vel.y > 0) {
			Point lowerRight = new Point(envHitbox.lowerRight);
			Point lowerLeft = new Point(envHitbox.lowerLeft);
			
			lowerRight.addVector(new Vector(pos));
			lowerLeft.addVector(new Vector(pos));
			
			double nextY = lowerRight.y + this.vel.y;
			
			boolean collision = false;	//stop movement if collision
			
			double collisionY = 0;
			
			for(double i = lowerLeft.x; i < lowerRight.x; i++) {
				int tileX = (int) (i);
				int tileY = (int) (nextY - cushion);
				
				for(int id : Entity.wallTiles) {
					if(map.map[tileY][tileX] == id) {
						collision = true;
						collisionY = (double) tileY - cushion;
						break;
					}
				}
			}
			
			int tileX = (int) (lowerRight.x);
			int tileY = (int) (nextY - cushion);
			
			for(int id : Entity.wallTiles) {
				if(map.map[tileY][tileX] == id) {
					collision = true;
					collisionY = (double) tileY - cushion;
					break;
				}
			}
			
			if(collision) {
				this.envCollision = true;
				this.pos.y += (collisionY - lowerRight.y);
				this.vel.y = 0;
			}
			else {
				this.pos.y += this.vel.y;
			}
		}
		//upward movement
		else if(this.vel.y < 0) {
			Point upperRight = new Point(envHitbox.upperRight);
			Point upperLeft = new Point(envHitbox.upperLeft);
			
			upperRight.addVector(new Vector(pos));
			upperLeft.addVector(new Vector(pos));
			
			double nextY = upperRight.y + this.vel.y;
			
			boolean collision = false;	//stop movement if collision
			
			double collisionY = 0;
			
			for(double i = upperLeft.x; i < upperRight.x; i++) {
				int tileX = (int) (i);
				int tileY = (int) (nextY - cushion);
				
				for(int id : Entity.wallTiles) {
					if(map.map[tileY][tileX] == id) {
						collision = true;
						collisionY = (double) tileY + cushion + 1d;
						break;
					}
				}
			}
			
			int tileX = (int) (upperRight.x);
			int tileY = (int) (nextY - cushion);
			
			for(int id : Entity.wallTiles) {
				if(map.map[tileY][tileX] == id) {
					collision = true;
					collisionY = (double) tileY + cushion + 1d;
					break;
				}
			}
			
			if(collision) {
				this.envCollision = true;
				this.pos.y += (collisionY - upperRight.y);
				this.vel.y = 0;
			}
			else {
				this.pos.y += this.vel.y;
			}
		}
		
	}
}
