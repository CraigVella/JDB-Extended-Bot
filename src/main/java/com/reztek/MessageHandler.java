package com.reztek;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.reztek.Base.ICommandModule;
import com.reztek.Utils.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageHandler {

	HashMap<String,ICommandModule> p_commandModules = new HashMap<String,ICommandModule>();
	private ExecutorService p_executor = Executors.newCachedThreadPool();

	public void addCommandModule(ICommandModule cpm) {
		if (p_commandModules.containsKey(cpm.getModuleID())) {
			System.out.println("[ERROR] Unable to add module " + cpm.getModuleName() + " {" + cpm.getModuleID() + "} it's already added or duplicate unique ID!");
		} else {
			for (ICommandModule m : p_commandModules.values()) {
				for (String c : m.getCommands()) {
					if (cpm.respondsToCommand(c)) {
						System.out.println("[ERROR] Unable to add module " + cpm.getModuleName() + " {" + cpm.getModuleID() + "} it responds to command '!" + c.toUpperCase() + "' which {" +  m.getModuleID() + "} already responds to!");
						return;
					}
				}
			}
			p_commandModules.put(cpm.getModuleID(), cpm);
			System.out.println("Added Command Module: " + cpm.getModuleName() + " - " + cpm.getAuthorName());
		}
	}
	
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
			for (File f : pluginDir.listFiles(jarFilter)) {
				try {
					URLClassLoader pluginModule = new URLClassLoader( new URL[] {f.toURI().toURL()}, getClass().getClassLoader());
					Class<?> plugin = Class.forName(f.getName().substring(0, f.getName().length() - 4), true, pluginModule);
					addCommandModule((ICommandModule) plugin.newInstance());
				} catch (MalformedURLException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
					System.out.println("Failed Loading Plugin: " + f.getName() + " [" + e.toString() + "]");
				}
			}
		} else {
			pluginDir.mkdirs();
			System.out.println("[WARNING] Plugin Directory did not exits - creating one");
		}
	}
	
	public boolean isCommandTaken(String command) {
		for (ICommandModule m : p_commandModules.values()) {
			if (m.respondsToCommand(command.toLowerCase())) return true;
		}
		return false;
	}
	
	public void removeCommandModule(String commandModuleID) {
		p_commandModules.remove(commandModuleID);
	}
	
	public final ICommandModule getCommandModuleByID(String moduleID) {
		return p_commandModules.get(moduleID);
	}
	
	public Collection<ICommandModule> getAllLoadedCommandModules() {
		return Collections.unmodifiableCollection(p_commandModules.values());
	}
	
	public void processMessage(MessageReceivedEvent mre) {
		if (mre.getMessage().getRawContent().length() > 0 && mre.getMessage().getRawContent().charAt(0) == '!' && !mre.getAuthor().isBot()) {
			
			String command = mre.getMessage().getRawContent().substring(1);
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
}
