package com.varijon.tinies.ElytraRace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.varijon.tinies.ElytraRace.object.ElytraCourse;
import com.varijon.tinies.ElytraRace.object.ElytraRaceData;

import ibxm.Player;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager.PlayerOrderedLoadingCallback;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class ElytraRaceHandler 
{
	int tickCount = 0;
	MinecraftServer server;
	PlayerLocationStorage playerStorage;
	
	
	public ElytraRaceHandler() 
	{
		server = FMLCommonHandler.instance().getMinecraftServerInstance();	
		playerStorage = new PlayerLocationStorage();
	}
	

//	@SubscribeEvent
//	public void onItemPickup (ItemPickupEvent event)
//	{
//		if(event.isCanceled())
//		{
//			return;
//		}
//		if(event.player == null)
//		{
//			return;
//		}
//		if(event.player.isCreative())
//		{
//			return;
//		}
//		ItemStack raceCard = getRaceCard(event.player.inventory.mainInventory);
//		if(raceCard == null)
//		{
//			return;
//		}
//		NBTTagCompound stampTags = raceCard.getTagCompound();
//		if(!stampTags.hasKey("player"))
//		{
//			stampTags.setString("player", event.player.getUniqueID().toString());
//			stampTags.setInteger("ringCount", 0);
//			raceCard.setTagCompound(stampTags);
//			updateStampCardDisplay(raceCard, (EntityPlayerMP) event.player);
//		}
//		
//	}
	
	@SubscribeEvent
	public void onWorldTick (WorldTickEvent event)
	{
		try
		{
			if(event.phase != Phase.END)
			{
				return;
			}
			playerStorage.purgeOldLocations();
//			for(EntityPlayerMP player : server.getPlayerList().getPlayers())
//			{
//				PlayerLocation playerLoc = playerStorage.getPlayerLocation(player);
//				if(playerLoc == null)
//				{
//					playerLoc = playerStorage.addPlayerLocation(player);
//				}
//				playerLoc.tickAge++;
//				if(playerLoc.oldPos.distanceTo(player.getPositionVector()) > 80)
//				{
//					playerLoc.oldPos = player.getPositionVector();
//					playerLoc.lstLocs.clear();
//					playerLoc.tickAge = 0;
//				}
//				playerLoc.lstLocs.add(player.getPositionVector());
//				if(playerLoc.tickAge > 20)
//				{
//					playerLoc.oldPos = playerLoc.lstLocs.get(playerLoc.getLstLocs().size()-1);
//					playerLoc.tickAge = 0;
//					playerLoc.lstLocs.clear();
//				}
//				
//				
//			}
			if(event.world.getWorldInfo().getWorldName().equals("world"))
			{
				tickCount++;
				if(tickCount > 10)
				{
					for(EntityPlayerMP player : server.getPlayerList().getPlayers())
					{
						PlayerLocation playerLoc = playerStorage.getPlayerLocation(player);
						if(playerLoc == null)
						{
							playerLoc = playerStorage.addPlayerLocation(player);
						}
						if(playerLoc.oldPos.distanceTo(player.getPositionVector()) > 50)
						{
							playerLoc.oldPos = player.getPositionVector();
						}
						playerLoc.lstLocs.add(player.getPositionVector());
						playerLoc.oldPos = playerLoc.lstLocs.get(playerLoc.getLstLocs().size()-1);
						playerLoc.lstLocs.clear();
						
					}	
					tickCount = 0;
				}
			}
			WorldServer world = (WorldServer) event.world;
			
			for(Entity entity : event.world.loadedEntityList)
			{
				if(entity instanceof EntityArmorStand)
				{
					EntityArmorStand stand = (EntityArmorStand) entity;
					NBTTagCompound standNBT = stand.getEntityData();
					if(!standNBT.hasKey("isRingDetector"))
					{
						continue;
					}
					double range = 0;
					if(standNBT.hasKey("range"))
					{
						range = standNBT.getDouble("range");
					}
					int maxRings = 0;
					if(standNBT.hasKey("maxRings"))
					{
						maxRings = standNBT.getInteger("maxRings");
					}
					boolean allowRestart = false;
					if(standNBT.hasKey("allowRestart"))
					{
						allowRestart = standNBT.getBoolean("allowRestart");
					}
					double penaltyTime = 0;
					if(standNBT.hasKey("penaltyTime"))
					{
						penaltyTime = standNBT.getDouble("penaltyTime");
					}
					
					String ringName = "";
					if(standNBT.hasKey("ringName"))
					{
						ringName = standNBT.getString("ringName");
					}
					else
					{
						ringName = stand.getUniqueID().toString();
					}
					boolean startRing = false;
					String courseID = "";
					if(standNBT.hasKey("courseID"))
					{
						courseID = standNBT.getString("courseID");
					}
					String courseName = "";
					ElytraCourse elytraCourse = null;
					if(standNBT.hasKey("startRing"))
					{
						if(standNBT.hasKey("courseID") && standNBT.hasKey("courseName"))
						{
							courseID = standNBT.getString("courseID");
							courseName = standNBT.getString("courseName");
							elytraCourse = RaceDataManager.getElytraCourse(courseID);
							if(elytraCourse == null)
							{
								elytraCourse = RaceDataManager.addElytraCourse(new ElytraCourse(courseID, courseName, penaltyTime, maxRings, 0, new ArrayList<>()));
							}
							elytraCourse.setCourseName(courseName);
							elytraCourse.setPenaltyTime(penaltyTime);
							elytraCourse.setMaxRings(maxRings);

							startRing = true;
						}
						if(standNBT.hasKey("attemptLimit"))
						{
							if(standNBT.hasKey("courseID") && standNBT.hasKey("courseName"))
							{
								courseID = standNBT.getString("courseID");
								courseName = standNBT.getString("courseName");
								elytraCourse = RaceDataManager.getElytraCourse(courseID);
								if(elytraCourse == null)
								{
									elytraCourse = RaceDataManager.addElytraCourse(new ElytraCourse(courseID, courseName, penaltyTime, maxRings,0, new ArrayList<>()));
								}
								elytraCourse.setAttemptLimit(standNBT.getInteger("attemptLimit"));
							}
						}
					}
					boolean finishRing = false;
					if(standNBT.hasKey("finishRing"))
					{
						finishRing = true;
					}
//					for(EntityPlayerMP player : getPlayersNearby((WorldServer) stand.getEntityWorld(), range, stand))
//					{
//						ItemStack raceCard = getRaceCard(player.inventory.mainInventory);
//						if(raceCard == null)
//						{
//							continue;
//						}
//						NBTTagCompound stampTags = raceCard.getTagCompound();
//						if(!stampTags.getString("player").equals(player.getUniqueID().toString()))
//						{
//							continue;
//						}
//						raceCard = addStampToNBT(raceCard, player, ringName, points);
//						if(raceCard == null)
//						{	
//							continue;
//						}
//						updateStampCardDisplay(raceCard, player);
//					}
					AxisAlignedBB boundingBox = getArmorStandBB(stand, range);
					if(boundingBox == null)
					{
						continue;
					}
					for(PlayerLocation playerLoc : playerStorage.lstPlayerLocation)
					{
						AxisAlignedBB playerBB = getPlayerBB(playerLoc);
						if(playerBB == null)
						{
							continue;
						}
						if(!boundingBox.intersects(playerBB))
						{
							continue;
						}
						elytraCourse = RaceDataManager.getElytraCourse(courseID);
						if(elytraCourse == null)
						{
							continue;
						}
						if(elytraCourse.getAttemptLimit() == 0)
						{
							continue;
						}
						ElytraRaceData raceData = elytraCourse.getPlayerRaceData(playerLoc.playerUUID);
						if(raceData == null)
						{
							raceData = elytraCourse.addElytraRaceData(new ElytraRaceData(playerLoc.playerUUID, new ArrayList<>(), System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, false));
						}
						if(raceData.isHasFinished() && !allowRestart)
						{
							continue;
						}
						if(!raceData.isHasFinished() && finishRing)
						{
							EntityPlayerMP player = playerLoc.getPlayer();
							long finalPenaltyTime = (long) (elytraCourse.getPenaltyTime() * 1000 * (elytraCourse.getMaxRings() - raceData.getObtainedRings().size()));
							raceData.setFinishTime(System.currentTimeMillis() + finalPenaltyTime);
							raceData.setHasFinished(true);
							Collections.sort(elytraCourse.getLstElytraRaceData());
							
							if(elytraCourse.getAttemptLimit() != -1)
							{
								raceData.setNumberAttempts(raceData.getNumberAttempts() + 1);
								player.sendMessage(new TextComponentString(TextFormatting.RED + "[" + TextFormatting.RED + raceData.getNumberAttempts() + "/" + elytraCourse.getAttemptLimit() + "] " + TextFormatting.GREEN + "You finished " + TextFormatting.GOLD +  elytraCourse.getCourseID() + TextFormatting.GREEN  +  " in: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(raceData.getFinishTime() - raceData.getStartTime(),"mm'm 'ss's 'SS'ms'", true) + TextFormatting.GREEN +", in position " + TextFormatting.GOLD + elytraCourse.getRaceDataIndex(raceData) + TextFormatting.GREEN + "!"));
							}
							else
							{
								player.sendMessage(new TextComponentString(TextFormatting.GREEN + "You finished " + TextFormatting.GOLD +  elytraCourse.getCourseID() + TextFormatting.GREEN  + " in: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(raceData.getFinishTime() - raceData.getStartTime(),"mm'm 'ss's 'SS'ms'", true) + TextFormatting.GREEN +", in position " + TextFormatting.GOLD + elytraCourse.getRaceDataIndex(raceData) + TextFormatting.GREEN + "!"));
							}					
							
							for(EntityPlayerMP playerOnline : player.getServer().getPlayerList().getPlayers())
							{
								if(playerOnline.canUseCommand(4, "elytrarace.seetimes"))
								{
									if(elytraCourse.getAttemptLimit() == -1)
									{
										playerOnline.sendMessage(new TextComponentString(TextFormatting.GOLD + player.getName() + TextFormatting.YELLOW + " finished " + TextFormatting.GOLD +  elytraCourse.getCourseID() +  TextFormatting.YELLOW + " in: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(raceData.getFinishTime() - raceData.getStartTime(),"mm'm 'ss's 'SS'ms'", true) + TextFormatting.YELLOW +", in position " + TextFormatting.GOLD + elytraCourse.getRaceDataIndex(raceData) + TextFormatting.YELLOW + "!"));
									}
									else
									{
										playerOnline.sendMessage(new TextComponentString(TextFormatting.RED + "[" + TextFormatting.RED + raceData.getNumberAttempts() + "/" + elytraCourse.getAttemptLimit() + "] " + TextFormatting.GOLD + player.getName() + TextFormatting.YELLOW + " finished " + TextFormatting.GOLD +  elytraCourse.getCourseID() +  TextFormatting.YELLOW + " in: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(raceData.getFinishTime() - raceData.getStartTime(),"mm'm 'ss's 'SS'ms'", true) + TextFormatting.YELLOW +", in position " + TextFormatting.GOLD + elytraCourse.getRaceDataIndex(raceData) + TextFormatting.YELLOW + "!"));
									}
								}
							}
							
							if(elytraCourse.getMaxRings() != raceData.getObtainedRings().size())
							{
								player.sendMessage(new TextComponentString(TextFormatting.RED + "You missed " + TextFormatting.GOLD + (elytraCourse.getMaxRings() - raceData.getObtainedRings().size()) + TextFormatting.RED + " rings, resulting in " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(finalPenaltyTime,"mm'm 'ss's 'SS'ms'", true) + TextFormatting.RED + " penalty!"));								
							}
							if(raceData.getFinishTime() - raceData.getStartTime() > raceData.getRaceTime())
							{
								player.sendMessage(new TextComponentString(TextFormatting.RED + "Previous time of " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(raceData.getRaceTime(),"mm'm 'ss's 'SS'ms'", true) +  TextFormatting.RED + " did not improve!"));
							}
							else
							{
								player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Beat personal record of: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(raceData.getRaceTime(),"mm'm 'ss's 'SS'ms'", true) + TextFormatting.GREEN + "!"));
								raceData.setRaceTime(raceData.getFinishTime() - raceData.getStartTime());
							}
							
							world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, stand.getPositionVector().x,stand.getPositionVector().y, stand.getPositionVector().z, 30, range, range, range, 0, new int[]{});
							world.playSound(null, stand.getPosition(), SoundEvents.ENTITY_FIREWORK_LARGE_BLAST_FAR, SoundCategory.BLOCKS, 1f, 1f);
							RaceDataManager.writeElytraRaceData(elytraCourse);
							continue;
						}
						if(!startRing)
						{
							if(raceData.getObtainedRings().size() == elytraCourse.getMaxRings())
							{
								continue;
							}
							if(!raceData.addRingToData(stand.getUniqueID().toString()))
							{	
								continue;
							}
							world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, stand.getPositionVector().x,stand.getPositionVector().y, stand.getPositionVector().z, 30, range, range, range, 0, new int[]{});
							world.playSound(null, stand.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);
						}
						else
						{
							if(!allowRestart && raceData.isHasFinished())
							{
								continue;
							}
							if(!allowRestart && raceData.getObtainedRings().size() > 0)
							{
								continue;
							}
							if(elytraCourse.getAttemptLimit() == 0)
							{
								continue;
							}
							if(raceData.getNumberAttempts() >= elytraCourse.getAttemptLimit() && elytraCourse.getAttemptLimit() != -1)
							{
								continue;
							}
							raceData.setHasFinished(false);
							raceData.clearRings();
							raceData.setStartTime(System.currentTimeMillis());
							raceData.setFinishTime(System.currentTimeMillis());
						}
					}
					//bounding box check for players around it every tick
					//if player is inside structurevoid do the card stuff
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public AxisAlignedBB getPlayerBB(PlayerLocation playerLoc)
	{
		AxisAlignedBB box = null;
		if(playerLoc.oldPos.distanceTo(playerLoc.getPlayer().getPositionVector()) > 50)
		{
			playerLoc.oldPos = playerLoc.getPlayer().getPositionVector();
			return null;
		}
		Vec3d checkPos1 = playerLoc.getOldPos();
		Vec3d checkPos2 = playerLoc.getPlayer().getPositionVector();
		Vec3d minPos = checkPos1.addVector(-0.3, -0.3,-0.3);
		Vec3d maxPos = checkPos2.addVector(0.3, 0.3, 0.3);
		box = new AxisAlignedBB(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z);
		
		return box;
	}
	public AxisAlignedBB getArmorStandBB(EntityArmorStand stand, double range)
	{
		AxisAlignedBB box = null;
		Vec3d checkPos = stand.getPositionVector();
		if(stand.rotationYaw == 0 || stand.rotationYaw == 180)
		{
			Vec3d minPos = checkPos.addVector(-range, -range, -0.3);
			Vec3d maxPos = checkPos.addVector(range, range, 0.3);
			box = new AxisAlignedBB(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z);
		}
		if(stand.rotationYaw == 90 || stand.rotationYaw == -90)
		{
			Vec3d minPos = checkPos.addVector(-0.3, -range, -range);
			Vec3d maxPos = checkPos.addVector(0.3, range, range);
			box = new AxisAlignedBB(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z);
		}
		return box;
	}
	
	public List<EntityPlayerMP> getPlayersNearby(WorldServer world, double range, EntityArmorStand stand)
	{
		Vec3d checkPos = stand.getPositionVector();
		Vec3d minPos = checkPos.addVector(-range, -range, -range);
		Vec3d maxPos = checkPos.addVector(range, range, range);
		List<EntityPlayerMP> lstPlayers = world.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z));
		return lstPlayers;
	}
	public static ItemStack getRaceCard(Iterable<ItemStack> itemList)
	{
		if(itemList != null)
		{
			for (ItemStack item : itemList) 
			{
				if(item != null)
				{
					if(item.hasTagCompound())
					{
						NBTTagCompound nbt = item.getTagCompound();
						if(nbt.hasKey("isRaceCard"))
						{
							return item;					
						}
					}
				}
			}
		}
		return null;
	}
	
	public static ItemStack addStampToNBT(ItemStack item, EntityPlayerMP player, String stampName, int stampPoints)
	{
		NBTTagCompound tags = item.getTagCompound();
		
		if(!tags.hasKey("stampList"))
		{
			tags.setTag("stampList", new NBTTagList());
		}
		NBTTagList stampList = tags.getTagList("stampList",Constants.NBT.TAG_COMPOUND);
		for(NBTBase nbt : stampList)
		{
			NBTTagCompound nbtTag = (NBTTagCompound) nbt;
			if(nbtTag.getString("stampName").equals(stampName))
			{
				return null;
			}
		}
		
		NBTTagCompound newTag = new NBTTagCompound();
		newTag.setString("stampName", stampName);
		newTag.setInteger("stampPoints", stampPoints);
		stampList.appendTag(newTag);
		
		item.setTagCompound(tags);
		return item;
	}
	
	public static ItemStack updateStampCardDisplay(ItemStack item, EntityPlayerMP player)
	{
		NBTTagCompound tags = item.getTagCompound();
		
		if(!tags.hasKey("display"))
		{
			tags.setTag("display", new NBTTagCompound());
		}
				
		NBTTagList loreList = new NBTTagList();
		loreList.appendTag(new NBTTagString(TextFormatting.GRAY + "Player Name: " + TextFormatting.YELLOW + player.getName()));
		
//		int totalPointCount = 0;
		int totalRings = 0;
		//loreList.appendTag(new NBTTagString(TextFormatting.AQUA + "Rings:"));

		if(tags.hasKey("ringCount"))
		{
			totalRings = tags.getInteger("ringCount");
		}


		loreList.appendTag(new NBTTagString(TextFormatting.RED + "Ring Count:" + TextFormatting.DARK_AQUA + " --- " + TextFormatting.GREEN + totalRings + "/" + tags.getInteger("maxRings")));
		loreList.appendTag(new NBTTagString(TextFormatting.RED + "Missing Ring Penalty: " + TextFormatting.YELLOW + DurationFormatUtils.formatDuration((long) tags.getDouble("penaltyTime") * 1000l,"s' seconds'", true)));
		
		if(tags.hasKey("finishTime"))
		{
			loreList.appendTag(new NBTTagString(TextFormatting.RED + "Race Time:" + TextFormatting.DARK_AQUA + " --- " + TextFormatting.GREEN + DurationFormatUtils.formatDuration(tags.getLong("finishTime") - tags.getLong("startTime"),"mm'm 'ss's 'SS'ms'", true)));			
		}
		
//		loreList.appendTag(new NBTTagString(TextFormatting.RED + "Total Points:" + TextFormatting.DARK_AQUA + " --- " + TextFormatting.GREEN + totalPointCount + "pts"));

		tags.getCompoundTag("display").setTag("Lore", loreList);
					
		item.setTagCompound(tags);
		return item;
	}
}
