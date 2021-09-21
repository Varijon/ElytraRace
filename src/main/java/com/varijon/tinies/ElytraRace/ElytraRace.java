package com.varijon.tinies.ElytraRace;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid="elytrarace", version="1.2.10", acceptableRemoteVersions="*")
public class ElytraRace
{
	public static String MODID = "modid";
	public static String VERSION = "version";

		
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{

	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new ElytraRaceHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		RaceDataManager.loadStorage();
	}

	 @EventHandler
	 public void serverLoad(FMLServerStartingEvent event)
	 {	 
		 event.registerServerCommand(new ElytraRaceCommand());
	 }
}