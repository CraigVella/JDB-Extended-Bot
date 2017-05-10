package com.reztek.modules.GuardianControl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

import com.reztek.Utils.BotUtils;
import com.reztek.Utils.MySQLConnector;

public class AuthenticatedGuardian extends Guardian {
	private static final String BUNGIE_API_REFRESH = "https://www.bungie.net/Platform/App/GetAccessTokensFromRefreshToken/";
	private static final int    EXPIRE_TIME_ADJUST = 300000;
	
	private String p_CurrentAuthToken          = null;
	private String p_CurrentAuthTokenExpire    = null;
	private String p_CurrentRefreshToken       = null;
	private String p_CurrentRefreshTokenExpire = null;
	
	protected AuthenticatedGuardian() {
		
	}
	
	public static AuthenticatedGuardian AuthenticatedGuardianFromNameAndPlatform(String name, String platform) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM registered WHERE displayName = '" + name + "'" + ((platform != PLATFORM_ALL) ? " AND platform = '" + platform + "'" : ""));
		try {
			if (!rs.last()) {
				return null;
			} else {
				return AuthenticatedGuardianFromMembershipAndPlatform(rs.getString("membershipId"), rs.getString("platform"));
			}
		} catch (SQLException e) {
			return null;
		}
	}
	
	public static AuthenticatedGuardian AuthenticatedGuardianFromMembershipAndPlatform(String membershipID, String platform) {
		ResultSet rs = MySQLConnector.getInstance().runQueryWithResult("SELECT * FROM registered WHERE membershipId = '" + membershipID + "' AND platform = '" + platform + "'");
		try {
			if (!rs.last()) {
				return null;
			} else {
				AuthenticatedGuardian ag = new AuthenticatedGuardian();
				
				ag.p_id                        = rs.getString("membershipId");
				ag.p_name                      = rs.getString("displayName");
				ag.p_platform                  = rs.getString("platform");
				ag.p_CurrentAuthToken          = rs.getString("currentToken");
				ag.p_CurrentAuthTokenExpire    = rs.getString("currentTokenExpire");
				ag.p_CurrentRefreshToken       = rs.getString("renewToken");
				ag.p_CurrentRefreshTokenExpire = rs.getString("renewTokenExpire");
				
				ag.getGuardianBungieExtended();
				ag.getGuardianDTR();
				ag.getGuardianGG();
				
				return ag;
			}
		} catch (SQLException e) {
			return null;
		}
	}
	
	public boolean areTokensValid() {
		// Check time on current token
		Date d = new Date();
		if ((d.getTime() - EXPIRE_TIME_ADJUST) > Long.valueOf(p_CurrentAuthTokenExpire)) { // check current time -5min against tok expiration
			// Current token expired - try to get new one
			return refreshAuthToken();
		}
		return true;
	}
	
	protected boolean refreshAuthToken() {
		Date d = new Date();
		if ((d.getTime() - EXPIRE_TIME_ADJUST) < Long.valueOf(p_CurrentAuthTokenExpire))    return false; // Check to see if current token is really expired
		if ((d.getTime() - EXPIRE_TIME_ADJUST) > Long.valueOf(p_CurrentRefreshTokenExpire)) return false; // Check to see if the refresh token is expired
		
		// Refresh token is valid, current token is invalid - refresh current token
		HashMap<String,String> props = new HashMap<String,String>();
		props.put("X-API-Key", BUNGIE_API_KEY);
		JSONObject ob = new JSONObject(BotUtils.getJSONStringPost(BUNGIE_API_REFRESH, props, "{\"refreshToken\":\""+ p_CurrentRefreshToken +"\"}"));
		
		if (ob.getInt("ErrorCode") != 1) { return false; }
		
		p_CurrentAuthToken = ob.getJSONObject("Response").getJSONObject("accessToken").getString("value");
		p_CurrentAuthTokenExpire = String.valueOf(d.getTime() + (ob.getJSONObject("Response").getJSONObject("accessToken").getInt("expires") * 1000));
		p_CurrentRefreshToken = ob.getJSONObject("Response").getJSONObject("refreshToken").getString("value");
		p_CurrentAuthTokenExpire = String.valueOf(d.getTime() + (ob.getJSONObject("Response").getJSONObject("refreshToken").getInt("expires") * 1000));
		
		// Write new values to DB
		MySQLConnector.getInstance().runUpdateQuery("UPDATE registered SET currentToken = '" + p_CurrentAuthToken + "', currentTokenExpire = '" + p_CurrentAuthTokenExpire + "', "
				+ "renewToken = '" + p_CurrentRefreshToken + "', renewTokenExpire = '" + p_CurrentRefreshTokenExpire + 
				"' WHERE membershipId = '" + p_id + "' AND platform = '" + p_platform + "'");
		
		return true;
	}
}
