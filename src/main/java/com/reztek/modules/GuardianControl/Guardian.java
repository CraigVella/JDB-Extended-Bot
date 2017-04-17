package com.reztek.modules.GuardianControl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	private static final String BUNGIE_ACCOUNT_URL = "/Account/";
	private static final String GUARDIAN_API_BASE_URL = "https://api.guardian.gg";
	private static final String GUARDIAN_API_ELO = "/elo/";
	private static final String GUARDIAN_API_FIRETEAM = "/fireteam/14/";
	private static final String DTR_API_BASE_URL = "https://api.destinytrialsreport.com";
	private static final String DTR_API_PLAYER = "/player/";
	
	// -- BUNGIE
	private String p_id = null;
	private String p_name = null;
	private String p_platform = null;
	private String p_characterIdLastPlayed = null;
	private String p_grimoireScore = null;
	private String p_characterLastPlayedSubclassHash = null;
	
	private static final Map<String,String> BungieHashDefinitions;
	static {
		Map<String,String> bHashMap = new HashMap<String,String>();
		bHashMap.put("21395672", "Sunbreaker");
		bHashMap.put("21395673", "Sunbreaker");
		bHashMap.put("1256644900", "Stormcaller");
		bHashMap.put("1256644901", "Stormcaller");
		bHashMap.put("1716862031", "Gunslinger");
		bHashMap.put("2007186000", "Defender");
		bHashMap.put("2455559914", "Striker");
		bHashMap.put("2962927168", "Bladedancer");
		bHashMap.put("3658182170", "Sunsinger");
		bHashMap.put("3828867689", "Voidwalker");
		bHashMap.put("4143670656", "Nightstalker");
		bHashMap.put("4143670657", "Nightstalker");
		BungieHashDefinitions = Collections.unmodifiableMap(bHashMap);
	}
	
	// -- Guardian GG
	private String p_rumbleELO = null;
	private String p_rumbleRank = null;
	private String p_trialsELO = null;
	private String p_trialsRank = null;
	private String p_lighthouseCount = null;
	
	// -- Destiny Trials Report
	private String p_thisWeekTrialsFlawless = null;
	private String p_thisWeekTrialsMatches = null;
	private String p_thisWeekTrialsLosses = null;
	private String p_thisWeekTrialsKills = null;
	private String p_thisWeekTrialsDeaths = null;
	private float  p_thisWeekTrialsKD = 0;
	private String p_thisYearTrialsMatches = null;
	private String p_thisYearTrialsKills = null;
	private String p_thisYearTrialsDeaths = null;
	private float  p_thisYearTrialsKD = 0;
	
	private ArrayList<GuardianWeaponStats> p_thisMapWepStats = new ArrayList<GuardianWeaponStats>();
	
	private Guardian() {
		
	}
	
	public static Guardian guardianFromName(String guardianName, String platform) {
		Guardian g = new Guardian();
		if (!g.findGuardianIdOnBungie(guardianName,platform)) return null;
		return Guardian.guardianFromMembershipId(g.getId(), g.getName(), g.getPlatform(), g);
	}
	
	public static Guardian guardianFromMembershipId(String membershipId, String name, String platform) {
		Guardian g = new Guardian();
		return guardianFromMembershipId(membershipId, name, platform, g);
	}
	
	protected static Guardian guardianFromMembershipId(String membershipId, String name, String platform, Guardian g) {		
		g.p_id = membershipId;
		g.p_name = name;
		g.p_platform = platform;
		
		g.getGuardianGG();
		g.getGuardianDTR();
		
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
	
	private boolean getGuardianDTR() {
		JSONObject ob = new JSONObject("{\"DTRArray\":" + BotUtils.getJSONString(DTR_API_BASE_URL + DTR_API_PLAYER + p_id,null) + "}").getJSONArray("DTRArray").getJSONObject(0);
		JSONObject flYearArray = null;
		
		try {
			flYearArray = ob.getJSONObject("flawless").getJSONObject("years");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
		
		String[] years = JSONObject.getNames(flYearArray);
		int flawlessCount  = 0;
		
		for (String year : years) {
			flawlessCount += flYearArray.optJSONObject(year).getInt("count");
		}
		
		p_lighthouseCount = String.valueOf(flawlessCount);
		
		JSONObject thisWeekOb = ob.getJSONArray("thisWeek").getJSONObject(0);
		
		p_thisWeekTrialsMatches =  String.valueOf(thisWeekOb.getInt("matches"));
		p_thisWeekTrialsFlawless =  String.valueOf(thisWeekOb.getInt("flawless"));
		p_thisWeekTrialsLosses = String.valueOf(thisWeekOb.getInt("losses"));
		p_thisWeekTrialsKills = String.valueOf(thisWeekOb.getInt("kills"));
		p_thisWeekTrialsDeaths = String.valueOf(thisWeekOb.getInt("deaths"));
		p_thisWeekTrialsKD = (Float.valueOf(p_thisWeekTrialsKills) / Float.valueOf(p_thisWeekTrialsDeaths));
		
		p_thisYearTrialsKills = String.valueOf(ob.getInt("kills"));
		p_thisYearTrialsDeaths =  String.valueOf(ob.getInt("deaths"));
		p_thisYearTrialsMatches = String.valueOf(ob.getInt("match_count"));
		p_thisYearTrialsKD = (Float.valueOf(p_thisYearTrialsKills) / Float.valueOf(p_thisYearTrialsDeaths));
		
		JSONArray thisMapWeps = ob.getJSONArray("thisMapWeapons");
		for (int x = 0; x < thisMapWeps.length(); ++x) {
			JSONObject wep = thisMapWeps.getJSONObject(x);
			GuardianWeaponStats gws = new GuardianWeaponStats();
			gws.p_WepName = wep.getString("itemTypeName");
			gws.p_WepKills = String.valueOf(wep.getInt("sum_kills"));
			gws.p_WepHeadshots = String.valueOf(wep.getInt("sum_headshots"));
			p_thisMapWepStats.add(gws);
		}
		
		return true;
	}
	
	private boolean getGuardianGG() {
		try {
			JSONArray ob = new JSONObject("{\"GGArray\":" + BotUtils.getJSONString(GUARDIAN_API_BASE_URL + GUARDIAN_API_ELO + p_id, null) + "}").getJSONArray("GGArray");
			for (int x = 0; x < ob.length(); ++x) {
				JSONObject itObj = ob.getJSONObject(x);
				if (itObj.getInt("mode") == 13) { // rumble
					p_rumbleELO = String.valueOf(itObj.getBigDecimal("elo").setScale(0, BigDecimal.ROUND_HALF_EVEN));
					p_rumbleRank = (itObj.getInt("rank") > 0 ? String.valueOf(itObj.getInt("rank")) : "N/A");
				} else if (itObj.getInt("mode") == 14) { // trials
					p_trialsELO = String.valueOf(itObj.getBigDecimal("elo").setScale(0, BigDecimal.ROUND_HALF_EVEN));
					p_trialsRank = (itObj.getInt("rank") > 0 ? String.valueOf(itObj.getInt("rank")) : "N/A");
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private boolean findGuardianIdOnBungie(String guardianName, String platform) {
		HashMap<String,String> props = new HashMap<String,String>();
		props.put("X-API-Key", BUNGIE_API_KEY);
		
		try {
			JSONObject ob = new JSONObject (BotUtils.getJSONString(BUNGIE_BASE_URL + BUNGIE_SEARCH_URL + platform + "/" + guardianName + "/", props));
			p_id = ob.getJSONArray("Response").getJSONObject(0).getString("membershipId");
			p_name = ob.getJSONArray("Response").getJSONObject(0).getString("displayName");
			p_platform = String.valueOf(ob.getJSONArray("Response").getJSONObject(0).getInt("membershipType"));
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
		
		try {
			JSONObject ob = new JSONObject(BotUtils.getJSONString(BUNGIE_BASE_URL + "/" + p_platform + BUNGIE_ACCOUNT_URL + p_id + "/", props));
			p_grimoireScore = String.valueOf(ob.getJSONObject("Response").getJSONObject("data").getInt("grimoireScore"));
			p_characterIdLastPlayed = ob.getJSONObject("Response").getJSONObject("data").getJSONArray("characters").getJSONObject(0).getJSONObject("characterBase").getString("characterId");
			p_characterLastPlayedSubclassHash = String.valueOf(ob.getJSONObject("Response").getJSONObject("data").getJSONArray("characters").getJSONObject(0).getJSONObject("characterBase").getJSONObject("peerView").getJSONArray("equipment").getJSONObject(0).getBigInteger("itemHash"));
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
		

		return true;
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
	
	public String getGrimoireScore() {
		return p_grimoireScore;
	}
	
	public String getLastPlayedCharacterId() {
		return p_characterIdLastPlayed;
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
	
	public ArrayList<GuardianWeaponStats> getThisWeekMapWeaponStats() {
		return p_thisMapWepStats;
	}
	
	public String getCharacterLastPlayedSubclassHash() {
		return p_characterLastPlayedSubclassHash;
	}
	
	public String getCharacterLastPlayedSubclass() {
		return BungieHashDefinitions.getOrDefault(p_characterLastPlayedSubclassHash, "Unknown");
	}
}
