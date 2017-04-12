package com.reztek;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.login.LoginException;

import com.reztek.base.Taskable;
import com.reztek.modules.GuardianControl.GuardianControlCommands;
import com.reztek.modules.MiscCommands.MiscCommands;
import com.reztek.modules.RumbleCommands.RumbleCommands;
import com.reztek.modules.TrialsCommands.TrialsCommands;
import com.reztek.secret.GlobalDefs;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;

public class SGAExtendedBot extends TimerTask implements EventListener {
	private boolean p_ready = false;
	private MessageHandler p_mh;
	private ArrayList<Taskable> p_taskList = new ArrayList<Taskable>();
	private Timer p_timer = new Timer("SGAExtendedBotTimer");
	private AtomicBoolean p_tasksrunning = new AtomicBoolean(false);
	
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		SGAExtendedBot bot = new SGAExtendedBot();
		
		JDA jda = new JDABuilder(AccountType.BOT).setToken(GlobalDefs.BOT_BETA ? GlobalDefs.BOT_TOKEN_BETA : GlobalDefs.BOT_TOKEN).addListener(bot).buildBlocking();
		
		bot.run(jda);
	}
	
	public void run(JDA jda) throws InterruptedException {
		p_mh = new MessageHandler();
		
		p_mh.addCommandProcessor(new MiscCommands(jda, this));
		p_mh.addCommandProcessor(new RumbleCommands(jda, this));
		p_mh.addCommandProcessor(new GuardianControlCommands(jda, this));
		p_mh.addCommandProcessor(new TrialsCommands(jda, this));
		
		p_timer.schedule(this, GlobalDefs.TIMER_TICK, GlobalDefs.TIMER_TICK);
		
		jda.setAutoReconnect(true);
	}
	
	public void addTask(Taskable task) {
		p_taskList.add(task);
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
			if (GlobalDefs.BOT_BETA) {
				System.out.println("SGA-Beta-Bot - running in Beta Mode!");
			} else {
				System.out.println("SGA-Extended-Bot ready!");
			}
		}
		
		if (e instanceof MessageReceivedEvent) {
			if (isReady()){
				getMessageHandler().processMessage((MessageReceivedEvent) e);
			}
		}
		
	}

	@Override
	public void run() {
		// Called every 1 minute and passed to all tasks sequentially,
		// some tasks may not get called every 1 minute depending on how
		// long each task in front of it takes to run. This is purposely done
		// to prevent too many web calls from going off at once
		if (!p_tasksrunning.get()) {
			p_tasksrunning.set(true);
			synchronized (p_taskList) {
				for (Taskable task : p_taskList) {
					task.__taskTick();
				}
			}
			p_tasksrunning.set(false);
		}
	}
}
