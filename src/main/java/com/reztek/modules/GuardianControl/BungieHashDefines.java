package com.reztek.modules.GuardianControl;

import org.json.JSONObject;

import com.reztek.Utils.BotUtils;

public abstract class BungieHashDefines {
	static class BaseHashReturn {
		protected String _hash;
		protected String _name;
		protected String _icon;
		public String getName() {
			return _name;
		}
		public String getIcon() {
			return _icon;
		}
		public String getHash() {
			return _hash;
		}
	}
	
	static class StepsHashReturn extends BaseHashReturn {
		protected String _description;
		public String getDescription() {
			return _description;
		}
	}
	
	static class ArmorHashReturn extends BaseHashReturn {
		protected String _description;
		protected int _tier;
		public String getDescription() {
			return _description;
		}
		public int getTier() {
			return _tier;
		}
	}
	
	private static JSONObject SubclassHashDefinitions = null;
	public static BaseHashReturn GetSubclassForHash(String Hash) {
		BaseHashReturn hr = new BaseHashReturn();
		if (SubclassHashDefinitions == null) {
			SubclassHashDefinitions = new BotUtils.JsonConverter("DestinySubclassDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		hr._hash = Hash;
		hr._name = SubclassHashDefinitions.getJSONObject(Hash).getString("name");
		hr._icon = SubclassHashDefinitions.getJSONObject(Hash).getString("icon");
		return hr;
	}
	
	private static JSONObject WeaponHashDefinitions = null;
	public static BaseHashReturn GetWeaponForHash(String Hash) {
		BaseHashReturn hr = new BaseHashReturn();
		if (WeaponHashDefinitions == null) {
			WeaponHashDefinitions = new BotUtils.JsonConverter("DestinyWeaponDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		hr._hash = Hash;
		hr._name = WeaponHashDefinitions.getJSONObject(Hash).getString("name");
		hr._icon = WeaponHashDefinitions.getJSONObject(Hash).getString("icon");
		return hr;
	}
	
	private static JSONObject StepsHashDefinitions = null;
	public static StepsHashReturn GetStepForHash(String Hash) {
		StepsHashReturn sr = new StepsHashReturn();
		if (StepsHashDefinitions == null) {
			StepsHashDefinitions = new BotUtils.JsonConverter("DestinyStepsDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		sr._hash = Hash;
		sr._name = StepsHashDefinitions.getJSONObject(Hash).getString("n");
		sr._icon = StepsHashDefinitions.getJSONObject(Hash).getString("i");
		sr._description = StepsHashDefinitions.getJSONObject(Hash).getString("d");
		return sr;
	}
	
	private static JSONObject ArmorHashDefinitions = null;
	public static ArmorHashReturn GetArmorForHash(String Hash) {
		ArmorHashReturn ar = new ArmorHashReturn();
		if (ArmorHashDefinitions == null) {
			ArmorHashDefinitions = new BotUtils.JsonConverter("DestinyArmorDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		ar._hash = Hash;
		ar._name = ArmorHashDefinitions.getJSONObject(Hash).getString("name");
		ar._icon = ArmorHashDefinitions.getJSONObject(Hash).getString("icon");
		ar._description = ArmorHashDefinitions.getJSONObject(Hash).getString("description");
		ar._tier = ArmorHashDefinitions.getJSONObject(Hash).getInt("tierType");
		return ar;
	}
	
	private static JSONObject TalentGridDefinitions = null;
	public static String getStepHashForTalentGridNode(String talentGridHash, int node, int stepIndex) {
		if (TalentGridDefinitions == null) {
			TalentGridDefinitions = new BotUtils.JsonConverter("DestinyTalentGridDefinition.json", BungieHashDefines.class).getJsonObject();
		}
		return String.valueOf(TalentGridDefinitions.getJSONArray(talentGridHash).getJSONObject(node).getJSONArray("s").getBigInteger(stepIndex));
	}
	
	public static boolean isHashDangerous(String hash) {
		switch (hash) {
		// List of Dangerous Perks
		case "3125734432": /* Rangefinder */           case "2399787380": /* Unstoppable */         case "2110331143": /* "Rifled Barrel" */
		case "386636896" : /* "Luck in the Chamber" */ case "3528431156": /* "Hidden Hand" */       case "855592488" : /* "Unflinching" */
		case "2806121217": /* "Outlaw" */              case "2479685401": /* "Snapshot" */          case "3058480256": /* "High Caliber Rounds" */
		case "265561391" : /* "Counterbalance" */      case "868135889" : /* "Full Auto" */         case "838935487" : /* "Third Eye" */
		case "1085914778": /* "Icarus" */              case "3687977041": /* "Rescue Mag" */        case "1628173634": /* "Grenades and Horseshoes" */
		case "4031916258": /* "Tracking" */            case "3811945819": /* Lightning Grenade */   case "2803404526": /* Aftershocks */
		case "4161884601": /* Shoulder Charge */       case "2707746729": /* Aftermath */           case "2239884867": /* Amplify */
		case "893232146" : /* Increased Control */     case "1324739096": /* Perfect Balance */     case "2133887516": /* Mulligan */
		case "1419200282": /* Persistance */           case "2164126547": /* Eye of the storm */    case "3967906128": /* Keeper of the pack */
		case "35631069"  : /* Plan C */                case "3561707758": /* Str of the Ram */      case "4168601465": /* Skorri Harmony */
		case "3861334725": /* The whisper */           case "2786446346": /* Hungering Void */      case "1435119775": /* Starfire Protocol */
		case "2464979084": /* Universal Remote*/       case "1123695440": /* Mida */                case "3614286088": /* NLB */ 
		case "453334051" : /* Braced Frame */          case "3409718360": /* QuickDraw */           case "1729865861": /* Longview */
		case "788826872" : /* ShortGaze*/              case "186107093" : /* AGG */                 case "1644354530": /* Sword Strike */
		case "1402025913": /* Last Word */             case "3359693707": /* Explosive Rounds */    case "904218236" : /* Zen Moment */
		case "941706941" : /* Tac */                   case "1288562798": /* no backpack */         case "3985037411": /* Force Multiplier */
		case "1373548822": /* Elusive Shadow */        case "212007830" : /* Rapid Cooldown */      case "3240940051": /* NBBL 1 */
		case "3757369523": /* NBBL 2 */				   case "3844755601": /* Flashbang */			case "79448980"  : /* Pulse Grenade */
		case "2880143637": /* Magnetic Grenade */	   case "332456255" : /* Spike Grenade */       case "1701872319": /* Suppressor Grenade */
		case "162969499" : /* Juggernaut */            case "679801967" : /* Transfusion */	        case "2777454294": /* Headstrong */
		case "3122403418": /* Increased Height */	   case "441640956" : /* Shockwave */		    case "618747093" : /* Overload */
		case "141946841" : /* Discharge */			   case "3943586113": /* Armor of Light */		case "1140240393": /* Blessing Of Light */
		case "2392353811": /* Weapons of Light */      case "4159561638": /* Gift of Light */	    case "1219909731": /* Unbreakable */
		case "4189296612": /* War Machine */	       case "3515582023": /* Catapult */		    case "2921757985": /* Bastion */
		case "582778565" : /* Gift of the Void */      case "4175262968": /* Relentless */			case "478012927" : /* Illuminated */
		case "4182145704": /* Iron Harvest */		   case "3395472529": /* Untouchable */			case "3975810408": /* Forge Master */
		case "329796776" : /* Scorched Earth */ 	   case "1329601567": /* Suncharge */			case "562560721" : /* Melting Point */
		case "505818755" : /* Stoke the Forge */	   case "1090584231": /* Thermal Vent */	    case "3255243428": /* Explosive Pyre */
		case "1754924561": /* Flameseeker */		   case "2098505219": /* Fleetfire */			case "346180023" : /* Cauterize */
		case "15930289"  : /* Fire Keeper */		   case "65858584"  : /* Simmering Flames */	case "3632189236": /* Lockdown */
		case "2845051978": /* Ice Breaker */		   case "2995804739": /* Fusion Grenade */ 	    
			return true;
		default:
			return false;
		}
	}
}
