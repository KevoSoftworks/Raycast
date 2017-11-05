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
	
	public float getLength(){
		return (float)Math.sqrt(x*x + y*y);
	}
	
	public Vector2 normalised(){
		float length = this.getLength();
		if(length == 0) return new Vector2(0,0);
		return new Vector2(this.x / length, this.y / length);
	}
	
	public void normalise(){
		float length = this.getLength();
		if(length == 0){
			this.x = 0f;
			this.y = 0f;
		} else {
			this.x /= length;
			this.y /= length;
		}
	}
	
	public void add(Vector2 v){
		this.x += v.getX();
		this.y += v.getY();
	}
	
	public void multiply(float m){
		this.x *= m;
		this.y *= m;
	}

}
