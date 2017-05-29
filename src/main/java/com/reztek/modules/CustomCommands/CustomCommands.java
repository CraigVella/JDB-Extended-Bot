package com.reztek.modules.CustomCommands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import com.reztek.JDBExtendedBot;
import com.reztek.Base.CommandModule;
import com.reztek.Global.GlobalDefs;
import com.reztek.Utils.BotUtils;
import com.reztek.Utils.MySQLConnector;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CustomCommands extends CommandModule {
	
	public static final String PLUGIN_ID = "CUSTOMCOMMANDS";
	
	public static final int CMD_ERROR        = -1;
	public static final int CMD_CHANNEL_TEXT =  0;
	public static final int CMD_PRIVATE_TEXT =  1;
    
    protected HashMap<String, ICustomCommand> p_customCommands = new HashMap<String, ICustomCommand>();
    
    private int commandTypeFromString(String type) {
    	switch (type.toUpperCase()) {
    	case "CHANNEL_TEXT":
    		return CMD_CHANNEL_TEXT;
    	case "PRIVATE_TEXT":
    		return CMD_PRIVATE_TEXT;
    	default:
    		return -1;
    	}
    }
    
    private String commandStringFromType(int type) {
    	switch (type) {
    	case CMD_CHANNEL_TEXT:
    		return "CHANNEL_TEXT";
    	case CMD_PRIVATE_TEXT:
    		return "PRIVATE_TEXT";
    	default:
    		return "CMD_ERROR";
    	}
    }

	public CustomCommands() {
		super(PLUGIN_ID);
		setModuleNameAndAuthor("Custom Commands", "ChaseHQ85");
		setVersion(GlobalDefs.BOT_VERSION);
		addCommand(new String[] {"custom-add", "custom-remove", "custom-list"});
		try {
			ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM customCommands");
			while (rs.next()) {
				CustomCommand cmd = null;
				switch (rs.getInt("type")) {
				case CMD_CHANNEL_TEXT:
					cmd = new ChannelTextCommand(rs.getString("command"), new String(rs.getBlob("data1").getBytes(1, (int) rs.getBlob("data1").length())));
					cmd.setDBID(rs.getInt("id"));
					break;
				case CMD_PRIVATE_TEXT:
					cmd = new PrivateTextCommand(rs.getString("command"), new String(rs.getBlob("data1").getBytes(1, (int) rs.getBlob("data1").length())));
					cmd.setDBID(rs.getInt("id"));
					break;
					default:
				}
				if (cmd != null) {
					addCommand(cmd.getCommand());
					p_customCommands.put(cmd.getCommand(), cmd);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void processCommand(String command, String args, MessageReceivedEvent mre) {
		String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}
		
		if (command.equals("custom-add")) {
			if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
				if (args == null || splitArg.length < 3) {
					sendCustomCommandHelp(mre);
				} else {
					if (splitArg[0].getBytes()[0] == '!') splitArg[0] = splitArg[0].substring(1); // If they accidently put ! trim it out
					if (commandTypeFromString(splitArg[1].toUpperCase()) == CMD_ERROR) {
						mre.getChannel().sendMessage("Incorrect Usage").queue();
						sendCustomCommandHelp(mre);
					} else if(JDBExtendedBot.GetBot().getMessageHandler().isCommandTaken(splitArg[0].toLowerCase())) {
						mre.getChannel().sendMessage("Sorry " + mre.getAuthor().getAsMention() + ", that command is already taken, try another name").queue();
					} else {
						// Add Command
						String cmdText = "";
						for (int x = 2; x < splitArg.length; ++x) {
							cmdText += splitArg[x] + " ";
						}
						cmdText = cmdText.trim();
						CustomCommand cmd = null;
						switch (commandTypeFromString(splitArg[1].toUpperCase())) {
						case CMD_CHANNEL_TEXT:
							cmd = new ChannelTextCommand(splitArg[0].toLowerCase(), cmdText);
							break;
						case CMD_PRIVATE_TEXT:
							cmd = new PrivateTextCommand(splitArg[0].toLowerCase(), cmdText);
							break;
						case CMD_ERROR:
							default:
						}
						if (cmd == null) {
							mre.getChannel().sendMessage("Something went wrong adding custom command!").queue();
						} else {
							addCommand(cmd.getCommand());
							p_customCommands.put(cmd.getCommand(), cmd);
							mre.getChannel().sendMessage("Command '!"+ cmd.getCommand() +"' Successfully Added").queue();
							cmd.DBID = MySQLConnector.getInstance().runInsertReturnID("INSERT INTO customCommands (command,type,data1) VALUES (\"" +
									cmd.getCommand() + "\"," + cmd.getCommandType() + ",\"" + cmd.getData() + "\")");
						}
					}
				}
			}
		} else if (command.equals("custom-remove")) {
			if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
				if (args == null) {
					sendHelpString(mre, "!custom-remove <COMMAND>");
				} else {
					ICustomCommand cmd = p_customCommands.get(args);
					if (cmd == null) {
						mre.getChannel().sendMessage(args + ", is not a custom command, You can only remove added custom commands!").queue();
					} else {
						p_customCommands.remove(cmd.getCommand());
						removeCommand(cmd.getCommand());
						mre.getChannel().sendMessage("Removed the '!" + args + "' command").queue();
						MySQLConnector.getInstance().runUpdateQuery("DELETE FROM customCommands WHERE id = " + cmd.getDBID());
					}
				}
			}
		} else if (command.equals("custom-list")) {
			if (p_customCommands.size() == 0) {
				mre.getChannel().sendMessage("There are currently no custom commands registered").queue();
			} else {
				String commandList = "Command" + BotUtils.GetPaddingForLen("Command", 16) + " - " + "Command Type";
				for (ICustomCommand cmd : p_customCommands.values()) {
					commandList += "\n!" + cmd.getCommand() + BotUtils.GetPaddingForLen(cmd.getCommand(), 15) + " - " + commandStringFromType(cmd.getCommandType());
				}
				mre.getChannel().sendMessage("**The current custom command list is: **```" + commandList + "```").queue();
			}
		} else {
			// We are being sent this command, it's not '!custom', so it must be a user defined command
			ICustomCommand cmd = p_customCommands.get(command);
			if (cmd != null) {
				cmd.runCommand(mre);
			}
		}
	}
	
	protected void sendCustomCommandHelp(MessageReceivedEvent mre) {
		sendHelpString(mre, "!custom <COMMAND> <COMMAND_TYPE> <DATA>\n"
				+ "```COMMAND_TYPE's are:\n"
				+ "CHANNEL_TEXT - sends value in <DATA> to Channel\n"
				+ "PRIVATE_TEXT - sends value in <DATA> to user in private message\n\n"
				+ "Example: !custom sayhi private_text hi!     - This would send a private message of 'hi!' to a user that typed !sayhi```");
	}

}
