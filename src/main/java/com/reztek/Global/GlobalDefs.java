package com.reztek.Global;

import com.reztek.Utils.ConfigReader;

/**
 * Internal Global Definitions
 * @author Craig Vella
 *
 */
public abstract class GlobalDefs {
	public static final String DB_HOST                = ConfigReader.GetConfigReader().getConfigString("DB_HOST");
	public static final String DB_USER                = ConfigReader.GetConfigReader().getConfigString("DB_USER");
	public static final String DB_PASS                = ConfigReader.GetConfigReader().getConfigString("DB_PASS");
	public static final String DB_DBASE               = ConfigReader.GetConfigReader().getConfigString("DB_DBASE");
	
	public static final String BOT_TOKEN              = ConfigReader.GetConfigReader().getConfigString("BOT_TOKEN");
	
	public static final Long   TIMER_TICK             = ConfigReader.GetConfigReader().getConfigLong  ("TIMER_TICK");
	public static final String TMP_LOCATION           = ConfigReader.GetConfigReader().getConfigString("TMP_LOCATION");
	
	public static final String WWW_HOST               = ConfigReader.GetConfigReader().getConfigString("WWW_HOST");
	public static final String WWW_BADGE_CACHE        = ConfigReader.GetConfigReader().getConfigString("WWW_BADGE_CACHE");
	public static final String WWW_ASSETS             = ConfigReader.GetConfigReader().getConfigString("WWW_ASSETS");
	public static final String LOCAL_BADGE_CACHE      = ConfigReader.GetConfigReader().getConfigString("LOCAL_BADGE_CACHE");
	
	public static final String  BOT_VERSION           = "2.7";
}
