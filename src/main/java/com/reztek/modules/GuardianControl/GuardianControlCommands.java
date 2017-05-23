package com.reztek.modules.GuardianControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.reztek.Base.CommandModule;
import com.reztek.Secret.GlobalDefs;
import com.reztek.modules.GuardianControl.Badges.InfoBadge;
import com.reztek.modules.GuardianControl.Guardian.GuardianPerk;
import com.reztek.modules.GuardianControl.Guardian.GuardianWeaponStats;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GuardianControlCommands extends CommandModule {
	
	public static final int LOADOUT_ALL = 0;
	public static final int LOADOUT_PRIMARY_ONLY = 1;
	public static final int LOADOUT_SPECIAL_ONLY = 2;
	public static final int LOADOUT_HEAVY_ONLY = 3;

	public GuardianControlCommands() {
		super("GUARDIANCOMMANDS");
		setModuleNameAndAuthor("Destiny Guardian", "ChaseHQ85");
		addCommand(new String[]{
				"debugguardian", "getauthguardian", "getauthguardian-ps", "getauthguardian-xb",
				"primary","primary-ps","primary-xb","special","special-ps","special-xb",
				"heavy","heavy-ps","heavy-xb","loadout","loadout-ps","loadout-xb",
				"info","info-xb","info-ps", "build", "build-ps", "build-xb", 
				"activity", "activity-ps", "activity-xb"
				});
	}

	@Override
	public void processCommand(String command, String args, MessageReceivedEvent mre) {
		switch (command) {
			case "debugguardian":
				if (args == null) {
					sendHelpString(mre, "!debugGuardian PlayerNameHere");
				} else {
					debugGuardian(mre.getChannel(), args);
				}
			break;
			case "getauthguardian":
			case "getauthguardian-ps":
			case "getauthguardian-xb":
				if (args != null) {
					sendHelpString(mre, "!getAuthGuardian[-ps][-xb]");
				} else {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					getAuthGuardian(mre, d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command));
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
			case "build":
			case "build-ps":
			case "build-xb":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					build(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command));
				} else {
					build(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
				}
			case "activity":
			case "activity-ps":
			case "activity-xb":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					activity(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command));
				} else {
					activity(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
				}
				break;
		}
	}
	
	protected void build(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(g.getName() + "'s current build",null);
			eb.setThumbnail(g.getCharacterLastPlayedSubclassIcon());
			eb.setDescription("***" + g.getCharacterLastPlayedSubclass() + "***");
			for (GuardianPerk p : g.getCurrentSubclassPerks()) {
				eb.addField(p.getPerkName(), p.getPerkDesc(), false);
			}
			String platformS = null, platformD = null;
			if (g.getPlatform().equals("1")) { platformS = "XB"; platformD = "XBox"; }
			if (g.getPlatform().equals("2")) { platformS = "PS"; platformD = "Playstation"; }
			eb.setFooter(platformD + " Guardian", GlobalDefs.WWW_HOST + GlobalDefs.WWW_ASSETS + platformS + "_ICON.png");
			mc.sendMessage(eb.build()).queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void activity(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			EmbedBuilder eb = new EmbedBuilder();
			if (g.getCurrentActivity().getPerkHash().equals("0")) {
				eb.setTitle(g.getName() + " is " + g.getCurrentActivity().getPerkName(),null);
			} else {
				eb.setTitle(g.getName() + " is playing " + g.getCurrentActivity().getPerkName(),null);
			}
			eb.setThumbnail(g.getCurrentActivity().getPerkIcon());
			eb.setDescription("***" + g.getCurrentActivity().getPerkDesc() + "***");
			String platformS = null, platformD = null;
			if (g.getPlatform().equals("1")) { platformS = "XB"; platformD = "XBox"; }
			if (g.getPlatform().equals("2")) { platformS = "PS"; platformD = "Playstation"; }
			eb.setFooter(platformD + " Guardian", GlobalDefs.WWW_HOST + GlobalDefs.WWW_ASSETS + platformS + "_ICON.png");
			mc.sendMessage(eb.build()).queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
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
					for (GuardianPerk gp : gw.getWepPerks()) {
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
				String platformS = null, platformD = null;
				if (g.getPlatform().equals("1")) { platformS = "XB"; platformD = "XBox"; }
				if (g.getPlatform().equals("2")) { platformS = "PS"; platformD = "Playstation"; }
				eb.setFooter(platformD + " Guardian", GlobalDefs.WWW_HOST + GlobalDefs.WWW_ASSETS + platformS + "_ICON.png");
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
	
	protected void getAuthGuardian(MessageReceivedEvent mre, String playerName, String platform) {
		mre.getChannel().sendTyping().queue();
		AuthenticatedGuardian ag = AuthenticatedGuardian.AuthenticatedGuardianFromNameAndPlatform(playerName, platform);
		PrivateChannel pc;
		try {
			pc = mre.getAuthor().hasPrivateChannel() ? mre.getAuthor().getPrivateChannel() : mre.getAuthor().openPrivateChannel().submit().get();
			if (ag == null) {
				pc.sendMessage("Hey " + mre.getAuthor().getAsMention() + ", You need to allow us permission - Follow this link\n" + GlobalDefs.BUNGIE_APP_AUTH).queue();
			} else {
				if (ag.areTokensValid()) {
					pc.sendMessage(mre.getAuthor().getAsMention() + ", I have succesfully validated that we have permission to your authenticated character").queue();
				} else {
					pc.sendMessage(mre.getAuthor().getAsMention() + ", It's been a while, I need you to reauthenticate with us here - \n" + GlobalDefs.BUNGIE_APP_AUTH).queue();
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			mre.getChannel().sendMessage(mre.getAuthor().getAsMention() + ", I tried to open private chat with you but it failed. Let me?").queue();
		}
	}
}
