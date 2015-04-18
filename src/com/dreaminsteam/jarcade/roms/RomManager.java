package com.dreaminsteam.jarcade.roms;

import java.io.File;
import java.util.List;

import com.dreaminsteam.jarcade.database.DatabaseController;
import com.dreaminsteam.jarcade.emulators.Emulator;

public class RomManager {
	
	public static final String DATABASE_FILE = "./resources/database/roms.db";
	
	private DatabaseController dbController;
	
	public RomManager(){
		dbController = DatabaseController.accessDatabase(DATABASE_FILE);
	}
	
	private void clearDatabase(){
		dbController.closeConnection();
		File dbFile = new File(DATABASE_FILE);
		dbFile.delete();
		dbController = DatabaseController.accessDatabase(DATABASE_FILE);
	}
	
	private void addRom(Rom<Emulator> rom){
		dbController.storeIfNotThere(rom);
	}
	
	public static void clearAndRepopulateRomDatabase(RomDiscoverer<Emulator> discoverer){
		RomManager romManager = new RomManager();
		romManager.clearDatabase();
		
		List<Rom<Emulator>> discoverRoms = discoverer.discoverRoms();
		
		discoverRoms.stream().forEach((rom) -> {
			romManager.addRom(rom);
		});
	}
}
