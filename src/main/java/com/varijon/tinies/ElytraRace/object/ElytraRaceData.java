package com.varijon.tinies.ElytraRace.object;

import java.util.ArrayList;
import java.util.UUID;

public class ElytraRaceData implements Comparable<ElytraRaceData>
{
	UUID playerUUID;
	ArrayList<String> obtainedRings;
	long startTime;
	long finishTime;
	long raceTime;
	boolean hasFinished;
	int numberAttempts;
		
	
	public ElytraRaceData(UUID playerUUID, ArrayList<String> obtainedRings, long startTime, long finishTime, long raceTime, int numberAttempts,
			boolean hasFinished) {
		super();
		this.playerUUID = playerUUID;
		this.obtainedRings = obtainedRings;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.raceTime = raceTime;
		this.hasFinished = hasFinished;
		this.numberAttempts = numberAttempts;
	}
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	public void setPlayerUUID(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
	public ArrayList<String> getObtainedRings() {
		return obtainedRings;
	}
	public void setObtainedRings(ArrayList<String> obtainedRings) {
		this.obtainedRings = obtainedRings;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	public boolean isHasFinished() {
		return hasFinished;
	}
	public void setHasFinished(boolean hasFinished) {
		this.hasFinished = hasFinished;
	}
	
	public boolean addRingToData(String name)
	{
		boolean exists = false;
		for(String ringName : obtainedRings)
		{
			if(name.equals(ringName))
			{
				exists = true;
			}
		}
		if(!exists)
		{
			obtainedRings.add(name);
			return true;
		}
		return false;
	}
	public void clearRings() 
	{
		obtainedRings.clear();
		
	}
	
	
	
	public int getNumberAttempts() {
		return numberAttempts;
	}
	public void setNumberAttempts(int numberAttempts) {
		this.numberAttempts = numberAttempts;
	}
	public Long getRaceTime()
	{
		if(raceTime < 1)
		{
			raceTime = finishTime - startTime;
		}
		return raceTime;
	}
	
	public void setRaceTime(long raceTime)
	{
		this.raceTime = raceTime;
	}

	@Override
	public int compareTo(ElytraRaceData o) {
		// TODO Auto-generated method stub
		return this.getRaceTime().compareTo(o.getRaceTime());
	}
}
