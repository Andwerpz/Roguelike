package particle;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import entity.Entity;
import map.Map;
import util.FontTools;
import util.GraphicsTools;
import util.Vector;

public class DamageNumber extends Particle {
	
	public static double yVelBase = -0.5;
	public static double xVelBase = 0;	//x vel is introduced through spread
	
	public static double yVelSpread = 0.1;
	public static double xVelSpread = 0.6;
	
	public static Font font;
	public static int fontSize = 20;
	
	public static int totalFrames = 90;
	public static int fadeFrames = 60;
	
	public int value;

	public DamageNumber(Vector pos, int value) {
		super(pos, new Vector(xVelBase + (Math.random() - 0.5) * xVelSpread, yVelBase + (Math.random() - 0.5) * yVelSpread), 0, 0, null);
		this.value = value;
		
		this.doCollision = false;
	}
	
	public static void loadTextures() {
		DamageNumber.font = FontTools.deriveSize(fontSize, FontTools.PressStart2P);
//		InputStream is;
//		try {
//			is = GraphicsTools.class.getResourceAsStream("/fonts/PressStart2P.ttf");
//			DamageNumber.font = Font.createFont(Font.TRUETYPE_FONT, is);
//			DamageNumber.font = DamageNumber.font.deriveFont((float) fontSize);
//		} catch (FontFormatException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void tick(Map map) {
		this.move(map);
		this.frameCounter ++;
		
		if(this.frameCounter == DamageNumber.totalFrames) {
			this.despawn();
		}
	}

	@Override
	public void draw(Graphics g) {
		int fontWidth = GraphicsTools.calculateTextWidth(value + "", DamageNumber.font);
		Vector screenPos = Entity.convertVectorToScreen(this.pos);
		
		int startFrames = totalFrames - fadeFrames;
		double alpha = this.frameCounter <= (totalFrames - fadeFrames)? 1d : (double) (DamageNumber.fadeFrames - this.frameCounter + startFrames) / (double) DamageNumber.fadeFrames;
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(GraphicsTools.makeComposite(alpha));
		
		g.setFont(DamageNumber.font);
		g.setColor(Color.RED);
		g.drawString(value + "", (int) screenPos.x - fontWidth / 2, (int) screenPos.y);
		
		g2.setComposite(GraphicsTools.makeComposite(1));
	}
}
