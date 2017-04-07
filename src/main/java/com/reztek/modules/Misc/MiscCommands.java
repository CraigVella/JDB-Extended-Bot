package com.reztek.modules.Misc;

import java.util.Random;

import com.reztek.Guardian;
import com.reztek.SGAExtendedBot;
import com.reztek.base.Command;
import com.reztek.base.ICommandProcessor;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MiscCommands extends Command implements ICommandProcessor {

	public MiscCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot);
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
		
		/*String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}*/
		
		switch (command) {
			case "chase":
				chaseCmd(mre.getChannel());
				break;
			case "decision":
				if (args == null) {
					mre.getChannel().sendMessage("Listen " + mre.getAuthor().getAsMention() + ", I'm here to help, but you need to ask me a question too!").queue();
				} else {
					decision(mre, args);
				}
				break;
			case "debugguardian":
				if (args == null) {
					sendHelpString(mre, "!debugGuardian PlayerNameHere");
				} else {
					debugGuardian(mre.getChannel(), args);
				}
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	protected void chaseCmd(MessageChannel mc) {
		mc.sendTyping().queue();
		mc.sendMessage("Peeks twice!").queue();
	}
	
	protected void decision(MessageReceivedEvent mre, String question) {
		mre.getChannel().sendTyping().queue();
		String[] decArray = {"I would say yes to that.", "Most definitely yes.", "Absolutely not.", "Sometime next year probably.", 
				"I'd say maybe...", "Never seen a bigger NO to a question ever.","'Kosa stands a better chance than you."};
		
		Random random = new Random();
		
		mre.getChannel().sendMessage(mre.getAuthor().getAsMention() + ", " + decArray[random.nextInt(decArray.length)]).queue();
	}
	
	protected void debugGuardian(MessageChannel mc, String playerName) {
		mc.sendTyping().queue();
		Guardian g = Guardian.guardianFromName(playerName,Guardian.PLATFORM_ALL);
		mc.sendMessage("DEBUG: " + g.getId()).queue();
	}

}
