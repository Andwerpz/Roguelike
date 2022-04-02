package util;

import java.awt.Graphics;
import java.awt.Color;

import main.MainPanel;

public class TransitionManager {
	
	public static final int TO_LOADING_STATE = 0;
	public static final int LOADING_STATE = 1;
	public static final int FROM_LOADING_STATE = 2;
	public static final int IDLE_STATE = 3;
	
	public static Color darkBlue = new Color((int) 0x140f59);
	public static Color lightBlue = new Color((int) 0x201978);
	
	public static int squareSize = 120;
	
	public int frameCounter = 0;
	
	public int state;
	
	public boolean doneLoading;
	
	public TransitionManager() {
		this.state = IDLE_STATE;
	}

	public void draw(Graphics g) {
		if(this.state == IDLE_STATE) {
			//don't do anything
		}
		else if(this.state == TO_LOADING_STATE || this.state == LOADING_STATE || this.state == FROM_LOADING_STATE) {
			for(int ox = 0; ox < MainPanel.WIDTH; ox += squareSize) {
				for(int oy = 0; oy < MainPanel.HEIGHT; oy += squareSize) {
					//to make checkerboard pattern, just take mod of manhattan dist to 0, 0
					if((ox + oy) % (squareSize * 2) == 0) {
						g.setColor(darkBlue);
					}
					else {
						g.setColor(lightBlue);
					}
					
					int curSquareSize = squareSize;
					
					if(this.state == TO_LOADING_STATE) {
						curSquareSize = (frameCounter * 4);
					}
					else if(this.state == FROM_LOADING_STATE) {
						curSquareSize = squareSize - (frameCounter * 4);
					}
					
					int x = ox + squareSize / 2;
					int y = oy + squareSize / 2;
					
					g.fillRect(x - curSquareSize / 2, y - curSquareSize / 2, curSquareSize, curSquareSize);
				}
			}
		}	
	}
	
	public void tick() {
		this.frameCounter ++;
		
		if(this.state == LOADING_STATE) {
			if(doneLoading && frameCounter >= 60) {
				this.changeToFromLoadingState();
			}
		}
		else if(this.state == TO_LOADING_STATE) {
			if(frameCounter == 30) {
				this.changeToLoadingState();
			}
		}
		else if(this.state == FROM_LOADING_STATE) {
			if(frameCounter == 30) {
				this.changeToIdleState();
			}
		}
	}
	
	public void startTransition() {
		this.doneLoading = false;
		this.changeToToLoadingState();
	}
	
	public void endTransition() {
		this.doneLoading = true;
		this.frameCounter = 0;
	}
	
	public void changeToToLoadingState() {
		this.state = TransitionManager.TO_LOADING_STATE;
		this.frameCounter = 0;
	}
	
	public void changeToLoadingState() {
		this.state = TransitionManager.LOADING_STATE;
		this.frameCounter = 0;
	}
	
	public void changeToFromLoadingState() {
		this.state = TransitionManager.FROM_LOADING_STATE;
		this.frameCounter = 0;
	}
	
	public void changeToIdleState() {
		this.state = TransitionManager.IDLE_STATE;
		this.frameCounter = 0;
	}
	
}
