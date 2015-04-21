package com.dreaminsteam.jarcade.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class JarcadeMoviePlayer extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private final BufferedImage image;
	private final MediaPlayerFactory factory;
	private final DirectMediaPlayer mediaPlayer;

	private int height;
	private int width;
	
	public JarcadeMoviePlayer(int width, int height){
		this.width = width;
		this.height = height;
		this.setBackground(Color.BLACK);
		
		this.setSize(width, height);
		this.setMinimumSize(new Dimension(width, height));
		this.setPreferredSize(new Dimension(width, height));
		
		image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
		image.setAccelerationPriority(1.0f);
				
		factory = new MediaPlayerFactory();
		mediaPlayer = factory.newDirectMediaPlayer(new BufferFormatCallback(){

			@Override
			public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
				return new RV32BufferFormat(JarcadeMoviePlayer.this.width, JarcadeMoviePlayer.this.height);
			}
			
		}, new RenderCallbackAdapter(((DataBufferInt)image.getRaster().getDataBuffer()).getData()){

			@Override
			protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
				repaint();
			}
			
		});
	}
	
	public void playMedia(String url, MediaPlayerEventListener listener){
		mediaPlayer.playMedia(url);
		if(listener != null){
			mediaPlayer.addMediaPlayerEventListener(listener);
		}
	}
	
	public void stop(){
		mediaPlayer.stop();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(image, null, 0, 0);
	}
	
}
