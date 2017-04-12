package com.reztek.modules.GuardianControl;

import com.reztek.SGAExtendedBot;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;
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
	
	protected void playerInfo(MessageChannel mc, String playerName, String platform) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			mc.sendMessage("Here's some info about **" + g.getName() + "**. \n```md\n" +
					"[Rumble Elo](" + BotUtils.getPaddingForLen(g.getRumbleELO(), 4) + g.getRumbleELO() +")<RK:" + BotUtils.getPaddingForLen(g.getRumbleRank(), 6) + g.getRumbleRank() +">\n" +
					"[Trials Elo](" + BotUtils.getPaddingForLen(g.getTrialsELO(), 4) + g.getTrialsELO() +")<RK:" + BotUtils.getPaddingForLen(g.getTrialsRank(), 6) + g.getTrialsRank() +">\n" +
					"[Flawlesses](" + BotUtils.getPaddingForLen(g.getLighthouseCount(), 4) + g.getLighthouseCount() +")\n" +
					"```").queue();
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
}
