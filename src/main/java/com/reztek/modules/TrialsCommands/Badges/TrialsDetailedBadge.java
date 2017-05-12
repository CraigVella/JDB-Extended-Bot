package com.reztek.modules.TrialsCommands.Badges;

import java.io.IOException;
import java.net.URL;

import com.reztek.Badges.Badge;
import com.reztek.modules.GuardianControl.Guardian;

public class TrialsDetailedBadge extends Badge {

	protected TrialsDetailedBadge() throws IOException {
		super(TrialsDetailedBadge.class.getResourceAsStream("B_TRIALS.png"));
	}
	
	public static final TrialsDetailedBadge TrialsDetailedBadgeFromGuardian(Guardian g) throws IOException {
		TrialsDetailedBadge b = new TrialsDetailedBadge();
		b.drawURLImage(new URL(g.getCharacterLastPlayedBackgroundPath()), 10, 9, 698, 139);
		b.drawURLImage(new URL(g.getCharacterLastPlayedEmblem()), 10, 9, 141, 139);
		b.drawURLImage(new URL(g.getCharacterLastPlayedSubclassIcon()), 20, 315, 80, 80);
		b.drawURLImage(new URL(g.getCurrentPrimaryWep().getWepIcon()), 372, 172, 90, 90);
		b.drawURLImage(new URL(g.getCurrentSpecialWep().getWepIcon()), 372, 301, 90, 90);
		b.drawURLImage(new URL(g.getCurrentHeavyWep().getWepIcon()), 372, 432, 90, 90);
		
		return b;
	}

}
