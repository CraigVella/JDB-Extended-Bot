package com.reztek.modules.CustomCommands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.reztek.Base.CommandModule;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CustomCommands extends CommandModule {
	
	private static final Map<String, Integer> CommandType;
    static {
        Map<String, Integer> ct = new HashMap<String, Integer>();
        ct.put("ERROR"       ,  -1);
        ct.put("CHANNEL_TEXT",   0);
        ct.put("PRIVATE_TEXT",   1);
        CommandType = Collections.unmodifiableMap(ct);
    }
    
    private int commandTypeFromString(String type) {
    	return CommandType.getOrDefault(type, -1);
    }

	public CustomCommands() {
		super("CUSTOMCOMMANDS");
		setModuleNameAndAuthor("Custom Commands", "ChaseHQ85");
		addCommand("custom");
	}

	@Override
	public void processCommand(String command, String args, MessageReceivedEvent mre) {
		
	}

}
