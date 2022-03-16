package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


import main.MainPanel;
import map.Map;
import state.GameManager;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public abstract class Entity {
	
	public Hitbox envHitbox;	//the environment hitbox is going to just be 1 rectangle. Multiple rectangles are too tedious, and not worth the debugging
	
	public double width, height;
	
	public Vector vel;
	public Vector pos;
	
	public static double cushion = 0.001;	//to facilitate smooth movement
	
	public double acceleration = 0.1;	//units per frame. For now, units are map grid cells
	
	public boolean onGround = false;
	
	public double maxSpeed = 0.25;	//maximum tiles per frame
	
	public double friction = 0.25;	//how much speed is leaked between frames.
	
	public boolean envCollision = false;	//did this entity collide with the environment?	used for projectiles
	
	public Entity() {
		this.vel = new Vector(0, 0);
		this.pos = new Vector(0, 0);
		this.envHitbox = new Hitbox(1, 1);
	}
	
	public abstract void tick(Map map);
	public abstract void draw(Graphics g);
	
	public boolean collision(Entity e) {
		return this.envHitbox.collision(this.pos, e.envHitbox, e.pos);
	}
	
	public static Point convertPointToScreen(Point real) {
		Point ans = new Point(real);
		ans.x = (ans.x * GameManager.tileSize - GameManager.cameraOffset.x);
		ans.y = (ans.y * GameManager.tileSize - GameManager.cameraOffset.y);
		return ans;
	}
	
	public static Point convertPointToReal(Point screen) {
		Point ans = new Point(screen);
		ans.x = (ans.x + GameManager.cameraOffset.x) / GameManager.tileSize; 
		ans.y = (ans.y + GameManager.cameraOffset.y) / GameManager.tileSize; 
		return ans;
	}
	
	//draws shadow centered around bottom middle of hitbox
	public void drawShadow(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(GraphicsTools.makeComposite(0.25));
		g.setColor(Color.BLACK);
	
		Point screen = new Point(this.pos);
		screen.addVector(new Vector(-width / 2, height / 4));
		screen = Entity.convertPointToScreen(screen);
		
		g.fillOval((int) screen.x, (int) screen.y, (int) (this.width * (double) GameManager.tileSize), (int) (this.height / 2 * (double) GameManager.tileSize));
		
		g2.setComposite(GraphicsTools.makeComposite(1));
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
	
	//rotating clockwise?
	public void drawRotatedSprite(BufferedImage sprite, Graphics g, double rads) {
		BufferedImage rotatedImg = GraphicsTools.rotateImageByDegrees(sprite, Math.toDegrees((rads)));
		this.drawSprite(rotatedImg, g);
	}
	
	//rotating clockwise?
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
	
	public void drawPointAtSprite(BufferedImage sprite, Graphics g, Vector pointAt, double width, double height) {
		
		double rads = Math.atan2(pointAt.y, pointAt.x);
		
		this.drawRotatedSprite(sprite, g, rads, width, height);
	}
	
	public void drawHitboxes(Graphics g) {
		g.setColor(Color.black);
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
		
		//ground check
		
		this.onGround = false;
		
		for(int i = 0; i < 4; i++) {
			Point p = new Point(envHitbox.corners[i]);
			int tileX = (int) (p.x + this.pos.x);
			int tileY = (int) (p.y + this.pos.y + cushion * 2);
			//System.out.println(tileX + " " + tileY + " " + map.map[tileY][tileX]);
			if(map.map[tileY][tileX] == 0) {
				this.onGround = true;
				this.envCollision = true;
				this.vel.y = 0;
				this.pos.y += (((double) tileY - cushion) - (p.y + this.pos.y));
				//System.out.println(((double) tileY - cushion) - (p.y + this.pos.y));
				break;
			}
		}
		
		//System.out.println((0.5d + this.pos.y) + " " +  this.onGround);
		
		//friction
		this.vel.multiply(1d - this.friction);
		//this.vel.setMagnitude(Math.min(maxSpeed, this.vel.getMagnitude()));
		
		
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
				
				if(map.map[tileY][tileX] == 0) {
					collision = true;
					collisionX = (double) tileX + cushion + 1d;
					break;
				}
			}
			
			int tileX = (int) (nextX - cushion);
			int tileY = (int) (lowerLeft.y);
			
			if(map.map[tileY][tileX] == 0) {
				collision = true;
				collisionX = (double) tileX + cushion + 1d;
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
				
				if(map.map[tileY][tileX] == 0) {
					collision = true;
					collisionX = (double) tileX - cushion;
					break;
				}
			}
			
			int tileX = (int) (nextX - cushion);
			int tileY = (int) (lowerRight.y);
			
			if(map.map[tileY][tileX] == 0) {
				collision = true;
				collisionX = (double) tileX - cushion;
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
		if(!this.onGround && this.vel.y > 0) {
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
				
				if(map.map[tileY][tileX] == 0) {
					collision = true;
					collisionY = (double) tileY - cushion * 10;
					break;
				}
			}
			
			int tileX = (int) (lowerRight.x);
			int tileY = (int) (nextY - cushion);
			
			if(map.map[tileY][tileX] == 0) {
				collision = true;
				collisionY = (double) tileY - cushion * 10;
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
				
				if(map.map[tileY][tileX] == 0) {
					collision = true;
					collisionY = (double) tileY + cushion + 1d;;
					break;
				}
			}
			
			int tileX = (int) (upperRight.x);
			int tileY = (int) (nextY - cushion);
			
			if(map.map[tileY][tileX] == 0) {
				collision = true;
				collisionY = (double) tileY + cushion + 1d;
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
