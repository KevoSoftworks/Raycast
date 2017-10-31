package com.kevosoftworks.raycast.matrix;

import com.kevosoftworks.raycast.vector.Vector2;

public class Matrix2 extends Matrix{
	
	Vector2[] vector;
	
	public Matrix2(Vector2[] c){
		this.vector = c;
	}
	
	public Matrix2(Vector2 c1, Vector2 c2){
		this.vector = new Vector2[2];
		this.vector[0] = c1;
		this.vector[1] = c2;		
	}
	
	public Vector2 multiply(Vector2 v){
		return new Vector2(
					vector[0].getX() * v.getX() + vector[1].getX() * v.getY(),
					vector[0].getY() * v.getX() + vector[1].getY() * v.getY()
				);
	}

}
