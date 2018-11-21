package com.kevosoftworks.raycast;

import java.awt.geom.Point2D;

import com.kevosoftworks.raycast.matrix.Matrix2;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.wall.PortalWall;
import com.kevosoftworks.raycast.wall.Wall;

public class Camera {
	
	Map m;
	Location l;
	float UPH = 8f;
	
	float walkSpeed = 0.05f;
	float rotateSpeed = 0.025f;
	
	Vector2 direction;
	Vector2 plane;
	
	int mouseX = 0;
	int mouseY = 0;
	float mouseSensitivity = 0.03f;
	
	float fov = 80f;
	
	private Matrix2 perpRotMat = this.getRotationMatrix((float)(Math.PI / 2d));
	
	public Camera(Map m, Location l){
		this.m = m;
		this.l = l;
		direction = new Vector2(0f,1f);
		plane = new Vector2(-1f, 0f);
		this.setFOV(this.fov);
	}
	
	public Location getLocation(){
		return this.l;
	}
	
	public void setFOV(float fov){
		if(fov > 90f) fov = 90f;
		this.plane.normalise();
		this.plane.multiply((float)(Math.tan(Math.toRadians(0.5f * fov))));
		this.fov = fov;
	}
	
	public void tick(InputHandler i){
		Vector2 nd = this.direction.normalised();
		Vector2 ndRot = this.perpRotMat.multiply(this.direction).normalised();
		
		if(i.mouseX < (Main.screenRes.width / 2) || i.mouseX > (Main.screenRes.width / 2)){
			if(i.inFocus){
				int diff = i.mouseX - Main.screenRes.width / 2;
				Matrix2 rotMat = this.getRotationMatrix(diff * rotateSpeed * mouseSensitivity);
				this.direction = rotMat.multiply(direction);
				this.plane = rotMat.multiply(plane);
				i.mouseX = Main.screenRes.width / 2;
				i.mouseY = Main.screenRes.height / 2;
				i.r.mouseMove(Main.screenRes.width / 2, Main.screenRes.height / 2);
			}
		}
		
		Vector2 movement = new Vector2(0,0);
		
		if(i.keyup){
			movement.add(nd);
		}
		if(i.keydown){
			movement.add(new Vector2(-nd.getX(),-nd.getY()));
		}
		if(i.keyleft){
			movement.add(new Vector2(-ndRot.getX(), -ndRot.getY()));
		}
		if(i.keyright){
			movement.add(ndRot);
		}
		
		movement.normalise();
		movement.multiply(walkSpeed);
		if(i.keyshift) movement.multiply(1.5f);
		
		//Collision
		for(Wall w:m.getCurrentRoom().getWalls()){
			Location is = m.getIntersectionLocation(new Location(this.l.getX() + movement.getX(), this.l.getY() + movement.getY()), w.getLocations());
			if(is != null){
				if(is.getX() >= Math.min(this.l.getX(), this.l.getX() + movement.getX()) && is.getX() <= Math.max(this.l.getX(), this.l.getX() + movement.getX())){
					if(is.getY() >= Math.min(this.l.getY(), this.l.getY() + movement.getY()) && is.getY() <= Math.max(this.l.getY(), this.l.getY() + movement.getY())){
						if(w instanceof PortalWall){
							m.curuuid = ((PortalWall)w).getRoomId();
						} else {
							movement = new Vector2(0, 0);
						}
					}
				}
			}
		}
		this.l.setX(this.l.getX() + movement.getX());
		this.l.setY(this.l.getY() + movement.getY());
	}
	
	public void render(){
		
	}
	
	public Point2D getPoint2D(Location l){
		float x = ((l.getX() - this.getLocation().getX()) / (UPH / (float)Main.RH * (float)Main.RW)) * (float)Math.cos(this.getLocation().getRot()) + ((l.getY() - this.getLocation().getY()) / UPH) * (float)Math.sin(this.getLocation().getRot());
		float y = ((l.getX() - this.getLocation().getX()) / (UPH / (float)Main.RH * (float)Main.RW)) * -1f * (float)Math.sin(this.getLocation().getRot()) + ((l.getY() - this.getLocation().getY()) / UPH) * (float)Math.cos(this.getLocation().getRot());
		
		return new Point2D.Double(
					((x + 1d) / 2f) * (float)Main.RW,
					((y + 1d) / 2f) * (float)Main.RH
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
