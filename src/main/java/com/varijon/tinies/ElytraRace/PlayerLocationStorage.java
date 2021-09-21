package com.varijon.tinies.ElytraRace;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerLocationStorage 
{

	ArrayList<PlayerLocation> lstPlayerLocation;

	public PlayerLocationStorage() 
	{
		this.lstPlayerLocation = new ArrayList<>();
	}

	public ArrayList<PlayerLocation> getLstPlayerLocation() {
		return lstPlayerLocation;
	}

	public void setLstPlayerLocation(ArrayList<PlayerLocation> lstPlayerLocation) {
		this.lstPlayerLocation = lstPlayerLocation;
	}
	
	public PlayerLocation getPlayerLocation(EntityPlayerMP player)
	{
		PlayerLocation playerLoc = null;
		for(PlayerLocation playerLocation : lstPlayerLocation)
		{
			if(playerLocation.getPlayer() == null)
			{
				continue;
			}
			if(player.getUniqueID().equals(playerLocation.getPlayer().getUniqueID()))
			{
				playerLoc = playerLocation;
			}
		}
		return playerLoc;
	}
	
	public PlayerLocation addPlayerLocation(EntityPlayerMP player)
	{
		PlayerLocation playerLoc = new PlayerLocation(player.getPositionVector(), 0, player.getUniqueID());
		lstPlayerLocation.add(playerLoc);
		return playerLoc;
	}
	
	public void purgeOldLocations()
	{
		ArrayList<PlayerLocation> lstRemoval = new ArrayList<>();
		for(PlayerLocation playerLocation : lstPlayerLocation)
		{
			if(playerLocation.getPlayer() == null)
			{
				lstRemoval.add(playerLocation);
			}
		}
		for(PlayerLocation playerLocation : lstRemoval)
		{
			lstPlayerLocation.remove(playerLocation);
		}
	}
}
