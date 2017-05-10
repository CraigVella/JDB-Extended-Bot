package com.reztek.modules.SGAAutoPromoter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.reztek.SGAExtendedBot;
import com.reztek.Base.Taskable;
import com.reztek.modules.GuardianControl.Guardian;
import com.reztek.modules.SGAAutoPromoter.SGARankDefines.SGARank;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;

public class SGAAutoPromoterTask extends Taskable {

	private SGAAutoPromoterCommands p_sgaAutoPromoter = null;
	
	public SGAAutoPromoterTask(SGAAutoPromoterCommands sgaAutoPromoter, SGAExtendedBot bot) {
		super(bot);
		p_sgaAutoPromoter = sgaAutoPromoter;
	}
	
	protected SGAAutoPromoterCommands getSGAAutoPromoterCommands() {
		return p_sgaAutoPromoter;
	}
	
	@Override
	public void runTask() {
		System.out.println("Starting Promotion Check...");
		runPromotions();
		System.out.println("Promotion Check Complete!");
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
						eb.setDescription(m.getAsMention());
						try {
							eb.setImage("http://reztek.net/SGA/SGAFunctions.php?u=" + URLEncoder.encode(g.getName(), "UTF-8") + "&r=" + URLEncoder.encode(rank.getRankTitle(), "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						eb.setColor(getSGAAutoPromoterCommands().getSGAGuild().getRoleById(rank.getRankId()).getColor());
						eb.setFooter("Congratulations Guardian", "https://s-media-cache-ak0.pinimg.com/736x/15/fc/63/15fc63d39f85b5c73b286a58781645ae.jpg");
						getSGAAutoPromoterCommands().getSGACourtyardChannel().sendMessage(eb.build()).queue();
					}
				}
			}
		}
	}

}
