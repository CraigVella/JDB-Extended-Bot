package com.reztek.modules.GuardianControl.Badges;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import com.reztek.Badges.Badge;
import com.reztek.Badges.StockImages.StockImages;
import com.reztek.modules.GuardianControl.Guardian;

public class InfoBadge extends Badge {

	public InfoBadge(Guardian g) throws IOException {
		super(InfoBadge.class.getResourceAsStream("GC_INFO.png"));
		drawURLImage(new URL(g.getCharacterLastPlayedBackgroundPath()), 7, 4);
		drawURLImage(new URL(g.getCharacterLastPlayedEmblem()), 7, 4);
		setFontSize(20);
		drawShadowedText(g.getTrialsELO(), 215, 199, TEXT_RIGHT, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleELO(), 215, 256, TEXT_RIGHT, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getTrialsRank(), 335, 199, TEXT_RIGHT, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleRank(), 335, 256, TEXT_RIGHT, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getThisYearTrialsKD(), 440, 199, TEXT_RIGHT, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleKD(), 440, 256, TEXT_RIGHT, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getLighthouseCount()+"X", 155, 314, TEXT_LEFT, new Color(17,114,4), Color.GRAY);
		setFontSize(24);
		drawShadowedText(g.getName(), 105, 53, TEXT_LEFT, Color.WHITE, Color.BLACK);
		drawText(g.getCharacterLastPlayedSubclass(), 107, 72, TEXT_LEFT, Color.WHITE, 16);
		setFontName("Arial");
		setFontSize(45);
		drawShadowedText(g.getCurrentLevel(), 465,45, TEXT_RIGHT, Color.WHITE, Color.GRAY);
		setFontSize(25);
		drawImage(StockImages.ImageFromName("LIGHT_STAR.png"), 399, 51, 20,20);
		drawShadowedText(g.getCurrentLight(), 462,69, TEXT_RIGHT, Color.YELLOW, Color.GRAY);
		setFontSize(22);
		drawImage(StockImages.ImageFromName("GRIM_ICON.png"), 392, 77, 15,15);
		drawShadowedText(g.getGrimoireScore(), 462, 92, TEXT_RIGHT, Color.WHITE, Color.GRAY);
	}

}
