package com.dreaminsteam.jarcade;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.dreaminsteam.jarcade.gui.JarcadeMainWindow;
import com.sun.jna.NativeLibrary;

public class Jarcade {

	public static boolean RUN_FULL_SCREEN = false;
	
	private JarcadeMainWindow mainWindow;
	
	public Jarcade(){
		mainWindow = new JarcadeMainWindow(this);
	}
	
	public void startInstance(){
		GraphicsDevice primaryScreen = findPrimaryScreen();
		if(primaryScreen == null && RUN_FULL_SCREEN){
			throw new RuntimeException("No devices support full-screen mode.  Can't create window.");
		}
		mainWindow.setUpGUI();
		
		if(RUN_FULL_SCREEN){
			primaryScreen.setFullScreenWindow(mainWindow);
		}else{
			mainWindow.setBounds(new Rectangle(0,0,800,600));
			mainWindow.setVisible(true);
		}
		
		//mainWindow.playStartupMovie();
	}
	
	public void shutdownGracefully(){
		mainWindow.setVisible(false);
		mainWindow.dispose();
		System.exit(0);
	}
	
	private GraphicsDevice findPrimaryScreen(){
		GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
		if(defaultScreenDevice.isFullScreenSupported()){
			return defaultScreenDevice;
		}
		
		//Default screen does not support full screen...?  Let's find one that does.
		GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
		for(GraphicsDevice device : screenDevices){
			if(device.isFullScreenSupported()){
				return device;
			}
		}
		
		//No luck...
		return null;
	}
	
	public static void startANewInstance(){
		Jarcade instance = new Jarcade();
		instance.startInstance();
	}
	
	public static void main(String[] args){
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/Applications/VLC.app/Contents/MacOS/lib");
		Jarcade.startANewInstance();
	}
}
