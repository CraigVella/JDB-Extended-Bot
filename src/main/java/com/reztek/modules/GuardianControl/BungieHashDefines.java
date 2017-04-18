package com.reztek.modules.GuardianControl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BungieHashDefines {
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
	public static String GetStringForHash(String Hash) {
		return BungieHashDefinitions.getOrDefault(Hash, "Unknown");
	}
}
