package com.reztek.base;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {
	private JDA p_jda;
	
	public Command(JDA pJDA) {
		p_jda = pJDA;
	}
	
	public void sendHelpString(MessageReceivedEvent mre, String usage) {
		mre.getChannel().sendMessage(new MessageBuilder().append("Hey " + mre.getAuthor().getAsMention() + ", that command works like this - \"" + usage + "\"").build()).queue();
	}
	
	protected JDA getJDA() {
		return p_jda;
	}
}
