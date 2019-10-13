package com.reztek.modules.CustomCommands;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PrivateTextCommand extends CustomCommand {

	public PrivateTextCommand(String command, String toShow) {
		p_command = command;
		p_data = toShow;
		p_commandType = CustomCommands.CMD_PRIVATE_TEXT;
	}
	
	@Override
	public void runCommand(MessageReceivedEvent mre) {
		PrivateChannel pc = mre.getAuthor().openPrivateChannel().complete();
		pc.sendMessage(p_data).queue();
	}

}
