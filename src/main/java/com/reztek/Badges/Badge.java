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
import com.reztek.Secret.GlobalDefs;

public class Badge {
	private BufferedImage p_baseImage = null;
	private Graphics2D p_gx = null;
	private String p_fontName = "Arial Bold";
	private int p_fontSize = 24;
	private Color p_fontColor = Color.BLACK;
	private int p_fontType = Font.PLAIN;
	
	public Badge(InputStream baseImage) throws IOException {
		p_baseImage = ImageIO.read(baseImage);
		_initialize();
	}
	
	protected void _initialize() {
		p_gx = p_baseImage.createGraphics();
		p_gx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		setFontNameSizeColorType(p_fontName, p_fontSize, p_fontColor, p_fontType);
	}
	
	public void drawImage(BufferedImage bi, int x, int y) throws IOException {
		drawImage(bi, x, y, bi.getWidth(), bi.getHeight());
	}
	
	public void drawURLImage(URL urlImg, int x, int y) throws IOException {
		BufferedImage bi = ImageIO.read(urlImg);
		drawImage(bi, x, y, bi.getWidth(), bi.getHeight());
	}
	
	public void drawURLImage(URL urlImg, int x, int y, int width, int height) throws IOException {
		BufferedImage bi = ImageIO.read(urlImg);
		drawImage(bi, x, y, width, height);
	}
	
	protected void drawImage(BufferedImage i, int x, int y, int width, int height) throws IOException {
		p_gx.drawImage(i, x, y, width, height, null);
	}
	
	public void setFontNameSizeColorType(String fontName, int fontSize, Color color, int type) {
		p_fontName = fontName;
		p_fontSize = fontSize;
		p_fontColor = color;
		p_fontType = type;
		p_gx.setFont(BadgeFont.FontFromName(fontName).deriveFont(type, fontSize));
		p_gx.setColor(color);
	}
	
	public void setFontName(String fontName) {
		setFontNameSizeColorType(fontName, p_fontSize, p_fontColor, p_fontType);
	}
	
	public void setFontSize(int fontSize) {
		setFontNameSizeColorType(p_fontName, fontSize, p_fontColor, p_fontType);
	}
	
	public void setFontColor(Color color) {
		setFontNameSizeColorType(p_fontName, p_fontSize, color, p_fontType);
	}
	
	public void setFontType(int ft) {
		setFontNameSizeColorType(p_fontName, p_fontSize, p_fontColor, ft);
	}
	
	public void drawText(String text, int x, int y) {
		drawText(text,x,y,false);
	}
	
	public void drawText(String text, int x, int y, boolean rightAlign) {
		drawText(text,x,y,rightAlign,p_fontColor);
	}
	
	public void drawText(String text, int x, int y, boolean rightAlign, Color color) {
		drawText(text,x,y,rightAlign,color,p_fontSize);
	}
	
	public void drawText(String text, int x, int y, boolean rightAlign, Color color, int size) {
		drawText(text,x,y,rightAlign,color,size,p_fontName);
	}
	
	public void drawText(String text, int x, int y, boolean rightAlign, Color color, int size, String fontName) {
		drawText(text,x,y,rightAlign,color,size,fontName,p_fontType);
	}
	
	public void drawText(String text, int x, int y, boolean rightAlign, Color color, int size, String fontName, int type) {
		setFontNameSizeColorType(fontName, size, color, type);
		p_gx.drawString(text, (rightAlign ? (x - p_gx.getFontMetrics().stringWidth(text)) : x), y);
	}
	
	public void drawShadowedText(String text, int x, int y, boolean rightAlign, Color foreColor, Color backColor) {
		drawText(text, x+1, y+1, rightAlign, backColor);
		drawText(text, x, y, rightAlign, foreColor);
	}
	
	public String finalizeBadge() throws IOException {
		SecureRandom random = new SecureRandom();
		int num = random.nextInt(10000000);
		String formatted = String.format("%07d", num); 
		File f = new File((GlobalDefs.BOT_DEV ? GlobalDefs.LOCAL_DEV_BADGE_CACHE : GlobalDefs.LOCAL_BADGE_CACHE) + String.valueOf(new Date().getTime()) + "_" + formatted + ".png");
		f.createNewFile();
		ImageIO.write(p_baseImage,"png", f);
		return GlobalDefs.WWW_HOST + GlobalDefs.WWW_BADGE_CACHE + f.getName();
	}
	
	public void cleanup() {
		p_gx.dispose();
	}
}
