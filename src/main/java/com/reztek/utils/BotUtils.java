package com.reztek.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.reztek.Global.GlobalDefs;

/**
 * A collection of Bot Utilites and other useful methods
 * @author Craig Vella
 *
 */
public abstract class BotUtils {
	/**
	 * Gets a String of Padding to be used to align Text in a Discord Markdown box
	 * @param toPad - The String to pad
	 * @param desiredLen - The Desired Max Length of padding needed
	 * @return A String containing the correct amount of padding
	 */
	public static String GetPaddingForLen(String toPad, int desiredLen) {
		String padding = "";
		if (toPad == null) return padding;
		for (int y = 0; y < (desiredLen - toPad.length()); ++y) {
			padding += ' ';
		}
		return padding;
	}
	
	/**
	 * Get JSON String from a URL as a GET request
	 * @param sURL - A String containing the REST endpoint
	 * @param props - HashMap of properties to include in the Header of the request, or null if none are needed
	 * @return String containing the result intended to be used in a new JSONObject
	 */
	public static String GetJSONStringGet(String sURL, HashMap<String, String> props) {
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
	
	/**
	 * Get JSON String from a URL as a POST request
	 * @param sURL - A String containing the REST endpoint
	 * @param props - HashMap of properties to include in the Header of the request, or null if none are needed
	 * @param content - The Data to be included in the POST request
	 * @return String containing the result intended to be used in a new JSONObject
	 */
	public static String GetJSONStringPost(String sURL, HashMap<String, String> props, String content) {
		String retObj = null;
		
		try {
			URL url = new URL(sURL);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.addRequestProperty("User-Agent", "Mozilla/4.76"); 
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			urlConnection.setRequestProperty( "charset", "utf-8");
			urlConnection.setRequestProperty( "Content-Length", Integer.toString( content.getBytes().length ));
			if (props != null) {
				Set<String> keys = props.keySet();
				for (String key : keys) {
					urlConnection.setRequestProperty(key, props.get(key));
				}
			}
			
			try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
				wr.write(content.getBytes());
				wr.close();
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
	
	/**
	 * Get the current version of the Bot
	 * @return String containing the Bot version
	 */
	public static String GetVersion() {
		return GlobalDefs.BOT_VERSION;
	}
	
	/**
	 * A Tuple Helper Object
	 * @author Craig Vella
	 *
	 * @param <T> Class type of Tuple
	 */
	public static class Tuple<T> {
		public Tuple(T first, T second) {
			_first = first;
			_second = second;
		}
		public Tuple() {
			
		}
		private T _first;
		private T _second;
		public void setFirst(T first) {
			_first = first;
		}
		public void setSecond(T second) {
			_second = second;
		}
		public T getFirst() {
			return _first;
		}
		public T getSecond() {
			return _second;
		}
	}
	
	/**
	 * Export the internal JAR resource to file
	 * @param resourceName - The Resource name to be exported
	 * @param requestObject - A class containing the resource within that object
	 * @return {@code true} if it is succesfully exported {@code false} if not
	 */
    static public boolean ExportResource(String resourceName, Class<?> requestObject) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = requestObject.getResourceAsStream(resourceName);
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(GetExecutionPath(requestObject) + "/" + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            stream.close();
            resStreamOut.close();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    /**
     * Get the Execution path of the Bot or JAR
     * @param requestObject - The object whose execution path you are inquring
     * @return String containing the Execution path
     */
    public static String GetExecutionPath(Class<?> requestObject){
	    String absolutePath = requestObject.getProtectionDomain().getCodeSource().getLocation().getPath();
	    absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
	    absolutePath = absolutePath.replaceAll("%20"," "); 
	    return absolutePath;
	}
	
    /**
     * A Class to help convert JSON Resources to JSONObjects
     * @author Craig Vella
     *
     */
	public static class JsonConverter {
		Class<?> _requestclass;
		String jsonFileName;
		
		/**
		 * Constructor for JsonConverter class
		 * @param jsonFileName - Filename of JSON resource
		 * @param requestObject - the class whose package contains the resource
		 */
		public JsonConverter(String jsonFileName, Class<?> requestObject){
		    this.jsonFileName = jsonFileName;
		    _requestclass = requestObject;
		}
		
		/**
		 * Get the JSONObject from object
		 * @return {@link JSONObject} containing data of requested JSON Resource
		 */
		public JSONObject getJsonObject(){
		    //Create input stream
		    InputStream inputStreamObject = getRequestclass().getResourceAsStream(jsonFileName);
	
		   try {
		       BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStreamObject, "UTF-8"));
		       StringBuilder responseStrBuilder = new StringBuilder();
	
		       String inputStr;
		       while ((inputStr = streamReader.readLine()) != null)
		           responseStrBuilder.append(inputStr);
	
		       JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
		       return jsonObject;
	
		   } catch (IOException e) {
		       e.printStackTrace();
		   } catch (JSONException e) {
		       e.printStackTrace();
		   }
	
		    //if something went wrong, return null
		    return null;
		}
	
		private Class<?> getRequestclass(){
		    return _requestclass;
		}
	}
	
	/**
	 * Abbreviate String <br />
	 * Ex: "Hello World" -> "He. World"
	 * @param toAbv - String to Abbreviate
	 * @return String containing abbreviation
	 */
	public static String AbvString(String toAbv) {
		String abvName = null;
		if (toAbv.contains(" ")) {
			int y = 0;
			for (String w : toAbv.split(" ")) {
				if (y++ == 0) {
					abvName = w.substring(0, 2) + ".";
				} else {
					abvName += " " + w;
				}
			}
		} else {
			abvName = toAbv;
		}
		return abvName;
	}
}
