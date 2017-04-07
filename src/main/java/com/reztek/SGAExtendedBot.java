package com.reztek;

import javax.security.auth.login.LoginException;

import com.reztek.modules.GuardianControl.GuardianControlCommands;
import com.reztek.modules.Misc.MiscCommands;
import com.reztek.modules.RumbleList.RumbleListCommands;
import com.reztek.secret.GlobalDefs;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;

public class SGAExtendedBot implements EventListener {
	private boolean p_ready = false;
	private MessageHandler p_mh;
	
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		SGAExtendedBot bot = new SGAExtendedBot();
		
		JDA jda = new JDABuilder(AccountType.BOT).setToken(GlobalDefs.BOT_TOKEN).addListener(bot).buildBlocking();
		
		bot.run(jda);
	}
	
	public void run(JDA jda) throws InterruptedException {
		p_mh = new MessageHandler();
		
		p_mh.addCommandProcessor(new MiscCommands(jda));
		p_mh.addCommandProcessor(new RumbleListCommands(jda));
		p_mh.addCommandProcessor(new GuardianControlCommands(jda));
		
		jda.setAutoReconnect(true);
	}
	
	public boolean isReady() {
		return p_ready;
	}
	
	public MessageHandler getMessageHandler() {
		return p_mh;
	}

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof ReadyEvent) {
			p_ready = true;
			System.out.println("SGA-Extended-Bot ready!");
		}
		
		if (e instanceof MessageReceivedEvent) {
			if (isReady()){
				getMessageHandler().processMessage((MessageReceivedEvent) e);
			}
		}
		
	}
	

}
