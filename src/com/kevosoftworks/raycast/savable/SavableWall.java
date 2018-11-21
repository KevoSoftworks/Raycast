package com.kevosoftworks.raycast.savable;

import com.kevosoftworks.raycast.Location;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.wall.PortalWall;
import com.kevosoftworks.raycast.wall.Wall;

public class SavableWall extends Savable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private Location p1;
	private Location p2;
	private int texNum;
	private float height;
	private Vector2 normal;
	private int wallType;
	
	//PortalWall
	private int rid;
	private float pStart;
	private float pStop;
	
	public SavableWall(Wall w){
		this.id = w.getId();
		this.p1 = w.getLocation1();
		this.p2 = w.getLocation2();
		this.texNum = w.getTexture().getNumber();
		this.height = w.getHeight();
		this.normal = w.getNormal();
		this.wallType = w.getWallType();
		
		if(w instanceof PortalWall){
			PortalWall pw = (PortalWall) w;
			this.rid = pw.getRoomId();
			this.pStart = pw.getPortalStart();
			this.pStop = pw.getPortalStop();
		}
	}
	
	public int getId(){
		return this.id;
	}
	
	public Location getLocation1(){
		return this.p1;
	}
	
	public Location getLocation2(){
		return this.p2;
	}
	
	public int getTextureNumber(){
		return this.texNum;
	}
	
	public float getHeight(){
		return this.height;
	}
	
	public Vector2 getNormal(){
		return this.normal;
	}
	
	public int getWallType(){
		return this.wallType;
	}
	
	public int getPortalRoomId(){
		return this.rid;
	}
	
	public float getPortalStart(){
		return this.pStart;
	}
	
	public float getPortalStop(){
		return this.pStop;
	}

}
