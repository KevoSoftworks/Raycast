package com.kevosoftworks.raycast.art;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Texture {
	
	protected BufferedImage image;
	public int width;
	public int height;
	
	private static final float THRESHOLD_DIST = 2.5f;
	private static final int MAX_MIPMAP = 16;
	
	ArrayList<Mipmap> mm;
	
	public Texture(String uri){
		try {
			this.image = ImageIO.read(Texture.class.getResource(uri));
			this.width = this.image.getWidth();
			this.height = this.image.getHeight();
			
			this.mm = new ArrayList<Mipmap>();
			for(int i = 1; i <= MAX_MIPMAP; i++){
				mm.add(new Mipmap(this.image, 1/(float)(i)));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int[] getColumn(float walldist, float dist){
		Mipmap m = mm.get(this.getMipmapIndex(dist));
		int[][] pA = m.getPixelArray();
		int col = (int)Math.floor(walldist * m.height % (m.width));
		int[] ret = new int[m.height];
		
		for(int i = 0; i < m.height; i++){
			ret[i] = pA[i][col];
		}
		return ret;
	}
	
	public int getMipmapIndex(float dist){
		int threshPass = (int) Math.floor(dist/THRESHOLD_DIST);
		if(threshPass > MAX_MIPMAP) threshPass = MAX_MIPMAP;
		if(threshPass < 1) threshPass = 1;
		return threshPass - 1;
	}
	
	public BufferedImage getBufferedImage(){
		return this.image;
	}
	
	public Mipmap getMipmap(int i){
		return this.mm.get(i);
	}
}
