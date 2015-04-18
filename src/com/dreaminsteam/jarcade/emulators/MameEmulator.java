package com.dreaminsteam.jarcade.emulators;

import java.io.File;

import com.dreaminsteam.jarcade.roms.Rom;

public class MameEmulator extends Emulator{

	public MameEmulator() {
		super("MAME");
	}

	@Override
	public void launchRom(Rom rom) {
		
	}

	@Override
	public File getPathToExecutable() {
		return new File("/Applications/MAME.app");
	}

}
