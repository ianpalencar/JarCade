package com.dreaminsteam.jarcade.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class JarcadeMoviePlayer extends JPanel{

	private EmbeddedMediaPlayerComponent mediaPlayer;
	
	public JarcadeMoviePlayer(){
		mediaPlayer = new EmbeddedMediaPlayerComponent();
		
		setLayout(new BorderLayout());
		add(mediaPlayer, BorderLayout.CENTER);

	}
	
	public void playMedia(String url){
		System.out.println("Attempting to open: " + url);
		mediaPlayer.getMediaPlayer().playMedia(url.toString());
	}
	
	public void stopMedia(){
		mediaPlayer.getMediaPlayer().stop();
	}
}
