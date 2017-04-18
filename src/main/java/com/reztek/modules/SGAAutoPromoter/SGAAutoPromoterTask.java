package com.reztek.modules.SGAAutoPromoter;

import java.awt.Color;

import com.reztek.base.Taskable;
import com.reztek.modules.GuardianControl.Guardian;
import com.reztek.modules.SGAAutoPromoter.SGARankDefines.SGARank;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;

public class SGAAutoPromoterTask extends Taskable {

	private SGAAutoPromoterCommands p_sgaAutoPromoter = null;
	
	public SGAAutoPromoterTask(SGAAutoPromoterCommands sgaAutoPromoter) {
		super();
		p_sgaAutoPromoter = sgaAutoPromoter;
	}
	
	protected SGAAutoPromoterCommands getSGAAutoPromoterCommands() {
		return p_sgaAutoPromoter;
	}
	
	@Override
	public void runTask() {
		runPromotions();
	}
	
	public void runPromotions() {
		for (Member m : getSGAAutoPromoterCommands().getSGAGuild().getMembers()) {
			if ( (m.getOnlineStatus() == OnlineStatus.ONLINE || m.getOnlineStatus() == OnlineStatus.IDLE) && !m.getUser().isBot() ) {
				Guardian g = Guardian.guardianFromNickname(m.getEffectiveName());
				if (g != null) {
					SGARank rank = SGARankDefines.GetRankForGuardian(g);
					if (SGARankDefines.ShouldUpgradeToRank(m, rank)) {
						getSGAAutoPromoterCommands().getSGAGuild().getController().addRolesToMember(m, getSGAAutoPromoterCommands().getSGAGuild().getRoleById(rank.getRankId())).queue();
						EmbedBuilder eb = new EmbedBuilder();
						eb.setDescription(MessageBuilder.MentionType.EVERYONE + " ** ATTENTION ** -- " + m.getEffectiveName() + " THROUGH HARD WORK HAS PROGRESSED TO RANK *" + rank.getRankTitle() + "* -- ** ATTENTION **");
						eb.setColor(Color.GREEN);
						getSGAAutoPromoterCommands().getSGACourtyardChannel().sendMessage(eb.build()).queue();
					}
				}
			}
		}
	}

}
