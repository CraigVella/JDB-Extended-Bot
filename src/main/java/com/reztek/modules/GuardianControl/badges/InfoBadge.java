package com.reztek.modules.GuardianControl.Badges;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import com.reztek.Badges.Badge;
import com.reztek.modules.GuardianControl.Guardian;

public class InfoBadge extends Badge {

	public InfoBadge(Guardian g) throws IOException {
		super(InfoBadge.class.getResourceAsStream("GC_INFO.png"));
		drawURLImage(new URL(g.getCharacterLastPlayedBackgroundPath()), 124, 76);
		drawURLImage(new URL(g.getCharacterLastPlayedEmblem()), 124, 76);
		setFontSize(24);
		drawShadowedText(g.getName(), 225, 125, false, Color.WHITE, Color.BLACK);
		drawText(g.getCharacterLastPlayedSubclass(), 225, 145, false, Color.WHITE, 14);
		setFontSize(20);
		drawShadowedText(g.getTrialsELO(), 335, 271, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleELO(), 335, 328, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getTrialsRank(), 455, 271, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleRank(), 455, 328, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getThisYearTrialsKD(), 550, 271, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getRumbleKD(), 550, 328, true, Color.BLACK, Color.GRAY);
		drawShadowedText(g.getLighthouseCount()+"X", 275, 386, false, new Color(17,114,4), Color.GRAY);
	}

}
