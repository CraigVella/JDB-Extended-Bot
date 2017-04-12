package com.reztek.modules.GuardianControl;

import com.reztek.SGAExtendedBot;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;

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
					"[Rumble Elo]("+ g.getRumbleELO() +")<RK: "+ g.getRumbleRank() +">\n" +
					"[Trials Elo]("+ g.getTrialsELO() +")<RK: "+ g.getTrialsRank() +">\n" +
					"[Flawlesses]("+ g.getLighthouseCount() +")\n" +
					"```").queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
}
