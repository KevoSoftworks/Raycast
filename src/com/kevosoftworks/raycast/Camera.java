package com.kevosoftworks.raycast;

import java.awt.geom.Point2D;

import com.kevosoftworks.raycast.matrix.Matrix2;
import com.kevosoftworks.raycast.vector.Vector2;

public class Camera {
	
	Location l;
	int fov = 90;
	float UPH = 8f;
	
	Vector2 direction;
	Vector2 plane;
	
	public Camera(Location l){
		this.l = l;
		direction = new Vector2(0,-1);
		plane = new Vector2(1,0);
	}
	
	public Location getLocation(){
		return this.l;
	}
	
	public void tick(InputHandler i){
		//this.l.setX((float)(3f*Math.sin(Main.ticks / 128d)));
		//this.l.setX(-0.5f);
		//this.l.setY(5f);
		//this.l.setY((float)(5f*Math.sin(Main.ticks / 256d)));
		//this.l.setRot((float)(Math.PI * 0.0));
		//this.l.setRot(0.5f*(float)Math.sin(Main.ticks/512d));
		if(i.reset) this.l = new Location(0,0,0);
		if(i.rotateleft){
			this.direction = this.getRotationMatrix(-0.05f).multiply(direction);
			this.plane = this.getRotationMatrix(-0.05f).multiply(plane);
		}
		if(i.rotateright){
			this.direction = this.getRotationMatrix(0.05f).multiply(direction);
			this.plane = this.getRotationMatrix(0.05f).multiply(plane);
		}
		
		if(i.keyup) this.l.setY(this.l.getY() - 0.05f);
		if(i.keydown) this.l.setY(this.l.getY() + 0.05f);
		if(i.keyleft) this.l.setX(this.l.getX() - 0.05f);
		if(i.keyright) this.l.setX(this.l.getX() + 0.05f);
	}
	
	public void render(){
		
	}
	
	public float minAngle(){
		return (this.l.getRot() - (0.5f * this.getFovInRadians())) % 2f * (float)Math.PI;
	}
	
	public float maxAngle(){
		return (this.l.getRot() + (0.5f * this.getFovInRadians())) % 2f * (float)Math.PI;
	}
	
	public float radiansPerPixel(){
		return this.getFovInRadians() / Main.RW;
	}
	
	public float getFovInRadians(){
		return ((float)fov) * (float)Math.PI / 180f;
	}
	
	public Point2D getPoint2D(Location l){
		float x = ((l.getX() - this.getLocation().getX()) / (UPH / Main.RH * Main.RW)) * (float)Math.cos(this.getLocation().getRot()) + ((l.getY() - this.getLocation().getY()) / UPH) * (float)Math.sin(this.getLocation().getRot());
		float y = ((l.getX() - this.getLocation().getX()) / (UPH / Main.RH * Main.RW)) * -1 * (float)Math.sin(this.getLocation().getRot()) + ((l.getY() - this.getLocation().getY()) / UPH) * (float)Math.cos(this.getLocation().getRot());
		
		return new Point2D.Float(
					(int)Math.floor(((x + 1f) / 2f) * (float)Main.RW),
					(int)Math.floor(((y + 1f) / 2f) * (float)Main.RH)
				);
	}
	
	public Point2D[] getPoints2D(Location[] l){
		Point2D[] p = new Point2D[l.length];
		for(int i = 0; i < l.length; i++){
			p[i] = getPoint2D(l[i]);
		}
		return p;
	}
	
	public Matrix2 getRotationMatrix(float a){
		return new Matrix2(
					new Vector2((float)Math.cos(a), (float)Math.sin(a)),
					new Vector2(-1f*(float)Math.sin(a), (float)Math.cos(a))
				);
	}

}
