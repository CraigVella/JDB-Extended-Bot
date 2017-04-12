package com.reztek.modules.RumbleCommands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.reztek.base.Taskable;
import com.reztek.modules.GuardianControl.Guardian;
import com.reztek.utils.BotUtils;
import com.reztek.utils.MySQLConnector;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;

public class RumbleList extends Taskable {
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
	
	public void showList(MessageChannel mc, String startIndex, Color color) {
		String Query = "SELECT * FROM rumbleList ORDER BY rank ASC LIMIT 10 OFFSET " + startIndex;
		if (startIndex.equals("-1")) {
			Query = "SELECT * FROM rumbleList ORDER BY rank ASC";
			startIndex = "0";
		} 
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult(Query);
		try {
			int x = 1;
			String platformName = "";
			String rumbleList = "**Current Rumble Leaders**\n```";
			while (rs.next()) {
				if (rs.getString("platform").equalsIgnoreCase("1")) platformName = "XB";
				if (rs.getString("platform").equalsIgnoreCase("2")) platformName = "PS";
				rumbleList += String.valueOf(x + Integer.valueOf(startIndex)) + "." + BotUtils.getPaddingForLen(String.valueOf(x++ + Integer.valueOf(startIndex)), 3) + 
						rs.getString("playerName") + BotUtils.getPaddingForLen(rs.getString("playerName"),18) + 
						" (Rank:"+ BotUtils.getPaddingForLen(rs.getString("rank"), 6) + rs.getString("rank") + " |"+ 
						platformName +"| Elo: " + BotUtils.getPaddingForLen(rs.getString("elo"), 4) + rs.getString("elo") + ")\n";
			}
			rumbleList += "```";
			EmbedBuilder eb = new EmbedBuilder();
			eb.setDescription(rumbleList);
			eb.setColor(color);
			mc.sendMessage(eb.build()).queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void sendListCSV(PrivateChannel pc) {
		String csvOut = "";
		refreshList(pc, true);
		pc.sendMessage("*Now generating CSV File and sending... Please wait...*").queue();
		pc.sendTyping().queue();
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM rumbleList");
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int x = 1; x <= rsmd.getColumnCount(); ++x) {
				csvOut += rsmd.getColumnName(x) + (rsmd.getColumnCount() == x ? "" : ",");
			}
			csvOut += "\n";
			while (rs.next()) {
				for (int x = 1; x <= rsmd.getColumnCount(); ++x) {
					csvOut += rs.getString(x) + (rsmd.getColumnCount() == x ? "" : ",");
				}
				csvOut += "\n";
			}
			pc.sendFile(csvOut.getBytes(), "RumbleListExport.csv", null).queue();;
		} catch (SQLException e) {
			e.printStackTrace();
			pc.sendMessage("**Error generating CSV File**").queue();
		}
	}
	
	public void refreshList(MessageChannel mc, boolean verbose) {
		if (verbose) {
			mc.sendMessage("*Updating Rumble List Please Wait...*").queue();
			mc.sendTyping().queue();
		}
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM rumbleList");
		Guardian g;
		try {
			while (rs.next()) {
				g = Guardian.guardianFromMembershipId(rs.getString("membershipId"),rs.getString("playerName"), rs.getString("platform"));
				MySQLConnector.getInstance().runUpdateQuery("UPDATE rumbleList SET elo = " + g.getRumbleELO() + ", rank = " + g.getRumbleRank() + " WHERE membershipId = '" + g.getId() + "'");
			}
			if (verbose) mc.sendMessage("*Updating Complete!*").queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runTask() {
		System.out.println("RumbleList Task Running...");
		refreshList(null, false);
		System.out.println("RumbleList Task Complete...");
	}
}
