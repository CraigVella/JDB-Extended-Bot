package com.reztek.modules.GuardianControl.Badges;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import com.reztek.Badges.Badge;
import com.reztek.modules.GuardianControl.Guardian;

public class InfoBadge extends Badge {

	public InfoBadge(Guardian g) throws IOException {
		super(InfoBadge.class.getResourceAsStream("GC_INFO.png"));
		drawURLImage(new URL(g.getCharacterLastPlayedBackgroundPath()), 7, 4);
		drawURLImage(new URL(g.getCharacterLastPlayedEmblem()), 7, 4);
		setFontSize(24);
		drawShadowedText(g.getName(), 105, 53, false, Color.WHITE, Color.BLACK);
		drawText(g.getCharacterLastPlayedSubclass(), 107, 72, false, Color.WHITE, 16);
		setFontSize(20);
		drawShadowedText(g.getTrialsELO(), 215, 199, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleELO(), 215, 256, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getTrialsRank(), 335, 199, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleRank(), 335, 256, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getThisYearTrialsKD(), 440, 199, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleKD(), 440, 256, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getLighthouseCount()+"X", 155, 314, false, new Color(17,114,4), Color.GRAY);
	}

}
