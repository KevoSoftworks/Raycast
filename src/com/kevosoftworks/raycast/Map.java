package com.kevosoftworks.raycast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.kevosoftworks.raycast.vector.Vector2;

public class Map {
	
	Camera camera;
	ArrayList<Wall> walls;
	
	boolean isTopDown = true;
	
	public Map(){
		camera = new Camera(new Location(0f,0f));
		walls = new ArrayList<Wall>();
		
		//walls.add(new Wall(new Location(), new Location(), Color.red));
		walls.add(new Wall(new Location(-5f, -3f), new Location(2f, -5f), Color.red));
		walls.add(new Wall(new Location(2f, -5f), new Location(4f, -4f), Color.green));
		walls.add(new Wall(new Location(4f, -4f), new Location(6f, 3f), Color.blue));
		walls.add(new Wall(new Location(6f, 3f), new Location(20f, 10f), Color.cyan));
		walls.add(new Wall(new Location(20f, 10f), new Location(16f, 13f), Color.orange));
		walls.add(new Wall(new Location(16f, 13f), new Location(6f, 13f), Color.magenta));
		walls.add(new Wall(new Location(6f, 13f), new Location(0f, 5f), Color.pink));
		walls.add(new Wall(new Location(0f, 5f), new Location(-5f, 5f), Color.gray));
		walls.add(new Wall(new Location(-5f, 5f), new Location(-5f, -3f), Color.yellow));
	}
	
	public void tick(InputHandler input){
		camera.tick(input);
		//System.out.println("Min: " + camera.minAngle() + "; Max: " + camera.maxAngle());
		if(input.switchview){
			input.switchview = false;
			isTopDown = !isTopDown;
		}
	}
	
	public void render(Graphics[] gA){
		Location dLoc = new Location(camera.getLocation().getX() + camera.direction.getX(), camera.getLocation().getY() + camera.direction.getY());
		Location pLoc1 = new Location(camera.getLocation().getX() + camera.direction.getX() - camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() - camera.plane.getY());
		Location pLoc2 = new Location(camera.getLocation().getX() + camera.direction.getX() + camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() + camera.plane.getY());
		Point2D dCoord = camera.getPoint2D(dLoc);
		Point2D pCoord1 = camera.getPoint2D(pLoc1);
		Point2D pCoord2 = camera.getPoint2D(pLoc2);
		for(Wall w:walls){
			Location[] l = w.getLocations();
			Point2D[] p = camera.getPoints2D(l);
			//We assume the screen to have 16 units in the y coordinate
			gA[1].setColor(w.getColor());
			gA[1].drawLine((int)p[0].getX(), (int)p[0].getY(), (int)p[1].getX(), (int)p[1].getY());
		}
		//Draw camera angle
		gA[1].setColor(Color.white);
		gA[1].drawLine((int)(0.5f * Main.RW), (int)(0.5f * Main.RH), (int)dCoord.getX(), (int)dCoord.getY());
		gA[1].drawLine((int)pCoord1.getX(), (int)pCoord1.getY(), (int)pCoord2.getX(), (int)pCoord2.getY());
		if(!isTopDown){	
			//Cast Rays
			for(int i = 0; i <= Main.RW; i++){
				//Remap x-coordinate to [-1, 1] interval
				float cameraX = 2 * ((float)i/(float)Main.RW) - 1;
				Vector2 rayDir = new Vector2(
							camera.direction.getX() + camera.plane.getX() * cameraX,
							camera.direction.getY() + camera.plane.getY() * cameraX
						);
				Location rLoc = new Location(camera.getLocation().getX() + rayDir.getX(), camera.getLocation().getY() + rayDir.getY());
				//Point2D rCoord = camera.getPoint2D(rLoc);
				//gA[1].setColor(Color.blue);
				//if(i == 0 || i == Main.RW) gA[1].drawLine((int)(0.5f * Main.RW), (int)(0.5f * Main.RH), (int)rCoord.getX(), (int)rCoord.getY());
				//gA[1].setColor(Color.yellow);
				for(Wall w:walls){
					Location[] l = w.getLocations();
					Location intersect = getIntersectionLocation(rLoc, l);
					if(intersect == null) continue;
					Point2D iCoord = camera.getPoint2D(intersect);
					//gA[1].drawLine((int)iCoord.getX(), (int)iCoord.getY(), (int)iCoord.getX(), (int)iCoord.getY());
					//calculate distance
					float dist = (float) (Math.abs(
								((iCoord.getX() - pCoord1.getX()) * -(pCoord2.getY() - pCoord1.getY())) + 
								((iCoord.getY() - pCoord1.getY()) * (pCoord2.getX() - pCoord1.getX()))
							) / Math.sqrt(
									Math.pow(pCoord2.getY() - pCoord1.getY(),2) + Math.pow(pCoord2.getX() - pCoord1.getX(),2)
							));
					//float dist = (float) Math.sqrt(Math.pow(dCoord.getX() - iCoord.getX(), 2) + Math.pow(dCoord.getY() - iCoord.getY(), 2));
					int lH = (int)(4*(float)Main.RH / dist);
					if(lH > Main.RH) lH = Main.RH;
					
					int rc = (int) (w.getColor().getRed() * (2*(float)lH / (float)Main.RH));
					int gc = (int) (w.getColor().getGreen() * (2*(float)lH / (float)Main.RH));
					int bc = (int) (w.getColor().getBlue() * (2*(float)lH / (float)Main.RH));
					
					if(rc > 255) rc = 255;
					if(rc < 0) rc = 0;
					if(gc > 255) gc = 255;
					if(gc < 0) gc = 0;
					if(bc > 255) bc = 255;
					if(bc < 0) bc = 0;
					
					gA[1].setColor(new Color(rc, gc, bc));
					gA[1].drawLine(i, (int)((float)Main.RH * 0.5f + lH * 0.5f), i, (int)((float)Main.RH * 0.5f - lH * 0.5f));
					//gA[1].setColor(Color.pink);
					//gA[1].drawLine((int)iCoord.getX(), (int)iCoord.getY(), (int)iCoord.getX(), (int)iCoord.getY());
				}
			}
		}			
	}
    
