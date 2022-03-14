package animation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animator {
	//does all the animation
	
	public ArrayList<BufferedImage> animation;
	public int curFrame = 0;
	
	public Animator(ArrayList<BufferedImage> animation) {
		this.animation = animation;
	}
	
	
	
}
