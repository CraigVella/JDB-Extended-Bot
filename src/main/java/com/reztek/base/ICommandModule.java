package com.reztek.Base;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICommandModule {
	void processCommand(String command, String args, MessageReceivedEvent mre);
	boolean respondsToCommand(String command);
	public String getModuleName();
	public String getAuthorName();
	public String getModuleID();
	public CommandModule getModule();
}
