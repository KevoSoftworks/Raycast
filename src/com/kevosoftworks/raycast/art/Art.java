package com.kevosoftworks.raycast.art;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Art {
	
	public static final int TEXTURE_NONE = 0;
	public static final int TEXTURE_WALL = 1;
	public static final int TEXTURE_WALL_BLUE = 2;
	public static final int TEXTURE_WALL_DARK_BLUE = 3;
	public static final int TEXTURE_WALL_RED = 4;
	public static final int TEXTURE_WALL_DARK_RED = 5;
	public static final int TEXTURE_WALL_GREEN = 6;
	public static final int TEXTURE_WALL_DARK_GREEN = 7;
	public static final int TEXTURE_WALL2 = 8;
	public static final int TEXTURE_WALL3 = 9;
	public static final int TEXTURE_ORIENTAL_WALL = 10;
	
	private Texture none;
	private Texture wall;
	private Texture wallBlue;
	private Texture wallDBlue;
	private Texture wallRed;
	private Texture wallDRed;
	private Texture wallGreen;
	private Texture wallDGreen;
	private Texture wall2;
	private Texture wall3;
	private Texture orientalWall;
	
	private Font font;
	
	private Skybox skybox;
	
	public Art(){
		none = new Texture(TEXTURE_NONE, "/textures/default.png");
		wall = new Texture(TEXTURE_WALL, "/textures/wall.png");
		wallBlue = new Texture(TEXTURE_WALL_BLUE, "/textures/wall_blue.png");
		wallDBlue = new Texture(TEXTURE_WALL_DARK_BLUE, "/textures/wall_dark_blue.png");
		wallRed = new Texture(TEXTURE_WALL_RED, "/textures/wall_red.png");
		wallDRed = new Texture(TEXTURE_WALL_DARK_RED, "/textures/wall_dark_red.png");
		wallGreen = new Texture(TEXTURE_WALL_GREEN, "/textures/wall_green.png");
		wallDGreen = new Texture(TEXTURE_WALL_DARK_GREEN, "/textures/wall_dark_green.png");
		wall2 = new Texture(TEXTURE_WALL2, "/textures/wall2.png");
		wall3 = new Texture(TEXTURE_WALL3, "/textures/wall3.png");
		orientalWall = new Texture(TEXTURE_ORIENTAL_WALL, "/textures/wall_oriental.png");
		
		font = new Font("/font.png");
		skybox = new Skybox("/skybox.png");
	}
	
	public Texture getTexture(int num){
		switch(num){
			case TEXTURE_WALL:
				return wall;
			case TEXTURE_WALL_BLUE:
				return wallBlue;
			case TEXTURE_WALL_DARK_BLUE:
				return wallDBlue;
			case TEXTURE_WALL_RED:
				return wallRed;
			case TEXTURE_WALL_DARK_RED:
				return wallDRed;
			case TEXTURE_WALL_GREEN:
				return wallGreen;
			case TEXTURE_WALL_DARK_GREEN:
				return wallDGreen;
			case TEXTURE_WALL2:
				return wall2;
			case TEXTURE_WALL3:
				return wall3;
			case TEXTURE_ORIENTAL_WALL:
				return orientalWall;
			default:
				return none;
		}
	}
	
	public Skybox getSkybox(){
		return this.skybox;
	}
	
	public BufferedImage text(String t, Color c, float tr){
		return font.generateString(t, c, tr);
	}
	
	public BufferedImage text(String t, Color c){
		return font.generateString(t, c);
	}
	
	public BufferedImage text(String t){
		return font.generateString(t);
	}
}
