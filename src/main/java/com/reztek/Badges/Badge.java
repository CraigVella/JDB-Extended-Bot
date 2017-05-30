package com.reztek.Badges;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Date;

import javax.imageio.ImageIO;

import com.reztek.Badges.Fonts.BadgeFont;
import com.reztek.Global.GlobalDefs;

/**
 * The base class of a Badge, includes all methods and helpers to create a badge
 * @author Craig Vella
 *
 */
public class Badge {
	public static final int TEXT_LEFT   = 0;
	public static final int TEXT_RIGHT  = 1;
	public static final int TEXT_CENTER = 2;
	
	private BufferedImage p_baseImage = null;
	private Graphics2D p_gx = null;
	private String p_fontName = "Arial Bold";
	private int p_fontSize = 24;
	private Color p_fontColor = Color.BLACK;
	private int p_fontType = Font.PLAIN;
	
	/**
	 * Constructor of a badge - built on whatever base image is sent to it
	 * @param BaseImage as InputStream
	 * @throws IOException
	 */
	public Badge(InputStream baseImage) throws IOException {
		p_baseImage = ImageIO.read(baseImage);
		_initialize();
	}
	
	private void _initialize() {
		p_gx = p_baseImage.createGraphics();
		p_gx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		setFontNameSizeColorType(p_fontName, p_fontSize, p_fontColor, p_fontType);
	}
	
	/**
	 * Draws Buffered Image at given location on badge
	 * @param BufferedImage to draw
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @throws IOException
	 */
	public void drawImage(BufferedImage bi, int x, int y) throws IOException {
		drawImage(bi, x, y, bi.getWidth(), bi.getHeight());
	}
	
	/**
	 * Draws a URL Image at given location on badge
	 * @param URL of image to draw on badge
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @throws IOException
	 */
	public void drawURLImage(URL urlImg, int x, int y) throws IOException {
		BufferedImage bi = ImageIO.read(urlImg);
		drawImage(bi, x, y, bi.getWidth(), bi.getHeight());
	}
	
	/**
	 * Draws a URL Image at given location on badge, with width and height
	 * @param urlImg URL to draw on badge
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @param width of image to draw
	 * @param height of image to draw
	 * @throws IOException
	 */
	public void drawURLImage(URL urlImg, int x, int y, int width, int height) throws IOException {
		BufferedImage bi = ImageIO.read(urlImg);
		drawImage(bi, x, y, width, height);
	}
	
	/**
	 * Draws a BufferedImage Image at given location on badge, with width and height
	 * @param i BufferedImage to draw on badge
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @param width of image to draw
	 * @param height of image to draw
	 * @throws IOException
	 */
	protected void drawImage(BufferedImage i, int x, int y, int width, int height) throws IOException {
		p_gx.drawImage(i, x, y, width, height, null);
	}
	
	/**
	 * Helper function to quickly set the font name, size, color and type for subsequent text writes
	 * @param fontName - The name of the font {@code "Arial Bold"}
	 * @param fontSize - The font size in px
	 * @param color - The Font Color {@code Color.RED}
	 * @param type - The Font type, like Font.PLAIN or Font.BOLD
	 */
	public void setFontNameSizeColorType(String fontName, int fontSize, Color color, int type) {
		p_fontName = fontName;
		p_fontSize = fontSize;
		p_fontColor = color;
		p_fontType = type;
		p_gx.setFont(BadgeFont.FontFromName(fontName).deriveFont(type, fontSize));
		p_gx.setColor(color);
	}
	
	/**
	 * Set font name for subsequent calls to text draws
	 * @param fontName - Font name like {@code "Arial Bold"}
	 */
	public void setFontName(String fontName) {
		setFontNameSizeColorType(fontName, p_fontSize, p_fontColor, p_fontType);
	}
	
	/**
	 * Set font size for subsequent calls to text draws
	 * @param fontSize - font size in px
	 */
	public void setFontSize(int fontSize) {
		setFontNameSizeColorType(p_fontName, fontSize, p_fontColor, p_fontType);
	}
	
	/**
	 * Set font color for subsequent calls to text draws
	 * @param color - Color for font ( {@code Color.RED} )
	 */
	public void setFontColor(Color color) {
		setFontNameSizeColorType(p_fontName, p_fontSize, color, p_fontType);
	}
	
	/**
	 * Set font type for subsequent calls to text draws
	 * @param ft - Font type ( {@code FONT.Plain} )
	 */
	public void setFontType(int ft) {
		setFontNameSizeColorType(p_fontName, p_fontSize, p_fontColor, ft);
	}
	
