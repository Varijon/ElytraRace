package com.varijon.tinies.ElytraRace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import com.varijon.tinies.ElytraRace.object.ElytraCourse;
import com.varijon.tinies.ElytraRace.object.ElytraRaceData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.UsernameCache;

public class ElytraRaceCommand extends CommandBase {

	private List aliases;
	
	public ElytraRaceCommand()
	{
	   this.aliases = new ArrayList();
	   this.aliases.add("elr");
	   this.aliases.add("elytrarace");
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "elytrarace";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "elytrarace";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if(args.length == 0)
		{
			sendCommandOptionHelp(sender);
			return;
		}
		if(args[0].equals("leaderboard"))
		{
			if(sender.canUseCommand(4, "elytrarace.leaderboard"))
			{
				if(args.length == 2 || args.length == 3)
				{
					int page = 1;
					if(args.length == 3)
					{
						if(!NumberUtils.isNumber(args[2]))
						{
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /elytrarace leaderboard courseID <page>"));	
							return;
						}
						else
						{
							page = Integer.parseInt(args[2]);
							if(page < 1)
							{
								sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /elytrarace leaderboard courseID <page>"));	
								return;
							}
						}
					}
					ElytraCourse course = RaceDataManager.getElytraCourse(args[1]);
					if(course != null)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Times for " + TextFormatting.GOLD + course.getCourseName()));
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "------------------------------"));
						ArrayList<String> lstBoard = new ArrayList<>();
						

						Collections.sort(course.getLstElytraRaceData());
						
						for(ElytraRaceData data : course.getLstElytraRaceData())
						{
//							if(!data.isHasFinished())
//							{
//								continue;
//							}
							if(data.getRaceTime() < 1)
							{
								continue;
							}
							String playerName = UsernameCache.getLastKnownUsername(data.getPlayerUUID());

							if(playerName == null)
							{
								playerName = "Someone";
							}
							lstBoard.add(TextFormatting.GOLD + playerName + TextFormatting.GRAY + " -- " + TextFormatting.GREEN + DurationFormatUtils.formatDuration(data.getRaceTime(),"mm'm 'ss's 'SS'ms'", true));
						}
						
						for(int x = 0; x < lstBoard.size(); x++)
						{
							if(x >= (page-1) * 10 && x < page * 10)
							{
								sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "" + (x+1) + ". " + lstBoard.get(x)));
							}
						}
						
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "------------------------------"));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Page: " + TextFormatting.GREEN + page));
						return;
					}
					else
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Course not found!"));
						return;
					}
				}
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /elytrarace leaderboard courseID <page>"));
				return;
			}
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have permission to use this command"));
			return;
		}
		if(args[0].equals("clear"))
		{
			if(sender.canUseCommand(4, "elytrarace.clear"))
			{
				if(args.length == 2)
				{
					ElytraCourse course = RaceDataManager.getElytraCourse(args[1]);
					if(course != null)
					{
						course.clearElytraRaceData();
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Cleared data for: " + TextFormatting.GOLD + course.getCourseID()));
						RaceDataManager.writeElytraRaceData(course);
						return;
					}
					else
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Course not found!"));
						return;
					}
				}
				if(args.length == 3)
				{
					ElytraCourse course = RaceDataManager.getElytraCourse(args[1]);
					if(course != null)
					{

						UUID playerUUID = server.getServer().getPlayerProfileCache().getGameProfileForUsername(args[2]).getId();

						if(playerUUID != null)
						{
							ElytraRaceData raceData = course.getPlayerRaceData(playerUUID);
							if(raceData != null)
							{
								raceData.setHasFinished(false);
								raceData.clearRings();
								raceData.setFinishTime(System.currentTimeMillis());
								raceData.setStartTime(System.currentTimeMillis());
								raceData.setRaceTime(0);
								raceData.setNumberAttempts(0);
								sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Cleared data for: " + TextFormatting.GOLD + args[2] + TextFormatting.GREEN + " on " + TextFormatting.GOLD + course.getCourseID()));	
								RaceDataManager.writeElytraRaceData(course);
								return;
							}
							else
							{
								sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player has no course data!"));									
								return;
							}
						}
						else
						{
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found!"));						
						}
						return;
					}
					else
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Course not found!"));
						return;
					}
				}
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /elytrarace clear courseID [player]"));					
				return;
			}
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have permission to use this command"));
			return;
		}
		sendCommandOptionHelp(sender);
	    return;
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) 
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) 
	{
		if(args.length == 1)
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			lstTabComplete.add("leaderboard");
			if(sender.canUseCommand(4, "elytrarace.clear"))
			{
				lstTabComplete.add("clear");				
			}
			return CommandBase.getListOfStringsMatchingLastWord(args, lstTabComplete);
		}
		if(args.length == 2)
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			for(ElytraCourse course : RaceDataManager.lstElytraCourse)
			{
				lstTabComplete.add(course.getCourseID());
			}
			return CommandBase.getListOfStringsMatchingLastWord(args, lstTabComplete);
		}
		
		return Collections.emptyList();
	}
	private void sendCommandOptionHelp(ICommandSender sender)
	{

		sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Elytra Race Command Options:"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/elytrarace leaderboard " + TextFormatting.GOLD + "- Check leaderboard"));
		if(sender.canUseCommand(4, "elytrarace.clear"))
		{
			sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/elytrarace clear " + TextFormatting.GOLD + "- Clear course or player data"));
		}
	}
}
