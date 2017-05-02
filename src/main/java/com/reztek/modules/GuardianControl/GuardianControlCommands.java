package com.reztek.modules.GuardianControl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import com.reztek.SGAExtendedBot;
import com.reztek.base.CommandModule;
import com.reztek.modules.GuardianControl.Guardian.GuardianWeaponPerk;
import com.reztek.modules.GuardianControl.Guardian.GuardianWeaponStats;
import com.reztek.secret.GlobalDefs;

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
				File f = new File(GlobalDefs.LOCAL_IMGFOLDER + String.valueOf(new Date().getTime()) + g.getId() + ".png");
				f.createNewFile();
				
				BufferedImage gEmblem = ImageIO.read(new URL(g.getCharacterLastPlayedEmblem()));
				BufferedImage gBackground = ImageIO.read(new URL(g.getCharacterLastPlayedBackgroundPath()));
				BufferedImage im = ImageIO.read(getClass().getResourceAsStream("GC_INFO.png"));
				Graphics2D gr = im.createGraphics();
				
				gr.drawImage(gBackground, 3, 2, gBackground.getWidth(), gBackground.getHeight(), null);
				gr.drawImage(gEmblem, 3,2, gEmblem.getWidth(), gEmblem.getHeight(),null);
				
				gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				gr.setFont(new Font("Arial Bold", Font.PLAIN, 24));
				gr.setColor(Color.BLACK);
				gr.drawString(g.getName(), 112, 47);
				gr.setColor(Color.WHITE);
				gr.drawString(g.getName(), 110, 45);
				
				
				gr.setFont(new Font("Arial Bold", Font.PLAIN, 14));
				gr.drawString(g.getCharacterLastPlayedSubclass(), 110, 65);
				
				gr.setFont(new Font("Arial Bold", Font.PLAIN, 20));
				gr.setColor(Color.GRAY);
				gr.drawString(g.getTrialsELO(), 206 - gr.getFontMetrics().stringWidth(g.getTrialsELO()), 197);
				gr.drawString(g.getRumbleELO(), 206 - gr.getFontMetrics().stringWidth(g.getRumbleELO()), 254);
				gr.drawString(g.getTrialsRank(), 336 - gr.getFontMetrics().stringWidth(g.getTrialsRank()), 197);
				gr.drawString(g.getRumbleRank(), 336 - gr.getFontMetrics().stringWidth(g.getRumbleRank()), 254);
				gr.drawString(g.getThisYearTrialsKD(), 431 - gr.getFontMetrics().stringWidth(g.getThisYearTrialsKD()), 197);
				gr.drawString(g.getRumbleKD(), 431 - gr.getFontMetrics().stringWidth(g.getRumbleKD()), 254);
				gr.drawString(g.getLighthouseCount()+"X", 166, 312);
				
				gr.setColor(Color.BLACK);
				gr.drawString(g.getTrialsELO(), 205 - gr.getFontMetrics().stringWidth(g.getTrialsELO()), 196);
				gr.drawString(g.getRumbleELO(), 205 - gr.getFontMetrics().stringWidth(g.getRumbleELO()), 253);
				gr.drawString(g.getTrialsRank(), 335 - gr.getFontMetrics().stringWidth(g.getTrialsRank()), 196);
				gr.drawString(g.getRumbleRank(), 335 - gr.getFontMetrics().stringWidth(g.getRumbleRank()), 253);
				gr.drawString(g.getThisYearTrialsKD(), 430 - gr.getFontMetrics().stringWidth(g.getThisYearTrialsKD()), 196);
				gr.drawString(g.getRumbleKD(), 430 - gr.getFontMetrics().stringWidth(g.getRumbleKD()), 253);
				gr.setColor(new Color(17,114,4));
				gr.drawString(g.getLighthouseCount()+"X", 165, 311);
				
				ImageIO.write(im, "png", f);
				gr.dispose();
				eb.setImage(GlobalDefs.WWW_HOST + GlobalDefs.WWW_IMGFOLDER + f.getName());
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
