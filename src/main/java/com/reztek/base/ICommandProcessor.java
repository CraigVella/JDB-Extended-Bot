package com.reztek.base;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICommandProcessor {
	boolean processCommand(String command, String args, MessageReceivedEvent mre);
}
