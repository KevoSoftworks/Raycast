package com.kevosoftworks.raycast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.kevosoftworks.raycast.art.Art;
import com.kevosoftworks.raycast.wall.PortalWall;
import com.kevosoftworks.raycast.wall.SolidWall;
import com.kevosoftworks.raycast.wall.Wall;

public class Map {
	
	Camera camera;
	Art art;
	ArrayList<ConvexRoom> rooms;
	int curuuid = 1;
	
	boolean isTopDown = false;
	boolean renderMap = false;
	
	HashMap<Integer, Long> time;
	HashMap<Integer, Long> timeWall;
	HashMap<Integer, Long> timeFloor;
	HashMap<Integer, Long> timeCeil;
	
	public Map(){
		art = new Art();
		camera = new Camera(this, new Location(0f,0f));
		rooms = new ArrayList<ConvexRoom>();
		
		ArrayList<Wall>w1 = new ArrayList<Wall>();
		ArrayList<Wall>w2 = new ArrayList<Wall>();
		ArrayList<Wall>w3 = new ArrayList<Wall>();
		ArrayList<Wall>w4 = new ArrayList<Wall>();
		ArrayList<Wall>w5 = new ArrayList<Wall>();
		ArrayList<Wall>w6 = new ArrayList<Wall>();
		ArrayList<Wall>w7 = new ArrayList<Wall>();

		w1.add(new SolidWall(new Location(-5f, -3f), new Location(-2.5f, -3.5f), Art.TEXTURE_WALL_DARK_RED,2));
		w1.add(new SolidWall(new Location(-1.5f, -3.5f), new Location(2f, -5f), Art.TEXTURE_WALL_DARK_RED,2));
		w1.add(new PortalWall(new Location(-2.5f, -3.5f), new Location(-1.5f, -3.5f), Art.TEXTURE_WALL, 2, 2, 3));
		w1.add(new SolidWall(new Location(2f, -5f), new Location(4f, -4f), Art.TEXTURE_WALL, 2));
		w1.add(new SolidWall(new Location(4f, -4f), new Location(6f, 3f), Art.TEXTURE_WALL, 2));
		w1.add(new SolidWall(new Location(0f, 5f), new Location(-5f, 5f), Art.TEXTURE_WALL, 2));
		w1.add(new SolidWall(new Location(-5f, 5f), new Location(-5f, 1f), Art.TEXTURE_WALL_BLUE, 2));
		w1.add(new SolidWall(new Location(-5f, 1f), new Location(-5f, 0f), Art.TEXTURE_WALL_DARK_BLUE, 2));
		w1.add(new SolidWall(new Location(-5f, 0f), new Location(-5f, -3f), Art.TEXTURE_WALL_BLUE, 2));
		w1.add(new PortalWall(new Location(6f, 3f), new Location(0f, 5f), Art.TEXTURE_WALL, 2, 1, 2));
		
		w2.add(new SolidWall(new Location(6f, 3f), new Location(20f, 10f), Art.TEXTURE_WALL));
		w2.add(new SolidWall(new Location(20f, 10f), new Location(16f, 13f), Art.TEXTURE_WALL));
		w2.add(new SolidWall(new Location(16f, 13f), new Location(6f, 13f), Art.TEXTURE_WALL));
		w2.add(new SolidWall(new Location(6f, 13f), new Location(0f, 5f), Art.TEXTURE_WALL));
		w2.add(new PortalWall(new Location(6f, 3f), new Location(0f, 5f), Art.TEXTURE_WALL, 1, 1, 1));
		
		w3.add(new PortalWall(new Location(-2.5f, -3.5f), new Location(-1.5f, -3.5f), Art.TEXTURE_WALL, 2, 2, 1));
		w3.add(new SolidWall(new Location(-2.5f, -3.5f), new Location(-2.5f, -10f), Art.TEXTURE_WALL_RED, 2));
		w3.add(new SolidWall(new Location(-1.5f, -3.5f), new Location(-1.5f, -10f), Art.TEXTURE_WALL_RED, 2));
		w3.add(new PortalWall(new Location(-2.5f, -10f), new Location(-1.5f, -10f), Art.TEXTURE_WALL, 2, 1, 4));
		
		w4.add(new PortalWall(new Location(-2.5f, -10f), new Location(-1.5f, -10f), Art.TEXTURE_WALL, 1, 1, 3));
		w4.add(new PortalWall(new Location(-2.5f, -12f), new Location(-1.5f, -12f), Art.TEXTURE_WALL, 1, 1, 6));
		w4.add(new PortalWall(new Location(-2.5f, -10f), new Location(-2.5f, -12f), Art.TEXTURE_WALL, 1, 1, 5));
		w4.add(new PortalWall(new Location(-1.5f, -10f), new Location(-1.5f, -12f), Art.TEXTURE_WALL, 1, 1, 7));
		
		w5.add(new PortalWall(new Location(-2.5f, -10f), new Location(-2.5f, -12f), Art.TEXTURE_WALL, 1, 1, 4));
		w5.add(new SolidWall(new Location(-2.5f, -10f), new Location(-5f, -10f), Art.TEXTURE_WALL_DARK_BLUE));
		w5.add(new SolidWall(new Location(-2.5f, -12f), new Location(-5f, -12f), Art.TEXTURE_WALL_DARK_BLUE));
		w5.add(new SolidWall(new Location(-5f, -10f), new Location(-5f, -12f), Art.TEXTURE_WALL_DARK_BLUE));
		
		w6.add(new PortalWall(new Location(-2.5f, -12f), new Location(-1.5f, -12f), Art.TEXTURE_WALL, 1, 1, 4));
		w6.add(new SolidWall(new Location(-2.5f, -12f), new Location(-2.5f, -17f), Art.TEXTURE_WALL_GREEN));
		w6.add(new SolidWall(new Location(-1.5f, -12f), new Location(0f, -16f), Art.TEXTURE_WALL_GREEN));
		w6.add(new SolidWall(new Location(-2.5f, -17f), new Location(0f, -16f), Art.TEXTURE_WALL_GREEN));
		
		w7.add(new PortalWall(new Location(-1.5f, -10f), new Location(-1.5f, -12f), Art.TEXTURE_WALL, 1, 1, 4));
		w7.add(new SolidWall(new Location(-1.5f, -10f), new Location(5f, -10f), Art.TEXTURE_WALL_BLUE));
		w7.add(new SolidWall(new Location(-1.5f, -12f), new Location(5f, -12f), Art.TEXTURE_WALL_BLUE));
		w7.add(new SolidWall(new Location(5f, -10f), new Location(5f, -12f), Art.TEXTURE_WALL_BLUE));
		
		
		rooms.add(new ConvexRoom(w1, 1, Art.TEXTURE_GRASS, Art.TEXTURE_NONE));
		rooms.add(new ConvexRoom(w2, 2, Art.TEXTURE_WALL_RED, Art.TEXTURE_WALL));
		rooms.add(new ConvexRoom(w3, 3, Art.TEXTURE_GRASS, Art.TEXTURE_NONE));
		rooms.add(new ConvexRoom(w4, 4, Art.TEXTURE_WALL, Art.TEXTURE_WALL_DARK_BLUE));
		rooms.add(new ConvexRoom(w5, 5, Art.TEXTURE_WALL, Art.TEXTURE_WALL_DARK_BLUE));
		rooms.add(new ConvexRoom(w6, 6, Art.TEXTURE_WALL, Art.TEXTURE_WALL_DARK_BLUE));
		rooms.add(new ConvexRoom(w7, 7, Art.TEXTURE_WALL, Art.TEXTURE_WALL_DARK_BLUE));
	}
	
