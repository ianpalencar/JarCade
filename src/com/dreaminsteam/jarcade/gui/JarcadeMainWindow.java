package com.dreaminsteam.jarcade.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import com.dreaminsteam.jarcade.Jarcade;

public class JarcadeMainWindow extends JFrame{

	private static final long serialVersionUID = 1L;

	private Jarcade parent;
	
	public JarcadeMainWindow(Jarcade parent){
		this.parent = parent;
		this.getContentPane().setBackground(Color.BLACK);
		this.setBackground(Color.BLACK);
	}
	
	public void setUpGUI(){
		this.setUndecorated(true);
		this.setBackground(Color.BLACK);

		registerKeyboardHandler();
	}
	
	public void playStartupMovie(){
		try{
			final JarcadeMoviePlayer jmp = new JarcadeMoviePlayer(this.getWidth(), this.getHeight());
			this.setContentPane(jmp);
			File f = new File("./resources/arcade_intro.mp4");
			String url = "file://" + f.getAbsolutePath();
			jmp.playMedia(url, new MediaPlayerEventAdapter(){
				@Override
				public void finished(MediaPlayer mediaPlayer) {
					SwingUtilities.invokeLater(() -> { 
							JarcadeMainWindow.this.remove(jmp);
							loadToMenu();
						});
					
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadToMenu(){
		JPanel jPanel = new JPanel();
		jPanel.setBackground(Color.BLACK);
		jPanel.setSize(this.getWidth(), this.getHeight());
		jPanel.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		
		JLabel label = new JLabel("MENU!");
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(JLabel.CENTER);
		
		jPanel.setLayout(new BorderLayout());
		jPanel.add(label, BorderLayout.CENTER);
		
		this.setContentPane(jPanel);
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
