package com.kevosoftworks.raycast.art;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

public class Mipmap{
	
	byte[] raw;
	public int[][] pA = null;
	boolean hasAlpha;
	public int width;
	public int height;
	
	protected BufferedImage image;
	
	private static final boolean INTERPOLATION = true;
	private static final boolean ANTIALIAS = false;
	
	public Mipmap(BufferedImage img, float scale){
		this.width = (int)(img.getWidth() * scale);
		if(this.width < 1) this.width = 1;
		
		this.height = (int)(img.getHeight() * scale);
		if(this.height < 1) this.height = 1;
		
		this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics g = image.getGraphics();
		if(INTERPOLATION)((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		if(ANTIALIAS)((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(img, 0, 0, this.width, this.height, null);
		g.dispose();
		
		DataBuffer db = this.image.getRaster().getDataBuffer();
		this.raw = ((DataBufferByte) db).getData();
		this.hasAlpha = img.getAlphaRaster() != null;
		this.pA = this.getPixelArray();
	}
	
	public int[] getColumn(float dist){
		int col = (int)Math.floor(dist * height % (width));
		int[] ret = new int[height];
		
		for(int i = 0; i < this.height; i++){
			ret[i] = this.pA[i][col];
		}
		return ret;
	}
	
	public int[][] getPixelArray(){
		if(this.pA != null) return this.pA;
		int[][] ret = new int[height][width];
		int row = 0, col = 0;
		for(int i = 0; i < this.raw.length; i += this.hasAlpha ? 4 : 3){
			int offset = this.hasAlpha ? 1 : 0;
			int argb = 0;
			argb += this.hasAlpha ? (raw[i] & 0xff) << 24 : -16777216;
			argb += (raw[i+offset] & 0xff);
			argb += (raw[i+offset+1] & 0xff) << 8;
			argb += (raw[i+offset+2] & 0xff) << 16;
			ret[row][col] = argb;
			col++;
			if(col == this.width){
				row++;
				col = 0;
			}
		}
		return ret;
	}
	
	public BufferedImage getBufferedImage(){
		return this.image;
	}

}
