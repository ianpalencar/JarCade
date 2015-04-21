package com.dreaminsteam.ledcontroller;

import java.awt.Color;
import java.io.Closeable;

/**
 * Represents an object which can talk to LEDs in some fashion.
 */
public interface LedController extends Closeable {
	
	/**
	 * Sets the color of the LEDs to the given RGB components.
	 * @param r The red component, 0-255 (inclusive).
	 * @param g The green component, 0-255 (inclusive).
	 * @param b The blue component, 0-255 (inclusive).
	 * @return <code>true</code> if no errors were reported.
	 */
	public boolean setColor(int r, int g, int b);
	
	/**
	 * Sets the color of the LEDs to the given {@link Color} object.
	 * @param color The color to set the LEDs to.
	 * @return <code>true</code> if no errors were reported while sending the data 
	 * to the underlying device driver.
	 */
	public default boolean setColor(Color color) { return setColor(color.getRed(), color.getGreen(), color.getBlue()); }
}
