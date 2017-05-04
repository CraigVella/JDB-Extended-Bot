package com.reztek.modules.GuardianControl;

import java.io.IOException;
import java.util.ArrayList;

import com.reztek.SGAExtendedBot;
import com.reztek.base.CommandModule;
import com.reztek.modules.GuardianControl.Guardian.GuardianWeaponPerk;
import com.reztek.modules.GuardianControl.Guardian.GuardianWeaponStats;
import com.reztek.modules.GuardianControl.badges.InfoBadge;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GuardianControlCommands extends CommandModule {
	
	public static final int LOADOUT_ALL = 0;
	public static final int LOADOUT_PRIMARY_ONLY = 1;
	public static final int LOADOUT_SPECIAL_ONLY = 2;
	public static final int LOADOUT_HEAVY_ONLY = 3;

	public GuardianControlCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot,"GUARDIANCOMMANDS");
		setModuleNameAndAuthor("Destiny Guardian", "ChaseHQ85");
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
		
		/*String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}*/
		
		switch (command) {
			case "debugguardian":
				if (args == null) {
					sendHelpString(mre, "!debugGuardian PlayerNameHere");
				} else {
					debugGuardian(mre.getChannel(), args);
				}
			break;
			case "primary":
			case "primary-ps":
			case "primary-p-xb":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					loadOutInfo(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command), LOADOUT_PRIMARY_ONLY);
				} else {
					loadOutInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), LOADOUT_PRIMARY_ONLY);
				}
				break;

			case "special":
			case "special-ps":
			case "special-xb":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					loadOutInfo(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command), LOADOUT_SPECIAL_ONLY);
				} else {
					loadOutInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), LOADOUT_SPECIAL_ONLY);
				}
				break;

			case "heavy":
			case "heavy-ps":
			case "heavy-xb":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					loadOutInfo(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command), LOADOUT_HEAVY_ONLY);
				} else {
					loadOutInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), LOADOUT_HEAVY_ONLY);
				}
				break;
			case "loadout":
			case "loadout-ps":
			case "loadout-xb":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					loadOutInfo(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command), LOADOUT_ALL);
				} else {
					loadOutInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), LOADOUT_ALL);
				}
				break;
			case "info-ps":
			case "info-xb":
			case "info":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					playerInfo(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command));
				} else {
					playerInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
				}
				break;
			default: 
				return false;
		}
		
		return true;
	}
	
	protected void loadOutInfo(MessageChannel mc, String playerName, String platform, int showLoadoutType) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		String loadoutTitle = "Loadout";
		if (showLoadoutType == LOADOUT_HEAVY_ONLY) loadoutTitle = "Heavy Weapon";
		if (showLoadoutType == LOADOUT_SPECIAL_ONLY) loadoutTitle = "Special Weapon";
		if (showLoadoutType == LOADOUT_PRIMARY_ONLY) loadoutTitle = "Primary Weapon";
		if (g != null) {
			mc.sendMessage("**" + g.getName() + "**'s " + loadoutTitle).queue();
			ArrayList<GuardianWeaponStats> wsa = new ArrayList<GuardianWeaponStats>();
			if (showLoadoutType == LOADOUT_ALL) {
				wsa.add(g.getCurrentPrimaryWep());
				wsa.add(g.getCurrentSpecialWep());
				wsa.add(g.getCurrentHeavyWep());
			}
			if (showLoadoutType == LOADOUT_PRIMARY_ONLY) { wsa.add(g.getCurrentPrimaryWep()); }
			if (showLoadoutType == LOADOUT_SPECIAL_ONLY) { wsa.add(g.getCurrentSpecialWep()); }
			if (showLoadoutType == LOADOUT_HEAVY_ONLY) { wsa.add(g.getCurrentHeavyWep()); }
			for (GuardianWeaponStats gw : wsa) {
				if (gw != null) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle(gw.getWeaponName(), null);
					eb.setColor(gw.getDamageType().getColor());
					eb.setFooter(gw.getDamageType().getName(), gw.getDamageType().getDamageIcon());
					eb.setThumbnail(gw.getWepIcon());
					if (gw.getWeaponKills() != null) {
						eb.addField("Kills", gw.getWeaponKills(), true);
						eb.addField("Headshot %", gw.getHeadshotPercentage(), true);
					}
					for (GuardianWeaponPerk gp : gw.getWepPerks()) {
						eb.addField(gp.getPerkName(), gp.getPerkDesc(), false);
					}
					mc.sendMessage(eb.build()).queue();
				}
			}
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void playerInfo(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			EmbedBuilder eb = new EmbedBuilder();
			try {
				InfoBadge b = new InfoBadge(g);
				eb.setImage(b.finalizeBadge());
				b.cleanup();
				mc.sendMessage(eb.build()).queue();
			} catch (IOException e) {
				mc.sendMessage("Something went wrong... [GuardianControlCommands.playerInfo()] Tellsomeone?").queue();
			}
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void debugGuardian(MessageChannel mc, String playerName) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName,Guardian.PLATFORM_ALL);
		mc.sendMessage("DEBUG: " + g.getId() +"\n" +
					   "Name: " + g.getName() + "\n" + 
				       "Platform: " + g.getPlatform()).queue();
		
	}
}
