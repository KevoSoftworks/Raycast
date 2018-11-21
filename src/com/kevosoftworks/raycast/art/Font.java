package com.kevosoftworks.raycast.art;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class Font extends Texture{
	
	private String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?()_-+=#|:;.,*/\\<>[]\'\"%~`^@ abcdefghijklmnopqrstuvwxyz";
	private int[] widths = {/*A*/5, 5, 4, 5, 4, 4, 5, 5, 4, 5, 5, 4, 6, 5, 5, 5, 5, 5, 5, 4, 5, 5, 6, 5, 5, 4, /*0*/5, 3, 5, 5, 5, 5, 5, 5, 5, 5, /*!*/2, 5, 3, 3, 5, 4, 4, 4, 6, 2, 2, 2, 2, 3, 4, 6, 6, 4, 4, 3, 3, 2, 4, 6, 5, 3, 4, 6, /*Space*/4, /*a*/5, 5, 4, 5, 5, 5, 5, 5, 2, 3, 5, 2, 6, 5, 5, 5, 5, 4, 5, 4, 5, 5, 6, 4, 5, 5};
	private BufferedImage[] chars;

	public Font(String uri){
		super(-1, uri);
		this.chars = this.generateCharacters();
	}
	
	private BufferedImage[] generateCharacters(){
		if(this.characters.length() != this.widths.length) throw new ArrayIndexOutOfBoundsException("The amount of character entries does not correspond with amount of width entries");
		chars = new BufferedImage[this.widths.length];
		
		int offset = 0;
		for(int i = 0; i < widths.length; i++){
			BufferedImage bi = new BufferedImage(widths[i], this.getMipmap(0).height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.getGraphics();
			g.drawImage(this.image, -offset, 0, null);
			g.dispose();
			offset += widths[i];
			chars[i] = bi;
		}
		return chars;
	}
	
	public BufferedImage generateString(String t, Color c, float transparency){
		int width = 0;
		for(int i = 0; i < t.length(); i++){
			width += widths[characters.indexOf(t.charAt(i))];
		}
		
		BufferedImage res = new BufferedImage(width, this.getMipmap(0).height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = res.getGraphics();
		
		RescaleOp ro = new RescaleOp(
				new float[]{c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, transparency},
				new float[]{0f, 0f, 0f, 0f},
				null);
		
		int offset = 0;
		for(int i = 0; i < t.length(); i++){
			int index = characters.indexOf(t.charAt(i));
			if(c == Color.WHITE && transparency == 1f){
				g.drawImage(this.chars[index], offset, 0, null);
			} else {
				g.drawImage(ro.filter(this.chars[index], null), offset, 0, null);
			}
			offset += widths[index];
		}
		g.dispose();
		
		return res;
	}
	
	public BufferedImage generateString(String t, Color c){
		return this.generateString(t, c, 1f);
	}
	
	public BufferedImage generateString(String t){
		return this.generateString(t, Color.WHITE, 1f);
	}

}
