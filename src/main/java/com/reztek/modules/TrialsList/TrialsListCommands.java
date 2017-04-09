package com.reztek.modules.TrialsList;

import com.reztek.Guardian;
import com.reztek.SGAExtendedBot;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TrialsListCommands extends Command implements ICommandProcessor {
	
	protected TrialsList p_trialsList = new TrialsList();

	public TrialsListCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot);
		// I have a task!
		p_trialsList.setTaskDelay(5);
		getBot().addTask(p_trialsList);
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
			switch (command) {
			case "trialslist":
				trialsList(mre.getChannel());
				break;
			case "trialsrefresh":
				trialsRefresh(mre.getChannel());
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
	
	protected void trialsList(MessageChannel mc) {
		mc.sendTyping().queue();
		p_trialsList.showList(mc);
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
}
