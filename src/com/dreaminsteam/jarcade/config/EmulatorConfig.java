package com.dreaminsteam.jarcade.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class EmulatorConfig {

	private static final String FILE_ON_DISK = "./resources/config/emulators.config";
	private final Properties configFile = new Properties();
	
	private EmulatorConfig(){
		File file = new File(FILE_ON_DISK);
		try (FileInputStream fis = new FileInputStream(file)){
			if(file.exists()){
				configFile.load(fis);
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
	
	//I don't recommend this in practice...
	private static class LazySingleton{
		private static final EmulatorConfig INSTANCE = new EmulatorConfig();
	}
	
	public static EmulatorConfig getInstance(){
		return LazySingleton.INSTANCE;
	}
}
