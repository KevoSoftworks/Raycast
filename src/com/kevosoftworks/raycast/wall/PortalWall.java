package com.kevosoftworks.raycast.wall;

import com.kevosoftworks.raycast.Location;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.art.Texture;

public class PortalWall extends Wall{
	
	int rid;
	
	float pStart;
	float pStop;
	
	public PortalWall(int id, Location p1, Location p2, Vector2 norm, Texture tex, float pStart, float pStop, float height, int rid){
		super(id, p1, p2, norm, tex, height);
		this.rid = rid;
		this.pStart = pStart;
		this.pStop = pStop;
		this.wallType = Wall.WALLTYPE_PORTAL;
	}
	
	public int getRoomId(){
		return this.rid;
	}
	
	public float getPortalHeight(){
		return this.pStop - this.pStart;
	}
	
	public float getPortalStart(){
		return this.pStart;
	}
	
	public float getPortalStop(){
		return this.pStop;
	}
	
	public void setPortalStart(float start){
		this.pStart = start;
	}
	
	public void setPortalStop(float stop){
		this.pStop = stop;
	}
	
	public void setRoomId(int rid){
		this.rid = rid;
	}

}
