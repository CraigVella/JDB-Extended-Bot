package com.reztek;

import java.util.ArrayList;

import com.reztek.base.ICommandProcessor;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageHandler {
	
	ArrayList<ICommandProcessor> p_commandProcessors = new ArrayList<ICommandProcessor>();
	
	public void addCommandProcessor(ICommandProcessor cpm) { 
		p_commandProcessors.add(cpm);
	}
	
	public void processMessage(MessageReceivedEvent mre) {
		if (mre.getMessage().getStrippedContent().charAt(0) == '!' && !mre.getAuthor().isBot()) {
			
			String command = mre.getMessage().getStrippedContent().substring(1);
			String[] cmdSplit = command.toLowerCase().split(" ");
			
			String args = "";
			
			for (int x = 1; x < cmdSplit.length; ++x) {
				args += cmdSplit[x] + " ";
			}
			
			for (ICommandProcessor proc : p_commandProcessors) {
				if (proc.processCommand(cmdSplit[0], args.equals("") ? null : args.trim(), mre)) {
					return;
				}
			}
		}
	}
}
