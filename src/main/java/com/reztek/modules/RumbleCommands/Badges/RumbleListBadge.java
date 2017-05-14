package com.reztek.modules.RumbleCommands.Badges;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import com.reztek.Badges.Badge;
import com.reztek.Badges.StockImages.StockImages;
import com.reztek.modules.RumbleCommands.RumbleList;

public class RumbleListBadge extends Badge {
	
	private int p_playerCount = 0;
	private int p_playerOffset = 0;
	
	private RumbleListBadge(InputStream is, int playerOffset) throws IOException {
		super(is);
		p_playerOffset = playerOffset;
	}
	
	public static RumbleListBadge BadgeFromType(String BadgeType) throws IOException {
		InputStream is = null;
		int playerOffset = 0;
		switch (BadgeType) {
		case RumbleList.RUMBLE_WOOD:
			is = RumbleListBadge.class.getResourceAsStream("SGA_RWOOD.png");
			playerOffset = 30;
			break;
		case RumbleList.RUMBLE_BRONZE:
			is = RumbleListBadge.class.getResourceAsStream("SGA_RBRONZE.png");
			playerOffset = 20;
			break;
		case RumbleList.RUMBLE_SILVER:
			is = RumbleListBadge.class.getResourceAsStream("SGA_RSILVER.png");
			playerOffset = 10;
			break;
		case RumbleList.RUMBLE_GOLD:
			is = RumbleListBadge.class.getResourceAsStream("SGA_RGOLD.png");
			break;
		}
		if (is != null) {
			return new RumbleListBadge(is,playerOffset);
		} else {
			return null;
		}
	}
	
	public boolean addPlayer(String name, String rank, String elo, String console) {
		if (p_playerCount >= 10) return false;
		setFontName("Arial Bold");
		setFontSize(22);
		drawShadowedText(String.valueOf(++p_playerCount + p_playerOffset) + ".", 55, 160 + (p_playerCount * 30), true, Color.DARK_GRAY, Color.GRAY);
		setFontColor(Color.BLACK);
		drawText(name, 60, 160 + (p_playerCount * 30), false);
		setFontColor(Color.BLACK);
		drawText(rank, 315, 160 + (p_playerCount * 30), true);
		drawText(elo, 415, 160 + (p_playerCount * 30), true);
		try {
			drawImage(StockImages.ImageFromName(console + "_ICON_C.png"),480,140 + (p_playerCount * 30), 25,25);
		} catch (IOException e) {}
		return true;
	}

}
