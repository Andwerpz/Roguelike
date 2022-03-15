package player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Entity;
import entity.Hitbox;
import map.Map;
import main.Main;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class Player extends Entity{
	
	public static final int BASE_HEALTH = 100;
	public static final int BASE_STAMINA = 100;
	public static final double BASE_CRIT_CHANCE = 0.1;
	public static final int BASE_CRIT_MULTIPlIER = 2;
	public static final int BASE_IMMUNE_FRAMES = 30;
	
	public static ArrayList<BufferedImage> idleAnimation;
	public static ArrayList<BufferedImage> runAnimation;
	
	public ArrayList<BufferedImage> curAnimation;
	public int curAnimationFrame;
	public int animationInterval = 10;	//num ticks between each frame
	public boolean interruptible = false;
	
	public int health;
	public int maxHealth = 100; 	//after buffs
	
	public int stamina;
	public int maxStamina = 100;
	public int staminaRegenDelay = 30;
	
	public double critChance = 0.1;
	public int critMultiplier = 2;
	
	public boolean left = false;
	public boolean right = false;
	public boolean up = false;
	public boolean down = false;
	
	public boolean immune = false;
	public int immuneTimeLeft;
	public int immuneTime = 30;
	
	public int timeSinceLastAttack;
	
	public boolean mouseAttack = false;
	public boolean leftAttack = false;
	public boolean rightAttack = false;
	
	public boolean pickUpWeapon = false;
	
	//public Weapon equippedWeapon = new AK47(new Vector(0, 0));
	
	public java.awt.Point mouse = new java.awt.Point(0, 0);
	
	//weapon ideas:
	//air shotgun: pushes back enemies
	//black hole launcher: wherever it lands, it makes something that drags enemies towards it
	//enemy primer: gotta come up with a better name, it will prime enemies so that damage taken in the future is doubled.
	
	public Player(Vector pos) {
		super();
		
		this.width = 0.5;
		this.height = 0.5;
		this.envHitbox = new Hitbox(width, height);
		this.pos = new Vector(pos);
		
		this.immuneTimeLeft = 0;
		
		this.health = maxHealth;
		this.stamina = maxStamina;
		
		this.timeSinceLastAttack = 0;
		
		this.friction = 0.9;
		this.acceleration = 1.2;
		
		this.curAnimation = Player.idleAnimation;
		this.curAnimationFrame = 0;
	}
	
	//resets the players stats to their base levels.
	public void resetBuffs() {
		
	}
	
	public static void loadTextures() {
		Player.idleAnimation = GraphicsTools.loadAnimation("/knight_idle.png", 19, 25);
		Player.runAnimation = GraphicsTools.loadAnimation("/knight_run.png", 19, 25);
	}
	
	//takes in hitbox and position vector and checks whether the hitbox collides with the player
	//if yes, then calculate the x difference from the player to the position vector. 
	//The y vel change is constant, the direction of x is dependent on the x difference.
	public void hit(Hitbox h, Vector pos, int damage) {
		if(this.envHitbox.collision(this.pos, h, pos) && !this.immune) {
			
			this.vel.y = -0.2;
			this.vel.x = (this.pos.x - pos.x) < 0? -1 : 1;
			
			this.pos.y -= this.cushion * 2;
			
			this.immune = true;
			this.immuneTimeLeft = this.immuneTime;
			
			//GameManager.particles.add(new DamageNumber(damage, this.pos, false));
			
			this.health -= damage;
			
		}
	}
	
	@Override
	public void tick(Map map) {
		
		this.timeSinceLastAttack ++;
		
		if(this.stamina < this.maxStamina && this.timeSinceLastAttack >= this.staminaRegenDelay) {
			this.stamina ++;
		}
		
		this.health = Math.min(this.health, this.maxHealth);
		
		if(this.immune) {
			this.immuneTimeLeft --;
			if(this.immuneTimeLeft < 0) {
				this.immune = false;
			}
		}
		
		//this.attack();
		
		this.mouse = new java.awt.Point(mouse.x, mouse.y);
		
		Vector accel = new Vector(0, 0);

		if(this.left) {
			accel.x -= this.acceleration;
		}
		if(this.right) {
			accel.x += this.acceleration;
		}
		if(this.up) {
			accel.y -= this.acceleration;
		}
		if(this.down) {
			accel.y += this.acceleration;
		}
		if(accel.getMagnitude() > this.acceleration) {
			accel.setMagnitude(this.acceleration);
		}
		this.vel.addVector(accel);
		this.move(map);
		
		this.curAnimationFrame ++;
		if(this.curAnimationFrame == this.curAnimation.size() * this.animationInterval) {
			this.curAnimationFrame = 0;
		}
	}
	
	public void handleAnimation() {
		
		
		
	}
	
	/*
	public void attack() {
		//equipped a gun
		if(this.equippedWeapon != null) {
			int multishot = 1 + this.buffManager.numBuffs.getOrDefault(BuffManager.MULTISHOT, 0);
			if(this.stamina >= this.equippedWeapon.attackStaminaCost && (this.mouseAttack || this.leftAttack || this.rightAttack) && this.timeSinceLastAttack >= this.equippedWeapon.attackDelay) {
				this.timeSinceLastAttack = 0;
				this.stamina -= this.equippedWeapon.attackStaminaCost;
				for(int i = 0; i < multishot; i++) {
					if(this.mouseAttack) {
						
						Point center = new Point((pos.x) * GameManager.tileSize + MainPanel.WIDTH / 2 - GameManager.cameraOffset.x, (pos.y) * GameManager.tileSize + MainPanel.HEIGHT / 2 - GameManager.cameraOffset.y);
						
						Vector attackVector = new Vector(center, new Point(mouse.x, mouse.y));
						
						this.equippedWeapon.attack(this.pos, attackVector);
						//this.ma.Slash(pos, new Point(mouse.x, mouse.y));
					}
					else if(this.leftAttack) {
						this.equippedWeapon.attack(this.pos, new Vector(-1, 0));
						//this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 - 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
					}
					else {
						this.equippedWeapon.attack(this.pos, new Vector(1, 0));
						//this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 + 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
					}
				}
				
				
			}
		}
		//do a attack
		else {
			
			
			if(this.stamina >= 10 && (this.mouseAttack || this.leftAttack || this.rightAttack) && this.timeSinceLastAttack >= 30) {
				this.timeSinceLastAttack = 0;
				this.stamina -= 10;
				if(this.mouseAttack) {
					
					Point center = new Point((pos.x) * GameManager.tileSize + MainPanel.WIDTH / 2 - GameManager.cameraOffset.x, (pos.y) * GameManager.tileSize + MainPanel.HEIGHT / 2 - GameManager.cameraOffset.y);
					
					Vector attackVector = new Vector(center, new Point(mouse.x, mouse.y));
					
					//this.equippedWeapon.attack(this.pos, attackVector);
					this.ma.Slash(pos, new Point(mouse.x, mouse.y));
				}
				else if(this.leftAttack) {
					//this.equippedWeapon.attack(this.pos, new Vector(-1, 0));
					this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 - 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
				}
				else if(this.rightAttack){
					//this.equippedWeapon.attack(this.pos, new Vector(1, 0));
					this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 + 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
				}
				
			}
			
			
		}
		
		
	}
	*/
	
	public void draw(Graphics g) {
		
		this.drawHitboxes(g);
		this.drawSprite(this.curAnimation.get(this.curAnimationFrame / this.animationInterval), g);
		
		/*
		
		//draw the currently equipped weapon
		BufferedImage wepImg = Weapon.sprites.get(this.equippedWeapon.id);
		Point center = new Point((pos.x) * GameManager.tileSize + MainPanel.WIDTH / 2 - GameManager.cameraOffset.x, (pos.y) * GameManager.tileSize + MainPanel.HEIGHT / 2 - GameManager.cameraOffset.y);
		
		Vector attackVector = new Vector(center, new Point(mouse.x, mouse.y));
		attackVector.setMagnitude(1.5);
		
		double rads = Math.atan2(attackVector.y, attackVector.x);
		
		//System.out.println(rads % (Math.PI * 2));
		if(rads < -Math.PI / 2d || rads > Math.PI / 2d) {
			wepImg = GraphicsTools.flipImageVertical(wepImg);
		}
		
		BufferedImage rotatedImg = GraphicsTools.rotateImageByDegrees(wepImg, Math.toDegrees((rads)));
		
		double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
		double w = this.equippedWeapon.width;
	    double h = this.equippedWeapon.height;
	    double newWidth = w * cos + h * sin;
	    double newHeight = h * cos + w * sin;
		
		this.equippedWeapon.pos = new Vector(this.pos);
		this.equippedWeapon.pos.addVector(attackVector);
		
		this.equippedWeapon.drawSprite(rotatedImg, g, newWidth, newHeight);
		*/
	}
	
	/*
	public void swapWeapon(Weapon wep) {
		if(this.equippedWeapon != null) {
			this.equippedWeapon.onDrop(this.pos);
		}
		GameManager.items.remove(wep);
		this.equippedWeapon = wep;
	}
	*/
	
	public void mousePressed(MouseEvent arg0) {	
		this.mouseAttack = true;
		//this.attack(new Point(mouse.x, mouse.y));
	}
	
	public void mouseReleased() {
		this.mouseAttack = false;
	}
	
	public void keyPressed(int k) {
		
		if(k == KeyEvent.VK_A) {
			left = true;
		}
		else if(k == KeyEvent.VK_D){
			right = true;
		}
		else if(k == KeyEvent.VK_W) {
			up = true;
		}
		else if(k == KeyEvent.VK_S) {
			down = true;
		}
		else if(k == KeyEvent.VK_LEFT) {
			leftAttack = true;
		}
		else if(k == KeyEvent.VK_RIGHT) {
			rightAttack = true;
		}
		else if(k == KeyEvent.VK_E) {
			/*
			for(int i = 0; i < GameManager.items.size(); i++) {
				Item item = GameManager.items.get(i);
				if(item instanceof Weapon && item.collision(this)) {
					Weapon wep = (Weapon) item;
					wep.onPickup();
					return;
				}
				if(item instanceof Buff && item.collision(this)) {
					Buff buff = (Buff) item;
					buff.onPickup();
					return;
				}
			}
			*/
		}
	}
	
	public void keyReleased(int k) {
		if(k == KeyEvent.VK_A) {
			left = false;
		}
		else if(k == KeyEvent.VK_D){
			right = false;
		}
		else if(k == KeyEvent.VK_W) {
			up = false;
		}
		else if(k == KeyEvent.VK_S) {
			down = false;
		}
		else if(k == KeyEvent.VK_LEFT) {
			leftAttack = false;
		}
		else if(k == KeyEvent.VK_RIGHT) {
			rightAttack = false;
		}
	}
	
}
