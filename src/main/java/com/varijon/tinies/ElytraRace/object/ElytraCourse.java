package com.varijon.tinies.ElytraRace.object;

import java.util.ArrayList;
import java.util.UUID;

public class ElytraCourse 
{
	String courseID;
	String courseName;
	double penaltyTime;
	int maxRings;
	int attemptLimit;
	ArrayList<ElytraRaceData> lstElytraRaceData;
	public ElytraCourse(String courseID, String courseName, double penaltyTime, int maxRings, int attemptLimit,
			ArrayList<ElytraRaceData> lstElytraRaceData) {
		super();
		this.courseID = courseID;
		this.courseName = courseName;
		this.penaltyTime = penaltyTime;
		this.maxRings = maxRings;
		this.attemptLimit = attemptLimit;
		this.lstElytraRaceData = lstElytraRaceData;
	}
	public String getCourseID() {
		return courseID;
	}
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public double getPenaltyTime() {
		return penaltyTime;
	}
	public void setPenaltyTime(double penaltyTime) {
		this.penaltyTime = penaltyTime;
	}
	public int getMaxRings() {
		return maxRings;
	}
	public void setMaxRings(int maxRings) {
		this.maxRings = maxRings;
	}
	public ArrayList<ElytraRaceData> getLstElytraRaceData() {
		return lstElytraRaceData;
	}
	public void setLstElytraRaceData(ArrayList<ElytraRaceData> lstElytraRaceData) {
		this.lstElytraRaceData = lstElytraRaceData;
	}
	
		
	public int getAttemptLimit() {
		return attemptLimit;
	}
	public void setAttemptLimit(int attemptLimit) {
		this.attemptLimit = attemptLimit;
	}
	public ElytraRaceData getPlayerRaceData(UUID playerUUID)
	{
		ElytraRaceData raceData = null;
		for(ElytraRaceData data : lstElytraRaceData)
		{
			if(data.getPlayerUUID().equals(playerUUID))
			{
				raceData = data;
			}
		}
		return raceData;
	}
	
	public ElytraRaceData addElytraRaceData(ElytraRaceData raceData)
	{
		lstElytraRaceData.add(raceData);
		return raceData;
	}
	
	public void clearElytraRaceData()
	{
		lstElytraRaceData.clear();
	}
	
	public int getRaceDataIndex(ElytraRaceData data)
	{
		int count = 0;
		for(ElytraRaceData raceData : lstElytraRaceData)
		{
			if(raceData.hasFinished)
			{
				count++;
				if(raceData.playerUUID.equals(data.playerUUID))
				{
					return count;
				}
			}
		}
		return count;
	}
}
