package com.dreaminsteam.jarcade.roms;

import java.util.List;

import com.dreaminsteam.jarcade.emulators.Emulator;

public interface RomDiscoverer<E extends Emulator> {

	public List<Rom<E>> discoverRoms();
}
