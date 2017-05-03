package com.reztek;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import com.reztek.base.ICommandModule;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageHandler {

	HashMap<String,ICommandModule> p_commandModules = new HashMap<String,ICommandModule>();
	
	public void addCommandModule(ICommandModule cpm) {
		if (p_commandModules.containsKey(cpm.getModuleID())) {
			System.out.println("[ERROR] Unable to add module " + cpm.getModuleName() + " {" + cpm.getModuleID() + "} it's already added or duplicate unique ID!");
		} else {
			p_commandModules.put(cpm.getModuleID(), cpm);
			System.out.println("Added Command Module: " + cpm.getModuleName() + " - " + cpm.getAuthorName());
		}
	}
	
	public void removeCommandModule(String commandModuleID) {
		p_commandModules.remove(commandModuleID);
	}
	
	public final ICommandModule getCommandModuleByID(String moduleID) {
		return p_commandModules.get(moduleID);
	}
	
	public Collection<ICommandModule> getAllLoadedCommandModules() {
		return Collections.unmodifiableCollection(p_commandModules.values());
	}
	
	public void processMessage(MessageReceivedEvent mre) {
		if (mre.getMessage().getRawContent().length() > 0 && mre.getMessage().getRawContent().charAt(0) == '!' && !mre.getAuthor().isBot()) {
			
			String command = mre.getMessage().getRawContent().substring(1);
			String[] cmdSplit = command.toLowerCase().split(" ");
			
			String args = "";
			
			for (int x = 1; x < cmdSplit.length; ++x) {
				args += cmdSplit[x] + " ";
			}
			
			for (ICommandModule proc : getAllLoadedCommandModules()) {
				if (proc.processCommand(cmdSplit[0], args.equals("") ? null : args.trim(), mre)) {
					return;
				}
			}
		}
	}
}
