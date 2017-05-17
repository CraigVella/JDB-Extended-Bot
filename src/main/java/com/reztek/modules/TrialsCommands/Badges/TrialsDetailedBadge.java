package com.reztek.modules.TrialsCommands.Badges;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;

import com.reztek.Badges.Badge;
import com.reztek.Badges.StockImages.StockImages;
import com.reztek.modules.GuardianControl.Guardian;

public class TrialsDetailedBadge extends Badge {

	protected TrialsDetailedBadge() throws IOException {
		super(TrialsDetailedBadge.class.getResourceAsStream("B_TRIALS.png"));
	}
	
	public static final TrialsDetailedBadge TrialsDetailedBadgeFromGuardian(Guardian g) throws IOException {
		TrialsDetailedBadge b = new TrialsDetailedBadge();
		// Banner
		b.drawURLImage(new URL(g.getCharacterLastPlayedBackgroundPath()), 15, 7);
		b.drawURLImage(new URL(g.getCharacterLastPlayedEmblem()), 15, 7);
		b.setFontSize(24);
		b.drawShadowedText(g.getName(), 113, 56, TEXT_LEFT, Color.WHITE, Color.BLACK);
		b.drawText(g.getCharacterLastPlayedSubclass(), 115, 75, TEXT_LEFT, Color.WHITE, 16);
		b.setFontName("Arial");
		b.setFontSize(45);
		b.drawShadowedText(g.getCurrentLevel(), 473, 48, TEXT_RIGHT, Color.WHITE, Color.GRAY);
		b.setFontSize(25);
		b.drawImage(StockImages.ImageFromName("LIGHT_STAR.png"), 407, 54, 20,20);
		b.drawShadowedText(g.getCurrentLight(), 470,72, TEXT_RIGHT, Color.YELLOW, Color.GRAY);
		b.setFontSize(22);
		b.drawImage(StockImages.ImageFromName("GRIM_ICON.png"), 400, 80, 15,15);
		b.drawShadowedText(g.getGrimoireScore(), 470, 95, TEXT_RIGHT, Color.WHITE, Color.GRAY);
		// Graphic Info
		b.drawURLImage(new URL(g.getCharacterLastPlayedSubclassIcon()), 25, 218, 60, 60);
		b.setFontSize(12);
		b.setFontColor(Color.BLACK);
		b.drawText(g.getCharacterLastPlayedSubclass(), 55, 290, TEXT_CENTER);
		b.drawURLImage(new URL(g.getCurrentPrimaryWep().getWepIcon()), 260, 121, 60, 60);
		b.drawURLImage(new URL(g.getCurrentSpecialWep().getWepIcon()), 260, 211, 60, 60);
		b.drawURLImage(new URL(g.getCurrentHeavyWep().getWepIcon()), 260, 303, 60, 60);
		if (g.getCurrentExoticArmor().getArmorIcon() != null)
			b.drawURLImage(new URL(g.getCurrentExoticArmor().getArmorIcon()), 25, 305, 60, 60);
		b.drawURLImage(new URL(g.getCurrentArtifact().getArmorIcon()), 105, 305, 60, 60);
		// Current Weps	
		b.setFontName("Arial Bold");
		b.setFontSize(14);
		b.drawText(g.getCurrentPrimaryWep().getWeaponKills(), 295, 202);
		b.drawText(g.getCurrentPrimaryWep().getHeadshotPercentage(), 380, 202);
		b.drawText(g.getCurrentSpecialWep().getWeaponKills(), 295, 291);
		b.drawText(g.getCurrentSpecialWep().getHeadshotPercentage(), 380, 291);
		b.drawText(g.getCurrentHeavyWep().getWeaponKills(), 298, 381);
		b.drawText(g.getCurrentHeavyWep().getHeadshotPercentage(), 380, 381);
		// Text Info
		b.setFontSize(15);
		b.setFontName("Arial Bold");
		Color textColor = new Color(31,133,0);
		b.drawShadowedText(g.getTrialsELO(), 120, 133, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText(g.getLighthouseCount(), 243, 133, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText(g.getThisWeekTrialsFlawless(), 245, 162, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText(g.getThisWeekTrialsKD() + (((Integer.valueOf(g.getThisWeekTrialsMatches()) < 10) ? " (" + g.getThisWeekTrialsMatches() + " GAMES)" : "")), 103, 189, TEXT_LEFT, textColor, Color.GRAY);
		b.drawShadowedText(g.getThisYearTrialsKD(), 131, 162, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText("T" + String.valueOf(g.getCurrentIntellect() / 60), 238, 316, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText("T" + String.valueOf(g.getCurrentDiscipline() / 60), 238, 340, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText("T" + String.valueOf(g.getCurrentStrength() / 60), 238, 364, TEXT_RIGHT, textColor, Color.GRAY);
		// Best Weps This Week
		b.setFontNameSizeColorType("Arial Bold", 14, Color.BLACK, Font.PLAIN);
		for (int x = 0; x < g.getThisWeekMapWeaponStats().size(); x++) {
			switch (x) {
			case 0:
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getWeaponName(), 161, 403);
				break;
			case 1:
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getWeaponName(), 284, 403);
				break;
			case 2:
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getWeaponName(), 400, 403);
				break;
			default:
			}
		}
		return b;
	}
}
