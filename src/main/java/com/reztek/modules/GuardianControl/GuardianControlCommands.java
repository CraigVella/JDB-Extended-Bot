package com.reztek.modules.GuardianControl;

import com.reztek.Guardian;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GuardianControlCommands extends Command implements ICommandProcessor {

	public GuardianControlCommands(JDA pJDA) {
		super(pJDA);
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
		
		/*String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}*/
		
		switch (command) {
			case "info-ps":
			case "info-xb":
			case "info":
				if (args == null) {
					sendHelpString(mre, "!info[or !info-ps or !info-xb] PlayerNameHere");
				} else {
					playerInfo(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
				}
				break;
			default: 
				return false;
		}
		
		return true;
	}
	
	protected void playerInfo(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			mc.sendMessage("Here's some info about **" + g.getName() + "**. \n```md\n" +
					"[Rumble Elo]("+ g.getRumbleELO() +")<#"+ g.getRumbleRank() +">\n" +
					"[Trials Elo]("+ g.getTrialsELO() +")<#"+ g.getTrialsRank() +">\n" +
					"[Flawlesses]("+ g.getLighthouseCount() +")\n" +
					"```").queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}

}
