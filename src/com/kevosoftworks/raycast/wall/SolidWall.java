package com.kevosoftworks.raycast.wall;

import com.kevosoftworks.raycast.Location;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.art.Texture;

public class SolidWall extends Wall{

	public SolidWall(int id, Location p1, Location p2, Vector2 norm, Texture tex){
		super(id, p1, p2, norm, tex);
		this.wallType = Wall.WALLTYPE_SOLID;
	}
	
	public SolidWall(int id, Location p1, Location p2, Vector2 norm, Texture tex, float height){
		super(id, p1, p2, norm, tex, height);
		this.wallType = Wall.WALLTYPE_SOLID;
	}

}
