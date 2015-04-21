package com.dreaminsteam.ledcontroller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

/**
 * Note that this class opens-on-instantiation, and once closed cannot be reused.
 */
public class MJSLedController implements LedController {
	
	/**
	 * The vendor class ID of the MJS USB-LED-1.
	 */
	public static final short MJS_USB_LED_1_VENDOR_ID = 0x0B40;
	
	/**
	 * The USB product ID of the MJS USB-LED-1.
	 */
	public static final short MJS_USB_LED_1_PRODUCT_ID = 0x012D;
	
	/**
	 * A slf4j logger used for various bits and pieces in this class.
	 */
	private final Logger logger = LoggerFactory.getLogger(MJSLedController.class);
	
	public static final List<LedController> findAndOpenAllMJSDevices() {
		return PureJavaHidApi.enumerateDevices()
			.stream()
			.filter(info -> { return MJS_USB_LED_1_VENDOR_ID == info.getVendorId() && MJS_USB_LED_1_PRODUCT_ID == info.getProductId(); })
			.map(info -> { return new MJSLedController(info); })
			.collect(Collectors.toList());
	}
	
	/**
	 * The HidDevice, which keeps track of open state by being not null (for now).
	 */
	private HidDevice device;
	
	/**
	 * Opens this controller with the specified {@link HidDeviceInfo}.
	 * @param deviceInfo The {@link HidDeviceInfo} to use to open the connection.
	 */
	private MJSLedController(HidDeviceInfo deviceInfo) {
		open(deviceInfo);
	}
	
	@Override
	public void close() throws IOException {
		setColor(0, 0, 0);
		if (device != null) {
			device.close();
		}
		device = null;
	}
	
	@Override
	public boolean setColor(int r, int g, int b) {
		if (!isOpen()) {
			return false;
		}
		
		//scale to 10 bit numbers.
		int red = r * 4;
		int green = g * 4;
		int blue = b * 4;
		
		//pack into four bytes, as such:
		// [ ][ ][b][b][b][b][b][b]
		// [b][b][b][b][r][r][r][r]
		// [r][r][r][r][r][r][g][g]
		// [g][g][g][g][g][g][g][g]
		
		byte b1, b2, b3, b4;
		
		b1 = (byte)((0 << 6) | (byte)(blue >> 4)); //is the 0 << 6 necessary?
		b2 = (byte)((blue << 4) | (byte)(red >> 6));
		b3 = (byte)((red << 2) | (byte)(green >> 8));
		b4 = (byte)(green);
		
		if (!writeBytes((byte)0xAF, (byte)0x04, b1, b2, b3, b4)) {
			return false;
		}
		
		if (!clearPort()) {
			return false;
		}
		
		if (!colorCorrect()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * This might need to change implementation in future, but for now just null-checking the device.
	 * @return <code>true</code> if the device is open and ready to do communications.
	 */
	private boolean isOpen() {
		return device != null;
	}
	
	/**
	 * Performs 'allegro color correction', whatever that means.
	 * @return <code>true</code> if the command was sent to the underlying library successfully.
	 */
	private boolean colorCorrect() {
		if (!isOpen()) {
			return false;
		}
		
		return writeBytes(0x46, 0xE1, 0xFC, 0x6E, 0, 0) && clearPort();
	}
	
	/**
	 * Clears the port? Assuming this is to formalize the end of the data.
	 * @return <code>true</code> if the port was cleared.
	 */
	private boolean clearPort() {
		if (!isOpen()) {
			return false;
		}
		
		return writeBytes(0x9F, 0x03, 0x05, 0x01, 0, 0) && writeBytes(0x9F, 0x03, 0x05, 0, 0, 0);
	}
	
	
	
	/**
	 * Write the given six bytes to the device.
	 * @param b1 The first byte (usually a header of some sort)
	 * @param b2 The second byte (usually a header of some sort)
	 * @param b3 The third byte (usually data)
	 * @param b4 The fourth byte (usually data)
	 * @param b5 The fifth byte (usually data)
	 * @param b6 The sixth byte (usually data)
	 * @return <code>true</code> if the bytes were sent successfully to the library for sending to the device.
	 */
	private boolean writeBytes(int b1, int b2, int b3, int b4, int b5, int b6) {
		return writeBytes((byte)(b1 & 0xff), (byte)(b2 & 0xff), (byte)(b3 & 0xff), (byte)(b4 & 0xff), (byte)(b5 & 0xff), (byte)(b6 & 0xff));
	}
	
	/**
	 * Write the given six bytes to the device.
	 * @param b1 The first byte (usually a header of some sort)
	 * @param b2 The second byte (usually a header of some sort)
	 * @param b3 The third byte (usually data)
	 * @param b4 The fourth byte (usually data)
	 * @param b5 The fifth byte (usually data)
	 * @param b6 The sixth byte (usually data)
	 * @return <code>true</code> if the bytes were sent successfully to the library for sending to the device.
	 */
	private boolean writeBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
		if (!isOpen()) {
			return false;
		}
		
		byte[] report = new byte[65];
		Arrays.fill(report, (byte)0);
		
		report[0] = b1;
		report[1] = b2;
		report[2] = b3;
		report[3] = b4;
		report[4] = b5;
		report[5] = b6;
		
		int reportLength = device.setOutputReport((byte)0, report, report.length);
		
		return reportLength == report.length;
	}
	
	/**
	 * Opens the device if the device hasn't been opened yet.
	 */
	private void open(HidDeviceInfo info) {
		if (isOpen()) {
			return;
		}
		
		try {
			device = PureJavaHidApi.openDevice(info.getPath());
		} catch (IOException ex) {
			logger.error("Unable to open HID device", ex);
			device = null;
			return;
		}
		
		try {
			if (!initPort()) {
				close();
			} else if (!setColor(0, 0, 0)) {
				close();
			}
		} catch (IOException ex) {
			logger.error("Unable to properly open port", ex);
		}
	}
	
	private boolean initPort() {
		if (!isOpen()) {
			return false;
		}
		
		return writeBytes(0x93, 0xC0, 0x20, 0, 0, 0)
				&& writeBytes(0x9F, 0x06, 0, 0, 0, 0)
				&& writeBytes(0x9F, 0x03, 0x06, 0, 0, 0);
	}
}
