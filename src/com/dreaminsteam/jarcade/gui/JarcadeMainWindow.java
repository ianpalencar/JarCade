package com.dreaminsteam.jarcade.gui;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;

import com.dreaminsteam.jarcade.Jarcade;

public class JarcadeMainWindow extends JFrame{

	private static final long serialVersionUID = 1L;

	private Jarcade parent;
	
	public JarcadeMainWindow(Jarcade parent){
		this.parent = parent;
	}
	
	public void setUpGUI(){
		this.setUndecorated(true);
		this.setBackground(Color.BLACK);

		registerKeyboardHandler();
		
	}
	
	private void registerKeyboardHandler(){
		KeyEventDispatcher keyboardHandler = new KeyEventDispatcher(){
			@Override
			public boolean dispatchKeyEvent(KeyEvent event) {
				int eventId = event.getID();
				switch(eventId){
					case KeyEvent.KEY_PRESSED:

						break;
					case KeyEvent.KEY_RELEASED:
						int keyCode = event.getKeyCode();
						if(KeyEvent.VK_ESCAPE == keyCode){
							parent.shutdownGracefully();
						}
						break;
					case KeyEvent.KEY_TYPED:
						//Don't put things here.  It won't get called.  Use pressed or released.
						break;
					default: break;
				}
				return false;
			}
		};
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(keyboardHandler);
	}
}
