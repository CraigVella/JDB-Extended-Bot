package com.reztek.modules.TrialsCommands.Badges;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;

import com.reztek.Badges.Badge;
import com.reztek.Badges.StockImages.StockImages;
import com.reztek.Utils.BotUtils;
import com.reztek.modules.GuardianControl.Guardian;

public class TrialsDetailedBadge extends Badge {

	protected TrialsDetailedBadge() throws IOException {
		super(TrialsDetailedBadge.class.getResourceAsStream("B_TRIALS.png"));
	}
	
	public static final TrialsDetailedBadge TrialsDetailedBadgeFromGuardian(Guardian g) throws IOException {
		TrialsDetailedBadge b = new TrialsDetailedBadge();
		// Banner
		b.drawURLImage(new URL(g.getCharacterLastPlayedBackgroundPath()), 7, 6);
		b.drawURLImage(new URL(g.getCharacterLastPlayedEmblem()), 7, 6);
		b.setFontSize(24);
		b.drawShadowedText(g.getName(), 105, 55, TEXT_LEFT, Color.WHITE, Color.BLACK);
		b.drawText(g.getCharacterLastPlayedSubclass(), 105, 74, TEXT_LEFT, Color.WHITE, 16);
		b.setFontName("Arial");
		b.setFontSize(45);
		b.drawShadowedText(g.getCurrentLevel(), 465, 47, TEXT_RIGHT, Color.WHITE, Color.GRAY);
		b.setFontSize(25);
		b.drawImage(StockImages.ImageFromName("LIGHT_STAR.png"), 399, 53, 20,20);
		b.drawShadowedText(g.getCurrentLight(), 462,71, TEXT_RIGHT, Color.YELLOW, Color.GRAY);
		b.setFontSize(22);
		b.drawImage(StockImages.ImageFromName("GRIM_ICON.png"), 392, 79, 15,15);
		b.drawShadowedText(g.getGrimoireScore(), 462, 94, TEXT_RIGHT, Color.WHITE, Color.GRAY);
		// Graphic Info
		b.drawURLImage(new URL(g.getCharacterLastPlayedSubclassIcon()), 17, 217, 60, 60);
		b.setFontSize(12);
		b.setFontColor(Color.BLACK);
		b.drawText(g.getCharacterLastPlayedSubclass(), 47, 289, TEXT_CENTER);
		b.drawURLImage(new URL(g.getCurrentPrimaryWep().getWepIcon()), 252, 120, 60, 60);
		b.drawURLImage(new URL(g.getCurrentSpecialWep().getWepIcon()), 252, 210, 60, 60);
		b.drawURLImage(new URL(g.getCurrentHeavyWep().getWepIcon()), 252, 302, 60, 60);
		if (g.getCurrentExoticArmor().getArmorIcon() != null) {
			b.drawURLImage(new URL(g.getCurrentExoticArmor().getArmorIcon()), 25, 304, 60, 60);
			b.drawText(g.getCurrentExoticArmor().getArmorName().split(" ")[g.getCurrentExoticArmor().getArmorName().split(" ").length - 1], 55, 375, TEXT_CENTER);
		}
		b.drawURLImage(new URL(g.getCurrentArtifact().getArmorIcon()), 102, 304, 60, 60);
		b.drawText(g.getCurrentArtifact().getArmorName().split(" ")[g.getCurrentArtifact().getArmorName().split(" ").length - 1], 132, 375, TEXT_CENTER);
		// Current Weps	
		b.setFontName("Arial Bold");
		b.setFontSize(14);
		b.drawText(g.getCurrentPrimaryWep().getWeaponKills(), 287, 200);
		b.drawText(g.getCurrentPrimaryWep().getHeadshotPercentage(), 362, 200);
		b.drawText(g.getCurrentSpecialWep().getWeaponKills(), 287, 290);
		b.drawText(g.getCurrentSpecialWep().getHeadshotPercentage(), 362, 290);
		b.drawText(g.getCurrentHeavyWep().getWeaponKills(), 290, 380);
		b.drawText(g.getCurrentHeavyWep().getHeadshotPercentage(), 362, 380);
		// Text Info
		b.setFontSize(15);
		b.setFontName("Arial Bold");
		Color textColor = new Color(31,133,0);
		Color textWarn  = Color.RED;
		b.drawShadowedText(g.getTrialsELO(), 112, 132, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText(g.getLighthouseCount(), 235, 132, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText(g.getThisWeekTrialsFlawless(), 237, 161, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText(g.getThisWeekTrialsKD() + (((Integer.valueOf(g.getThisWeekTrialsMatches()) < 10) ? " (" + g.getThisWeekTrialsMatches() + " GAMES)" : "")), 92, 188, TEXT_LEFT, textColor, Color.GRAY);
		b.drawShadowedText(g.getThisYearTrialsKD(), 120, 161, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText("T" + String.valueOf(g.getCurrentIntellect() / 60), 230, 314, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText("T" + String.valueOf(g.getCurrentDiscipline() / 60), 230, 338, TEXT_RIGHT, textColor, Color.GRAY);
		b.drawShadowedText("T" + String.valueOf(g.getCurrentStrength() / 60), 230, 362, TEXT_RIGHT, textColor, Color.GRAY);
		// Best Weps This Week
		b.setFontNameSizeColorType("Arial Bold", 14, Color.BLACK, Font.PLAIN);
		for (int x = 0; x < g.getThisWeekMapWeaponStats().size(); x++) {
			switch (x) {
			case 0:
				b.setFontSize(15);
				b.drawText(BotUtils.abvString(g.getThisWeekMapWeaponStats().get(x).getWeaponName()), 123, 401);
				b.setFontSize(10);
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getWeaponKills(), 130, 415);
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getHeadshotPercentage(), 162, 415);
				break;
			case 1:
				b.setFontSize(15);
				b.drawText(BotUtils.abvString(g.getThisWeekMapWeaponStats().get(x).getWeaponName()), 246, 401);
				b.setFontSize(10);
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getWeaponKills(), 256, 415);
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getHeadshotPercentage(), 290, 415);
				break;
			case 2:
				b.setFontSize(15);
				b.drawText(BotUtils.abvString(g.getThisWeekMapWeaponStats().get(x).getWeaponName()), 380, 401);
				b.setFontSize(10);
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getWeaponKills(), 387, 415);
				b.drawText(g.getThisWeekMapWeaponStats().get(x).getHeadshotPercentage(), 428, 415);
				break;
			default:
			}
		}
		return b;
	}
}
