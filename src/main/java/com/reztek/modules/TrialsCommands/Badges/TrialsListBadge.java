package com.reztek.modules.TrialsCommands.Badges;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import com.reztek.Badges.Badge;
import com.reztek.Badges.StockImages.StockImages;
import com.reztek.modules.TrialsCommands.TrialsList;

public class TrialsListBadge extends Badge{
	
	private int p_playerCount = 0;
	private int p_playerOffset = 0;

	private TrialsListBadge(InputStream baseImage, int playerOffset) throws IOException {
		super(baseImage);
		p_playerOffset = playerOffset;
	}
	
	public static TrialsListBadge BadgeFromType(String BadgeType) throws IOException {
		InputStream is = null;
		int playerOffset = 0;
		switch (BadgeType) {
		case TrialsList.TRIALS_WOOD:
			is = TrialsListBadge.class.getResourceAsStream("SGA_TWOOD.png");
			playerOffset = 30;
			break;
		case TrialsList.TRIALS_BRONZE:
			is = TrialsListBadge.class.getResourceAsStream("SGA_TBRONZE.png");
			playerOffset = 20;
			break;
		case TrialsList.TRIALS_SILVER:
			is = TrialsListBadge.class.getResourceAsStream("SGA_TSILVER.png");
			playerOffset = 10;
			break;
		case TrialsList.TRIALS_GOLD:
			is = TrialsListBadge.class.getResourceAsStream("SGA_TGOLD.png");
			break;
		}
		if (is != null) {
			return new TrialsListBadge(is,playerOffset);
		} else {
			return null;
		}
	}
	
	public boolean addPlayer(String name, String rank, String elo, String console, String flawlessCount) {
		if (p_playerCount >= 10) return false;
		setFontName("Arial Bold");
		setFontSize(22);
		drawShadowedText(String.valueOf(++p_playerCount + p_playerOffset) + ".", 85, 163 + (p_playerCount * 30), TEXT_RIGHT, Color.DARK_GRAY, Color.GRAY);
		setFontColor(Color.BLACK);
		drawText(name, 87, 163 + (p_playerCount * 30), TEXT_LEFT);
		setFontColor(Color.BLACK);
		drawText(rank, 357, 163 + (p_playerCount * 30), TEXT_RIGHT);
		drawText(elo, 450, 163 + (p_playerCount * 30), TEXT_RIGHT);
		drawText(flawlessCount, 538, 163 + (p_playerCount * 30), TEXT_RIGHT);
		try {
			drawImage(StockImages.ImageFromName(console + "_ICON_C.png"),615,143 + (p_playerCount * 30), 25,25);
		} catch (IOException e) {}
		return true;
	}

}
