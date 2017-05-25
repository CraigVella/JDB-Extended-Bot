package com.reztek.modules.RumbleCommands;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.reztek.SGAExtendedBot;
import com.reztek.Base.CommandModule;
import com.reztek.Global.GlobalDefs;
import com.reztek.modules.GuardianControl.Guardian;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RumbleCommands extends CommandModule {
	
	public static final String PLUGIN_ID = "RUMBLECOMMANDS";
	
	protected RumbleList p_rumbleList = null;

	public RumbleCommands() {
		super(PLUGIN_ID);
		setModuleNameAndAuthor("Rumble", "ChaseHQ85");
		addCommand(new String[] {
				"rumblelist-importcsv", "rumblelist", "rumblelist-csv", 
				"rumblelistgold", "rumblelistsilver", "rumblelistbronze", "rumblelistwood",
				"rumblerefresh", "rumbleaddtolist-ps", "rumbleaddtolist-xb", "rumbleaddtolist",
				"rumbleremovefromlist-ps", "rumbleremovefromlist-xb", "rumbleremovefromlist"
		});
		// I have a task!
		p_rumbleList = new RumbleList();
		p_rumbleList.setTaskName("RumbleList Refresh");
		p_rumbleList.setTaskDelay(90);
		SGAExtendedBot.GetBot().addTask(p_rumbleList);
	}

	@Override
	public void processCommand(String command, String args, MessageReceivedEvent mre) {
		
		/*String[] splitArg = {""};
		if (args != null) {
			splitArg = args.split(" ");
		}*/
		
		switch (command) {
			case "rumblelist-importcsv":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					rumbleImportCSV(mre);
				}
			break;
			case "rumblelist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					rumbleList(mre.getChannel(), RumbleList.RUMBLE_ALL, Color.WHITE);
				}
				break;
			case "rumblelist-csv":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					rumbleListCSV(mre);
				} 
				break;
			case "rumblelistgold":
				rumbleList(mre.getChannel(), RumbleList.RUMBLE_GOLD, new Color(212,175,55));
				break;
			case "rumblelistsilver":
				rumbleList(mre.getChannel(), RumbleList.RUMBLE_SILVER, new Color(192,192,192));
				break;
			case "rumblelistbronze":
				rumbleList(mre.getChannel(), RumbleList.RUMBLE_BRONZE, new Color(205, 127, 50));
				break;
			case "rumblelistwood":
				rumbleList(mre.getChannel(), RumbleList.RUMBLE_WOOD, new Color(160, 82, 45));
				break;
			case "rumblerefresh":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					rumbleRefresh(mre.getChannel());
				}
				break;
			case "rumbleaddtolist-ps":
			case "rumbleaddtolist-xb":
			case "rumbleaddtolist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					if (args == null) {
						sendHelpString(mre, "!rumbleAddToList[or !rumbleAddToList-ps or !rumbleAddToList-xb] PlayerNameHere");
					} else {
						rumbleAddToList(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
					}
				}
				break;
			case "rumbleremovefromlist-ps":
			case "rumbleremovefromlist-xb":
			case "rumbleremovefromlist":
				if (mre.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
					if (args == null) {
						sendHelpString(mre, "!rumbleRemoveFromList[or !rumbleRemoveFromList-ps or !rumbleRemoveFromList-xb] PlayerNameHere");
					} else {
						rumbleRemoveFromList(mre.getChannel(), args, Guardian.platformCodeFromCommand(command));
					}
				}
				break;
		}
	}
	
	protected void rumbleImportCSV(MessageReceivedEvent mre) {
		if (mre.getMessage().getAttachments().size() < 1) {
			mre.getChannel().sendMessage("Hmm... did you forget to attach a file?").queue();
		} else {
			if (mre.getMessage().getAttachments().get(0).getFileName().split("\\.")[1].equalsIgnoreCase("csv")) {
				// its a csv :)
				String tmpName = String.valueOf(System.currentTimeMillis()) + "-" + mre.getAuthor().getName() + "-tmp.tmp";
				File csvFile = new File((GlobalDefs.BOT_DEV ? GlobalDefs.DEV_TMP_LOCATION : GlobalDefs.TMP_LOCATION) + tmpName);
				try {
					csvFile.deleteOnExit();
					mre.getMessage().getAttachments().get(0).download(csvFile);
					CSVParser parser = CSVParser.parse(csvFile, StandardCharsets.UTF_8, CSVFormat.EXCEL.withHeader());
					Set<String> headers = parser.getHeaderMap().keySet();
					if (headers.contains("playername") && headers.contains("platform") && headers.contains("show")) {
						List<CSVRecord> records = parser.getRecords();
						mre.getChannel().sendMessage("Import of **" + records.size() + "** Record(s) starting...").queue();
						for (CSVRecord record : records) {
							if (record.get("show").equalsIgnoreCase("1")) {
								p_rumbleList.addPlayer(mre.getChannel(), Guardian.guardianFromName(record.get("playername"), record.get("platform")), false);
							}
						}
						mre.getChannel().sendMessage("**Import complete**").queue();
					} else {
						mre.getChannel().sendMessage("The CSV Headers must be 'playername, platform, show' fix and re-upload").queue();
					}
				} catch (IOException e) {
					mre.getChannel().sendMessage("**Error creating temporary file**");
					e.printStackTrace();
				}
			} else {
				mre.getChannel().sendMessage("Sorry, I only accept CSV's for import.").queue();
			}
		}
	}
	
	protected void rumbleListCSV(MessageReceivedEvent mre) {
		mre.getChannel().sendMessage("On it " + mre.getAuthor().getAsMention() + ", lets take this to a private chat!").queue();
		try {
			p_rumbleList.sendListCSV(mre.getAuthor().hasPrivateChannel() ? mre.getAuthor().getPrivateChannel() : mre.getAuthor().openPrivateChannel().submit().get());
		} catch (InterruptedException | ExecutionException e) {
			mre.getChannel().sendMessage("Hmm... " + mre.getAuthor().getAsMention() + ", I tried to open a private chat with you but I was denied.").queue();
		}
	}
	
	protected void rumbleList(MessageChannel mc, String indexStart, Color color) {
		p_rumbleList.showList(mc,indexStart, color);
	}
	
	protected void rumbleRefresh(MessageChannel mc) {
		p_rumbleList.refreshList(mc, true);
	}
	
	protected void rumbleRemoveFromList(MessageChannel mc, String playerName, String platform) {
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			p_rumbleList.removePlayer(mc,g);
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
	
	protected void rumbleAddToList (MessageChannel mc, String playerName, String platform) {
		Guardian g = Guardian.guardianFromName(playerName, platform);
		if (g != null) {
			if (g.getRumbleRank() == "N/A" || g.getRumbleRank() == null) {
				mc.sendMessage("Sorry " + playerName + " hasn't played enough rumble this season to be added.").queue();
			} else {
				p_rumbleList.addPlayer(mc,g,true);
			}
		} else {
			mc.sendMessage("Hmm... Cant seem to find " + playerName + ", You sure you have the right platform or spelling?").queue();
		}
	}
}
