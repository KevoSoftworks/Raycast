package com.kevosoftworks.raycast;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener{
	
	boolean keyup = false;
	boolean keydown = false;
	boolean keyleft = false;
	boolean keyright = false;
	boolean keyshift = false;
	
	boolean rotateleft = false;
	boolean rotateright = false;
	
	boolean reset = false;
	boolean switchview = false;
	boolean renderMap = true;
	
	public InputHandler(){
		
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
			switchview = true;
			break;
		case KeyEvent.VK_Q:
			renderMap = !renderMap;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
