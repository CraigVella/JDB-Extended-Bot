package com.reztek.modules.GuardianControl;

import java.util.ArrayList;
import java.util.HashMap;

import com.reztek.SGAExtendedBot;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;
import com.reztek.modules.GuardianControl.Guardian.GuardianWeaponStats;
import com.reztek.utils.BotUtils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GuardianControlCommands extends Command implements ICommandProcessor {

	public GuardianControlCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot);
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
		
		/*String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}*/
		
		switch (command) {
			case "trials-ps":
			case "trials-xb":
			case "trials":
				if (args == null) {
					// Try and get the Platform from name
					String nick = mre.getMember().getEffectiveName();
					String[] nickSep = nick.split("]");
					if (nickSep.length > 1) {
						// a Tag proceeds their name try and get platform
						String platform = Guardian.PLATFORM_ALL;
						if (nickSep[0].substring(1).equalsIgnoreCase("PS4")) platform = Guardian.PLATFORM_PS;
						if (nickSep[0].substring(1).equalsIgnoreCase("XB1")) platform = Guardian.PLATFORM_XB;
						trialsInfo(mre.getChannel(), nickSep[1], platform);
					} else {
						// No tag, process the name
						trialsInfo(mre.getChannel(), nick, Guardian.platformCodeFromCommand(command));
					}
				} else {
					trialsInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
				}
				break;
			case "info-ps":
			case "info-xb":
			case "info":
				if (args == null) {
					// Try and get the Platform from name
					String nick = mre.getMember().getEffectiveName();
					String[] nickSep = nick.split("]");
					if (nickSep.length > 1) {
						// a Tag proceeds their name try and get platform
						String platform = Guardian.PLATFORM_ALL;
						if (nickSep[0].substring(1).equalsIgnoreCase("PS4")) platform = Guardian.PLATFORM_PS;
						if (nickSep[0].substring(1).equalsIgnoreCase("XB1")) platform = Guardian.PLATFORM_XB;
						playerInfo(mre.getChannel(), nickSep[1], platform);
					} else {
						// No tag, process the name
						playerInfo(mre.getChannel(), nick, Guardian.platformCodeFromCommand(command));
					}
				} else {
					playerInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
				}
				break;
			case "fireteam-ps":
			case "fireteam-xb":
			case "fireteam":
				if (args == null) {
					sendHelpString(mre, "!fireteam[or !fireteam-ps or !fireteam-xb] PlayerNameHere");
				} else {
					fireteamInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
				}
				break;
			default: 
				return false;
		}
		
		return true;
	}
	
	protected void fireteamInfo(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		ArrayList<Guardian> gFireteam = new ArrayList<Guardian>();
		if (g != null) {
			ArrayList<HashMap<String,String>> members = g.getTrialsFireteamMembershipId();
			for (HashMap<String, String> hashMap : members) {
				gFireteam.add(Guardian.guardianFromMembershipId(hashMap.get("membershipId"), hashMap.get("name"), hashMap.get("platform")));
			}
			mc.sendMessage("**" + g.getName() + "**'s Current Fireteam \n"
					+ "```md\n" +
					g.getName() + "\n" +
					"[Trials Elo]("+ BotUtils.getPaddingForLen(g.getTrialsELO(), 4) + g.getTrialsELO() +")<RK:"+ BotUtils.getPaddingForLen(g.getTrialsRank(), 6) + g.getTrialsRank() +">\n" +
					"[Weekly K/D]("+ BotUtils.getPaddingForLen(g.getThisWeekTrialsKD(), 4)+ g.getThisWeekTrialsKD() +")<GP: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsMatches(), 5) + g.getThisWeekTrialsMatches() +">\n" +
					"[Season K/D]("+ BotUtils.getPaddingForLen(g.getThisYearTrialsKD(), 4)+ g.getThisYearTrialsKD() +")<GP: "+ BotUtils.getPaddingForLen(g.getThisYearTrialsMatches(), 5) + g.getThisYearTrialsMatches() +">\n" +
					"[Flawlesses]("+ BotUtils.getPaddingForLen(g.getLighthouseCount(), 4)+ g.getLighthouseCount() +")<WK: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsFlawless(), 5) + g.getThisWeekTrialsFlawless() +  ">\n" +
					"```").queue();
			for (Guardian gFt : gFireteam) {
						mc.sendMessage("```md\n" +
								gFt.getName() + "\n" +
							"[Trials Elo]("+ BotUtils.getPaddingForLen(gFt.getTrialsELO(), 4) + gFt.getTrialsELO() +")<RK:"+ BotUtils.getPaddingForLen(gFt.getTrialsRank(), 6) + gFt.getTrialsRank() +">\n" +
							"[Weekly K/D]("+ BotUtils.getPaddingForLen(gFt.getThisWeekTrialsKD(), 4)+ gFt.getThisWeekTrialsKD() +")<GP: "+ BotUtils.getPaddingForLen(gFt.getThisWeekTrialsMatches(), 5) + gFt.getThisWeekTrialsMatches() +">\n" +
							"[Season K/D]("+ BotUtils.getPaddingForLen(gFt.getThisYearTrialsKD(), 4)+ gFt.getThisYearTrialsKD() +")<GP: "+ BotUtils.getPaddingForLen(gFt.getThisYearTrialsMatches(), 5) + gFt.getThisYearTrialsMatches() +">\n" +
							"[Flawlesses]("+ BotUtils.getPaddingForLen(gFt.getLighthouseCount(), 4)+ gFt.getLighthouseCount() +")<WK: "+ BotUtils.getPaddingForLen(gFt.getThisWeekTrialsFlawless(), 5) + gFt.getThisWeekTrialsFlawless() +  ">\n" +
						"```").queue();
			}
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void playerInfo(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			mc.sendMessage("Here's some info about **" + g.getName() + "**. \n```md\n" +
					"[Rumble Elo]("+ g.getRumbleELO() +")<RK: "+ g.getRumbleRank() +">\n" +
					"[Trials Elo]("+ g.getTrialsELO() +")<RK: "+ g.getTrialsRank() +">\n" +
					"[Flawlesses]("+ g.getLighthouseCount() +")\n" +
					"```").queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void trialsInfo(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			String bestWeps = "-----------------Best Weapons-----------------\n";
			int x = 0;
			for (GuardianWeaponStats ws : g.getThisWeekWeaponStats()) {
				x++;
				bestWeps += String.valueOf(x) + ". " + ws.getWeaponName() + BotUtils.getPaddingForLen(ws.getWeaponName(), 15) + " - <" + "Kills: " + 
						BotUtils.getPaddingForLen(ws.getWeaponKills(), 4) + ws.getWeaponKills() + ">(HS: " + BotUtils.getPaddingForLen(ws.getHeadshotPercentage(),6)  + ws.getHeadshotPercentage() + ")\n";
			}
			mc.sendMessage("**" + g.getName() + "**'s Trials of Osiris Detailed Weekly Stats \n"
			+ "```md\n" +
			"[Trials Elo]("+ BotUtils.getPaddingForLen(g.getTrialsELO(), 4) + g.getTrialsELO() +")<RK:"+ BotUtils.getPaddingForLen(g.getTrialsRank(), 6) + g.getTrialsRank() +">\n" +
			"[Weekly K/D]("+ BotUtils.getPaddingForLen(g.getThisWeekTrialsKD(), 4)+ g.getThisWeekTrialsKD() +")<GP: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsMatches(), 5) + g.getThisWeekTrialsMatches() +">\n" +
			"[Season K/D]("+ BotUtils.getPaddingForLen(g.getThisYearTrialsKD(), 4)+ g.getThisYearTrialsKD() +")<GP: "+ BotUtils.getPaddingForLen(g.getThisYearTrialsMatches(), 5) + g.getThisYearTrialsMatches() +">\n" +
			"[Flawlesses]("+ BotUtils.getPaddingForLen(g.getLighthouseCount(), 4)+ g.getLighthouseCount() +")<WK: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsFlawless(), 5) + g.getThisWeekTrialsFlawless() +  ">\n" +
			bestWeps +
			"```").queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}

}
