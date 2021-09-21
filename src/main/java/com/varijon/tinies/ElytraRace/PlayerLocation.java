package com.varijon.tinies.ElytraRace;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayerLocation 
{
	Vec3d oldPos;
	int tickAge;
	UUID playerUUID;
	ArrayList<Vec3d> lstLocs;
	public PlayerLocation(Vec3d oldPos, int tickAge, UUID playerUUID) {
		super();
		this.oldPos = oldPos;
		this.tickAge = tickAge;
		this.playerUUID =  playerUUID;
		this.lstLocs = new ArrayList<>();
		lstLocs.add(oldPos);
	}
	public Vec3d getOldPos() {
		return oldPos;
	}
	public void setOldPos(Vec3d oldPos) {
		this.oldPos = oldPos;
	}
	public int getTickAge() {
		return tickAge;
	}
	public void setTickAge(int tickAge) {
		this.tickAge = tickAge;
	}
	public EntityPlayerMP getPlayer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerUUID);
	}
	public void setPlayer(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
	public ArrayList<Vec3d> getLstLocs() {
		return lstLocs;
	}
	public void setLstLocs(ArrayList<Vec3d> lstLocs) {
		this.lstLocs = lstLocs;
	}
	
	
	
	
}
