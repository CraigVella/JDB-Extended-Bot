package com.reztek.modules.BaseCommands;

import java.util.Random;

import com.reztek.SGAExtendedBot;
import com.reztek.base.CommandModule;
import com.reztek.base.ICommandModule;
import com.reztek.secret.GlobalDefs;
import com.reztek.utils.BotUtils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BaseCommands extends CommandModule {

	public BaseCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot,"BASECOMMANDS");
		setModuleNameAndAuthor("Base Bot Commands", "ChaseHQ85");
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
		
		/*String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}*/
		
		switch (command) {
			case "version":
				showVersion(mre.getChannel());
			break;
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
			case "showmodules":
				showModules(mre.getChannel());
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	protected void showModules(MessageChannel mc) {
		mc.sendTyping();
		String modules = "**All Loaded Modules**\n```";
		for (ICommandModule cm : getBot().getMessageHandler().getAllLoadedCommandModules()) {
			modules += cm.getModuleName() + BotUtils.getPaddingForLen(cm.getModuleName(), 20) + " by " + cm.getAuthorName() + "\n";
		}
		modules += "```";
		mc.sendMessage(modules).queue();
	}
	
	protected void chaseCmd(MessageChannel mc) {
		mc.sendTyping().queue();
		mc.sendMessage("Peeks twice!").queue();
	}
	
	protected void decision(MessageReceivedEvent mre, String question) {
		mre.getChannel().sendTyping().queue();
		String[] decArray = {"I would say yes to that.", "Most definitely yes.", "Absolutely not.", "Sometime next year probably.", 
				"I'd say maybe...", "Never seen a bigger NO to a question ever."};
		
		Random random = new Random();
		
		mre.getChannel().sendMessage(mre.getAuthor().getAsMention() + ", " + decArray[random.nextInt(decArray.length)]).queue();
	}
	
	protected void showVersion(MessageChannel mc) {
		mc.sendTyping().queue();
		mc.sendMessage("I am running version: " + GlobalDefs.BOT_VERSION + (GlobalDefs.BOT_DEV ? "-devel" : "")).queue();
	}

}