	public void tick(InputHandler input){
		camera.tick(input);
		//System.out.println("Min: " + camera.minAngle() + "; Max: " + camera.maxAngle());
		if(input.switchview){
			input.switchview = false;
			isTopDown = !isTopDown;
		}
		renderMap = input.renderMap;
	}
	
	public void render(Graphics[] gA){
		time = new HashMap<Integer, Long>();
		timeWall = new HashMap<Integer, Long>();
		timeFloor = new HashMap<Integer, Long>();
		timeCeil = new HashMap<Integer, Long>();
		
		gA[2].drawImage(art.text("TPS: " + Main.tps + "; FPS: " + Main.fps + "; Resolution: " + Main.RW + "x" + Main.RH), 0, 0, null);
		gA[2].drawImage(art.text("Use WASD to move, the arrow keys to rotate and SHIFT to sprint", Color.CYAN), Main.RW / 2 - art.text("Use WASD to move, the arrow keys to rotate and SHIFT to sprint").getWidth() / 2, (int) (Main.RH - 20 + 7*Math.sin((float)Main.ticks / 15f)), null);
		
		Location dLoc = new Location(camera.getLocation().getX() + camera.direction.getX(), camera.getLocation().getY() + camera.direction.getY());
		Location pLoc1 = new Location(camera.getLocation().getX() + camera.direction.getX() - camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() - camera.plane.getY());
		Location pLoc2 = new Location(camera.getLocation().getX() + camera.direction.getX() + camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() + camera.plane.getY());
		Point2D dCoord = camera.getPoint2D(dLoc);
		Point2D pCoord1 = camera.getPoint2D(pLoc1);
		Point2D pCoord2 = camera.getPoint2D(pLoc2);
		if(renderMap){
			for(ConvexRoom cr:rooms){
				Wall[] walls = cr.getWalls();
				for(Wall w:walls){
					Location[] l = w.getLocations();
					Point2D[] p = camera.getPoints2D(l);
					//We assume the screen to have 16 units in the y coordinate
					gA[1].setColor(Color.white);
					gA[1].drawLine((int)p[0].getX(), (int)p[0].getY(), (int)p[1].getX(), (int)p[1].getY());
				}
			}
			//Draw camera angle
			gA[1].setColor(Color.white);
			gA[1].drawLine((int)(0.5f * Main.RW), (int)(0.5f * Main.RH), (int)dCoord.getX(), (int)dCoord.getY());
			gA[1].drawLine((int)pCoord1.getX(), (int)pCoord1.getY(), (int)pCoord2.getX(), (int)pCoord2.getY());
		}
		if(!isTopDown){
			this.getCurrentRoom().render(this, gA, this.curuuid);
		}
		
		int num = 1;
		double sum = 0;
		DecimalFormat df = new DecimalFormat("#.##");
		for(int key:time.keySet()){
			double val = time.get(key)/1000000d;
			double valWall = timeWall.get(key)/1000000d;
			double valFloor = timeFloor.get(key)/1000000d;
			double valCeil = timeCeil.get(key)/1000000d;
			gA[1].drawImage(art.text(key+ ": " + df.format(val) + "ms"), 0, 10*num, null);
			gA[1].drawImage(art.text("| W=" + df.format(valWall) + "ms;"), 75, 10*num, null);
			gA[1].drawImage(art.text("F=" + df.format(valFloor) + "ms;"), 150, 10*num, null);
			gA[1].drawImage(art.text("C=" + df.format(valCeil) + "ms;"), 225, 10*num, null);
			num++;
			sum += val;
		}
		gA[1].drawImage(art.text("Total: " + df.format(sum) + "ms;"), 0, 10*(num+1), null);
		gA[1].drawImage(art.text("FPS: " + df.format(1/(sum/1000d))), 85, 10*(num+1), null);
	}
    
