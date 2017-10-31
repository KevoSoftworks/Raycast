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
		camera = new Camera(new Location(0,0));
		walls = new ArrayList<Wall>();
		
		walls.add(new Wall(new Location(-0.5f, -1f), new Location(0.5f, -1f), Color.red));
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
		for(Wall w:walls){
			Location[] l = w.getLocations();
			Point2D[] p = camera.getPoints2D(l);
			//We assume the screen to have 16 units in the y coordinate
			gA[1].setColor(w.getColor());
			gA[1].drawLine((int)p[0].getX(), (int)p[0].getY(), (int)p[1].getX(), (int)p[1].getY());
		}
		//Draw camera angle
		Location dLoc = new Location(camera.getLocation().getX() + camera.direction.getX(), camera.getLocation().getY() + camera.direction.getY());
		Location pLoc1 = new Location(camera.getLocation().getX() + camera.direction.getX() - camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() - camera.plane.getY());
		Location pLoc2 = new Location(camera.getLocation().getX() + camera.direction.getX() + camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() + camera.plane.getY());
		Point2D dCoord = camera.getPoint2D(dLoc);
		Point2D pCoord1 = camera.getPoint2D(pLoc1);
		Point2D pCoord2 = camera.getPoint2D(pLoc2);
		gA[1].setColor(Color.white);
		gA[1].drawLine((int)(0.5f * Main.RW), (int)(0.5f * Main.RH), (int)dCoord.getX(), (int)dCoord.getY());
		gA[1].drawLine((int)pCoord1.getX(), (int)pCoord1.getY(), (int)pCoord2.getX(), (int)pCoord2.getY());
		
		//Cast Rays
		for(int i = 0; i <= Main.RW; i++){
			//Remap x-coordinate to [-1, 1] interval
			float cameraX = 2 * ((float)i/(float)Main.RW) - 1;
			Vector2 rayDir = new Vector2(
						camera.direction.getX() + camera.plane.getX() * cameraX,
						camera.direction.getY() + camera.plane.getY() * cameraX
					);
			Location rLoc = new Location(camera.getLocation().getX() + rayDir.getX(), camera.getLocation().getY() + rayDir.getY());
			Point2D rCoord = camera.getPoint2D(rLoc);
			gA[1].setColor(Color.blue);
			if(i == 0 || i == Main.RW) gA[1].drawLine((int)(0.5f * Main.RW), (int)(0.5f * Main.RH), (int)rCoord.getX(), (int)rCoord.getY());
			gA[1].setColor(Color.yellow);
			for(Wall w:walls){
				Location[] l = w.getLocations();
				Location intersect = getIntersectionLocation(rLoc, l);
				if(intersect == null) continue;
				Point2D iCoord = camera.getPoint2D(intersect);
				gA[1].drawLine((int)iCoord.getX(), (int)iCoord.getY(), (int)iCoord.getX(), (int)iCoord.getY());
				//calculate distance: https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
				//then draw the line: http://lodev.org/cgtutor/raycasting.html
			}
		}
		/*float rayDist = (float)Math.sqrt(Math.pow(0 - (int)(0.5f * Main.RW),2) + Math.pow(0.5f*Main.RH, 2));
		float rayDist2 = (float)Math.sqrt(Math.pow(Main.RW - (int)(0.5f * Main.RW),2) + Math.pow(0.5f*Main.RH, 2));
		
		float addAngle = -1f*(float)Math.acos((0.5f*(float)Main.RW)/rayDist);
		float addAngle2 = (float)Math.acos((0.5f*(float)Main.RW)/rayDist2);
		
		if(isTopDown)gA[1].drawLine((int)(0.5f * Main.RW), (int)(0.5f * Main.RH), (int)(0.5f * Main.RW + (0.5f * Main.RH * addAngle)), 0);
		if(isTopDown)gA[1].drawLine((int)(0.5f * Main.RW), (int)(0.5f * Main.RH), (int)(0.5f * Main.RW + (0.5f * Main.RH * addAngle2)), 0);
		
		float angle = camera.minAngle();
		for(int i = 0; i <= Main.RW; i++){
			rayDist = (float)Math.sqrt(Math.pow((float)i - (float)(0.5f * Main.RW),2) + Math.pow(0.5f*Main.RH, 2));
			addAngle = (float)Math.acos((0.5f*(float)Main.RW)/rayDist);
			if(i < 0.5 * Main.RW) addAngle *= -1f;
			if(angle > camera.maxAngle()) break;
			ArrayList<Location> drawable = new ArrayList<Location>();
			Line2D line = new Line2D.Float((0.5f * Main.RW), (0.5f * Main.RH), (0.5f * Main.RW + (0.5f * Main.RH * addAngle)), 0);
			for(Wall w:walls){
				Location l2 = new Location(((l.getX() - camera.getLocation().getX()) / (8f / Main.RH * Main.RW)) * (float)Math.cos(camera.getLocation().getRot()) + ((l.getY() - camera.getLocation().getY()) / 8f) * (float)Math.sin(camera.getLocation().getRot()), ((l.getX() - camera.getLocation().getX()) / (8f / Main.RH * Main.RW)) * -1 * (float)Math.sin(camera.getLocation().getRot()) + ((l.getY() - camera.getLocation().getY()) / 8f) * (float)Math.cos(camera.getLocation().getRot()));
				Rectangle r = new Rectangle((int)(((l2.getX() + 1) / 2) * Main.RW), (int)(((l2.getY() + 1) / 2) * Main.RH), (int)Math.ceil(Main.RH / 16f), (int)Math.ceil(Main.RH / 16f));
				if(line.intersects(r)) drawable.add(l);
			}
			
			Location best = null;
			Point2D best_inter = null;
			float dist = Float.MAX_VALUE;
			for(Location l2:drawable){
				//if(i == Main.RW / 2) System.out.println("Wauw");
				Location l3 = new Location(((l2.getX() - camera.getLocation().getX()) / (8f / Main.RH * Main.RW)) * (float)Math.cos(camera.getLocation().getRot()) + ((l2.getY() - camera.getLocation().getY()) / 8f) * (float)Math.sin(camera.getLocation().getRot()), ((l2.getX() - camera.getLocation().getX()) / (8f / Main.RH * Main.RW)) * -1 * (float)Math.sin(camera.getLocation().getRot()) + ((l2.getY() - camera.getLocation().getY()) / 8f) * (float)Math.cos(camera.getLocation().getRot()));
				Rectangle r = new Rectangle((int)(((l3.getX() + 1) / 2) * Main.RW), (int)(((l3.getY() + 1) / 2) * Main.RH), (int)Math.ceil(Main.RH / 16f), (int)Math.ceil(Main.RH / 16f));
				Point2D intersect = getIntersectionPoint(line, r, new Point2D.Double(0.5d * (double)Main.RW, 0.5d * (double)Main.RH));
				if(l2.getColor() != Color.yellow){
					gA[1].setColor(Color.cyan);
					//gA[1].fillRect((int)r.getX(),(int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
					gA[1].setColor(Color.magenta);
					if(intersect != null){
						gA[1].fillRect((int)Math.floor(intersect.getX()), (int)Math.floor(intersect.getY()), 1, 1);
						gA[1].setColor(Color.pink);
						gA[1].drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
					}
					
					//if(intersect != null)System.out.println("X:" + r.getX() + "; Y:" + r.getY());
				}
				if(i == Main.RW / 2&& intersect == null){
					//System.out.println("Rip");
				}
				if(intersect == null) continue;
				float tmpdist = (float) Math.sqrt(Math.pow((0.5d * Main.RW) - intersect.getX(), 2) + Math.pow((0.5d * Main.RH) - intersect.getY(), 2));
				//if((0.5d * Main.RW) - intersect.getX() == 0 || i == Main.RW / 2) System.out.println(tmpdist);
				//float tmpdist = l2.distance(camera.getLocation());
				if(tmpdist < dist) {
					best = l2;
					dist = tmpdist;
					best_inter = intersect;
				}
			}
			
			if(best != null){
				if(dist > 0){
					gA[1].setColor(Color.DARK_GRAY);
					gA[1].fillRect((int)Math.floor(best_inter.getX()), (int)Math.floor(best_inter.getY()), 1, 1);
					
					float delta = 0.5f * (Main.RH - dist/* * (float)Math.cos(addAngle));//0.5f * (5f - dist) / 5f;
					if(delta < 0) delta = 0.2f;
					
					int rc = (int) (best.getColor().getRed() * Math.pow((delta) / (0.5f*Main.RH), 2));
					int gc = (int) (best.getColor().getGreen() * Math.pow((delta) / (0.5f*Main.RH), 2));
					int bc = (int) (best.getColor().getBlue() * Math.pow((delta) / (0.5f*Main.RH), 2));
					
					if(rc > 255) rc = 255;
					if(rc < 0) rc = 0;
					if(gc > 255) gc = 255;
					if(gc < 0) gc = 0;
					if(bc > 255) bc = 255;
					if(bc < 0) bc = 0;
					
					gA[1].setColor(new Color(rc, gc, bc));
					//if(best.getColor() == Color.green) System.out.println(dist);
					if(delta <= 0){
						System.out.println("rofl");
					}
					if(!isTopDown)if(delta > 0)gA[1].drawLine(i, (int)(0.5f * Main.RH + delta), i, (int)(0.5f * Main.RH - delta));//gA[1].drawLine(i, (int)(0.5f * Main.RH + delta * 0.5f * Main.RH), i, (int)(0.5f * Main.RH - delta * 0.5f * Main.RH));
				}
			} else {
				//gA[1].setColor(Color.white);
				//gA[1].drawLine(i, 0, i, Main.RH);
			}
			angle += camera.radiansPerPixel();
			//System.out.println(0.5 * Math.PI / camera.radiansPerPixel());
		}*/
			
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
