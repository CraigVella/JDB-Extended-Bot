package com.reztek.base;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICommandModule {
	boolean processCommand(String command, String args, MessageReceivedEvent mre);
	public String getModuleName();
	public String getAuthorName();
	public String getModuleID();
	public CommandModule getModule();
}
