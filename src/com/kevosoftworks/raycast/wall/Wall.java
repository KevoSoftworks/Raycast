package com.kevosoftworks.raycast.wall;

import com.kevosoftworks.raycast.Location;
import com.kevosoftworks.raycast.art.Art;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.art.Texture;

public class Wall{
	
	public static final int WALLTYPE_NONE = 0;
	public static final int WALLTYPE_SOLID = 1;
	public static final int WALLTYPE_PORTAL = 2;
	
	int id;
	Location p1;
	Location p2;
	Texture tex;
	float height;
	Vector2 normal;
	int wallType = WALLTYPE_NONE;
	
	public Wall(int id, Location p1, Location p2, Vector2 norm, Texture tex){
		this.id = id;
		this.p1 = p1;
		this.p2 = p2;
		this.tex = tex;
		this.height = 1;
		this.normal = norm;
	}
	
	public Wall(int id, Location p1, Location p2, Vector2 norm, Texture tex, float height){
		this.id = id;
		this.p1 = p1;
		this.p2 = p2;
		this.tex = tex;
		this.height = height;
		this.normal = norm;
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
	
	public Texture getTexture(){
		return this.tex;
	}
	
	public void setTexture(Texture t){
		this.tex = t;
	}
	
	public float getHeight(){
		return this.height;
	}
	
	public void setHeight(float height){
		this.height = height;
	}
	
	public Vector2 getNormal(){
		return this.normal;
	}
	
	public void setNormal(Vector2 v){
		this.normal = v;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getWallType(){
		return this.wallType;
	}
}
