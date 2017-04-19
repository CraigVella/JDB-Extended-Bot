package com.reztek.base;

import com.reztek.SGAExtendedBot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class CommandModule implements ICommandModule {
	private JDA p_jda;
	private SGAExtendedBot p_bot;
	private String p_moduleName;
	private String p_author;
	private String p_uniqueModuleID;
	
	public CommandModule(JDA pJDA, SGAExtendedBot pbot, String uniqueModuleID) {
		p_jda = pJDA;
		p_bot = pbot;
		p_moduleName = "Unknown-Module";
		p_author = "Unknown-Author";
		p_uniqueModuleID = uniqueModuleID;
	}
	
	public void sendHelpString(MessageReceivedEvent mre, String usage) {
		mre.getChannel().sendMessage(new MessageBuilder().append("Hey " + mre.getAuthor().getAsMention() + ", that command works like this - \"" + usage + "\"").build()).queue();
	}
	
	public String getModuleName() {
		return p_moduleName;
	}
	
	public String getAuthorName() {
		return p_author;
	}
	
	public String getModuleID() {
		return p_uniqueModuleID;
	}
	
	public CommandModule getModule() {
		return this;
	}
	
	protected void setModuleNameAndAuthor(String moduleName, String moduleAuthor) {
		setModuleName(moduleName);
		setAuthorName(moduleAuthor);
	}
	
	protected void setModuleName(String moduleName) {
		p_moduleName = moduleName;
	}
	
	protected void setAuthorName(String authorName) {
		p_author = authorName;
	}
	
	public JDA getJDA() {
		return p_jda;
	}
	
	protected SGAExtendedBot getBot() {
		return p_bot;
	}
}
