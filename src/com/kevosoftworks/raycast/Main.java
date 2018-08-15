package com.kevosoftworks.raycast;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public static final int TICKRATE = 64;
	
	private static final String TITLE = "Raycast Engine";
	public static final boolean FULLSCREEN = true;
	public static final boolean FULLRESOLUTION = false;
	public static int WH = 1080;
	public static int WW = 1920;
	public static final int RH = FULLRESOLUTION ? WH : 200;
	public static final int RW = FULLRESOLUTION ? WW : 320;
	public static Dimension screenRes;
	private JFrame jframe;
	
	private BufferedImage world;
	public InputHandler input;
	private BufferStrategy bs;
	
	public static double tps;
	public static double fps;
	public boolean shouldRender = true;
	public static int ticks = 0;
	
	boolean running = false;
	Thread thread;
	
	Map map;
	
	public Main(){
		jframe = new JFrame(TITLE);
		Toolkit tk = jframe.getToolkit();
		screenRes = tk.getScreenSize();
		if(FULLSCREEN){
			WH = screenRes.height;
			WW = screenRes.width;
		}
		
		Dimension d = new Dimension(WW, WH);
		input = new InputHandler();
		jframe.addKeyListener(input);
		jframe.addFocusListener(input);
		jframe.addMouseListener(input);
		jframe.addMouseMotionListener(input);
		
		jframe.setSize(d);
		jframe.setPreferredSize(d);
		jframe.setMinimumSize(d);
		jframe.setMaximumSize(d);
		jframe.setResizable(false);
		jframe.setLocationRelativeTo(null);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setUndecorated(true);
		jframe.setFocusable(true);
		jframe.toFront();
		jframe.requestFocusInWindow();
		
		jframe.setCursor(tk.createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null));
		
		jframe.setVisible(true);
		
		world = new BufferedImage(RW, RH, BufferedImage.TYPE_INT_RGB);
		
		jframe.createBufferStrategy(2);
		bs = jframe.getBufferStrategy();
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
		Graphics[] gA = new Graphics[2];
		gA[0] = bs.getDrawGraphics();
		gA[1] = world.getGraphics();
		
		((Graphics2D)gA[1]).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		((Graphics2D)gA[0]).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		gA[1].setColor(Color.black);
		gA[1].fillRect(0, 0, WW, WH);
		map.render(gA);
		
		gA[0].drawImage(world, 0, 0, WW, WH, null);
		
		gA[1].dispose();
		gA[0].dispose();
		bs.show();
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
