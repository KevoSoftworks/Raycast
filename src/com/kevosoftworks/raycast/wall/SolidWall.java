package com.kevosoftworks.raycast.wall;

import java.awt.Color;

import com.kevosoftworks.raycast.Location;

public class SolidWall extends Wall{

	public SolidWall(Location p1, Location p2){
		super(p1, p2);
	}
	
	public SolidWall(Location p1, Location p2, int texNum){
		super(p1, p2, texNum);
	}
	
	public SolidWall(Location p1, Location p2, int texNum, float height){
		super(p1, p2, texNum, height);
	}

}
