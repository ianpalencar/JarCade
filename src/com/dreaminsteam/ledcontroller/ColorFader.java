package com.dreaminsteam.ledcontroller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ColorFader {
	
	private static final double GOLDEN_RATIO = 0.618033988749895;
	
	private MJSLedController controller;
	private Thread fadeThread;
	private LinkedList<Color> colorList;
	private boolean running = false;
	
	public ColorFader(MJSLedController controller){
		if(!controller.isOpen()){
			try{
				controller.openConnectionAndInitialize();
			}catch(Throwable t){
				throw new IllegalArgumentException("Cannot communicate with controller", t);
			}
		}
		controller.changeColor(Color.BLACK);
		this.controller = controller;
	}
	
	public synchronized void stop(){
		running = false;
		if(fadeThread != null){
			while(fadeThread.isAlive()){
				try{
					Thread.sleep(15);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			fadeThread = null;
		}
		controller.changeColor(Color.BLACK);
	}
	
	public synchronized void pulseColor(Color color, int pulseRateSeconds){
		if(running){
			stop();
		}
		List<Color> colors = new ArrayList<Color>();
		colors.add(color);
		colors.add(Color.BLACK);
		colorCycle(colors, pulseRateSeconds, true);
	}
	
	public synchronized void colorCycle(List<Color> colors, final int secondsToFade, final boolean repeat){
		if(running){
			stop();
		}
		colorList = new LinkedList<Color>(colors);
		running = true;
		fadeThread = new Thread(){
			@Override
			public void run() {
				Color startColor = Color.BLACK;				
				Color nextColor = colorList.poll();
				long startTime = System.currentTimeMillis();
				while(running){
					long currentTime = System.currentTimeMillis();
					Color fadeStep = fadeStep(currentTime, startTime, secondsToFade*1000, startColor, nextColor);
					controller.changeColor(fadeStep);
					try{
						Thread.sleep(5);
					}catch(Throwable t){
						running = false;
					}
					if(currentTime > startTime + (secondsToFade*1000)){
						startTime = currentTime;
						startColor = nextColor;
						if(repeat){
							colorList.offer(nextColor);
						}
						nextColor = colorList.poll();
						if(nextColor == null){
							running = false;
						}
					}
				}
			}
		};
		fadeThread.start();
	}

	private Color fadeStep(long currentTime, long startTime, long fadeDuration, Color startColor, Color nextColor){
		long totalTime = currentTime - startTime;
		
		float fraction = (float)totalTime / fadeDuration;
		fraction = Math.min(1.0f, fraction);
		
		int red = (int)(fraction * nextColor.getRed() + (1 - fraction) * startColor.getRed());
		int green = (int)(fraction * nextColor.getGreen() + (1 - fraction) * startColor.getGreen());
		int blue = (int)(fraction * nextColor.getBlue() + (1 - fraction) * startColor.getBlue());
		
		return new Color(red, green, blue);
	}
	
	public static List<Color> getRandomColorList(){
		List<Color> colors = new ArrayList<Color>();
		double hue = Math.random();
		for(int i=0; i<100; i++){
			hue += GOLDEN_RATIO;
			hue = hue % 1.0;
			Color hsbColor = Color.getHSBColor((float)hue, (float).99, (float).99);
			colors.add(hsbColor);
		}
		return colors;
	}
}
