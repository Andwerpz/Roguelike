package util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import particle.DamageNumber;

public class FontTools {
	
	public static Font PressStart2P;
	public static Font AncientModernTales;
	public static Font DiaryOfAn8BitMage;
	public static Font Pixeboy;
	
	public static void loadFonts() {
		try {
			InputStream is = FontTools.class.getResourceAsStream("/fonts/PressStart2P.ttf");
			FontTools.PressStart2P = Font.createFont(Font.TRUETYPE_FONT, is);
			
			is = GraphicsTools.class.getResourceAsStream("/fonts/AncientModernTales.ttf");
			FontTools.AncientModernTales = Font.createFont(Font.TRUETYPE_FONT, is);
			
			is = GraphicsTools.class.getResourceAsStream("/fonts/DiaryOfAn8BitMage.ttf");
			FontTools.DiaryOfAn8BitMage = Font.createFont(Font.TRUETYPE_FONT, is);
			
			is = GraphicsTools.class.getResourceAsStream("/fonts/Pixeboy.ttf");
			FontTools.Pixeboy = Font.createFont(Font.TRUETYPE_FONT, is);
		}
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (FontFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static Font deriveSize(int size, Font font) {
		return font.deriveFont((float) size);
	}

}
