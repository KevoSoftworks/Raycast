package com.kevosoftworks.raycast;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
	
	byte[] raw;
	int[][] pA;
	boolean hasAlpha;
	int width;
	int height;
	
	public Texture(String uri){
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResource(uri));
			DataBuffer db = image.getRaster().getDataBuffer();
			this.raw = ((DataBufferByte) db).getData();
			this.hasAlpha = image.getAlphaRaster() != null;
			this.width = image.getWidth();
			this.height = image.getHeight();
			this.pA = this.getPixelArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int[] getColumn(float dist){
		int col = (int)Math.floor(dist * height % (width));
		int[] ret = new int[height];
		
		for(int i = 0; i < this.height; i++){
			ret[i] = this.pA[i][col];
		}
		return ret;
	}
	
	private int[][] getPixelArray(){
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
}
