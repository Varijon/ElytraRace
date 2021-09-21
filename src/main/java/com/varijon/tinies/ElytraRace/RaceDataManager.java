package com.varijon.tinies.ElytraRace;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.varijon.tinies.ElytraRace.object.ElytraCourse;

public class RaceDataManager 
{
static ArrayList<ElytraCourse> lstElytraCourse = new ArrayList<ElytraCourse>();
	
	public static boolean loadStorage()
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/ElytraRace";
		try
		{
			Gson gson = new Gson();
			
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			lstElytraCourse.clear();
			
			for(File file : dir.listFiles())
			{
				FileReader reader = new FileReader(file);
				
				ElytraCourse ElytraCourse = gson.fromJson(reader, ElytraCourse.class);
								
				lstElytraCourse.add(ElytraCourse);
				reader.close();
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public static void writeElytraRaceData(ElytraCourse course)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/ElytraRace";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			FileWriter writer = new FileWriter(source + "/" + course.getCourseID() + ".json");
			gson.toJson(course, writer);
			writer.close();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static ElytraCourse addElytraCourse(ElytraCourse elytraCourse)
	{
		lstElytraCourse.add(elytraCourse);
		return elytraCourse;
	}
	
	public static ElytraCourse getElytraCourse(String courseID)
	{
		ElytraCourse course = null;
		for(ElytraCourse elytraCourse : lstElytraCourse)
		{
			if(elytraCourse.getCourseID().equals(courseID))
			{
				course = elytraCourse;
			}
		}
		return course;
	}
	
//	public static void saveChangesToFile()
//	{
//		String basefolder = new File("").getAbsolutePath();
//        String source = basefolder + "/config/CatchEventReport";
//		
//		try
//		{
//			File dir = new File(source);
//			if(!dir.exists())
//			{
//				dir.mkdirs();
//			}
//			if(dir.listFiles().length == 0)
//			{
//				ArrayList<EventPokemon> lstEventPokemon = new ArrayList<EventPokemon>();
//				lstEventPokemon.add(new EventPokemon(EnumSpecies.Salandit, "winter", 10));
//				lstEventPokemon.add(new EventPokemon(EnumSpecies.Cutiefly, "winter", 20));
//				EventConfig event = new EventConfig("Example", "exampleTag", "Welcome to the Example event", lstEventPokemon);
//		
//				Gson gson = new GsonBuilder().setPrettyPrinting().create();
//					
//				FileWriter writer = new FileWriter(source + "/Example.json");
//				gson.toJson(event, writer);
//				writer.close();
//			}
//		}
//			
//		catch (Exception ex) 
//		{
//		    ex.printStackTrace();
//		}
//	}
	
	public static ArrayList<ElytraCourse> getElytraCourseList()
	{
		return lstElytraCourse;
	}
}
