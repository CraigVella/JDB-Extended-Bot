package com.reztek.modules.BaseCommands;

import java.awt.Color;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.reztek.SGAExtendedBot;
import com.reztek.Base.CommandModule;
import com.reztek.Base.ICommandModule;
import com.reztek.Base.Taskable;
import com.reztek.Secret.GlobalDefs;
import com.reztek.Utils.BotUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BaseCommands extends CommandModule {
	
	private static final String GOOGLE_CUSTOM_SEARCH = "https://www.googleapis.com/customsearch/v1?key=" + GlobalDefs.GOOGLE_API_KEY +
			"&cx=" + GlobalDefs.GOOGLE_API_CX + "&filter=1&searchType=image&q=";

	public BaseCommands(JDA pJDA, SGAExtendedBot pBot) {
		super(pJDA, pBot,"BASECOMMANDS");
		setModuleNameAndAuthor("Base Bot Commands", "ChaseHQ85");
		addCommand(new String[]{
				"version", "decision", "showmodules", "showtasks", 
				"chase", "taco"});
	}
	
	@Override
	public void processCommand(String command, String args, MessageReceivedEvent mre) {
		
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
			case "showtasks":
				showTasks(mre.getChannel());
				break;
			case "taco":
				showTaco(mre.getChannel());
				break;
		}
		
	}
	
	protected void showTaco(MessageChannel mc) {
		mc.sendTyping().queue();
		int startIndex = new Random().nextInt(90-1) + 1;
		JSONArray tacoArray = new JSONObject(BotUtils.getJSONStringGet(GOOGLE_CUSTOM_SEARCH+"taco&start=" + String.valueOf(startIndex), null)).getJSONArray("items");
		String tacoLink = tacoArray.getJSONObject((new Random().nextInt(tacoArray.length()))).getString("link");
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.YELLOW);
		eb.setImage(tacoLink);
		eb.setDescription("**Random Taco Generator Accessed**");
		mc.sendMessage(eb.build()).queue();
	}
	
	protected void showTasks(MessageChannel mc) {
		mc.sendTyping();
		String tasks = "**All Queued Tasks**\n```";
		for (Taskable task : getBot().getTasks()) {
			tasks += task.getTaskName() + BotUtils.getPaddingForLen(task.getTaskName(), 20) + " - runs every " + 
					BotUtils.getPaddingForLen(String.valueOf(task.getTaskDelay()), 3) + (task.getTaskDelay() == 0 ? "1" : String.valueOf(task.getTaskDelay())) +
					" min(s), will run in " + BotUtils.getPaddingForLen(((task.getTaskDelay() - task.getTaskDelayCount()) < 1 ? "1" : String.valueOf((task.getTaskDelay() - task.getTaskDelayCount()))) , 3) +
					((task.getTaskDelay() - task.getTaskDelayCount()) < 1 ? "1" : String.valueOf((task.getTaskDelay() - task.getTaskDelayCount()))) + 
					" min(s)\n";
		}
		tasks += "```";
		mc.sendMessage(tasks).queue();
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
