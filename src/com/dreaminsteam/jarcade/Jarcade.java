package com.dreaminsteam.jarcade;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.util.List;

import uk.co.caprica.vlcj.binding.LibC;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.version.LibVlcVersion;
import uk.co.caprica.vlcj.version.Version;

import com.dreaminsteam.jarcade.database.DatabaseController;
import com.dreaminsteam.jarcade.gui.JarcadeMainWindow;
import com.dreaminsteam.ledcontroller.ColorFader;
import com.dreaminsteam.ledcontroller.MJSLedController;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;

public class Jarcade {

	public static boolean RUN_FULL_SCREEN = true;
	public static boolean PLAY_STARTUP_MOVIE = false;
	
	private JarcadeMainWindow mainWindow;
	private GraphicsDevice primaryScreen;
	private ColorFader ledFader;
	
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
			this.primaryScreen = primaryScreen;
		}else{
			mainWindow.setBounds(new Rectangle(0,0,800,600));
			mainWindow.setVisible(true);
		}
		
		findAndConnectToLedController();
		if(PLAY_STARTUP_MOVIE){
			mainWindow.playStartupMovie();
		}else{
			mainWindow.loadToMenu();
		}
	}
	
	public void shutdownGracefully(){
		DatabaseController.shutdownControllersGracefully();
		
		if(primaryScreen != null){
			primaryScreen.setFullScreenWindow(null);
		}
		if(ledFader != null){			
			ledFader.stop();
		}
		mainWindow.setVisible(false);
		mainWindow.dispose();
		
		System.exit(0);
	}
	
	private void findAndConnectToLedController(){
		List<MJSLedController> connectedControllers = MJSLedController.getConnectedControllers();
		if(connectedControllers.isEmpty()){
			return;
		}
		MJSLedController mjsLedController = connectedControllers.get(0);
		ledFader = new ColorFader(mjsLedController);
		ledFader.colorCycle(ColorFader.getRandomColorList(), 2, true);
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
		new NativeDiscovery().discover();
		if(Platform.isMac()){
			LibC.INSTANCE.setenv("VLC_PLUGIN_PATH", "/Applications/VLC.app/Contents/MacOS/plugins", 1);
		}
		Jarcade.startANewInstance();
	}
}
