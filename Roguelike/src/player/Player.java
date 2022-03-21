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
import projectile.LaserRed;
import projectile.LongBullet;
import projectile.Projectile;
import main.Main;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.Point;
import util.Vector;
import weapon.AK47;
import weapon.M1911;
import weapon.PumpShotgun;
import weapon.Weapon;

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
	
	public int health;
	public int maxHealth = 6;
	
	public int shield;
	public int maxShield = 4;
	
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
	
	public Weapon equippedWeapon = new M1911(new Vector(0, 0));
	public boolean pickUpWeapon = false;
	
	//public Weapon equippedWeapon = new AK47(new Vector(0, 0));
	
	public java.awt.Point mouse = new java.awt.Point(0, 0);
	
	//weapon ideas:
	//air shotgun: pushes back enemies
	//black hole launcher: wherever it lands, it makes something that drags enemies towards it
	//enemy primer: gotta come up with a better name, it will prime enemies so that damage taken in the future is doubled.
	
	public Player(Vector pos) {
		super();
		
		this.width = 1.2;
		this.height = 1.2;
		this.envHitbox = new Hitbox(width, height);
		this.pos = new Vector(pos);
		
		this.immuneTimeLeft = 0;
		
		this.health = maxHealth;
		this.shield = maxShield;
		
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
		PlayerUI.loadTextures();
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
		
		Vector mouseReal = Entity.convertVectorToReal(GameManager.mouse);
		
		//making player face towards mouse
		if(mouseReal.x > this.pos.x) {
			this.drawSprite(this.curAnimation.get(this.curAnimationFrame / this.animationInterval), g);
		}
		else {
			this.drawHorizontalMirroredSprite(this.curAnimation.get(this.curAnimationFrame / this.animationInterval), g);
		}
		
		
		//draw the currently equipped weapon
		BufferedImage wepImg = this.equippedWeapon.sprite;
		
		Vector attackVector = new Vector(new Point(GameManager.player.pos), Entity.convertVectorToReal(new Vector(GameManager.mouse.x, GameManager.mouse.y)));
		attackVector.setMagnitude(0.5);
		
		//flipping image if on left side of player
		double rads = Math.atan2(attackVector.y, attackVector.x);
		if(rads < -Math.PI / 2d || rads > Math.PI / 2d) {
			wepImg = GraphicsTools.flipImageVertical(wepImg);
		}
		
		this.equippedWeapon.pos = new Vector(GameManager.player.pos);
		this.equippedWeapon.pos.addVector(attackVector);	//move wep in direction of mouse
		this.equippedWeapon.pos.addVector(new Vector(0, 0.35));	//move down so that the wep isn't in front of player's head
		
		this.equippedWeapon.drawPointAtSprite(wepImg, g, attackVector);
	}
	
	public boolean takeDamage(Projectile p) {
		if(!p.playerProjectile && this.collision(p)) {
			if(this.shield > 0) {
				this.shield -= p.damage;
				this.shield = Math.max(0, this.shield);
			}
			else {
				this.health -= p.damage;
			}
			
			Vector projectileVel = new Vector(p.vel);
			projectileVel.setMagnitude(0.2);
			this.vel.addVector(projectileVel);
			return true;
		}
		return false;
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
		this.equippedWeapon.attack();
	}
	
	public void mouseReleased(MouseEvent arg0) {
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
		
		if((up || down || left || right) && this.curAnimation != Player.runAnimation) {
			this.curAnimationFrame = 0;
			this.curAnimation = Player.runAnimation;
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
		
		if(!(up || down || left || right) && this.curAnimation != Player.idleAnimation) {
			this.curAnimationFrame = 0;
			this.curAnimation = Player.idleAnimation;
		}
	}
	
}