    public Location getIntersectionLocation(Location ray, Location[] wall){
    	Location camera = this.camera.getLocation();
    	
    	float dyRay = ray.getY() - camera.getY();
    	float dxRay = ray.getX() - camera.getX();
    	
    	float dyWall = wall[1].getY() - wall[0].getY();
    	float dxWall = wall[1].getX() - wall[0].getX();
    	
    	float xIntersect;
    	float yIntersect;
    	
    	if(dxRay == 0){
    		if(dxWall == 0) return null;
    		float slopeWall = dyWall / dxWall;
    		float offsetWall = wall[1].getY() - (wall[1].getX() * slopeWall);
    		
    		xIntersect = ray.getX();
    		yIntersect = slopeWall * xIntersect + offsetWall;
    	} else if(dxWall == 0){
    		float slopeRay = dyRay / dxRay;
	    	float offsetRay = ray.getY() - (ray.getX() * slopeRay);
	    	
	    	xIntersect = wall[1].getX();
	    	yIntersect = slopeRay * xIntersect + offsetRay;
    	} else {
	    	float slopeRay = dyRay / dxRay;
	    	float offsetRay = ray.getY() - (ray.getX() * slopeRay);
	    	float slopeWall = dyWall / dxWall;
	    	float offsetWall = wall[1].getY() - (wall[1].getX() * slopeWall);
	    	
	    	if(slopeRay == slopeWall) return null;
	    	xIntersect = (offsetWall - offsetRay) / (slopeRay - slopeWall);
	    	yIntersect = slopeRay * xIntersect + offsetRay;
    	}
    	Location r = new Location(xIntersect, yIntersect);
    	if(xIntersect >= Math.min(wall[1].getX(), wall[0].getX()) && xIntersect <= Math.max(wall[1].getX(), wall[0].getX())){
    		if(yIntersect >= Math.min(wall[1].getY(), wall[0].getY()) && yIntersect <= Math.max(wall[1].getY(), wall[0].getY())){
    			if(ray.getY() < camera.getY()){
    				//Up direction
    				if(yIntersect < camera.getY()){
    					if(ray.getX() < camera.getX()){
    						//left
    						if(xIntersect < camera.getX()) return r;
    					} else {
    						//right
    						if(xIntersect >= camera.getX()) return r;
    					}
    				}
    			} else {
    				//Down direction
    				if(yIntersect >= camera.getY()){
    					if(ray.getX() < camera.getX()){
    						//left
    						if(xIntersect < camera.getX()) return r;
    					} else {
    						//right
    						if(xIntersect >= camera.getX()) return r;
    					}
    				}
    			}
    		}
    	}
    	return null;
    }

}
