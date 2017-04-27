package com.reztek.modules.SGAAutoPromoter;

import java.net.URLEncoder;

import com.reztek.SGAExtendedBot;
import com.reztek.base.CommandModule;
import com.reztek.modules.SGAAutoPromoter.SGARankDefines.SGARank;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SGAAutoPromoterCommands extends CommandModule {
	
	public static final String SGA_GUILD_ID             = "252581874596184065";
	public static final String SGA_COURTYARD_CHANNEL_ID = "255514407121977344";
	
	private SGAAutoPromoterTask p_aptask = null; 
	private Guild p_sgaGuild = null;
	private MessageChannel p_sgaCourtyard = null;
	private boolean p_disabled = false;

	public SGAAutoPromoterCommands(JDA pJDA, SGAExtendedBot pbot) {
		super(pJDA, pbot, "SGAAUTOPROMOTER");
		setModuleNameAndAuthor("SGA Auto Promoter", "ChaseHQ85");
		p_sgaGuild = pJDA.getGuildById(SGA_GUILD_ID);
		if (p_sgaGuild == null) {
			System.out.println("Error Connecting To Guild - Disabling Plugin");
			p_disabled = true;
		} else {
			p_sgaCourtyard = p_sgaGuild.getTextChannelById(SGA_COURTYARD_CHANNEL_ID);
			if (p_sgaCourtyard == null) {
				System.out.println("Error Getting Courtyard Channel - Disabling Plugin");
				p_disabled = true;
			} else {
				p_aptask = new SGAAutoPromoterTask(this);
				p_aptask.setTaskDelay(30);
				getBot().addTask(p_aptask);
			}
		}
	}

	@Override
	public boolean processCommand(String command, String args, MessageReceivedEvent mre) {
		
		if (p_disabled) {
			getBot().getMessageHandler().removeCommandModule(getModuleID());
			System.out.println("SGA Auto Promoter removed due to disabled");
			return false;
		}
		
		switch (command) {
		case "runpromotions":
			if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
				mre.getChannel().sendMessage("*Kicking off promotions..*").queue();
				p_aptask.runPromotions();
				mre.getChannel().sendMessage("*Finished promotions..*").queue();
			}
			break;
		case "testmsg":
			if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
				EmbedBuilder eb = new EmbedBuilder();
				SGARank rank = SGARankDefines.GetRankForID(args);
				eb.setImage("http://reztek.net/SGA/SGAFunctions.php?u=" + URLEncoder.encode(mre.getAuthor().getName()) + "&r=" + URLEncoder.encode(rank.getRankTitle()));
				eb.setColor(getSGAGuild().getRoleById(rank.getRankId()).getColor());
				eb.setFooter("Congratulations Guardian", "https://s-media-cache-ak0.pinimg.com/736x/15/fc/63/15fc63d39f85b5c73b286a58781645ae.jpg");
				mre.getChannel().sendMessage(eb.build()).queue();
			}
			break;
		default:
			return false;
		}
		
		return true;
	}
	
	public Guild getSGAGuild() {
		return p_sgaGuild;
	}
	
	public MessageChannel getSGACourtyardChannel() {
		return p_sgaCourtyard;
	}

}
