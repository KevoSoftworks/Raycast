package com.kevosoftworks.raycast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.kevosoftworks.raycast.art.Art;
import com.kevosoftworks.raycast.matrix.Matrix2;
import com.kevosoftworks.raycast.savable.SavableConvexRoom;
import com.kevosoftworks.raycast.savable.SavableMap;
import com.kevosoftworks.raycast.savable.SavableWall;
import com.kevosoftworks.raycast.savable.SaveFile;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.wall.PortalWall;
import com.kevosoftworks.raycast.wall.SolidWall;
import com.kevosoftworks.raycast.wall.Wall;

public class Map {
	
	Camera camera;
	Art art;
	ArrayList<ConvexRoom> rooms;
	int curuuid = 0;
	SaveFile sf;
	
	boolean isTopDown = false;
	boolean renderMap = false;
	boolean renderDebugText = false;
	
	boolean renderFloor = true;
	
	HashMap<Integer, Long> time;
	HashMap<Integer, Long> timeWall;
	HashMap<Integer, Long> timeFloor;
	HashMap<Integer, Long> timeCeil;
	
	public Map(){
		sf = new SaveFile("maps/");
		art = new Art();
		camera = new Camera(this, new Location(0f,0f));
		rooms = new ArrayList<ConvexRoom>();
		
		System.out.println("Loading map!");
		SavableMap m = sf.loadMap();
		for(SavableConvexRoom scr:m.getRooms()){
			ArrayList<Wall> w = new ArrayList<Wall>();
			for(SavableWall sw:scr.getWalls()){
				if(sw.getWallType() == Wall.WALLTYPE_PORTAL){
					w.add(new PortalWall(
								sw.getId(),
								sw.getLocation1(),
								sw.getLocation2(),
								sw.getNormal(),
								art.getTexture(sw.getTextureNumber()),
								sw.getPortalStart(),
								sw.getPortalStop(),
								sw.getHeight(),
								sw.getPortalRoomId()
							));
				} else {
					w.add(new SolidWall(
								sw.getId(),
								sw.getLocation1(),
								sw.getLocation2(),
								sw.getNormal(),
								art.getTexture(sw.getTextureNumber()),
								sw.getHeight()
							));
				}
			}
			ConvexRoom r = new ConvexRoom(scr.getId(), w);
			r.setCeilingProperties(scr.hasCeiling(), art.getTexture(scr.getCeilingTextureNumber()), scr.getCeilingTextureScale());
			r.setFloorProperties(scr.hasFloor(), art.getTexture(scr.getFloorTextureNumber()), scr.getFloorTextureScale());
			r.setZHeight(scr.getZHeight());
			rooms.add(r);
		}
	}
	
	public void tick(InputHandler input){
		camera.tick(input);
		camera.setFOV(80f);
		//System.out.println("Min: " + camera.minAngle() + "; Max: " + camera.maxAngle());
		if(input.renderFloor){
			input.renderFloor = false;
			renderFloor = !renderFloor;
		}
		renderMap = input.renderMap;
		renderDebugText = input.renderDebugText;
		
		ConvexRoom r = this.getRoom(15);
		r.setZHeight((float)Math.sin(Main.ticks / 128f)+0.8f);
	}
	
	public void render(Graphics[] gA, BufferedImage[] bI){		
		time = new HashMap<Integer, Long>();
		timeWall = new HashMap<Integer, Long>();
		timeFloor = new HashMap<Integer, Long>();
		timeCeil = new HashMap<Integer, Long>();
		
		Location dLoc = new Location(camera.getLocation().getX() + camera.direction.getX(), camera.getLocation().getY() + camera.direction.getY());
		Location pLoc1 = new Location(camera.getLocation().getX() + camera.direction.getX() - camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() - camera.plane.getY());
		Location pLoc2 = new Location(camera.getLocation().getX() + camera.direction.getX() + camera.plane.getX(), camera.getLocation().getY() + camera.direction.getY() + camera.plane.getY());
		Point2D dCoord = camera.getPoint2D(dLoc);
		Point2D pCoord1 = camera.getPoint2D(pLoc1);
		Point2D pCoord2 = camera.getPoint2D(pLoc2);
		if(!isTopDown){
			this.getCurrentRoom().render(this, gA, bI, this.curuuid);
		}
		
		if(renderMap){
			for(ConvexRoom cr:rooms){
				Wall[] walls = cr.getWalls();
				for(Wall w:walls){
					if(w instanceof PortalWall && ((PortalWall) w).getPortalStart() < 0.5f) continue;
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
		if(renderDebugText){
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
			
			gA[1].drawImage(art.text("TPS: " + Main.tps + "; FPS: " + Main.fps + "; Resolution: " + Main.RW + "x" + Main.RH), 0, 0, null);
		}
		gA[1].drawImage(art.text("Move: WASD; Look: Mouse; Sprint: SHIFT; Map: Q; Debug: F1;", Color.CYAN), Main.RW / 2 - art.text("Move: WASD; Look: Mouse; Sprint: SHIFT; Map: Q; Debug: F1;").getWidth() / 2, Main.RH - 12, null);
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
    		if(cr.getId() == uuid) return cr;
    	}
    	return null;
    }
    
    public ConvexRoom[] getRooms(){
    	return this.rooms.toArray(new ConvexRoom[1]);
    }

}
