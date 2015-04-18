package com.dreaminsteam.jarcade.emulators;

import java.io.File;

import com.dreaminsteam.jarcade.roms.Rom;

public abstract class Emulator {

	private final String name;
	
	protected Emulator(String name){
		this.name = name;
	}
	
	public abstract void launchRom(Rom rom);
	public abstract File getPathToExecutable();
	
}
