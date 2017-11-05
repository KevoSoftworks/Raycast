package com.kevosoftworks.raycast;

public class Art {
	
	public static final int TEXTURE_WALL = 1;
	public static final int TEXTURE_WALL2 = 2;
	public static final int TEXTURE_WALL3 = 3;
	public static final int TEXTURE_GRASS = 4;
	
	private Texture wall;
	private Texture wall2;
	private Texture wall3;
	private Texture grass;
	
	public Art(){
		wall = new Texture("/textures/wall.png");
		wall2 = new Texture("/textures/wall2.png");
		wall3 = new Texture("/textures/wall3.png");
		grass = new Texture("/textures/grass.png");
	}
	
	public Texture getTexture(int num){
		switch(num){
			case TEXTURE_WALL:
				return wall;
			case TEXTURE_WALL2:
				return wall2;
			case TEXTURE_WALL3:
				return wall3;
			case TEXTURE_GRASS:
				return grass;
			default:
				return null;
		}
	}

}
