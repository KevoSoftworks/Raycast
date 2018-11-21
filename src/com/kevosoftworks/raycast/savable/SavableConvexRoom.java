package com.kevosoftworks.raycast.savable;

import java.util.ArrayList;

import com.kevosoftworks.raycast.ConvexRoom;
import com.kevosoftworks.raycast.art.Art;
import com.kevosoftworks.raycast.wall.Wall;

public class SavableConvexRoom extends Savable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Room properties
	private int id = -1;
	private ArrayList<SavableWall> walls;
	private float zHeight = 0f;
	
	//Floor properties
	private boolean hasFloor = false;
	private int floorTextureNum = Art.TEXTURE_NONE;
	private float floorTextureScale = 1f;
	
	//Ceiling properties
	private boolean hasCeil = false;
	private int ceilTextureNum = Art.TEXTURE_NONE;
	private float ceilTextureScale = 1f;
	
	public SavableConvexRoom(ConvexRoom c){
		this.id = c.getId();
		this.zHeight = c.getZHeight();
		
		walls = new ArrayList<SavableWall>();
		for(Wall w:c.getWalls()){
			walls.add(new SavableWall(w));
		}
		
		this.hasFloor = c.hasFloor();
		this.floorTextureNum = c.getFloorTexture().getNumber();
		this.floorTextureScale = c.getFloorTextureScale();
		
		this.hasCeil = c.hasCeiling();
		this.ceilTextureNum = c.getFloorTexture().getNumber();
		this.ceilTextureScale = c.getCeilingTextureScale();
	}
	
	public int getId(){
		return this.id;
	}
	
	public SavableWall[] getWalls(){
		return this.walls.toArray(new SavableWall[1]);
	}
	
	public float getZHeight(){
		return this.zHeight;
	}
	
	
	public boolean hasFloor(){
		return this.hasFloor;
	}
	
	public int getFloorTextureNumber(){
		return this.floorTextureNum;
	}
	
	public float getFloorTextureScale(){
		return this.floorTextureScale;
	}
	
	
	public boolean hasCeiling(){
		return this.hasCeil;
	}
	
	public int getCeilingTextureNumber(){
		return this.ceilTextureNum;
	}
	
	public float getCeilingTextureScale(){
		return this.ceilTextureScale;
	}

}
