package com.reztek.Global;

import com.reztek.Utils.ConfigReader;

public abstract class GlobalDefs {
	public static final String DB_HOST                = ConfigReader.GetConfigReader().getConfigString("DB_HOST");
	public static final String DB_HOST_DEV            = ConfigReader.GetConfigReader().getConfigString("DB_HOST_DEV");
	public static final String DB_USER                = ConfigReader.GetConfigReader().getConfigString("DB_USER");
	public static final String DB_PASS                = ConfigReader.GetConfigReader().getConfigString("DB_PASS");
	public static final String DB_DBASE               = ConfigReader.GetConfigReader().getConfigString("DB_DBASE");
	public static final String DB_DBASE_DEV           = ConfigReader.GetConfigReader().getConfigString("DB_DBASE_DEV");
	
	public static final String BUNGIE_API_KEY         = ConfigReader.GetConfigReader().getConfigString("BUNGIE_API_KEY");
	public static final String BOT_TOKEN              = ConfigReader.GetConfigReader().getConfigString("BOT_TOKEN");
	public static final String BOT_TOKEN_DEV          = ConfigReader.GetConfigReader().getConfigString("BOT_TOKEN_DEV");
	public static final String GOOGLE_API_KEY         = ConfigReader.GetConfigReader().getConfigString("GOOGLE_API_KEY");
	public static final String GOOGLE_API_CX          = ConfigReader.GetConfigReader().getConfigString("GOOGLE_API_CX");
	
	public static final long   TIMER_TICK             = ConfigReader.GetConfigReader().getConfigLong("TIMER_TICK");
	public static final String TMP_LOCATION           = ConfigReader.GetConfigReader().getConfigString("TMP_LOCATION");
	public static final String DEV_TMP_LOCATION       = ConfigReader.GetConfigReader().getConfigString("DEV_TMP_LOCATION");
	
	public static final String WWW_HOST               = ConfigReader.GetConfigReader().getConfigString("WWW_HOST");
	public static final String WWW_BADGE_CACHE        = ConfigReader.GetConfigReader().getConfigString("WWW_BADGE_CACHE");
	public static final String WWW_ASSETS             = ConfigReader.GetConfigReader().getConfigString("WWW_ASSETS");
	public static final String LOCAL_BADGE_CACHE      = ConfigReader.GetConfigReader().getConfigString("LOCAL_BADGE_CACHE");
	public static final String LOCAL_DEV_BADGE_CACHE  = ConfigReader.GetConfigReader().getConfigString("LOCAL_DEV_BADGE_CACHE");
	
	public static final String BUNGIE_APP_AUTH        = ConfigReader.GetConfigReader().getConfigString("BUNGIE_APP_AUTH");
	
	public static final boolean BOT_DEV               = true;
	public static final String  BOT_VERSION           = "2.0";
}
