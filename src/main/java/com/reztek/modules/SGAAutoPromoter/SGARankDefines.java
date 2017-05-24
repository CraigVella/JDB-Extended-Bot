package com.reztek.modules.SGAAutoPromoter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.reztek.modules.GuardianControl.Guardian;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class SGARankDefines {
	public static class SGARank {
		protected String _rankTitle = "Unknown";
		protected int _weight = -1;
		protected String _rankId = "Unknown";
		public String getRankTitle() {return _rankTitle;}
		public int getWeight() {return _weight;}
		public String getRankId() {return _rankId;}
		public SGARank(String rankId, String rankTitle, int weight) {
			_rankId = rankId;
			_rankTitle = rankTitle;
			_weight = weight;
		}
	}
	private static final int RANK_EVERYONE      				 = 0;
	private static final int RANK_NEW_RECRUIT				     = 1;
	private static final int RANK_FORCE_SENSITIVE_CANDIDATE      = 2;
	private static final int RANK_FORCE_BELIEVER                 = 3;
	private static final int RANK_INITIATE                       = 4;
	private static final int RANK_PADAWAN                        = 5;
	private static final int RANK_JEDI_KNIGHT                    = 6;
	private static final int RANK_JEDI_GUARDIAN                  = 7;
	private static final int RANK_JEDI_HIGH_GENERAL              = 8;
	private static final int RANK_JEDI_MASTER                    = 9;
	private static final int RANK_JEDI_GRAND_MASTER              = 10;
	private static final int RANK_JEDI_GRAND_MASTER_OF_THE_ORDER = 11;

	private static final List<SGARank> SGARanks;
	static {
		List<SGARank> sgaranks = new ArrayList<SGARank>();
		sgaranks.add(new SGARank("316965205626191884", "Everyone", RANK_EVERYONE));
		sgaranks.add(new SGARank("285837753617219584", "New Recruit", RANK_NEW_RECRUIT));
		sgaranks.add(new SGARank("254192803800678400", "Force Sensitive Candidate", RANK_FORCE_SENSITIVE_CANDIDATE));
		sgaranks.add(new SGARank("284213958301450250", "Force Believer", RANK_FORCE_BELIEVER));
		sgaranks.add(new SGARank("254192370722144256", "Initiate", RANK_INITIATE));
		sgaranks.add(new SGARank("254192251737997312", "Padawan", RANK_PADAWAN));
		sgaranks.add(new SGARank("254192217856540672", "Jedi Knight", RANK_JEDI_KNIGHT));
		sgaranks.add(new SGARank("254192171933106178", "Jedi Guardian", RANK_JEDI_GUARDIAN));
		sgaranks.add(new SGARank("284213825337556992", "Jedi High General", RANK_JEDI_HIGH_GENERAL));
		sgaranks.add(new SGARank("254192105100935168", "Jedi Master", RANK_JEDI_MASTER));
		sgaranks.add(new SGARank("265261646945976321", "Jedi Grand Master", RANK_JEDI_GRAND_MASTER));
		sgaranks.add(new SGARank("283322230577037333", "Jedi Grand Master of The Order", RANK_JEDI_GRAND_MASTER_OF_THE_ORDER));
		SGARanks = Collections.unmodifiableList(sgaranks);
	}
	
	public static List<SGARank> GetSGARankList() {
		return SGARanks;
	}
	
	public static SGARank GetRankForID(String id) {
		for (SGARank r : SGARanks) {
			if (r.getRankId().equals(id)) {
				return r;
			}
		}
		return null;
	}
	
	public static SGARank GetRankByWeight(int weight) {
		for (SGARank r: SGARanks) {
			if (r.getWeight() == weight) {
				return r;
			}
		}
		return null;
	}

	public static boolean ShouldUpgradeToRank(Member m, SGARank proposedRank) {
		int currentHighestRank = RANK_EVERYONE;
		for (Role r : m.getRoles()) {
			SGARank testRank = GetRankForID(r.getId());
			if (testRank != null) {
				if (testRank.getWeight() > currentHighestRank) {
					currentHighestRank = testRank._weight;
				}
			}
		}
		if (proposedRank.getWeight() > currentHighestRank) {
			return true;
		}
		return false;
	}
	
	public static SGARank GetRankForGuardian(Guardian g) {
		if (g == null) return null;
		SGARank returnRank = GetRankByWeight(RANK_EVERYONE);
		for (SGARank r : SGARanks) {
			try {
				switch (r._weight) {
				case RANK_NEW_RECRUIT:
						if (returnRank._weight < r._weight) {
							returnRank = r;
					}
					break;
				case RANK_FORCE_BELIEVER:
					if (Integer.valueOf(g.getRumbleRank()) <= 100000) {
							if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_INITIATE:
					if (Integer.valueOf(g.getRumbleRank()) <= 66000) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_PADAWAN:
					if (Integer.valueOf(g.getRumbleRank()) <= 22000) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_JEDI_KNIGHT:
					if (((Integer.valueOf(g.getRumbleRank()) <= 10000) && (Integer.valueOf(g.getLighthouseCount()) >= 1)) || 
							(Integer.valueOf(g.getLighthouseCount()) >= 30)) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_JEDI_GUARDIAN:
					if (((Integer.valueOf(g.getRumbleRank()) <= 3000) && (Integer.valueOf(g.getLighthouseCount()) >= 1)) || 
							(Integer.valueOf(g.getLighthouseCount()) >= 40) || 
							((Integer.valueOf(g.getRumbleRank()) <= 10000) && (Integer.valueOf(g.getLighthouseCount()) >= 15)) ) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_JEDI_HIGH_GENERAL:
					if (((Integer.valueOf(g.getRumbleRank()) <= 1500) && (Integer.valueOf(g.getLighthouseCount()) >= 1)) || 
							(Integer.valueOf(g.getLighthouseCount()) >= 50) || 
							((Integer.valueOf(g.getRumbleRank()) <= 3000) && (Integer.valueOf(g.getLighthouseCount()) >= 30)) ) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_JEDI_MASTER:
					if (((Integer.valueOf(g.getRumbleRank()) <= 1000) && (Integer.valueOf(g.getLighthouseCount()) >= 1)) || 
							(Integer.valueOf(g.getLighthouseCount()) >= 60) || 
							((Integer.valueOf(g.getRumbleRank()) <= 3000) && (Integer.valueOf(g.getLighthouseCount()) >= 50)) ) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_JEDI_GRAND_MASTER:
					if (((Integer.valueOf(g.getRumbleRank()) <= 500) && (Integer.valueOf(g.getLighthouseCount()) >= 1)) || 
							(Integer.valueOf(g.getLighthouseCount()) >= 100)) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				case RANK_JEDI_GRAND_MASTER_OF_THE_ORDER:
					if (((Integer.valueOf(g.getRumbleRank()) <= 250) && (Integer.valueOf(g.getLighthouseCount()) >= 1)) || 
							(Integer.valueOf(g.getLighthouseCount()) >= 150)) {
						if (returnRank._weight < r._weight) returnRank = r;
					}
					break;
				}
			} catch (NumberFormatException e) {
				// Number was probably "N/A" just move on...
				
			}
		}
		if (returnRank._weight == 1) {
			return null;
		}
		return returnRank;
	}
}
