package com.reztek.modules.TrialsList;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reztek.Guardian;
import com.reztek.base.Taskable;
import com.reztek.utils.BotUtils;
import com.reztek.utils.MySQLConnector;

import net.dv8tion.jda.core.entities.MessageChannel;

public class TrialsList extends Taskable {
	public void addPlayer(MessageChannel mc, Guardian guardian) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM trialsList WHERE membershipId = '" + guardian.getId() + "'");
		try {
			if (!rs.last()) {
				// userdoesn't exist add them
				MySQLConnector.getInstance().runUpdateQuery("INSERT INTO trialsList (membershipId,platform,playerName,rank,elo,flawlessCount) VALUES ('"
						+ guardian.getId() + "','" + guardian.getPlatform() + "','" + guardian.getName() + "','" 
						+ guardian.getTrialsRank() + "'," + guardian.getTrialsELO() + "," + guardian.getLighthouseCount() + ")");
				mc.sendMessage("Succesfully added " + guardian.getName() + " to the Trials of Osiris List").queue();
			} else {
				// user already exists in DB
				mc.sendMessage(guardian.getName() + " already exists in the Trials of Osiris List").queue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removePlayer (MessageChannel mc, Guardian guardian) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM trialsList WHERE membershipId = '" + guardian.getId() + "'");
		try {
			if (!rs.last()) {
				mc.sendMessage(guardian.getName() + " is not in the Trials List").queue();
			} else {
				MySQLConnector.getInstance().runUpdateQuery("DELETE FROM trialsList WHERE membershipId = '" + guardian.getId() + "'");
				mc.sendMessage(guardian.getName() + " has been removed from the Trials of Osiris List").queue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showList(MessageChannel mc) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM trialsList ORDER BY rank ASC");
		try {
			int x = 1;
			String platformName = "";
			String trialsList = "**Current Trials of Osiris Leaders**\n```";
			while (rs.next()) {
				if (rs.getString("platform").equalsIgnoreCase("1")) platformName = "XB";
				if (rs.getString("platform").equalsIgnoreCase("2")) platformName = "PS";
				trialsList += String.valueOf(x) + "." + BotUtils.getPaddingForLen(String.valueOf(x++), 3) + 
						rs.getString("playerName") + BotUtils.getPaddingForLen(rs.getString("playerName"),18) +
						" (Elo: " + BotUtils.getPaddingForLen(rs.getString("elo"), 4) + rs.getString("elo") + " |"+ platformName +
						"| Rank:" + BotUtils.getPaddingForLen(rs.getString("rank"),6) + rs.getString("rank") + " | FC:" + 
						BotUtils.getPaddingForLen(rs.getString("flawlessCount"), 4) + rs.getString("flawlessCount") + ")\n";
			}
			trialsList += "```";
			mc.sendMessage(trialsList).queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshList(MessageChannel mc, boolean verbose) {
		if (verbose) {
			mc.sendMessage("*Updating Trials of Osiris List Please Wait...*").queue();
			mc.sendTyping().queue();
		}
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM trialsList");
		Guardian g;
		try {
			while (rs.next()) {
				g = Guardian.guardianFromMembershipId(rs.getString("membershipId"),rs.getString("playerName"), rs.getString("platform"));
				MySQLConnector.getInstance().runUpdateQuery("UPDATE trialsList SET elo = " + g.getTrialsELO() + ", rank = " + g.getTrialsRank() + ", flawlessCount = " + g.getLighthouseCount() + " WHERE membershipId = '" + g.getId() + "'");
			}
			if (verbose) mc.sendMessage("*Updating Complete!*").queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runTask() {
		System.out.println("TrialsList Task Running...");
		refreshList(null, false);
		System.out.println("TrialsList Task Complete...");
	}
}
