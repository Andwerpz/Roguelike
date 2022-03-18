package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import item.Coin;
import map.Map;
import projectile.MagicBallSmall;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class Grunt extends Enemy {
	
	//small, slow moving enemy that spawns in projectiles to attack player
	
	//Default state is idle. When in idle, has 1% chance per frame to start attack, or move animation 
	
	//when starting to move, picks a random amount of frames between 60 and 180 to move, and picks
	//a direction within 45 deg of straight at the player to move.
	
	public static ArrayList<BufferedImage> spriteIdle;
	public static ArrayList<BufferedImage> spriteAttack;
	public static ArrayList<BufferedImage> spriteMove;
	
	public static final int IDLE_STATE = 0;
	public static final int MOVE_STATE = 1;
	public static final int ATTACK_STATE = 2;
	
	public Vector moveVel;
	public double moveSpeed = 0.03;
	public int moveFrames;
	public int maxMoveFrames = 180;
	public int minMoveFrames = 60;
	
	//only need to update on state transitions
	public boolean facingLeft = false;	//if true, then we need to vertically mirror drawn sprites
	
	public int frameCounter = 0;
	public int frameInterval = 10;
	
	public int state = Grunt.IDLE_STATE;
	
	public int numProjectiles = 4;
	public double projectilePosSpread = 1.5;
	public double projectileVel = 0.15;
	
	public Grunt(Vector pos) {
		super(pos, 0.6, 0.6, 5);
	}
	
	public static void loadTextures() {
		Grunt.spriteIdle = GraphicsTools.loadAnimation("/grunt_idle.png", 32, 32);
		Grunt.spriteAttack = GraphicsTools.loadAnimation("/grunt_attack.png", 32, 32);
		Grunt.spriteMove = GraphicsTools.loadAnimation("/grunt_move.png", 32, 32);
	}
	
	@Override
	public void tick(Map map) {
		
		//spawn projectiles if in attack state
		if(this.state == Grunt.ATTACK_STATE && frameCounter % frameInterval == 0 && frameCounter / frameInterval == 3) {
			Vector gruntToPlayer = new Vector(this.pos, GameManager.player.pos);
			gruntToPlayer.setMagnitude(this.projectileVel);
			
			for(int i = 0; i < this.numProjectiles; i++) {
				Vector offset = new Vector((Math.random() - 0.5) * projectilePosSpread, (Math.random() - 0.5) * projectilePosSpread);
				Vector projectilePos = new Vector(this.pos);
				projectilePos.addVector(offset);
				
				Vector projectileVel = new Vector(gruntToPlayer);
				
				GameManager.projectiles.add(new MagicBallSmall(projectilePos, projectileVel, 1));
			}
		}
		
		this.move(map);
		
		if(this.state == Grunt.MOVE_STATE) {
			this.moveFrames --;
			this.vel = new Vector(this.moveVel);
			
			if(this.moveFrames == 0) {
				this.changeToIdleState();
			}
			else if(this.envCollision) {
				this.changeToIdleState();
			}
		}
		
		//determine which state to go to next
		if(this.state == Grunt.IDLE_STATE) {
			if(Math.random() <= 0.01) {
				this.changeToAttackState();
			}
			
			else if(Math.random() <= 0.01) {
				this.changeToMoveState();
			}
		}
	}
	
	public void changeToAttackState() {
		this.state = Grunt.ATTACK_STATE;
		frameCounter = 0;
		this.facingLeft = this.pos.x > GameManager.player.pos.x;
	}
	
	public void changeToIdleState() {
		this.state = Grunt.IDLE_STATE;
		frameCounter = 0;
		
		this.vel = new Vector(0, 0);
	}
	
	public void changeToMoveState() {
		this.state = Grunt.MOVE_STATE;
		frameCounter = 0;
		
		Vector gruntToPlayer = new Vector(this.pos, GameManager.player.pos);
		gruntToPlayer.setMagnitude(this.moveSpeed);
		gruntToPlayer.rotateCounterClockwise(Math.toRadians((Math.random() - 0.5) * 90));
		this.moveVel = new Vector(gruntToPlayer);
		
		this.moveFrames = (int) (Math.random() * (this.maxMoveFrames - this.minMoveFrames)) + this.minMoveFrames; 
		
		this.facingLeft = this.moveVel.x < 0;
	}

	@Override
	public void draw(Graphics g) {	
		BufferedImage nextSprite = null;
		if(this.state == Grunt.IDLE_STATE) {
			nextSprite = Grunt.spriteIdle.get(frameCounter / frameInterval);
		}
		else if(this.state == Grunt.MOVE_STATE) {
			nextSprite = Grunt.spriteMove.get(frameCounter / frameInterval);
		}
		else if(this.state == Grunt.ATTACK_STATE) {
			nextSprite = Grunt.spriteAttack.get(frameCounter / frameInterval);
		}
		
		if(this.facingLeft) {
			nextSprite = GraphicsTools.flipImageHorizontal(nextSprite);
		}
		this.drawCenteredSprite(nextSprite, g);
		
		frameCounter ++;
		
		if(this.state == Grunt.IDLE_STATE) {
			if(frameCounter / frameInterval >= Grunt.spriteIdle.size()) {
				frameCounter = 0;
			}
		}
		else if(this.state == Grunt.MOVE_STATE) {
			if(frameCounter / frameInterval >= Grunt.spriteMove.size()) {
				frameCounter = 0;
			}
		}
		else if(this.state == Grunt.ATTACK_STATE) {	//finished attack
			if(frameCounter / frameInterval >= Grunt.spriteAttack.size()) {
				this.state = Grunt.IDLE_STATE;
				frameCounter = 0;
			}
		}
	}

	@Override
	public void onDeath() {
		GameManager.items.add(new Coin(this.pos));
	}
	
}
