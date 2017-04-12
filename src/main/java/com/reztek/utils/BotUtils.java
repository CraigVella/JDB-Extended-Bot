package com.reztek.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import com.reztek.secret.GlobalDefs;

public abstract class BotUtils {
	public static String getPaddingForLen(String toPad, int desiredLen) {
		String padding = "";
		for (int y = 0; y < (desiredLen - toPad.length()); ++y) {
			padding += ' ';
		}
		return padding;
	}
	
	public static String getJSONString(String sURL, HashMap<String, String> props) {
		String retObj = null;
		
		try {
			URL url = new URL(sURL);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.addRequestProperty("User-Agent", "Mozilla/4.76"); 
			urlConnection.setRequestMethod("GET");
			urlConnection.setUseCaches(false);
			if (props != null) {
				Set<String> keys = props.keySet();
				for (String key : keys) {
					urlConnection.setRequestProperty(key, props.get(key));
				}
			}
			
			InputStream is = urlConnection.getInputStream();
			String enc = urlConnection.getContentEncoding() == null ? "UTF-8" : urlConnection.getContentEncoding();
			String jsonReq = IOUtils.toString(is,enc);
			
			retObj = jsonReq;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retObj;
	}
	
	public static String getVersion() {
		return GlobalDefs.BOT_VERSION + (GlobalDefs.BOT_DEV ? "-devel" : "");
	}
}
