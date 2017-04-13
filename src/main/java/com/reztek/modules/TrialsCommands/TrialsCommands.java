package com.reztek.modules.TrialsCommands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import com.reztek.SGAExtendedBot;
import com.reztek.base.CommandModule;
import com.reztek.modules.GuardianControl.Guardian;
import com.reztek.modules.GuardianControl.Guardian.GuardianWeaponStats;
import com.reztek.utils.BotUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TrialsCommands extends CommandModule {
	
	private static final String DTR_MAP_URL = "https://api.destinytrialsreport.com/currentMap";
	private static final String BUNGIE_BASE = "https://www.bungie.net";
	
	protected TrialsList p_trialsList = new TrialsList();

	public TrialsCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot,"TRIALSCOMMANDS");
		// I have a task!
		setModuleNameAndAuthor("Trials of Osiris", "ChaseHQ85");
		p_trialsList.setTaskDelay(5);
		getBot().addTask(p_trialsList);
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
			switch (command) {
			case "fireteam-ps":
			case "fireteam-xb":
			case "fireteam":
				if (args == null) {
					sendHelpString(mre, "!fireteam[or !fireteam-ps or !fireteam-xb] PlayerNameHere");
				} else {
					fireteamInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), false);
				}
				break;
			case "fireteam+-ps":
			case "fireteam+-xb":
			case "fireteam+":
				if (args == null) {
					sendHelpString(mre, "!fireteam+[or !fireteam+-ps or !fireteam+-xb] PlayerNameHere");
				} else {
					fireteamInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), true);
				}
				break;
			case "trials-ps":
			case "trials-xb":
			case "trials":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					trialsInfo(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command), false);
				} else {
					trialsInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), false);
				}
				break;
			case "trials+-ps":
			case "trials+-xb":
			case "trials+":
				if (args == null) {
					Guardian.PlatformCodeFromNicknameData d = Guardian.platformCodeFromNickname(mre.getMember().getEffectiveName());
					trialsInfo(mre.getChannel(), d.getNickname(), d.usesTag() ? d.getPlatform() : Guardian.platformCodeFromCommand(command), true);
				} else {
					trialsInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command), true);
				}
				break;
			case "trialsmap":
				trialsMap(mre);
				break;
			case "trialslist-csv":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					trialListCSV(mre);
				} 
				break;
			case "trialslist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					trialsList(mre.getChannel(), "-1", Color.WHITE);
				}
				break;
			case "trialslistgold":
				trialsList(mre.getChannel(), "0", new Color(212,175,55));
				break;
			case "trialslistsilver":
				trialsList(mre.getChannel(), "10", new Color(192,192,192));
				break;
			case "trialslistbronze":
				trialsList(mre.getChannel(), "20", new Color(205, 127, 50));
				break;
			case "trialsrefresh":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					trialsRefresh(mre.getChannel());
				}
				break;
			case "trialsaddtolist-ps":
			case "trialsaddtolist-xb":
			case "trialsaddtolist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					if (args == null) {
						sendHelpString(mre, "!trialsAddToList[or !trialsAddToList-ps or !trialsAddToList-xb] PlayerNameHere");
					} else {
						trialsAddToList(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
					}
				}
				break;
			case "trialsremovegromlist-ps":
			case "trialsremovegromlist-xb":
			case "trialsremovegromlist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					if (args == null) {
						sendHelpString(mre, "!trialsRemoveFromList[or !trialsRemoveFromList-ps or !trialsRemoveFromList-xb] PlayerNameHere");
					} else {
						trialsRemoveFromList(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
					}
				}
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	protected void trialsMap(MessageReceivedEvent mre) {
		mre.getChannel().sendTyping().queue();
		JSONObject ob = new JSONObject("{\"DTRArray\":" + BotUtils.getJSONString(DTR_MAP_URL, null) + "}").getJSONArray("DTRArray").getJSONObject(0);
		MessageBuilder mb = new MessageBuilder();
		EmbedBuilder emBu = new EmbedBuilder();
		emBu.setImage(BUNGIE_BASE + ob.getString("pgcrImage"));
		emBu.setColor(java.awt.Color.YELLOW);
		mb.append(mre.getAuthor().getAsMention() + " the Trials of Osiris map for this week is **"+ob.getString("activityName") + "**");
		mb.setEmbed(emBu.build());
		mre.getChannel().sendMessage(mb.build()).queue();
	}
	
	protected void trialListCSV(MessageReceivedEvent mre) {
		mre.getChannel().sendTyping().queue();
		mre.getChannel().sendMessage("On it " + mre.getAuthor().getAsMention() + ", lets take this to a private chat!").queue();
		try {
			p_trialsList.sendListCSV(mre.getAuthor().hasPrivateChannel() ? mre.getAuthor().getPrivateChannel() : mre.getAuthor().openPrivateChannel().submit().get());
		} catch (InterruptedException | ExecutionException e) {
			mre.getChannel().sendMessage("Hmm... " + mre.getAuthor().getAsMention() + ", I tried to open a private chat with you but I was denied.").queue();
		}
	}
	
	protected void trialsList(MessageChannel mc, String indexStart, Color color) {
		mc.sendTyping().queue();
		p_trialsList.showList(mc, indexStart, color);
	}
	
	protected void trialsRefresh(MessageChannel mc) {
		mc.sendTyping().queue();
		p_trialsList.refreshList(mc, true);
	}
	
	protected void trialsRemoveFromList(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			p_trialsList.removePlayer(mc,g);
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void trialsAddToList (MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			if (g.getRumbleRank() == "N/A" || g.getRumbleRank() == null) {
				mc.sendMessage("Sorry " + playerName + " hasn't played enough Trials of Osiris this season to be added.").queue();
			} else {
				p_trialsList.addPlayer(mc,g);
			}
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void trialsInfo(MessageChannel mc, String playerName, String platform, boolean verbose) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			String bestWeps = "--Weekly Best Weapons  : Kills ( HS )--\n";
			int x = 0;
			for (GuardianWeaponStats ws : g.getThisWeekWeaponStats()) {
				x++;
				bestWeps += String.valueOf(x) + ". " + ws.getWeaponName() + BotUtils.getPaddingForLen(ws.getWeaponName(), 18) + "  : " + 
						BotUtils.getPaddingForLen(ws.getWeaponKills(), 5) + ws.getWeaponKills() + " (" + BotUtils.getPaddingForLen(ws.getHeadshotPercentage(),6)  + ws.getHeadshotPercentage() + ")\n";
			}
			mc.sendMessage("**" + g.getName() + "**'s Trials of Osiris " + (verbose ? "Detailed " : "") + "Weekly Stats \n"
			+ "```md\n" +
			"[Trials Elo]("+ BotUtils.getPaddingForLen(g.getTrialsELO(), 4) + g.getTrialsELO() +")" + (verbose ? "<RK:"+ BotUtils.getPaddingForLen(g.getTrialsRank(), 6) + g.getTrialsRank() +">" : "") + "\n" +
			"[Weekly K/D]("+ BotUtils.getPaddingForLen(g.getThisWeekTrialsKD(), 4)+ g.getThisWeekTrialsKD() +")" + (verbose ? "<GP: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsMatches(), 5) + g.getThisWeekTrialsMatches() +">" : "") + "\n" +
			"[Season K/D]("+ BotUtils.getPaddingForLen(g.getThisYearTrialsKD(), 4)+ g.getThisYearTrialsKD() +")" + (verbose ? "<GP: "+ BotUtils.getPaddingForLen(g.getThisYearTrialsMatches(), 5) + g.getThisYearTrialsMatches() +">" : "") + "\n" +
			"[Flawlesses]("+ BotUtils.getPaddingForLen(g.getLighthouseCount(), 4)+ g.getLighthouseCount() +")" + (verbose ? "<WK: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsFlawless(), 5) + g.getThisWeekTrialsFlawless() + ">" : "") + "\n" +
			bestWeps +
			"```").queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void fireteamInfo(MessageChannel mc, String playerName, String platform, boolean verbose) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		ArrayList<Guardian> gFireteam = new ArrayList<Guardian>();
		if (g != null) {
			ArrayList<HashMap<String,String>> members = g.getTrialsFireteamMembershipId();
			for (HashMap<String, String> hashMap : members) {
				gFireteam.add(Guardian.guardianFromMembershipId(hashMap.get("membershipId"), hashMap.get("name"), hashMap.get("platform")));
			}
			String bestWeps = "--Weekly Best Weapons  : Kills ( HS )--\n";
			int x = 0;
			for (GuardianWeaponStats ws : g.getThisWeekWeaponStats()) {
				x++;
				bestWeps += String.valueOf(x) + ". " + ws.getWeaponName() + BotUtils.getPaddingForLen(ws.getWeaponName(), 18) + "  : " + 
						BotUtils.getPaddingForLen(ws.getWeaponKills(), 5) + ws.getWeaponKills() + " (" + BotUtils.getPaddingForLen(ws.getHeadshotPercentage(),6)  + ws.getHeadshotPercentage() + ")\n";
			}
			mc.sendMessage("**" + g.getName() + "**'s Current Fireteam's " + (verbose ? "Detailed " : "") + "Weekly Stats \n"
					+ "```md\n" +
					g.getName() + "\n" +
					"[Trials Elo]("+ BotUtils.getPaddingForLen(g.getTrialsELO(), 4) + g.getTrialsELO() +")" + (verbose ? "<RK:"+ BotUtils.getPaddingForLen(g.getTrialsRank(), 6) + g.getTrialsRank() +">" : "") + "\n" +
					"[Weekly K/D]("+ BotUtils.getPaddingForLen(g.getThisWeekTrialsKD(), 4)+ g.getThisWeekTrialsKD() +")" + (verbose ? "<GP: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsMatches(), 5) + g.getThisWeekTrialsMatches() +">" :"") +"\n" +
					"[Season K/D]("+ BotUtils.getPaddingForLen(g.getThisYearTrialsKD(), 4)+ g.getThisYearTrialsKD() +")" + (verbose ? "<GP: "+ BotUtils.getPaddingForLen(g.getThisYearTrialsMatches(), 5) + g.getThisYearTrialsMatches() +">" : "") + "\n" +
					"[Flawlesses]("+ BotUtils.getPaddingForLen(g.getLighthouseCount(), 4)+ g.getLighthouseCount() +")" + (verbose ? "<WK: "+ BotUtils.getPaddingForLen(g.getThisWeekTrialsFlawless(), 5) + g.getThisWeekTrialsFlawless() +  ">" : "") + "\n" +
					(verbose ? bestWeps : "") +
					"```").queue();
			for (Guardian gFt : gFireteam) {
				String bestWepsFt = "--Weekly Best Weapons  : Kills ( HS )--\n";
				int xFt = 0;
				for (GuardianWeaponStats ws : gFt.getThisWeekWeaponStats()) {
					xFt++;
					bestWepsFt += String.valueOf(xFt) + ". " + ws.getWeaponName() + BotUtils.getPaddingForLen(ws.getWeaponName(), 18) + "  : " + 
							BotUtils.getPaddingForLen(ws.getWeaponKills(), 5) + ws.getWeaponKills() + " (" + BotUtils.getPaddingForLen(ws.getHeadshotPercentage(),6)  + ws.getHeadshotPercentage() + ")\n";
				}
				mc.sendMessage("```md\n" +
						gFt.getName() + "\n" +
					"[Trials Elo]("+ BotUtils.getPaddingForLen(gFt.getTrialsELO(), 4) + gFt.getTrialsELO() +")" + (verbose ? "<RK:"+ BotUtils.getPaddingForLen(gFt.getTrialsRank(), 6) + gFt.getTrialsRank() +">" :"") +"\n" +
					"[Weekly K/D]("+ BotUtils.getPaddingForLen(gFt.getThisWeekTrialsKD(), 4)+ gFt.getThisWeekTrialsKD() +")" + (verbose ? "<GP: "+ BotUtils.getPaddingForLen(gFt.getThisWeekTrialsMatches(), 5) + gFt.getThisWeekTrialsMatches() +">" :"") +"\n" +
					"[Season K/D]("+ BotUtils.getPaddingForLen(gFt.getThisYearTrialsKD(), 4)+ gFt.getThisYearTrialsKD() +")" + (verbose ? "<GP: "+ BotUtils.getPaddingForLen(gFt.getThisYearTrialsMatches(), 5) + gFt.getThisYearTrialsMatches() +">" :"") +"\n" +
					"[Flawlesses]("+ BotUtils.getPaddingForLen(gFt.getLighthouseCount(), 4)+ gFt.getLighthouseCount() +")" + (verbose ? "<WK: "+ BotUtils.getPaddingForLen(gFt.getThisWeekTrialsFlawless(), 5) + gFt.getThisWeekTrialsFlawless() +  ">" :"") +"\n" +
					(verbose ? bestWepsFt : "") +
					"```").queue();
			}
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
}
