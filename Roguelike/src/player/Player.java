package player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import decoration.Decoration;
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
	
	public static final int IDLE_STATE = 1;
	public static final int MOVE_STATE = 2;
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;
	
	//STATS
	public int health;
	public int maxHealth = 6;
	
	public int shield;
	public int maxShield = 10;
	
	public int gold = 0;
	
	public double critChance = 0.1;
	public int critMultiplier = 2;
	
	public boolean immune = false;
	public int immuneTimeLeft;
	public int immuneTime = 60;
	
	public int shieldRegenInterval = 90;
	public int shieldRegenCounter = 0;
	
	public double moveAcceleration = 1.2;
	public boolean fastMove = false;
	public boolean noClip = false;
	
	//CONTROLS
	public boolean left = false;
	public boolean right = false;
	public boolean up = false;
	public boolean down = false;
	
	public boolean interact = false;
	
	public int timeSinceLastAttack;
	
	public boolean mouseAttack = false;
	public boolean leftAttack = false;
	public boolean rightAttack = false;
	
	//ITEMS
	public Weapon equippedWeapon = new M1911(new Vector(0, 0));
	public boolean pickUpWeapon = false;
	
	//weapon ideas:
	//air shotgun: pushes back enemies
	//black hole launcher: wherever it lands, it makes something that drags enemies towards it
	//enemy primer: gotta come up with a better name, it will prime enemies so that damage taken in the future is doubled.
	
	public Player(Vector pos) {
		super(pos, new Vector(0, 0), 1.2, 1.2, Player.sprites);
		
		this.width = 1.2;
		this.height = 1.2;
		this.envHitbox = new Hitbox(width, height);
		this.pos = new Vector(pos);
		
		this.immuneTimeLeft = 0;
		
		this.health = maxHealth;
		this.shield = maxShield;
		
		this.timeSinceLastAttack = 0;
		
		this.friction = 0.9;
		
		this.state = IDLE_STATE;
		
		this.frameInterval = 10;
	}
	
	//resets the players stats to their base levels.
	public void resetBuffs() {
		
	}
	
	//trigger on death
	public void reset() {
		this.health = this.maxHealth;
		this.shield = this.maxShield;
		
		this.gold = 0;
		
		this.immune = false;
	}
	
	public static void loadTextures() {
		Player.sprites = new HashMap<>();
		ArrayList<BufferedImage> idleAnimation = GraphicsTools.loadAnimation("/knight_idle.png", 19, 25);
		ArrayList<BufferedImage> runAnimation = GraphicsTools.loadAnimation("/knight_run.png", 19, 25);
		
		Player.sprites.put(IDLE_STATE, idleAnimation);
		Player.sprites.put(MOVE_STATE, runAnimation);
		
		PlayerUI.loadTextures();
	}
	
	@Override
	public void tick(Map map) {
		if(this.noClip) {
			this.doCollision = false;
		}
		else {
			this.doCollision = true;
		}
		
		this.move(map);
		this.incrementFrameCounter();
		
		this.timeSinceLastAttack ++;
		
		//HEALTH AND SHIELDS
		if(this.immune) {
			this.immuneTimeLeft --;
			if(this.immuneTimeLeft < 0) {
				this.immune = false;
			}
		}
		
		if(this.shield != this.maxShield && !this.immune) {
			this.shieldRegenCounter --;
			if(this.shieldRegenCounter <= 0) {
				this.shield ++;
				if(this.shield < this.maxShield) {
					this.shieldRegenCounter = this.shieldRegenInterval;
				}
			}
		}
		
		//MOVEMENT
		if(this.fastMove) {
			this.moveAcceleration = 1.2 * 4;
		}
		else {
			this.moveAcceleration = 1.2;
		}
		
		Vector accel = new Vector(0, 0);
		if(this.left) {
			accel.x -= this.moveAcceleration;
		}
		if(this.right) {
			accel.x += this.moveAcceleration;
		}
		if(this.up) {
			accel.y -= this.moveAcceleration;
		}
		if(this.down) {
			accel.y += this.moveAcceleration;
		}
		if(accel.getMagnitude() > this.moveAcceleration) {
			accel.setMagnitude(this.moveAcceleration);
		}
		this.vel.addVector(accel);
		
		//INTERACTION
		interactLoop:
		if(this.interact) {
			this.interact = false;
			for(int i = GameManager.decorations.size() - 1; i >= 0; i--) {
				Decoration d = GameManager.decorations.get(i);
				if(this.collision(d)) {
					d.onInteract();
					break interactLoop;
				}
			}
		}
		
		//STATE HANDLING
		if(this.state == IDLE_STATE) {
			if(this.vel.getMagnitude() > 0.01) {
				this.changeToMove();
			}
		}
		else if(this.state == MOVE_STATE) {
			if(this.vel.getMagnitude() < 0.01) {
				this.changeToIdle();
			}
		}
	}
	
	public void changeToMove() {
		this.state = MOVE_STATE;
		this.frameCounter = 0;
	}
	
	public void changeToIdle() {
		this.state = IDLE_STATE;
		this.frameCounter = 0;
	}
	
	public void draw(Graphics g) {
		
		Vector mouseReal = Entity.convertVectorToReal(GameManager.mouse);
		
		//making player face towards mouse
		if(mouseReal.x > this.pos.x) {
			this.drawSprite(super.sprites.get(this.state).get(frameCounter / frameInterval), g);
		}
		else {
			this.drawHorizontalMirroredSprite(super.sprites.get(this.state).get(frameCounter / frameInterval), g);
		}
		
		
		//draw the currently equipped weapon
		BufferedImage wepImg = this.equippedWeapon.sprites.get(this.equippedWeapon.state).get(0);
		
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
			
			if(!this.immune) {
				if(this.shield > 0) {
					this.shield -= p.damage;
					this.shield = Math.max(0, this.shield);
				}
				else {
					this.health -= p.damage;
				}
				
				//reset shield regen timer
				this.shieldRegenCounter = this.shieldRegenInterval;
				
				//give player immune frames
				this.immune = true;
				this.immuneTimeLeft = this.immuneTime;
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
			interact = true;
		}
		else if(k == KeyEvent.VK_C) {
			this.noClip = !noClip;
		}
		else if(k == KeyEvent.VK_M) {
			this.fastMove = !fastMove;
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
