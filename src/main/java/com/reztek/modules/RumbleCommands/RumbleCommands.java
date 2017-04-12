package com.reztek.modules.RumbleCommands;

import java.awt.Color;
import java.util.concurrent.ExecutionException;

import com.reztek.SGAExtendedBot;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;
import com.reztek.modules.GuardianControl.Guardian;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RumbleCommands extends Command implements ICommandProcessor {
	
	protected RumbleList p_rumbleList = new RumbleList();

	public RumbleCommands(JDA pJDA, SGAExtendedBot pBot) {
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
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					rumbleList(mre.getChannel(), "-1", Color.WHITE);
				}
				break;
			case "rumblelist-csv":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					rumbleListCSV(mre);
				} 
				break;
			case "rumblelistgold":
				rumbleList(mre.getChannel(), "0", new Color(212,175,55));
				break;
			case "rumblelistsilver":
				rumbleList(mre.getChannel(), "10", new Color(192,192,192));
				break;
			case "rumblelistbronze":
				rumbleList(mre.getChannel(), "20", new Color(205, 127, 50));
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
	
	protected void rumbleListCSV(MessageReceivedEvent mre) {
		mre.getChannel().sendTyping().queue();
		mre.getChannel().sendMessage("On it " + mre.getAuthor().getAsMention() + ", lets take this to a private chat!").queue();
		try {
			p_rumbleList.sendListCSV(mre.getAuthor().hasPrivateChannel() ? mre.getAuthor().getPrivateChannel() : mre.getAuthor().openPrivateChannel().submit().get());
		} catch (InterruptedException | ExecutionException e) {
			mre.getChannel().sendMessage("Hmm... " + mre.getAuthor().getAsMention() + ", I tried to open a private chat with you but I was denied.").queue();
		}
	}
	
	protected void rumbleList(MessageChannel mc, String indexStart, Color color) {
		mc.sendTyping().queue();
		p_rumbleList.showList(mc,indexStart, color);
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
