package com.kevosoftworks.raycast;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public static final int TICKRATE = 64;
	
	private static final String TITLE = "Raycast Engine";
	private static final int WH = 1080;
	private static final int WW = 1920;
	public static final int RH = 288;
	public static final int RW = 512;
	private JFrame jframe;
	
	private BufferedImage img;
	private BufferedImage ui;
	public InputHandler input;
	
	public static double tps;
	public static double fps;
	public boolean shouldRender = true;
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
		
		ui = new BufferedImage(RW, RH, BufferedImage.TYPE_INT_ARGB);
		img = new BufferedImage(RW, RH, BufferedImage.TYPE_INT_ARGB);
		map = new Map();
	}

	public void run() {
		//Do things
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/TICKRATE;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			while(delta >= 1){
				ticks++;
				tick();
				delta -= 1;
			}
			if(shouldRender){
				frames++;
				render();
			}
			
			if(System.currentTimeMillis() - lastTimer >= 1000){
				lastTimer += 1000;
				System.out.println("TPS: " + ticks + "; FPS: " + frames + "; " + RW + "x" + RH);
				fps = frames;
				tps = ticks;
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void render(){
		Graphics[] gA = new Graphics[3];
		gA[0] = jframe.getGraphics();
		gA[1] = img.getGraphics();
		gA[2] = ui.getGraphics();
		
		((Graphics2D)gA[2]).setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		gA[2].fillRect(0,0,WW,WH);
		((Graphics2D)gA[2]).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		
		((Graphics2D)gA[1]).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		((Graphics2D)gA[0]).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		((Graphics2D)gA[0]).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		gA[1].setColor(Color.black);
		gA[1].fillRect(0, 0, WW, WH);
		map.render(gA);
		
		gA[1].drawImage(ui, 0, 0, null);
		gA[0].drawImage(img, 0, 0, WW, WH, null);
		
		gA[2].dispose();
		gA[1].dispose();
		gA[0].dispose();
	}
	
	public void tick(){
		ticks++;
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
