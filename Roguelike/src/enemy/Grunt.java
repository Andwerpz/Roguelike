package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import item.Coin;
import map.Map;
import projectile.MagicBallSmall;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class Grunt extends Enemy {
	
	//small, slow moving enemy that spawns in slow projectiles to attack player
	
	//Default state is idle. When in idle, has 1% chance per frame to start attack, or move animation 
	
	//when starting to move, picks a random amount of frames between 60 and 180 to move, and picks
	//a direction within 45 deg of straight at the player to move.
	
	public static HashMap<Integer, ArrayList<BufferedImage>> sprites;
	
	public static final int IDLE_STATE = 1;
	public static final int MOVE_STATE = 2;
	public static final int ATTACK_STATE = 3;
	
	//MOVE STATE
	public Vector moveVel;
	public double moveSpeed = 0.03;
	public int moveFrames;
	public int maxMoveFrames = 180;
	public int minMoveFrames = 60;
	
	//ATTACK STATE
	public int numProjectiles = 4;
	public double projectilePosSpread = 1.5;
	public double projectileVel = 0.1;
	
	public Grunt(Vector pos) {
		super(pos, 0.6, 0.6, 5, Grunt.sprites);
		this.state = Grunt.IDLE_STATE;
	}
	
	public static void loadTextures() {
		Grunt.sprites = new HashMap<>();
		Grunt.sprites.put(Grunt.IDLE_STATE, GraphicsTools.loadAnimation("/grunt_idle.png", 32, 32));
		Grunt.sprites.put(Grunt.ATTACK_STATE, GraphicsTools.loadAnimation("/grunt_attack.png", 32, 32));
		Grunt.sprites.put(Grunt.MOVE_STATE, GraphicsTools.loadAnimation("/grunt_move.png", 32, 32));
	}
	
	@Override
	public void tick(Map map) {
		
		this.move(map);
		this.incrementFrameCounter();
		this.checkForDeath();
		
		if(this.state == Grunt.IDLE_STATE) {
			if(Math.random() <= 0.01) {
				this.changeToAttack();
			}
			else if(Math.random() <= 0.01) {
				this.changeToMove();
			}
		}
		
		else if(this.state == Grunt.MOVE_STATE) {
			this.moveFrames --;
			this.vel = new Vector(this.moveVel);
			
			if(this.moveFrames == 0) {
				this.changeToIdle();
			}
			else if(this.envCollision) {
				this.changeToIdle();
			}
		}
		
		else if(this.state == Grunt.ATTACK_STATE) {
			if(frameCounter % frameInterval == 0 && frameCounter / frameInterval == 3) {
				//spawn new projectiles to attack player
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
			else if(this.animationLooped) {
				this.changeToIdle();
			}
		}
	}
	
	public void changeToAttack() {
		this.state = Grunt.ATTACK_STATE;
		frameCounter = 0;
		this.facingLeft = this.pos.x > GameManager.player.pos.x;
	}
	
	public void changeToIdle() {
		this.state = Grunt.IDLE_STATE;
		frameCounter = 0;
		
		this.vel = new Vector(0, 0);
	}
	
	public void changeToMove() {
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
	public void onDeath() {
		GameManager.items.add(new Coin(this.pos));
	}
	
}
