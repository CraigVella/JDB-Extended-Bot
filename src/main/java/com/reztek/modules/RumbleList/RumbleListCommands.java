package com.reztek.modules.RumbleList;

import com.reztek.Guardian;
import com.reztek.SGAExtendedBot;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RumbleListCommands extends Command implements ICommandProcessor {
	
	protected RumbleList p_rumbleList = new RumbleList();

	public RumbleListCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot);
		// I have a task!
		p_rumbleList.setTaskDelay(5);
		getBot().addTask(p_rumbleList);
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
		
		/*String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}*/
		
		switch (command) {
			case "rumblelist":
				rumbleList(mre.getChannel());
				break;
			case "rumblerefresh":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					rumbleRefresh(mre.getChannel());
				}
				break;
			case "rumbleaddtolist-ps":
			case "rumbleaddtolist-xb":
			case "rumbleaddtolist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					if (args == null) {
						sendHelpString(mre, "!rumbleAddToList[or !rumbleAddToList-ps or !rumbleAddToList-xb] PlayerNameHere");
					} else {
						rumbleAddToList(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
					}
				}
				break;
			case "rumbleremovefromlist-ps":
			case "rumbleremovefromlist-xb":
			case "rumbleremovefromlist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					if (args == null) {
						sendHelpString(mre, "!rumbleRemoveFromList[or !rumbleRemoveFromList-ps or !rumbleRemoveFromList-xb] PlayerNameHere");
					} else {
						rumbleRemoveFromList(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
					}
				}
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	protected void rumbleList(MessageChannel mc) {
		mc.sendTyping().queue();
		p_rumbleList.showList(mc);
	}
	
	protected void rumbleRefresh(MessageChannel mc) {
		mc.sendTyping().queue();
		p_rumbleList.refreshList(mc, true);
	}
	
	protected void rumbleRemoveFromList(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			p_rumbleList.removePlayer(mc,g);
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void rumbleAddToList (MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			if (g.getRumbleRank() == "N/A" || g.getRumbleRank() == null) {
				mc.sendMessage("Sorry " + playerName + " hasn't played enough rumble this season to be added.").queue();
			} else {
				p_rumbleList.addPlayer(mc,g);
			}
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}

}
