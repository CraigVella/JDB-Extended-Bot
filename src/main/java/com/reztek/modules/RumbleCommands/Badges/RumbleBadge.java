package com.reztek.modules.RumbleCommands.Badges;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import com.reztek.Badges.Badge;
import com.reztek.modules.RumbleCommands.RumbleList;

public class RumbleBadge extends Badge {
	
	private int p_playerCount = 0;
	private int p_playerOffset = 0;
	
	private RumbleBadge(InputStream is, int playerOffset) throws IOException {
		super(is);
		p_playerOffset = playerOffset;
	}
	
	public static RumbleBadge BadgeFromType(String BadgeType) throws IOException {
		InputStream is = null;
		int playerOffset = 0;
		switch (BadgeType) {
		case RumbleList.RUMBLE_BRONZE:
			is = RumbleBadge.class.getResourceAsStream("SGA_RBRONZE.png");
			playerOffset = 20;
			break;
		case RumbleList.RUMBLE_SILVER:
			is = RumbleBadge.class.getResourceAsStream("SGA_RSILVER.png");
			playerOffset = 10;
			break;
		case RumbleList.RUMBLE_GOLD:
			is = RumbleBadge.class.getResourceAsStream("SGA_RGOLD.png");
			break;
		}
		if (is != null) {
			return new RumbleBadge(is,playerOffset);
		} else {
			return null;
		}
	}
	
	public boolean addPlayer(String name, String rank, String elo, String console) {
		if (p_playerCount >= 10) return false;
		setFontName("Arial Bold");
		setFontSize(18);
		drawShadowedText(String.valueOf(++p_playerCount + p_playerOffset) + ".", 120, 170 + (p_playerCount * 30), true, Color.BLACK, Color.GRAY);
		setFontName("Arial Narrow");
		drawShadowedText(name, 125, 170 + (p_playerCount * 30), false, Color.BLACK, Color.GRAY);
		setFontName("Arial Bold");
		drawShadowedText(rank, 330, 170 + (p_playerCount * 30), true, Color.BLACK, Color.GRAY);
		drawShadowedText(elo, 465, 170 + (p_playerCount * 30), true, Color.BLACK, Color.GRAY);
		drawShadowedText(console, 550, 170 + (p_playerCount * 30), false, Color.BLACK, Color.GRAY);
		return true;
	}

}
