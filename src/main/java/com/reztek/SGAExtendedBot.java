package com.reztek;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.login.LoginException;

import com.reztek.Badges.BadgeCacheTask;
import com.reztek.Base.Taskable;
import com.reztek.Global.GlobalDefs;
import com.reztek.Utils.BotUtils;
import com.reztek.Utils.ConfigReader;
import com.reztek.modules.BaseCommands.BaseCommands;
import com.reztek.modules.CustomCommands.CustomCommands;
import com.reztek.modules.GuardianControl.GuardianControlCommands;
import com.reztek.modules.RumbleCommands.RumbleCommands;
import com.reztek.modules.SGAAutoPromoter.SGAAutoPromoterCommands;
import com.reztek.modules.TrialsCommands.TrialsCommands;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;

public class SGAExtendedBot extends TimerTask implements EventListener {
	private static SGAExtendedBot ps_bot = null;
	private boolean p_ready = false;
	private MessageHandler p_mh;
	private ArrayList<Taskable> p_taskList = new ArrayList<Taskable>();
	private Timer p_timer = new Timer("SGAExtendedBotTimer");
	private AtomicBoolean p_tasksrunning = new AtomicBoolean(false);
	private JDA p_jda = null;
	
	public static SGAExtendedBot GetBot() {
		if (ps_bot == null) {
			ps_bot = new SGAExtendedBot();
		}
		return ps_bot;
	}
	
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		if (!ConfigReader.GetConfigReader().isConfigLoaded()) { 
			System.out.println("Configuration Error - Cannot Continue"); 
			return; 
		}
		
		SGAExtendedBot bot = SGAExtendedBot.GetBot();
		try {
			JDA jda = new JDABuilder(AccountType.BOT).setToken(GlobalDefs.BOT_DEV ? GlobalDefs.BOT_TOKEN_DEV : GlobalDefs.BOT_TOKEN).addListener(bot).buildBlocking();
			bot.run(jda);
		} catch (LoginException e) {
			e.printStackTrace();
			System.out.println("[ERROR] Could not continue - It may be your configuration file?? Please check");
			System.exit(3);
		}
	}
	
	public void run(JDA jda) throws InterruptedException {
		p_jda = jda;
		
		p_mh = new MessageHandler();
		
		p_mh.addCommandModule(new BaseCommands());
		p_mh.addCommandModule(new RumbleCommands());
		p_mh.addCommandModule(new GuardianControlCommands());
		p_mh.addCommandModule(new TrialsCommands());
		p_mh.addCommandModule(new SGAAutoPromoterCommands());
		
		// Add custom commands last
		p_mh.addCommandModule(new CustomCommands());
		
		addTask(new BadgeCacheTask());
		
		p_timer.schedule(this, GlobalDefs.TIMER_TICK, GlobalDefs.TIMER_TICK);
		
		jda.setAutoReconnect(true);
	}
	
	public Collection<Taskable> getTasks() {
		return Collections.unmodifiableCollection(p_taskList);
	}
	
	public JDA getJDA() {
		return p_jda;
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
			System.out.println("SGA-Extended-Bot - Version: " + BotUtils.GetVersion());
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
				for (Taskable task : getTasks()) {
					try {
						task.__taskTick();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			p_tasksrunning.set(false);
		}
	}
}