	/**
	 * Draws the text at the given coordinate
	 * @param text - String of text to draw
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 */
	public void drawText(String text, int x, int y) {
		drawText(text,x,y,TEXT_LEFT);
	}
	
	/**
	 * Draws the text at the given coordinate and using the given alignment
	 * @param text - String of text to draw
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @param align - Set text alignment type {@code TEXT_LEFT | TEXT_RIGHT | TEXT_CENTER}
	 */
	public void drawText(String text, int x, int y, int align) {
		drawText(text,x,y,align,p_fontColor);
	}
	
	/**
	 * Draws the text at the given coordinate, using the given alignment, and with the given color
	 * @param text - String of text to draw
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @param align - Set text alignment type {@code TEXT_LEFT | TEXT_RIGHT | TEXT_CENTER}
	 * @param color - Sets the color of the text to be drawn {@code Color.RED}
	 */
	public void drawText(String text, int x, int y, int align, Color color) {
		drawText(text,x,y,align,color,p_fontSize);
	}
	
	/**
	 * Draws the text at the given coordinate, using the given alignment, the given color, and the given size
	 * @param text - String of text to draw
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @param align - Set text alignment type {@code TEXT_LEFT | TEXT_RIGHT | TEXT_CENTER}
	 * @param color - Sets the color of the text to be drawn {@code Color.RED}
	 * @param size - Sets the size of the text to be drawn in px
	 */
	public void drawText(String text, int x, int y, int align, Color color, int size) {
		drawText(text,x,y,align,color,size,p_fontName);
	}
	
	/**
	 * Draws the text at the given coordinate, using the given alignment, the given color, the given size, and the given Font name
	 * @param text - String of text to draw
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @param align - Set text alignment type {@code TEXT_LEFT | TEXT_RIGHT | TEXT_CENTER}
	 * @param color - Sets the color of the text to be drawn {@code Color.RED}
	 * @param size - Sets the size of the text to be drawn in px
	 * @param fontName - Sets the font name of the font {@code "Arial Bold"}
	 */
	public void drawText(String text, int x, int y, int align, Color color, int size, String fontName) {
		drawText(text,x,y,align,color,size,fontName,p_fontType);
	}
	
	public void drawText(String text, int x, int y, int align, Color color, int size, String fontName, int type) {
		setFontNameSizeColorType(fontName, size, color, type);
		switch (align) {
		case TEXT_RIGHT:
			p_gx.drawString(text,(x - p_gx.getFontMetrics().stringWidth(text)), y);
			break;
		case TEXT_CENTER:
			p_gx.drawString(text, (x - (p_gx.getFontMetrics().stringWidth(text) / 2)) ,y);
			break;
		case TEXT_LEFT:
		default:
			p_gx.drawString(text,  x, y);
			break;
		}
	}
	
	/**
	 * Draws shadowed looking text using the string given, at the coordinents given, using the text alignment, the fore color of the font and the color of the shadow
	 * @param text - String of text to draw
	 * @param x coordinate to draw at
	 * @param y coordinate to draw at
	 * @param align - Set text alignment type {@code TEXT_LEFT | TEXT_RIGHT | TEXT_CENTER}
	 * @param foreColor - Sets the shadow text forecolor {@code Color.GREEN}
	 * @param backColor - Sets the shadow of the text {@code Color.GRAY}
	 */
	public void drawShadowedText(String text, int x, int y, int align, Color foreColor, Color backColor) {
		drawText(text, x+1, y+1, align, backColor);
		drawText(text, x, y, align, foreColor);
	}
	
	/**
	 * <p>Finalizes all the draws on the badge, sets a randomized name, saves the badge to the {@link GlobalDefs.LOCAL_BADGE_CACHE} for webserving.
	 * Please call {@link Badge.cleanup()} when done with image to allow for cleanups.</p>
	 * @return the String of the hosted image
	 * @throws IOException
	 */
	public String finalizeBadge() throws IOException {
		SecureRandom random = new SecureRandom();
		int num = random.nextInt(10000000);
		String formatted = String.format("%07d", num); 
		File f = new File(GlobalDefs.LOCAL_BADGE_CACHE + String.valueOf(new Date().getTime()) + "_" + formatted + ".png");
		f.createNewFile();
		ImageIO.write(p_baseImage,"png", f);
		return GlobalDefs.WWW_HOST + GlobalDefs.WWW_BADGE_CACHE + f.getName();
	}
	
	/**
	 * Cleans up the Badge, call when your done with it
	 */
	public void cleanup() {
		p_gx.dispose();
	}
}
