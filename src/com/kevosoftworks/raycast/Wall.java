package com.kevosoftworks.raycast;

import java.awt.Color;

public class Wall {
	
	Location p1;
	Location p2;
	Color c;
	float startHeight;
	float endHeight;
	
	public Wall(Location p1, Location p2){
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Wall(Location p1, Location p2, Color c){
		this.p1 = p1;
		this.p2 = p2;
		this.c = c;
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
	
	public Color getColor(){
		return this.c;
	}

}
