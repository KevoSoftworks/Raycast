package com.kevosoftworks.raycast;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener{
	
	boolean keyup = false;
	boolean keydown = false;
	boolean keyleft = false;
	boolean keyright = false;
	boolean keyshift = false;
	
	boolean rotateleft = false;
	boolean rotateright = false;
	
	boolean reset = false;
	boolean renderFloor = false;
	boolean renderMap = false;
	boolean renderDebugText = false;
	
	boolean inFocus = false;
	
	int mouseX;
	int mouseY;
	
	Robot r;
	
	public InputHandler(){
		mouseX = Main.screenRes.width / 2;
		mouseY = Main.screenRes.height / 2;
		try{
			r = new Robot();
			r.setAutoDelay(0);
		} catch (AWTException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
			case KeyEvent.VK_W:
				keyup = true;
				break;
			case KeyEvent.VK_S:
				keydown = true;
				break;
			case KeyEvent.VK_A:
				keyleft = true;
				break;
			case KeyEvent.VK_D:
				keyright = true;
				break;
			case KeyEvent.VK_SHIFT:
				keyshift = true;
				break;
				
			case KeyEvent.VK_LEFT:
				rotateleft = true;
				break;
			case KeyEvent.VK_RIGHT:
				rotateright = true;
				break;
			
			case KeyEvent.VK_ESCAPE:
				reset = true;
				break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_W:
			keyup = false;
			break;
		case KeyEvent.VK_S:
			keydown = false;
			break;
		case KeyEvent.VK_A:
			keyleft = false;
			break;
		case KeyEvent.VK_D:
			keyright = false;
			break;
		case KeyEvent.VK_SHIFT:
			keyshift = false;
			break;
			
		case KeyEvent.VK_LEFT:
			rotateleft = false;
			break;
		case KeyEvent.VK_RIGHT:
			rotateright = false;
			break;
		
		case KeyEvent.VK_ESCAPE:
			reset = false;
			break;
		
		case KeyEvent.VK_E:
			renderFloor = true;
			break;
		case KeyEvent.VK_Q:
			renderMap = !renderMap;
			break;
		case KeyEvent.VK_F1:
			renderDebugText = !renderDebugText;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0){
		if(arg0.getXOnScreen() == Main.screenRes.width / 2 && arg0.getYOnScreen() == Main.screenRes.height / 2) return;
		this.mouseX = arg0.getXOnScreen();
		this.mouseY = arg0.getYOnScreen();
	}

	@Override
	public void mouseClicked(MouseEvent arg0){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusGained(FocusEvent arg0){
		this.inFocus = true;
		System.out.println("Focus gained!");
		r.mouseMove(Main.screenRes.width / 2, Main.screenRes.height / 2);
	}

	@Override
	public void focusLost(FocusEvent arg0){
		System.out.println("Focus lost!");
		r.mouseMove(Main.screenRes.width / 2, Main.screenRes.height / 2);
		keyup = false;
		keydown = false;
		keyleft = false;
		keyright = false;
		keyshift = false;
		
		rotateleft = false;
		rotateright = false;
		
		reset = false;
		renderFloor = false;
		renderMap = false;
		this.inFocus = false;
		
	}

}
