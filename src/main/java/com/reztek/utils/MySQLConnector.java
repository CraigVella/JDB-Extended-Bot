package com.reztek.Utils;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Connection;
import com.reztek.Global.GlobalDefs;

/**
 * A MySQL Helper class for database access and queries
 * @author Craig Vella
 *
 */
public class MySQLConnector {
	
	private static MySQLConnector instance;
	
	/**
	 * Gets the singleton instance of MySQLConnector
	 * @return MySQLConnector instance
	 */
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
	
	/**
	 * Runs the given update query
	 * @param query to run
	 * @return int containing number of effected rows
	 */
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
	
	/**
	 * Runs an insert based query and returns the new primary key
	 * @param query to run
	 * @return int containing the new generated primary key ID
	 */
	public int runInsertReturnID(String query) {
		try {
			PreparedStatement ps = p_con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Run Query with a {@link ResultSet}
	 * @param query to run
	 * @return {@link ResultSet} containing the results of the query
	 */
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
