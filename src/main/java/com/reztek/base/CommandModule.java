package com.reztek.Base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Abstract base class of a Command Module
 * All Modules/Plugins should extend this class and implement at least processCommand
 * <pre>
 * <code>class MyNewPlugin extends CommandModule {
 *  public MyNewPlugin() {
 *   super("PLUGINID");
 *   setModuleNameAndAuthor("My Awesome Plugin","Plugin Maker");
 *   setVersion("1.0");
 *   addCommand("ping");
 *  }
 *  
 *  void processCommand(String command, String args, MessageReceivedEvent mre) {
 *  	if (command.equals("ping")) {
 *         mre.getChannel().sendMessage("pong").queue();
 *      }
 *  }
 * }</code>
 * </pre>
 * @author Craig Vella
 * @since 1.0
 */
public abstract class CommandModule implements ICommandModule {
	private String p_moduleName;
	private String p_author;
	private String p_uniqueModuleID;
	private ArrayList<String> p_commandList = new ArrayList<String>();
	private String p_Version = "N/A";
	
	/**
	 * Constructor for CommandModule when extending you <b>must</b> pass a unique ID for plugin
	 * @param uniqueModuleID - a unique id to be used for plugin
	 */
	public CommandModule(String uniqueModuleID) {
		p_moduleName = "Unknown-Module";
		p_author = "Unknown-Author";
		p_uniqueModuleID = uniqueModuleID;
	}
	
	/**
	 * Can be hidden by extended class - Gets called before Instantiation of new object
	 */
	public static void SetupPlugin() {
		
	}
	
	/**
	 * Can be hidden by extended class - defines other plugins that this plugin depends on, setting this correctly will
	 * ensure all dependent plugins get loaded prior to this one
	 * @return A collection of ArrayList<String> containing Plugin/Module ID's that this plugin depends on 
	 */
	public static Collection<String> GetDependencies() {
		return Collections.unmodifiableCollection(new ArrayList<String>());
	}
	
	/**
	 * Add a command for this module/plugin to respond to
	 * @param command - Command that this module will respond to
	 */
	protected void addCommand(String command) {
		if (respondsToCommand(command.toLowerCase())) return;
		p_commandList.add(command.toLowerCase());
	}
	
	/**
	 * Remove command that this module/plugin responds to
	 * @param command - Command that this module should stop responding to and remove
	 */
	protected void removeCommand(String command) {
		p_commandList.remove(command.toLowerCase());
	}
	
	/**
	 * Add a command for this module/plugin to respond to
	 * @param commands - A collection of <String> for this plugin to respond to
	 */
	protected void addCommand(Collection<String> commands) {
		addCommand(commands.toArray(new String[0]));
	}
	
	/**
	 * Add a command for this module/plugin to respond to
	 * @param commands - An array of String[] for this plugin to respond to
	 */
	protected void addCommand(String[] commands) {
		for(String cmd : commands) {
			addCommand(cmd);
		}
	}
	
	/**
	 * Set the version of this plugin
	 * @param version - Version of this plugin to set
	 */
	protected void setVersion(String version) {
		p_Version = version;
	}
	
	public String getVersion() {
		return p_Version;
	}

	public boolean respondsToCommand(String command) {
		for (String cmd : p_commandList) {
			if (cmd.equals(command)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * A helper method for building a help response<br />
	 * {@code sendHelpString(mre, "!ping <personToPingHere>");}<br />
	 * This would send the message "Hey @person, that command works like this -> !ping <personToPingHere>" to the channel<br />
	 * @param mre - MessageRecivedEvent required to build the help response
	 * @param usage - a string containing the correct usage for the command
	 */
	public void sendHelpString(MessageReceivedEvent mre, String usage) {
		mre.getChannel().sendMessage(new MessageBuilder().append("Hey " + mre.getAuthor().getAsMention() + ", that command works like this -> " + usage).build()).queue();
	}
	
	public String getModuleName() {
		return p_moduleName;
	}
	
	public String getAuthorName() {
		return p_author;
	}
	
	public String getModuleID() {
		return p_uniqueModuleID;
	}
	
	public CommandModule getModule() {
		return this;
	}
	
	/**
	 * A Convenience method that sets the module Name and Author
	 * @param moduleName - Text name of method (Ex: "My Super Duper Plugin")
	 * @param moduleAuthor - The Author's name
	 */
	protected void setModuleNameAndAuthor(String moduleName, String moduleAuthor) {
		setModuleName(moduleName);
		setAuthorName(moduleAuthor);
	}
	
	/**
	 * Sets the module/plugin name
	 * @param moduleName - Text name of method (Ex: "My Super Duper Plugin")
	 */
	protected void setModuleName(String moduleName) {
		p_moduleName = moduleName;
	}
	
	/**
	 * Sets the module/plugin authors name
	 * @param authorName - The Author's name
	 */
	protected void setAuthorName(String authorName) {
		p_author = authorName;
	}
	
	public final Collection<String> getCommands() {
		return Collections.unmodifiableCollection(p_commandList);
	}
}
