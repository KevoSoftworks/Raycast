package com.kevosoftworks.raycast.wall;

import java.awt.Color;

import com.kevosoftworks.raycast.Location;

public class PortalWall extends Wall{
	
	int cruuid;

	public PortalWall(Location p1, Location p2, int crUuid){
		super(p1, p2);
		this.cruuid = crUuid;
	}
	
	public PortalWall(Location p1, Location p2, int texNum, int crUuid){
		super(p1, p2, texNum);
		this.cruuid = crUuid;
	}
	
	public int getRoomUuid(){
		return this.cruuid;
	}

}
