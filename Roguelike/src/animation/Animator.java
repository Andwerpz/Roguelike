package animation;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {
	//does all the animation
	
	public ArrayList<BufferedImage> animation;
	public int curFrame = 0;
	
	public boolean interruptible = false;
	
	public Animation(ArrayList<BufferedImage> animation, boolean interruptible) {
		this.animation = animation;
		this.interruptible = interruptible;
	}
	
	public void draw(Graphics g, int xOrigin, int yOrigin) {
		//g.drawImage(animation.get(curFrame), , yOrigin, yOrigin, yOrigin, yOrigin, yOrigin, xOrigin, yOrigin, null)
		curFrame ++;
	}
	
}
