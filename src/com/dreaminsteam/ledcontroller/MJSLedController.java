package com.dreaminsteam.ledcontroller;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

public class MJSLedController {

	public static final short VENDOR_ID = 0x0B40;
	public static final short PRODUCT_ID = 0x012D;
	
	private HidDeviceInfo deviceInfo;
	private HidDevice underlyingDevice;
	
	public static List<MJSLedController> getConnectedControllers(){
		List<HidDeviceInfo> deviceList = PureJavaHidApi.enumerateDevices();
		
		List<MJSLedController> allPossibleControllers = new ArrayList<MJSLedController>();
		for(HidDeviceInfo info : deviceList){
			if(info.getVendorId() == VENDOR_ID && info.getProductId() == PRODUCT_ID){
				allPossibleControllers.add(new MJSLedController(info));
			}
		}
		
		return allPossibleControllers;
	}
	
	private MJSLedController(HidDeviceInfo deviceInfo){
		this.deviceInfo = deviceInfo;
	}
	
	public boolean openConnectionAndInitialize() throws IOException{
		if(underlyingDevice == null){
			underlyingDevice = PureJavaHidApi.openDevice(deviceInfo.getPath());
			if(!initPort()){ return false; }
			if(!setColor(0,0,0)){ return false; }
			return true;
		}else{
			throw new IllegalStateException("Device connection is already open.");
		}
	}
	
	public boolean isOpen(){
		return underlyingDevice != null;
	}
	
	public void closeDevice(){
		if(underlyingDevice != null){
			setColor(0,0,0);
			underlyingDevice.close();
			underlyingDevice = null;
		}
	}
	
	public void changeColor(Color color){
		if(underlyingDevice != null){
			setColor(color.getRed(), color.getGreen(), color.getBlue());
		}
	}
	
	public void changeColor(int red, int green, int blue){
		if(underlyingDevice != null){	
			setColor(red, green, blue);
		}
	}
	
	
	private boolean initPort(){
		if(underlyingDevice == null){
			return false;
		}
		return writeBytes(underlyingDevice, 0x93, 0xc0, 0x20, 0, 0, 0)
				&& writeBytes(underlyingDevice, 0x9F, 0x06, 0x00, 0, 0, 0)
				&& writeBytes(underlyingDevice, 0x9F, 0x03, 0x06, 0x00, 0, 0);
	}
	
	private boolean setColor(int r, int g, int b){
		if(underlyingDevice == null){
			return false;
		}
				
		//---------------------------------------------------------------
		// Convert max of 255 to max of 1023
		//---------------------------------------------------------------
		int green = g*4;		
		int red = r*4;
		int blue = b*4;

		//---------------------------------------------------------------
		// Convert RGB to four bytes
		//---------------------------------------------------------------
		byte b3, b4, b5, b6;
		b3 = (byte)((0 << 6) | (byte)(blue >> 4));
		b4 = (byte)((blue << 4) | (byte)(red >> 6));
		b5 = (byte)((red << 2) | (byte)(green >> 8));
		b6 = (byte)(green);

		//---------------------------------------------------------------
		// Write out to the USB device
		//---------------------------------------------------------------
		if (!writeBytes(underlyingDevice, (byte)0xAF, (byte)0x04, b3, b4, b5, b6)) return false;
		if (!clearPort()) return false;
		return allegroColorCorrect();
	}
	
	private boolean allegroColorCorrect(){
		if(underlyingDevice == null){
			return false;
		}
		return writeBytes(underlyingDevice, 0x46, 0xE1, 0xFC, 0x6E, 0, 0)
				&& clearPort();
	}
	
	private boolean clearPort(){
		if(underlyingDevice == null){
			return false;
		}
		return  writeBytes(underlyingDevice, 0x9F, 0x03, 0x05, 0x01, 0, 0)
				&& writeBytes(underlyingDevice, 0x9F, 0x03, 0x05, 0x00, 0, 0);
	}
	
	private static boolean writeBytes(HidDevice device, int b1, int b2, int b3, int b4, int b5, int b6){
		return writeBytes(device, (byte)b1, (byte)b2, (byte)b3, (byte)b4, (byte)b5, (byte)b6);
	}
	
	private static boolean writeBytes(HidDevice device, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6){
		byte[] report = new byte[65];
		for(int i = 0; i < report.length; i++){
			report[i] = 0;
		}
		report[0] = b1;
		report[1] = b2;
		report[2] = b3;
		report[3] = b4;
		report[4] = b5;
		report[5] = b6;
		
		int setOutputReport = device.setOutputReport((byte)0, report, report.length);
		if(setOutputReport != report.length){
			return false;
		}
		return true;
	}
}
