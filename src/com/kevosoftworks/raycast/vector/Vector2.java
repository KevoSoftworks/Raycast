package com.kevosoftworks.raycast.vector;

public class Vector2 extends Vector{
	
	private float x;
	private float y;
	
	public Vector2(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public float dot(Vector2 v){
		return this.getX() * v.getX() + this.getY() * v.getY();
	}
	
	public Vector2 cross(Vector2 v){
		//We need a 3D vector for this
		/*return new Vector3(
					x,
					y,
					z
				);
		*/
		return null;
	}
	
	public Vector2 normalised(){
		float length = (float)Math.sqrt(x*x + y*y);
		return new Vector2(this.x / length, this.y / length);
	}

}
