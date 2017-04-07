package com.reztek.modules.RumbleList;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reztek.Guardian;
import com.reztek.utils.MySQLConnector;

import net.dv8tion.jda.core.entities.MessageChannel;

public class RumbleList {
	public void addPlayer(MessageChannel mc, Guardian guardian) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM rumbleList WHERE membershipId = '" + guardian.getId() + "'");
		try {
			if (!rs.last()) {
				// userdoesn't exist add them
				MySQLConnector.getInstance().runUpdateQuery("INSERT INTO rumbleList (membershipId,platform,playerName,rank,elo) VALUES ('"
						+ guardian.getId() + "','" + guardian.getPlatform() + "','" + guardian.getName() + "','" 
						+ guardian.getRumbleRank() + "'," + guardian.getRumbleELO() + ")");
				mc.sendMessage("Succesfully added " + guardian.getName() + " to the Rumble List").queue();
			} else {
				// user already exists in DB
				mc.sendMessage(guardian.getName() + " already exists in the Rumble List").queue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removePlayer (MessageChannel mc, Guardian guardian) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM rumbleList WHERE membershipId = '" + guardian.getId() + "'");
		try {
			if (!rs.last()) {
				mc.sendMessage(guardian.getName() + " is not in the Rumble List").queue();
			} else {
				MySQLConnector.getInstance().runUpdateQuery("DELETE FROM rumbleList WHERE membershipId = '" + guardian.getId() + "'");
				mc.sendMessage(guardian.getName() + " has been removed from the Rumble List").queue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showList(MessageChannel mc) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM rumbleList ORDER BY rank ASC");
		try {
			int x = 1;
			String platformName = "";
			String padding = "";
			String paddingRank = "";
			String rumbleList = "**Current Rumble Leaders**\n```";
			while (rs.next()) {
				padding = "";
				for (int y = 0; y < (20 - rs.getString("playerName").length()); ++y) {
					padding += ' ';
				}
				paddingRank = "";
				for (int y = 0; y < (6 - rs.getString("rank").length()); ++y) {
					paddingRank += ' ';
				}
				if (rs.getString("platform").equalsIgnoreCase("1")) platformName = "XB";
				if (rs.getString("platform").equalsIgnoreCase("2")) platformName = "PS";
				rumbleList += String.valueOf(x++) + ". " + rs.getString("playerName") + padding + " (Rank:"+ paddingRank + rs.getString("rank") + " |"+ platformName +"| Elo: " + rs.getString("elo") + ")\n";
			}
			rumbleList += "```";
			mc.sendMessage(rumbleList).queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshList(MessageChannel mc) {
		mc.sendMessage("*Updating List Please Wait...*").queue();
		mc.sendTyping().queue();
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM rumbleList");
		Guardian g;
		try {
			while (rs.next()) {
				g = Guardian.guardianFromMembershipId(rs.getString("membershipId"),rs.getString("playerName"), rs.getString("platform"));
				MySQLConnector.getInstance().runUpdateQuery("UPDATE rumbleList SET elo = " + g.getRumbleELO() + ", rank = " + g.getRumbleRank() + " WHERE membershipId = '" + g.getId() + "'");
			}
			mc.sendMessage("*Updating Complete!*").queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
