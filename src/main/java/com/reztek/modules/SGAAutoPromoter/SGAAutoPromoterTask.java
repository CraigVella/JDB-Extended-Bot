package com.reztek.modules.SGAAutoPromoter;

import com.reztek.base.Taskable;
import com.reztek.modules.GuardianControl.Guardian;
import com.reztek.modules.SGAAutoPromoter.SGARankDefines.SGARank;

import net.dv8tion.jda.core.EmbedBuilder;
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
						eb.setDescription("```markdown\n[ATTENTION][ATTENTION][ATTENTION][ATTENTION][ATTENTION]\n\n"
								        + g.getName() + " HAS PROGRESSED TO RANK (" + rank.getRankTitle() + ")"
										+ "\n\n[ATTENTION][ATTENTION][ATTENTION][ATTENTION][ATTENTION]```");
						eb.setColor(getSGAAutoPromoterCommands().getSGAGuild().getRoleById(rank.getRankId()).getColor());
						eb.setFooter("Congratulations Guardian", "https://s-media-cache-ak0.pinimg.com/736x/15/fc/63/15fc63d39f85b5c73b286a58781645ae.jpg");
						getSGAAutoPromoterCommands().getSGACourtyardChannel().sendMessage(eb.build()).queue();
					}
				}
			}
		}
	}

}
