package com.reztek.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigReader {
	
	private static ConfigReader p_cr = null;
	private boolean p_configLoaded = false;
	
	private ConfigReader() {
		
	}
	
	private JSONObject p_config = null;
	
	public static ConfigReader GetConfigReader() {
		if (p_cr == null) {
			p_cr = new ConfigReader();
			p_cr.refreshConfig();
		}
		return p_cr;
	}
	
	public boolean isConfigLoaded() {
		return p_configLoaded;
	}
	
	public String getConfigString(String key) {
		try {
			return p_config.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public long getConfigLong(String key) {
		try {
			return p_config.getLong(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public boolean refreshConfig() {
		File config = new File(BotUtils.GetExecutionPath(getClass()) + "/BotConfig.conf");
		if (!config.exists()) {
			System.out.println("[ERROR] Config file not found! - Creating Template - Please fill in and reload!");
			BotUtils.ExportResource("BotConfig.conf", getClass());
			p_configLoaded = false;
			return false;
		}
		try {
			p_config = new JSONObject(IOUtils.toString(new FileInputStream(config)));
			p_configLoaded = true;
			return true;
		} catch (IOException e) {
			p_configLoaded = false;
			return false;
		} 
	}
}
