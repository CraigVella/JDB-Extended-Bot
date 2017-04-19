package com.reztek.utils;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Connection;
import com.reztek.secret.GlobalDefs;

public class MySQLConnector {
	
	private static MySQLConnector instance;
	
	public static MySQLConnector getInstance() {
		if (instance == null) {
			instance = new MySQLConnector();
		}
		return instance;
	}
	
	private Connection p_con;
	
	private MySQLConnector() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			p_con = (Connection) DriverManager.getConnection("jdbc:mysql://" + 
					(GlobalDefs.BOT_DEV ? GlobalDefs.DB_HOST_DEV : GlobalDefs.DB_HOST) + "/" + 
					(GlobalDefs.BOT_DEV ? GlobalDefs.DB_DBASE_DEV : GlobalDefs.DB_DBASE) + "?autoReconnect=true",GlobalDefs.DB_USER,GlobalDefs.DB_PASS);
			p_con.setAutoReconnect(true);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int runUpdateQuery(String query) {
		Statement st;
		int result = 0;
		try {
			st = p_con.createStatement();
			result = st.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public ResultSet runQueryWithResult(String query) {
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = p_con.createStatement();
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
}
