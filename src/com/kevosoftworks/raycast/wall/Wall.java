package com.kevosoftworks.raycast.wall;

import java.awt.Color;

import com.kevosoftworks.raycast.Art;
import com.kevosoftworks.raycast.Location;

public class Wall{
	
	Location p1;
	Location p2;
	int texNum;
	float height;
	
	public Wall(Location p1, Location p2){
		this.p1 = p1;
		this.p2 = p2;
		this.texNum = Art.TEXTURE_WALL;
		this.height = 1;
	}
	
	public Wall(Location p1, Location p2, int texNum){
		this.p1 = p1;
		this.p2 = p2;
		this.texNum = texNum;
		this.height = 1;
	}
	
	public Wall(Location p1, Location p2, int texNum, float height){
		this.p1 = p1;
		this.p2 = p2;
		this.texNum = texNum;
		this.height = height;
	}
	
	public Location getLocation1(){
		return this.p1;
	}
	
	public Location getLocation2(){
		return this.p2;
	}
	
	public Location[] getLocations(){
		Location[] l = new Location[2];
		l[0] = this.getLocation1();
		l[1] = this.getLocation2();
		return l;
	}
	
	public int getTextureNumber(){
		return this.texNum;
	}
	
	public float getHeight(){
		return this.height;
	}

}