    public Location getIntersectionLocation(Location ray, Location[] wall){
    	Location camera = this.camera.getLocation();
    	
    	float dyRay = ray.getY() - camera.getY();
    	float dxRay = ray.getX() - camera.getX();
    	
    	float dyWall = wall[1].getY() - wall[0].getY();
    	float dxWall = wall[1].getX() - wall[0].getX();
    	
    	float xIntersect;
    	float yIntersect;
    	boolean yForce = false;
    	
    	if(dxRay == 0f){
    		if(dxWall == 0f) return null;
    		float slopeWall = dyWall / dxWall;
    		float offsetWall = wall[1].getY() - (wall[1].getX() * slopeWall);
    		
    		xIntersect = ray.getX();
    		yIntersect = slopeWall * xIntersect + offsetWall;
    	} else if(dxWall == 0f){
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
    	
    	if(dyWall == 0) yForce = true;
    	
    	Location r = new Location(xIntersect, yIntersect);
    	if(xIntersect >= Math.min(wall[1].getX(), wall[0].getX()) && xIntersect <= Math.max(wall[1].getX(), wall[0].getX())){
    		if((yIntersect >= Math.min(wall[1].getY(), wall[0].getY()) && yIntersect <= Math.max(wall[1].getY(), wall[0].getY())) || yForce){
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
    
    public ConvexRoom getCurrentRoom(){
    	return this.getRoom(this.curuuid);
    }
    
    public ConvexRoom getRoom(int uuid){
    	for(ConvexRoom cr:rooms){
    		if(cr.getUUID() == uuid) return cr;
    	}
    	return null;
    }

}
