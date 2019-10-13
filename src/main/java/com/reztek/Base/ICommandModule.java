package com.reztek.Base;

import java.util.Collection;

import com.reztek.ModuleHandler;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


/**
 * Command Module Interface
 * <p>A Command Module Interface is a representation of public and abstract methods that must be implemented that are accessable from all modules</p>
 * @author Craig Vella
 *
 */
public interface ICommandModule {
	/**
	 * Processes Commands that get massed to module
	 * <p>The processCommand gets called by the {@link ModuleHandler} when a registered command is called by a user</p>
	 * @param command - <b>in</b> - The command passed from the MessageHandler which contains the command typed by the user
	 * @param args - <b>in</b> - The arguments that follow after the command
	 * @param e - <b>in</b> - {@link MessageReceivedEvent} object sent by the ModuleHandler, which contains all functionality to send back a message
	 */
	void processCommand(String command, String args, MessageReceivedEvent e);
	
	/**
	 * The method returns true if this moudule should respond to the following passed command
	 * @param command - the command to test for
	 * @return {@code true} when the module responds to this command, {@code false} otherwise
	 */
	boolean respondsToCommand(String command);
	
	/**
	 * Returns a collection of commands registered by the module
	 * @return A collection of Strings that are commands responded to by the plugin
	 */
	Collection<String> getCommands();
	
	/**
	 * Get the Module/Plugin Name
	 * @return String containing the plugin name
	 */
	public String getModuleName();
	
	/**
	 * Get the Module/Plugin author name
	 * @return String containing the plugin authors name
	 */
	public String getAuthorName();
	
	/**
	 * Get the Module/Plugin ID
	 * @return String containing the plugin ID
	 */
	public String getModuleID();
	
	/**
	 * Get the actual CommandModule
	 * @return CommandModule of the instance
	 */
	public CommandModule getModule();
	
	/**
	 * Get the version of the Module/Plugin
	 * @return String containing the plugin Version
	 */
	public String getVersion();
	
	/**
	 * Processes Join events that get massed to module
	 * <p>The processMemberJoin gets called by the {@link ModuleHandler} when a plugin elects to receive these events</p>
	 * @param e - <b>in</b> - {@link GuildMemberJoinEvent} object sent by the ModuleHandler
	 */
	public void processMemberJoin(GuildMemberJoinEvent e);
	
	/**
	 * Processes Leave events that get massed to module
	 * <p>The processMemberLeave gets called by the {@link ModuleHandler} when a plugin elects to receive these events</p>
	 * @param e - <b>in</b> - {@link GuildMemberJoinEvent} object sent by the ModuleHandler
	 */
	public void processMemberLeave(GuildMemberLeaveEvent e);
	
	/**
	 * Gets whether or not this module will respond to a Join Event
	 * @return Boolean of {@code true} if it does respond to these events and {@code false} if it does not
	 */
	public Boolean respondsToJoinEvents();
	
	/**
	 * Gets whether or not this module will respond to a Leave Event
	 * @return Boolean of {@code true} if it does respond to these events and {@code false} if it does not
	 */
	public Boolean respondsToLeaveEvents();
}
