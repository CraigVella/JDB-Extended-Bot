package com.reztek.modules.CustomCommands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChannelTextCommand extends CustomCommand {

	public ChannelTextCommand(String command, String toShow) {
		p_command = command;
		p_data = toShow;
		p_commandType = CustomCommands.CMD_CHANNEL_TEXT;
	}
	
	@Override
	public void runCommand(MessageReceivedEvent mre) {
		mre.getChannel().sendMessage(p_data).queue();
	}

}
