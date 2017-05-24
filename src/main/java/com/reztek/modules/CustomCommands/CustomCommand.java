package com.reztek.modules.CustomCommands;

public abstract class CustomCommand implements ICustomCommand {
	protected String p_command = null;
	protected int    p_commandType = CustomCommands.CMD_ERROR;
	protected String p_data = null;
	protected int DBID = -1;
	
	public String getCommand() {
		return p_command;
	}
	public int getCommandType() {
		return p_commandType;
	}
	public String getData() {
		return p_data;
	}
	public int getDBID() {
		return DBID;
	}
	public void setDBID(int dbID) {
		DBID = dbID;
	}
}
