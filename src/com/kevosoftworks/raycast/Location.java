package com.kevosoftworks.raycast;

import java.io.Serializable;

import com.kevosoftworks.raycast.vector.Vector2;

public class Location implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	float x;
	float y;
	float rot;
	
	public Location(float x, float y){
		this.x = x;
		this.y = y;
		this.rot = 0;
	}
	
	public Location(float x, float y, float rot){
		this.x = x;
		this.y = y;
		this.rot = rot;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public float getRot(){
		return this.rot;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void setRot(float rot){
		this.rot = rot;
	}
	
	public float distance(Location l){
		return (float)Math.sqrt(Math.pow(this.x - l.getX(),2) + Math.pow(this.y - l.getY(), 2));
	}
	
	public Vector2 getVector(){
		return new Vector2(this.x, this.y);
	}
}
