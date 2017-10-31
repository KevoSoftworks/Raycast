package com.kevosoftworks.raycast;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public static final int TICKRATE = 64;
	
	private static final String TITLE = "Raycast Engine";
	private static final int WH = 1080/2;
	private static final int WW = 1920/2;
	public static final int RH = WH/4;
	public static final int RW = WW/4;
	private JFrame jframe;
	
	private BufferedImage img;
	public InputHandler input;
	
	public double tps;
	public double fps;
	public static int ticks = 0;
	
	boolean running = false;
	Thread thread;
	
	Map map;
	
	public Main(){
		jframe = new JFrame(TITLE);
		
		Dimension d = new Dimension(WW, WH);
		input = new InputHandler();
		jframe.addKeyListener(input);
		
		jframe.setSize(d);
		jframe.setPreferredSize(d);
		jframe.setMinimumSize(d);
		jframe.setMaximumSize(d);
		jframe.setResizable(false);
		jframe.setLocationRelativeTo(null);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setUndecorated(true);
		jframe.setVisible(true);
		
		img = new BufferedImage(RW, RH, BufferedImage.TYPE_INT_ARGB);
		map = new Map();
	}

	public void run() {
		//Do things
		long lastTickTime = System.nanoTime();
		long lastRenderTime = System.currentTimeMillis();
		long nsPerTick = 1000000000l / (long) TICKRATE;
		long deltaTickTime;
		long deltaRenderTime;
		long currentTickTime;
		long currentRenderTime;
		
		while(running){
			currentTickTime = System.nanoTime();
			deltaTickTime = currentTickTime - lastTickTime;
			if(deltaTickTime >= nsPerTick){
				lastTickTime = System.nanoTime();
				tps = 1000000000d / ((double)deltaTickTime);
				tick();
				
				currentRenderTime = System.currentTimeMillis();
				deltaRenderTime = currentRenderTime - lastRenderTime;
				lastRenderTime = currentRenderTime;
				fps = 1000d / ((double)deltaRenderTime);
				render();
			}
		}
	}
	
	public void render(){
		Graphics[] gA = new Graphics[2];
		gA[0] = jframe.getGraphics();
		gA[1] = img.getGraphics();
		
		gA[1].setColor(Color.black);
		gA[1].fillRect(0, 0, WW, WH);
		map.render(gA);
		
		gA[0].drawImage(img, 0, 0, WW, WH, null);
		
		gA[0].dispose();
	}
	
	public void tick(){
		ticks++;
		System.out.println("TPS: " + tps + "; FPS: " + fps);
		map.tick(input);
	}
	
	public synchronized void start(){
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop(){
		if(!running) return;
		running = false;
		try{
			thread.join();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new Main().start();
	}

}
