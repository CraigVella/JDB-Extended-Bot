package com.reztek.modules.GuardianControl;

import org.json.JSONObject;

import com.reztek.utils.BotUtils;

public abstract class BungieHashDefines {
	private static JSONObject SubclassHashDefinitions = null;
	public static String GetSubclassForHash(String Hash) {
		if (SubclassHashDefinitions == null) {
			SubclassHashDefinitions = new BotUtils.JsonConverter("DestinySubclassDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		return SubclassHashDefinitions.getJSONObject(Hash).getString("name");
	}
	
	private static JSONObject WeaponHashDefinitions = null;
	private static class WeaponHashReturn {
		protected String _name;
		protected String _icon;
		public String getName() {
			return _name;
		}
		public String getIcon() {
			return _icon;
		}
	}
	public static WeaponHashReturn GetWeaponForHash(String Hash) {
		WeaponHashReturn hr = new WeaponHashReturn();
		if (WeaponHashDefinitions == null) {
			WeaponHashDefinitions = new BotUtils.JsonConverter("DestinyWeaponDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		hr._name = WeaponHashDefinitions.getJSONObject(Hash).getString("name");
		hr._icon = WeaponHashDefinitions.getJSONObject(Hash).getString("icon");
		return hr;
	}
	
	private static JSONObject StepsHashDefinitions = null;
	private static class StepsHashReturn extends WeaponHashReturn {
		protected String _description;
		public String getDescription() {
			return _description;
		}
	}
	public static StepsHashReturn GetStepForHash(String Hash) {
		StepsHashReturn sr = new StepsHashReturn();
		if (StepsHashDefinitions == null) {
			StepsHashDefinitions = new BotUtils.JsonConverter("DestinyStepsDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		sr._name = StepsHashDefinitions.getJSONObject(Hash).getString("n");
		sr._icon = StepsHashDefinitions.getJSONObject(Hash).getString("i");
		sr._description = StepsHashDefinitions.getJSONObject(Hash).getString("d");
		return sr;
	}
}
