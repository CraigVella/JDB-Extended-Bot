package com.reztek;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
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
import com.reztek.Utils.URLClassLoaderUtil;
import com.reztek.modules.BaseCommands.BaseCommands;
import com.reztek.modules.CustomCommands.CustomCommands;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 * <h1>JDB-Extended-Bot</h1>
 * A Bot that uses a modular design along with optional plugins that allows 
 * diverse interaction types.
 * 
 * @author ChaseHQ85
 *
 */

public class JDBExtendedBot extends TimerTask implements EventListener {
	private static JDBExtendedBot ps_bot = null;
	private boolean p_ready = false;
	private MessageHandler p_mh;
	private ArrayList<Taskable> p_taskList = new ArrayList<Taskable>();
	private Timer p_timer = new Timer("JDBExtendedBotTimer");
	private AtomicBoolean p_tasksrunning = new AtomicBoolean(false);
	private JDA p_jda = null;
	
	/**
	 * Get the main Bot instance
	 * @return JDBExtendedBot Instance
	 */
	public static JDBExtendedBot GetBot() {
		if (ps_bot == null) {
			ps_bot = new JDBExtendedBot();
		}
		return ps_bot;
	}
	
	private void dynaLoadLibs() {
		File libDir = new File(BotUtils.GetExecutionPath(getClass()) + "/lib");
		if (libDir.exists() && libDir.isDirectory()) {
			FilenameFilter jarFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".jar")) {
						return true;
					} else {
						return false;
					}
				}
			};
			URLClassLoaderUtil u = new URLClassLoaderUtil(new URL[] {});
			for (File f : libDir.listFiles(jarFilter)) {
				try {
					u.addJarURL(f.toURI().toURL());
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("[ERROR] Error Dynamically loading lib - " + f.getName());
				}
			}
			try {
				u.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			libDir.mkdir();
		}
	}
	
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		if (!ConfigReader.GetConfigReader().isConfigLoaded()) { 
			System.out.println("Configuration Error - Cannot Continue"); 
			return; 
		}
		
		JDBExtendedBot bot = JDBExtendedBot.GetBot();
		bot.dynaLoadLibs();
		try {
			JDA jda = new JDABuilder(AccountType.BOT).setToken(GlobalDefs.BOT_DEV ? GlobalDefs.BOT_TOKEN_DEV : GlobalDefs.BOT_TOKEN).addEventListener(bot).buildBlocking();
			bot.run(jda);
		} catch (LoginException e) {
			e.printStackTrace();
			System.out.println("[ERROR] Could not continue - It may be your configuration file?? Please check");
			System.exit(3);
		}
	}
	
	private void run(JDA jda) throws InterruptedException {
		p_jda = jda;
		
		p_mh = new MessageHandler();
		
		p_mh.addCommandModule(new BaseCommands());
		
		p_mh.loadAllPlugins();
		
		// Add custom commands last
		p_mh.addCommandModule(new CustomCommands());
		
		addTask(new BadgeCacheTask());
		
		p_timer.schedule(this, GlobalDefs.TIMER_TICK, GlobalDefs.TIMER_TICK);
		
		jda.setAutoReconnect(true);
	}
	
	/**
	 * Retreive current registered tasks
	 * @return A collection of Taskable objects
	 */
	
	public Collection<Taskable> getTasks() {
		return Collections.unmodifiableCollection(p_taskList);
	}
	
	/**
	 * Retrieve the Java Discord API Object
	 * @return Java Discord API Object Instance
	 */
	public JDA getJDA() {
		return p_jda;
	}
	
	/**
	 * Add a task to the tasking system
	 * </p> The Tasking system will try and run a task once a minute (unless otherwise configured).
	 * The task is not guaranteed to run on time, the queue is linear and synchronous. This is purposely
	 * done to not put too much of a load on the system. </p>
	 * @param Task Object with Task as a base class
	 */
	public void addTask(Taskable task) {
		p_taskList.add(task);
	}
	
	/**
	 * Checks to see whether the Bot is in a ready state
	 * @return boolean of true if Bot is ready, false if not logged in
	 */
	public boolean isReady() {
		return p_ready;
	}
	
	/**
	 * Retrieve the MessageHandler instance
	 * @return MessageHandler instance
	 */
	public MessageHandler getMessageHandler() {
		return p_mh;
	}

	/**
	 * Overridden on event <b>DO NOT</b> call this method directly.
	 */
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof ReadyEvent) {
			p_ready = true;
			System.out.println("JDB-Extended-Bot - Version: " + BotUtils.GetVersion());
		}
		
		if (e instanceof MessageReceivedEvent) {
			if (isReady()){
				getMessageHandler().processMessage((MessageReceivedEvent) e);
			}
		}
		
	}

	/**
	 * Overriden run, <b>DO NOT</b> call this method directly.
	 */
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
