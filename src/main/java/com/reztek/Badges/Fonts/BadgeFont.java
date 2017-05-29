package com.reztek.Badges.Fonts;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.HashMap;

/**
 * Badge Font helper class
 * @author Craig Vella
 *
 */
public class BadgeFont {
	private static HashMap<String, Font>p_fontMap = new HashMap<String, Font>();
	
	/**
	 * Retrieves a font, using a String name from the font cache
	 * @param fontName - The name of the font to grab
	 * @return the Font requested
	 */
	public static Font FontFromName(String fontName) {
		Font f = p_fontMap.get(fontName);
		if (f == null) {
			try {
				f = Font.createFont(Font.TRUETYPE_FONT, BadgeFont.class.getResourceAsStream(fontName + ".ttf"));
				GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
				p_fontMap.put(fontName, f);
				return f;
			} catch (IOException | FontFormatException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return f;
		}
	}
}
