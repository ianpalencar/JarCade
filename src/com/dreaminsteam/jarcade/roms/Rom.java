package com.dreaminsteam.jarcade.roms;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;

import com.dreaminsteam.jarcade.emulators.Emulator;

public class Rom<E extends Emulator> {

	private E emulator;
	private String name;
	private int numberOfPlayers;
	private File pathOnDisk;
	private String fileName;
	private byte[] previewImage;
	private boolean needsSkip;
	private transient BufferedImage previewImageCache;
	
	public void launchRom(){
		emulator.launchRom(this);
	}
	
	public void setEmulator(E emulator){
		this.emulator = emulator;
	}
	
	public E getEmulator(){
		return emulator; 
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setNumberOfPlayers(int numPlayers){
		this.numberOfPlayers = numPlayers;
	}
	
	public int getNumberOfPlayers(){
		return numberOfPlayers;
	}
	
	public void setPathOnDisk(File pathOnDisk){
		this.pathOnDisk = pathOnDisk;
	}
	
	public File getPathOnDisk(){
		return pathOnDisk;
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public void setPreviewImage(BufferedImage image){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
		}catch(Throwable t){
			System.out.println("Cannot save image.");
			t.printStackTrace();
		}
	}
	
	public BufferedImage getPreviewImage(){
		if(previewImageCache == null){
			try{
				ByteArrayInputStream bais = new ByteArrayInputStream(previewImage);
				previewImageCache = ImageIO.read(bais);
			}catch(Throwable t){
				t.printStackTrace();
			}
		}
		return previewImageCache;
	}
	
	public void setNeedsSkip(boolean needsSkip){
		this.needsSkip = needsSkip;
	}
	
	public boolean getNeedsSkip(){
		return needsSkip;
	}
}
