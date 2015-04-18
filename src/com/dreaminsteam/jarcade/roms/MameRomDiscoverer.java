package com.dreaminsteam.jarcade.roms;

import java.util.List;

import com.dreaminsteam.jarcade.emulators.MameEmulator;

public class MameRomDiscoverer implements RomDiscoverer<MameEmulator>{

	@Override
	public List<Rom<MameEmulator>> discoverRoms() {
		MameEmulator mameEmulator = new MameEmulator();
		mameEmulator.getPathToExecutable();
		
		return null;
	}

}
