package com.reztek.base;

import com.reztek.SGAExtendedBot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {
	private JDA p_jda;
	private SGAExtendedBot p_bot;
	
	public Command(JDA pJDA, SGAExtendedBot pbot) {
		p_jda = pJDA;
		p_bot = pbot;
	}
	
	public void sendHelpString(MessageReceivedEvent mre, String usage) {
		mre.getChannel().sendMessage(new MessageBuilder().append("Hey " + mre.getAuthor().getAsMention() + ", that command works like this - \"" + usage + "\"").build()).queue();
	}
	
	protected JDA getJDA() {
		return p_jda;
	}
	
	protected SGAExtendedBot getBot() {
		return p_bot;
	}
}
