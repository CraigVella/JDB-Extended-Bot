package com.reztek.modules.GuardianControl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.reztek.secret.GlobalDefs;
import com.reztek.utils.BotUtils;

public class Guardian {
	
	public class GuardianWeaponStats {
		private String p_WepName = null;
		private String p_WepKills = null;
		private String p_WepHeadshots = null;
		public String getWeaponName() { return p_WepName; }
		public String getWeaponKills(){return p_WepKills; }
		public String getWeaponHeadshots() { return p_WepHeadshots; }
		public String getHeadshotPercentage() {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2); 
			float hsPerc = Float.valueOf(p_WepHeadshots) / Float.valueOf(p_WepKills);
			hsPerc *= 100;
			return df.format(hsPerc) + "%";
		}
	}
	
	public static class PlatformCodeFromNicknameData {
		protected String _nickName = null;
		protected String _platform = Guardian.PLATFORM_ALL;
		protected boolean _usesTag = false;
		public String getNickname() { return _nickName; }
		public String getPlatform() { return _platform; }
		public boolean usesTag() { return _usesTag; }
	}
	
	public static final String PLATFORM_XB = "1";
	public static final String PLATFORM_PS = "2";
	public static final String PLATFORM_ALL = "All";
	
	private static final String BUNGIE_API_KEY = GlobalDefs.BUNGIE_API_KEY;
	private static final String BUNGIE_BASE_URL = "https://www.bungie.net/Platform/Destiny";
	private static final String BUNGIE_SEARCH_URL = "/SearchDestinyPlayer/";
	private static final String GUARDIAN_API_BASE_URL = "https://api.guardian.gg";
	private static final String GUARDIAN_API_ELO = "/elo/";
	private static final String GUARDIAN_API_FIRETEAM = "/fireteam/14/";
	private static final String DTR_API_BASE_URL = "https://api.destinytrialsreport.com";
	private static final String DTR_API_PLAYER = "/player/";
	
	private String p_id = null;
	private String p_name = null;
	private String p_rumbleELO = null;
	private String p_rumbleRank = null;
	private String p_trialsELO = null;
	private String p_trialsRank = null;
	private String p_lighthouseCount = null;
	private String p_platform = null;
	
	private String p_thisWeekTrialsFlawless = null;
	private String p_thisWeekTrialsMatches = null;
	private String p_thisWeekTrialsLosses = null;
	private String p_thisWeekTrialsKills = null;
	private String p_thisWeekTrialsDeaths = null;
	private float p_thisWeekTrialsKD = 0;
	
	private String p_thisYearTrialsMatches = null;
	private String p_thisYearTrialsKills = null;
	private String p_thisYearTrialsDeaths = null;
	private float p_thisYearTrialsKD = 0;
	
	private ArrayList<GuardianWeaponStats> p_thisWeekWepStats = new ArrayList<GuardianWeaponStats>();
	
	private Guardian() {
		
	}
	
	public static Guardian guardianFromName(String guardianName, String platform) {		
		HashMap<String,String> bungMap = Guardian.findGuardianIdOnBungie(guardianName,platform);
		if (bungMap == null) return null;
		return Guardian.guardianFromMembershipId(bungMap.get("id"), bungMap.get("name"), bungMap.get("platform"));
	}
	
	public static Guardian guardianFromMembershipId(String membershipId, String name, String platform) {
		Guardian g = new Guardian();
		
		g.p_id = membershipId;
		g.p_name = name;
		g.p_platform = platform;
		
		HashMap<String,String> ggMap = g.getGuardianGG(membershipId);
		
		g.p_rumbleELO = ggMap.get("rumbleElo");
		g.p_trialsELO = ggMap.get("trialsElo");
		g.p_rumbleRank = ggMap.get("rumbleRank");
		g.p_trialsRank = ggMap.get("trialsRank");
		
		HashMap<String,String> dtrMap = g.getGuardianDTR(membershipId);
		
		g.p_lighthouseCount = dtrMap.get("lighthouseCount");
		
		g.p_thisWeekTrialsDeaths = dtrMap.get("thisWeekTrialsDeaths");
		g.p_thisWeekTrialsFlawless = dtrMap.get("thisWeekTrialsFlawless");
		g.p_thisWeekTrialsKills = dtrMap.get("thisWeekTrialsKills");
		g.p_thisWeekTrialsLosses = dtrMap.get("thisWeekTrialsLosses");
		g.p_thisWeekTrialsMatches = dtrMap.get("thisWeekTrialsMatches");
		g.p_thisWeekTrialsKD = (Float.valueOf(g.p_thisWeekTrialsKills) / Float.valueOf(g.p_thisWeekTrialsDeaths));
		
		g.p_thisYearTrialsKills = dtrMap.get("thisYearTrialsKills");
		g.p_thisYearTrialsDeaths = dtrMap.get("thisYearTrialsDeaths");
		g.p_thisYearTrialsKD = (Float.valueOf(g.p_thisYearTrialsKills) / Float.valueOf(g.p_thisYearTrialsDeaths));
		g.p_thisYearTrialsMatches = dtrMap.get("thisYearTrialsMatches");
		
		for (int x = 0; x < Integer.valueOf(dtrMap.get("mapWepTotal")); ++x) {
			GuardianWeaponStats gws = g.new GuardianWeaponStats();
			gws.p_WepName = dtrMap.get("mapWepName-" + x);
			gws.p_WepKills = dtrMap.get("mapWepKills-" + x);
			gws.p_WepHeadshots = dtrMap.get("mapWepHeadshots-" + x);
			g.p_thisWeekWepStats.add(gws);
		}
		
		return g;
	}
	
	public static String platformCodeFromCommand(String command) {
		String platform = Guardian.PLATFORM_ALL;
		for (String platCode : command.split("-")) {
			switch (platCode) {
				case "ps":
					platform = Guardian.PLATFORM_PS;
					break;
				case "xb":
					platform = Guardian.PLATFORM_XB;
					break;
				default:
					platform = Guardian.PLATFORM_ALL;
					break;
			}
		}
		return platform;
	}
	
	public static PlatformCodeFromNicknameData platformCodeFromNickname(String nickname) {
		PlatformCodeFromNicknameData ret = new PlatformCodeFromNicknameData();
		ret._nickName = nickname;
		
		String[] nickSep = nickname.split("]");
		if (nickSep.length > 1) {
			// a Tag proceeds their name try and get platform
			ret._usesTag = true;
			String[] nickCheckSlash = nickSep[0].split("\\/");
			String tag = null;
			if (nickCheckSlash.length > 1) {
				// 2 tags in there name seperated by / - just use first one
				tag = nickCheckSlash[0].substring(1);
			} else {
				tag = nickSep[0].substring(1);
			}
			if (tag.equalsIgnoreCase("PS4")) ret._platform = Guardian.PLATFORM_PS;
			if (tag.equalsIgnoreCase("XB1")) ret._platform = Guardian.PLATFORM_XB;
			ret._nickName = nickSep[1].trim();
		}
		
		return ret;
	}
	
	public ArrayList<HashMap<String,String>> getTrialsFireteamMembershipId() {
		ArrayList<HashMap<String,String>> fireteam =  new ArrayList<HashMap<String,String>>();
		JSONArray ob = new JSONObject("{\"GGArray\":" + BotUtils.getJSONString(GUARDIAN_API_BASE_URL + GUARDIAN_API_FIRETEAM + getId(), null) + "}").getJSONArray("GGArray");
		
		for (int x = 0; x < ob.length(); ++x) {
			JSONObject itObj = ob.getJSONObject(x);
			if (itObj.getString("membershipId").equals(getId())) continue;
			HashMap<String,String> hm = new HashMap<String,String>();
			hm.put("membershipId", itObj.getString("membershipId"));
			hm.put("name", itObj.getString("name"));
			hm.put("platform",String.valueOf(itObj.getInt("membershipType")));
			fireteam.add(hm);
		}
		return fireteam;
	}
	
	private HashMap<String, String> getGuardianDTR(String membershipId) {
		HashMap<String, String> dtrMap = new HashMap<String,String>();
		
		JSONObject ob = new JSONObject("{\"DTRArray\":" + BotUtils.getJSONString(DTR_API_BASE_URL + DTR_API_PLAYER + membershipId,null) + "}").getJSONArray("DTRArray").getJSONObject(0);
		JSONObject flYearArray = ob.getJSONObject("flawless").getJSONObject("years");
		
		String[] years = JSONObject.getNames(flYearArray);
		int flawlessCount  = 0;
		
		for (String year : years) {
			flawlessCount += flYearArray.optJSONObject(year).getInt("count");
		}
		
		dtrMap.put("lighthouseCount", String.valueOf(flawlessCount));
		
		JSONObject thisWeekOb = ob.getJSONArray("thisWeek").getJSONObject(0);
		
		dtrMap.put("thisWeekTrialsMatches", String.valueOf(thisWeekOb.getInt("matches")));
		dtrMap.put("thisWeekTrialsFlawless", String.valueOf(thisWeekOb.getInt("flawless")));
		dtrMap.put("thisWeekTrialsLosses", String.valueOf(thisWeekOb.getInt("losses")));
		dtrMap.put("thisWeekTrialsKills", String.valueOf(thisWeekOb.getInt("kills")));
		dtrMap.put("thisWeekTrialsDeaths", String.valueOf(thisWeekOb.getInt("deaths")));
		
		dtrMap.put("thisYearTrialsKills", String.valueOf(ob.getInt("kills")));
		dtrMap.put("thisYearTrialsDeaths", String.valueOf(ob.getInt("deaths")));
		dtrMap.put("thisYearTrialsMatches", String.valueOf(ob.getInt("match_count")));
		
		JSONArray thisMapWeps = ob.getJSONArray("thisMapWeapons");
		for (int x = 0; x < thisMapWeps.length(); ++x) {
			JSONObject wep = thisMapWeps.getJSONObject(x);
			dtrMap.put("mapWepName-" + String.valueOf(x), wep.getString("itemTypeName"));
			dtrMap.put("mapWepKills-" + String.valueOf(x), String.valueOf(wep.getInt("sum_kills")));
			dtrMap.put("mapWepHeadshots-" + String.valueOf(x), String.valueOf(wep.getInt("sum_headshots")));
		}
		dtrMap.put("mapWepTotal", String.valueOf(thisMapWeps.length()));
		
		return dtrMap;
	}
	
	private HashMap<String, String> getGuardianGG(String membershipId) {
		HashMap<String,String> ggMap = new HashMap<String,String>();
		JSONArray ob = new JSONObject("{\"GGArray\":" + BotUtils.getJSONString(GUARDIAN_API_BASE_URL + GUARDIAN_API_ELO + membershipId, null) + "}").getJSONArray("GGArray");
		
		for (int x = 0; x < ob.length(); ++x) {
			JSONObject itObj = ob.getJSONObject(x);
			if (itObj.getInt("mode") == 13) { // rumble
				ggMap.put("rumbleElo", String.valueOf(itObj.getBigDecimal("elo").setScale(0, BigDecimal.ROUND_HALF_EVEN)));
				ggMap.put("rumbleRank", itObj.getInt("rank") > 0 ? String.valueOf(itObj.getInt("rank")) : "N/A");
			} else if (itObj.getInt("mode") == 14) { // trials
				ggMap.put("trialsElo", String.valueOf(itObj.getBigDecimal("elo").setScale(0, BigDecimal.ROUND_HALF_EVEN)));
				ggMap.put("trialsRank", itObj.getInt("rank") > 0 ? String.valueOf(itObj.getInt("rank")) : "N/A");
			}
		}
		
		return ggMap;
	}
	
	private static HashMap<String,String> findGuardianIdOnBungie(String guardianName, String platform) {
		HashMap<String,String> ret = new HashMap<String,String>();
		HashMap<String,String> props = new HashMap<String,String>();
		props.put("X-API-Key", BUNGIE_API_KEY);
		
		JSONObject ob = new JSONObject (BotUtils.getJSONString(BUNGIE_BASE_URL + BUNGIE_SEARCH_URL + platform + "/" + guardianName + "/", props));
		
		try {
			ret.put("id",ob.getJSONArray("Response").getJSONObject(0).getString("membershipId"));
			ret.put("name",ob.getJSONArray("Response").getJSONObject(0).getString("displayName"));
			ret.put("platform", String.valueOf(ob.getJSONArray("Response").getJSONObject(0).getInt("membershipType")));
		} catch (JSONException e) {
			ret = null;
		}

		return ret;
	}
	
	// --------- Getters
	
	public String getId() {
		return p_id;
	}
	
	public String getName() {
		return p_name;
	}
	
	public String getRumbleELO() {
		return p_rumbleELO;
	}
	
	public String getTrialsELO() {
		return p_trialsELO;
	}
	
	public String getRumbleRank() {
		return p_rumbleRank;
	}
	
	public String getTrialsRank() {
		return p_trialsRank;
	}
	
	public String getLighthouseCount() {
		return p_lighthouseCount;
	}
	
	public String getPlatform() {
		return p_platform;
	}
	
	public String getThisWeekTrialsFlawless() {
		return p_thisWeekTrialsFlawless;
	}
	
	public String getThisWeekTrialsDeaths() {
		return p_thisWeekTrialsDeaths;
	}
	
	public String getThisWeekTrialsKills() {
		return p_thisWeekTrialsKills;
	}
	
	public String getThisWeekTrialsMatches() {
		return p_thisWeekTrialsMatches;
	}
	
	public String getThisWeekTrialsLosses() {
		return p_thisWeekTrialsLosses;
	}
	
	public String getThisWeekTrialsKD () {
		if (Double.isNaN(p_thisWeekTrialsKD)) return "N/A";
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2); 
		return df.format(p_thisWeekTrialsKD);
	}
	
	public String getThisYearTrialsKills() {
		return p_thisYearTrialsKills;
	}
	
	public String getThisYearTrialsDeaths() {
		return p_thisYearTrialsDeaths;
	}
	
	public String getThisYearTrialsKD () {
		if (Double.isNaN(p_thisYearTrialsKD)) return "N/A";
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		return df.format(p_thisYearTrialsKD);
	}
	
	public String getThisYearTrialsMatches() {
		return p_thisYearTrialsMatches;
	}
	
	public ArrayList<GuardianWeaponStats> getThisWeekWeaponStats() {
		return p_thisWeekWepStats;
	}
}
