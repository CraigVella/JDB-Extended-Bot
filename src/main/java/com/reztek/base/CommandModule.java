package com.reztek.Base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class CommandModule implements ICommandModule {
	private String p_moduleName;
	private String p_author;
	private String p_uniqueModuleID;
	private ArrayList<String> p_commandList = new ArrayList<String>();
	
	public CommandModule(String uniqueModuleID) {
		p_moduleName = "Unknown-Module";
		p_author = "Unknown-Author";
		p_uniqueModuleID = uniqueModuleID;
	}
	
	protected void addCommand(String command) {
		if (respondsToCommand(command.toLowerCase())) return;
		p_commandList.add(command.toLowerCase());
	}
	
	protected void removeCommand(String command) {
		p_commandList.remove(command.toLowerCase());
	}
	
	protected void addCommand(Collection<String> commands) {
		addCommand(commands.toArray(new String[0]));
	}
	
	protected void addCommand(String[] commands) {
		for(String cmd : commands) {
			addCommand(cmd);
		}
	}
	
	public boolean respondsToCommand(String command) {
		for (String cmd : p_commandList) {
			if (cmd.equals(command)) {
				return true;
			}
		}
		return false;
	}
	
	public void sendHelpString(MessageReceivedEvent mre, String usage) {
		mre.getChannel().sendMessage(new MessageBuilder().append("Hey " + mre.getAuthor().getAsMention() + ", that command works like this -> " + usage).build()).queue();
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
	
	public final Collection<String> getCommands() {
		return Collections.unmodifiableCollection(p_commandList);
	}
}
