package com.reztek.modules.CustomCommands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICustomCommand {
	void runCommand(MessageReceivedEvent mre);
	int getCommandType();
	String getCommand();
	String getData();
	int getDBID();
}
