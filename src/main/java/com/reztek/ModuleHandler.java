package com.reztek;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reflections.Reflections;

import com.reztek.Base.CommandModule;
import com.reztek.Base.ICommandModule;
import com.reztek.Utils.BotUtils;

import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ModuleHandler {

	HashMap<String,ICommandModule> p_commandModules = new HashMap<String,ICommandModule>();
	private ExecutorService p_executor = Executors.newCachedThreadPool();

	/**
	 * Add a CommandModule object to the Bot
	 * <p>Command Module will be checked that it's ID and commands do not intefere with any other command module</p>
	 * @param ICommandModule
	 * @return Returns true if Module Added, false if Module is not added
	 */
	public boolean addCommandModule(ICommandModule cpm) {
		if (p_commandModules.containsKey(cpm.getModuleID())) {
			System.out.println("[ERROR] Unable to add module " + cpm.getModuleName() + " {" + cpm.getModuleID() + "} it's already added or duplicate unique ID!");
			return false;
		} else {
			for (ICommandModule m : p_commandModules.values()) {
				for (String c : m.getCommands()) {
					if (cpm.respondsToCommand(c)) {
						System.out.println("[ERROR] Unable to add module " + cpm.getModuleName() + " {" + cpm.getModuleID() + "} it responds to command '!" + c.toUpperCase() + "' which {" +  m.getModuleID() + "} already responds to!");
						return false;
					}
				}
			}
			p_commandModules.put(cpm.getModuleID(), cpm);
			System.out.println("Added Command Module: " + cpm.getModuleName() + " V." + cpm.getVersion() + " - " + cpm.getAuthorName());
			return true;
		}
	}
	
	/**
	 * Loads all plugins in the plugin directly
	 */
	@SuppressWarnings("unchecked")
	public void loadAllPlugins() {
		File pluginDir = new File(BotUtils.GetExecutionPath(getClass()) + "/plugins");
		if (pluginDir.exists() && pluginDir.isDirectory()) {
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
			ArrayList<Class<?>> pluginList = new ArrayList<Class<?>>();
			for (File f : pluginDir.listFiles(jarFilter)) {
				try {
					Reflections r = new Reflections(f.toURI().toURL());
					for (String v : r.getStore().get("SubTypesScanner").get(CommandModule.class.getName())) {
						URLClassLoader pluginModule = new URLClassLoader( new URL[] {f.toURI().toURL()}, getClass().getClassLoader());
						Class<?> plugin = Class.forName(v, true, pluginModule);
						plugin.getMethod("SetupPlugin", null).invoke(null, null); // Run SetupPlugin
						pluginList.add(plugin);
					}
				} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | MalformedURLException | ClassNotFoundException e) {
					System.out.println("Failed Loading Plugin: " + f.getName() + " [" + e.toString() + "]");
				}
			}
			// All Plugins are now Setup - and Ready to be loaded - Just need to check dependencies before instantiating
			// We need to make sure that each plugin passes dependency checks - if it does take them off the pluginToLoad list
			ArrayList<Class<?>> pluginToLoad;
			int maxPluginTries = pluginList.size();
			for (int x = 0; ((x < maxPluginTries) && (pluginList.size() != 0)); ++x) {
				pluginToLoad = (ArrayList<Class<?>>) pluginList.clone();
				for (Class<?> c : pluginToLoad) {
					// Check each plugin to see if all depencies work - if it does load it and remove from list
					try {
						Collection<String> deps = (Collection<String>) c.getMethod("GetDependencies", null).invoke(null,null);
						boolean canLoadPlugin = true;
						for (String s : deps) {
							if (getCommandModuleByID(s) == null) canLoadPlugin = false;
						}
						if (canLoadPlugin) {
							pluginList.remove(c);
							addCommandModule((ICommandModule) c.newInstance());
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | InstantiationException e) {
						System.out.println("Failed Loading Plugin: [" + e.toString() + "]");
						e.printStackTrace();
					}
				}
			}
			// At this point whatever plugin didn't load will be left in the pluginList
			for (Class<?> c : pluginList) {
				Collection<String> deps;
				try {
					deps = (Collection<String>) c.getMethod("GetDependencies", null).invoke(null,null);
					System.out.println("[ERROR] Plugin dependency failed - Could not load [" + c.getName() + "] missing one of the following dependencies " + deps.toString());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					System.out.println("[ERROR] Plugin dependency failed - Could not load [" + c.getName() + "]");
				}
			}
		} else {
			pluginDir.mkdirs();
			System.out.println("[WARNING] Plugin Directory did not exist - creating one");
		}
	}
	
	/**
	 * Checks to see if the command is taken my a registered plugin
	 * @param String of a command to check
	 * @return Returns true if the command is taken, returns false if the command is available
	 */
	public boolean isCommandTaken(String command) {
		for (ICommandModule m : p_commandModules.values()) {
			if (m.respondsToCommand(command.toLowerCase())) return true;
		}
		return false;
	}
	
	/**
	 * Remove a command module by command module ID
	 * @param String of a command module ID to remove
	 */
	public void removeCommandModule(String commandModuleID) {
		p_commandModules.remove(commandModuleID);
	}
	
	/**
	 * Retrieves a command module from the given module ID
	 * @param String of a command module ID
	 * @return The command Module instance of the ID given, or null if none were found
	 */
	public final ICommandModule getCommandModuleByID(String moduleID) {
		return p_commandModules.getOrDefault(moduleID, null);
	}
	
	/**
	 * Gets a collection of all loaded command module plugins
	 * @return A collection of loaded command modules
	 */
	public Collection<ICommandModule> getAllLoadedCommandModules() {
		return Collections.unmodifiableCollection(p_commandModules.values());
	}
	
	/**
	 * Called by the Bot when a new command message arrives <b>DO NOT</b> call this method directly
	 * @param The MessageRecievedEvent of the incoming message
	 */
	public void processMessage(MessageReceivedEvent mre) {
		if (mre.getMessage().getContentRaw().length() > 0 && mre.getMessage().getContentRaw().charAt(0) == '!' && !mre.getAuthor().isBot()) {
			
			String command = mre.getMessage().getContentRaw().substring(1);
			String[] cmdSplit = command.split(" ");
			
			String args = "";
			
			for (int x = 1; x < cmdSplit.length; ++x) {
				args += cmdSplit[x] + " ";
			}
			
			final String argsTL = args;
			
			for (ICommandModule proc : getAllLoadedCommandModules()) {
				if (proc.respondsToCommand(cmdSplit[0].toLowerCase())) {
					p_executor.execute(new Runnable() {
						@Override
						public void run() {
							mre.getChannel().sendTyping().queue();
							proc.processCommand(cmdSplit[0].toLowerCase(), argsTL.equals("") ? null : argsTL.trim(), mre);
						}
					});
				}
			}
		}
	}
	
	/**
	 * Called by the Bot when a Member Joins a guild <b>DO NOT</b> call this method directly
	 * @param The MessageRecievedEvent of the incoming message
	 */
	public void processGuildMemberJoin(GuildMemberJoinEvent e) {
		for (ICommandModule proc : getAllLoadedCommandModules()) {
			if (proc.respondsToJoinEvents()) {
				p_executor.execute(new Runnable() {
					@Override
					public void run() {
						proc.processMemberJoin(e);
					}
				});
			}
		}
	}
	
	/**
	 * Called by the Bot when a Member logs off from a guild <b>DO NOT</b> call this method directly
	 * @param The MessageRecievedEvent of the incoming message
	 */
	public void processGuildMemberLeave(GuildMemberLeaveEvent e) {
		for (ICommandModule proc : getAllLoadedCommandModules()) {
			if (proc.respondsToLeaveEvents()) {
				p_executor.execute(new Runnable() {
					@Override
					public void run() {
						proc.processMemberLeave(e);
					}
				});
			}
		}
	}
}
