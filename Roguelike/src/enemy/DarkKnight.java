package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import item.Coin;
import map.Map;
import projectile.MagicBallPointed;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class DarkKnight extends Enemy{
	
	//medium enemy that shoots lots of slow moving bullets in the direction of the player
	
	//when moving, it chooses a direction that is minimum 60 deg and maximum 90 deg from the vector
	//from itself to the player
	
	//when attacking, it will shoot many slow moving bullets in a cone at the player
	
	public static ArrayList<BufferedImage> spriteIdle;
	public static ArrayList<BufferedImage> spriteMove;
	
	public static final int IDLE_STATE = 0;
	public static final int MOVE_STATE = 1;
	public static final int ATTACK_STATE = 2;
		
	//MOVE STATE
	public Vector moveVel;
	public double moveSpeed = 0.03;
	
	public int moveFrames;
	public int maxMoveFrames = 180;
	public int minMoveFrames = 60;
	
	public double minAngle = 45;
	public double maxAngle = 90;
	
	//ATTACK STATE
	public int attackFrames;
	public int minAttackFrames = 120;
	public int maxAttackFrames = 150;
	
	public double projectileSpeed = 0.2;
	public double projectileAngleSpread = 15;
	public double projectileSpeedSpread = 0.025;

	public DarkKnight(Vector pos) {
		super(pos, 1.3, 1.3, 20);
		this.state = DarkKnight.IDLE_STATE;
		this.sprites.put(DarkKnight.IDLE_STATE, spriteIdle);
		this.sprites.put(DarkKnight.MOVE_STATE, spriteMove);
		this.sprites.put(DarkKnight.ATTACK_STATE, spriteIdle);
	}
	
	public static void loadTextures() {
		DarkKnight.spriteIdle = GraphicsTools.loadAnimation("/dark_knight_idle.png", 32, 32);
		DarkKnight.spriteMove = GraphicsTools.loadAnimation("/dark_knight_move.png", 32, 32);
	}

	@Override
	public void tick(Map map) {
		this.move(map);
		this.incrementFrameCounter();
		
		if(this.state == DarkKnight.IDLE_STATE) {
			if(Math.random() <= 0.008) {
				this.changeToMove();
			}
			else if(Math.random() <= 0.008) {
				this.changeToAttack();
			}
		}
		
		else if(this.state == DarkKnight.MOVE_STATE) {
			this.moveFrames --;
			this.vel = new Vector(this.moveVel);
			
			if(this.moveFrames == 0) {
				this.changeToIdle();
			}
			else if(this.envCollision) {
				this.changeToIdle();
			}
		}
		
		else if(this.state == DarkKnight.ATTACK_STATE) {
			this.attackFrames --;
			
			if(this.frameCounter % 10 == 0) {	//fire projectile
				Vector toPlayer = new Vector(this.pos, GameManager.player.pos);
				
				this.facingLeft = toPlayer.x < 0;
				
				Vector projectilePosOffset = new Vector(0.85, 0.5);
				if(this.facingLeft) {
					projectilePosOffset.x *= -1;
				}
				
				Vector projectileVel = new Vector(toPlayer);
				projectileVel.setMagnitude(this.projectileSpeed + (Math.random() - 0.5) * this.projectileSpeedSpread);
				projectileVel.rotateCounterClockwise(Math.toRadians((Math.random() - 0.5) * this.projectileAngleSpread));
				
				Vector projectilePos = new Vector(this.pos);
				projectilePos.addVector(projectilePosOffset);
				GameManager.projectiles.add(new MagicBallPointed(projectilePos, projectileVel, 3));
			}
			
			if(this.attackFrames == 0) {
				this.changeToIdle();
			}
		}
	}
	
	public void changeToIdle() {
		this.state = DarkKnight.IDLE_STATE;
		this.frameCounter = 0;
	}
	
	public void changeToMove() {
		this.state = DarkKnight.MOVE_STATE;
		frameCounter = 0;
		
		double angle = Math.toRadians((Math.random() * (this.maxAngle - this.minAngle)) + this.minAngle) * (Math.random() >= 0.5? -1 : 1);
		Vector toPlayer = new Vector(this.pos, GameManager.player.pos);
		toPlayer.setMagnitude(this.moveSpeed);
		toPlayer.rotateCounterClockwise(angle);
		this.moveVel = new Vector(toPlayer);
		
		this.moveFrames = (int) (Math.random() * (this.maxMoveFrames - this.minMoveFrames)) + this.minMoveFrames; 
		
		this.facingLeft = this.moveVel.x < 0;
	}
	
	public void changeToAttack() {
		this.state = DarkKnight.ATTACK_STATE;
		frameCounter = 0;
		
		this.attackFrames = (int) (Math.random() * (this.maxAttackFrames - this.minAttackFrames)) + this.minAttackFrames; 
	}
	
	@Override
	public void onDeath() {
		for(int i = 0; i < 5; i++) {
			GameManager.items.add(new Coin(this.pos));
		}
	}

}
