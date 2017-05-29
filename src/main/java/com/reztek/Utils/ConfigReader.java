package com.reztek.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Configuration reader class
 * @author Craig Vella
 *
 */
public class ConfigReader {
	
	private static ConfigReader p_cr = null;
	private boolean p_configLoaded = false;
	
	private ConfigReader() {
		
	}
	
	private JSONObject p_config = null;
	
	/**
	 * Gets the ConfigReader Singleton
	 * @return ConfigReader object
	 */
	public static ConfigReader GetConfigReader() {
		if (p_cr == null) {
			p_cr = new ConfigReader();
			p_cr.refreshConfig();
		}
		return p_cr;
	}
	
	/**
	 * Returns whether or not the configuration has been loaded
	 * @return {@code true} if the configuration has been loaded {@code false} if it has not
	 */
	public boolean isConfigLoaded() {
		return p_configLoaded;
	}
	
	/**
	 * Gets the requested String from the configuration file
	 * @param key for the String to retrieve
	 * @return String containing the value from the configuration file
	 */
	public String getConfigString(String key) {
		try {
			return p_config.getString(key);
		} catch (JSONException e) {
			System.out.println("ConfigWarning - [" + key + "] does not exist or is not a String");
			return null;
		}
	}
	
	/**
	 * Gets the requested Long from the configuration file
	 * @param key for the Long to retrieve
	 * @return Long containing the value from the configuration file
	 */
	public Long getConfigLong(String key) {
		try {
			return p_config.getLong(key);
		} catch (JSONException e) {
			System.out.println("ConfigWarning - [" + key + "] does not exist or is not a Long");
			return null;
		}
	}
	
	/**
	 * Gets the requested Boolean from the configuration file
	 * @param key for the Boolean to retrieve
	 * @return Boolean containing the value from the configuration file
	 */
	public Boolean getConfigBoolean(String key) {
		try {
			return p_config.getBoolean(key);
		} catch (JSONException e) {
			System.out.println("ConfigWarning - [" + key + "] does not exist or is not a Boolean");
			return null;
		}
	}
	
	/**
	 * Add a new Key and Value to configuration file
	 * @param key of new Value
	 * @param value of new Key
	 */
	public void createNewConfigValue(String key, Object value) {
		if (!isConfigLoaded()) return;
		p_config.put(key, value);
		System.out.println("ConfigWarning - Added  [" + key + "] to the config file, please check it's value!");
		saveConfig();
	}
	
	/**
	 * Saves configuration file
	 */
	public void saveConfig() {
		File config = new File(BotUtils.GetExecutionPath(getClass()) + "/BotConfig.conf");
		if (config.exists()) {
			config.delete();
		}
		try {
			PrintWriter pw = new PrintWriter(config);
			pw.print(p_config.toString(5));
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Refresh's the configuration from file
	 * @return {@code true} if the configuration was refreshed, {@code false} if it could not be refreshed
	 */
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
