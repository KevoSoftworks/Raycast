package com.kevosoftworks.raycast.wall;

import com.kevosoftworks.raycast.Location;

public class PortalWall extends Wall{
	
	int cruuid;
	
	float portalHeight;
	
	public PortalWall(Location p1, Location p2, int texNum, float height, float portalHeight, int crUuid){
		super(p1, p2, texNum, height);
		this.cruuid = crUuid;
		this.portalHeight = portalHeight;
	}
	
	public int getRoomUuid(){
		return this.cruuid;
	}
	
	public float getPortalHeight(){
		return this.portalHeight;
	}

}
