package com.reztek;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import com.reztek.base.ITaskable;
import com.reztek.modules.GuardianControl.GuardianControlCommands;
import com.reztek.modules.Misc.MiscCommands;
import com.reztek.modules.RumbleList.RumbleListCommands;
import com.reztek.modules.TrialsList.TrialsListCommands;
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
	private ArrayList<ITaskable> p_taskList = new ArrayList<ITaskable>();
	private Timer p_timer = new Timer();
	private boolean p_continueTasks = true;
	
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		SGAExtendedBot bot = new SGAExtendedBot();
		
		JDA jda = new JDABuilder(AccountType.BOT).setToken(GlobalDefs.BOT_TOKEN).addListener(bot).buildBlocking();
		
		bot.run(jda);
	}
	
	public void run(JDA jda) throws InterruptedException {
		p_mh = new MessageHandler();
		
		p_mh.addCommandProcessor(new MiscCommands(jda, this));
		p_mh.addCommandProcessor(new RumbleListCommands(jda, this));
		p_mh.addCommandProcessor(new GuardianControlCommands(jda, this));
		p_mh.addCommandProcessor(new TrialsListCommands(jda, this));
		
		newTimer(GlobalDefs.TIMER_TICK);
		
		jda.setAutoReconnect(true);
	}
	
	public void addTask(ITaskable task) {
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
			System.out.println("SGA-Extended-Bot ready!");
		}
		
		if (e instanceof MessageReceivedEvent) {
			if (isReady()){
				getMessageHandler().processMessage((MessageReceivedEvent) e);
			}
		}
		
	}
	
	public boolean isTasksRunning() {
		return p_continueTasks;
	}
	
	public void pauseTasks() {
		if (p_continueTasks) {
			p_continueTasks = false;
			p_timer.cancel();
		}
	}
	
	public void resumeTasks() {
		if (!p_continueTasks) {
			newTimer(GlobalDefs.TIMER_TICK);
			p_continueTasks = true;
		}
	}

	@Override
	public void run() {
		// Called every 1 minute and passed to all tasks sequentially,
		// some tasks may not get called every 1 minute depending on how
		// long each task in front of it takes to run. This is purposely done
		// to prevent too many web calls from going off at once
		synchronized (p_taskList) {
			for (ITaskable task : p_taskList) {
				task.runTask();
			}
		}
		if (p_continueTasks)
			newTimer(GlobalDefs.TIMER_TICK);
	}
	
	protected void newTimer(long timeout) {
		p_timer.cancel();
		p_timer.schedule(this, timeout);
	}
	
}
