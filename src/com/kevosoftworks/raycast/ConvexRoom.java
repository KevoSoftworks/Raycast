package com.kevosoftworks.raycast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;

import com.kevosoftworks.raycast.art.Mipmap;
import com.kevosoftworks.raycast.art.Texture;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.wall.PortalWall;
import com.kevosoftworks.raycast.wall.Wall;

public class ConvexRoom{
	
	public int uuid;
	ArrayList<Wall> walls;
	
	int floorTexNum;
	int ceilTexNum;

	float lightDist = 16f;
	float maxLight = 0.6f;
	
	boolean forceLightRender = true;
	
	static final int FLOOR_MIPMAP = 2;
	
	public ConvexRoom(ArrayList<Wall> walls, int uuid, int floorTex, int ceilTex){
		this.uuid = uuid;
		this.walls = walls;
		this.floorTexNum = floorTex;
		this.ceilTexNum = ceilTex;
	}
	
	public int getUUID(){
		return this.uuid;
	}
	
	public Wall[] getWalls(){
		return this.walls.toArray(new Wall[walls.size()]);
	}
	
	public void addWall(Wall w){
		this.walls.add(w);
	}
	
	public void render(Map m, Graphics[] gA, int originroom){
		gA[1].drawImage(m.art.getSkybox().getBufferedImage(), 0, 0, Main.RW, Main.RH, null);
		//Cast Rays
		for(int i = 0; i <= Main.RW; i++){
			this.renderRay(m, gA, i, originroom, 0);
		}
	}
	
	public void renderRay(Map m, Graphics[] gA, int i, int originroom, float wallTopScreenCoord){	
		if(!m.time.containsKey(uuid)){
			m.time.put(uuid, 0l);
			m.timeWall.put(uuid, 0l);
			m.timeCeil.put(uuid, 0l);
			m.timeFloor.put(uuid, 0l);
		}
		
		long curTime = System.nanoTime();
		long otherTime = 0;
		forceLightRender = true;
		
		Location pLoc1 = new Location(m.camera.getLocation().getX() + m.camera.direction.getX() - m.camera.plane.getX(), m.camera.getLocation().getY() + m.camera.direction.getY() - m.camera.plane.getY());
		Location pLoc2 = new Location(m.camera.getLocation().getX() + m.camera.direction.getX() + m.camera.plane.getX(), m.camera.getLocation().getY() + m.camera.direction.getY() + m.camera.plane.getY());
		//Remap x-coordinate to [-1, 1] interval
		float cameraX = (float) (2f * ((float)i/Main.RW) - 1f);
		Vector2 rayDir = new Vector2(
					m.camera.direction.getX() + m.camera.plane.getX() * cameraX,
					m.camera.direction.getY() + m.camera.plane.getY() * cameraX
				);
		Location rLoc = new Location(m.camera.getLocation().getX() + rayDir.getX(), m.camera.getLocation().getY() + rayDir.getY());
		for(Wall w:walls){
			long curTimeWall = System.nanoTime();
			Location[] l = w.getLocations();
			//We still want to do some culling here.
			
			Location intersect = m.getIntersectionLocation(rLoc, l);
			if(intersect != null){
				float dist = (float) (Math.abs(
							((intersect.getX() - pLoc1.getX()) * -(pLoc2.getY() - pLoc1.getY())) + 
							((intersect.getY() - pLoc1.getY()) * (pLoc2.getX() - pLoc1.getX()))
						) / Math.sqrt(
								Math.pow(pLoc2.getY() - pLoc1.getY(),2) + Math.pow(pLoc2.getX() - pLoc1.getX(),2)
						));
				float distFrac = (float)dist / lightDist;
				float sf = 1f - distFrac;
				if(sf > maxLight) sf = maxLight;
				if(sf < 0) sf = 0;
				
				float portalH = 0;
				
				float lH = 1/dist;
				if(lH > 255) lH = 255;
				float wallTop = -lH - (w.getHeight() - 1f)*2f*lH;
				
				long ot2 = 0;
				if(w instanceof PortalWall){
					PortalWall pw = (PortalWall) w;
					int ruid = pw.getRoomUuid();
					portalH = ((PortalWall) w).getPortalHeight();
					long ot = System.nanoTime();
					if(ruid != originroom && (sf > 0 || forceLightRender)) m.getRoom(((PortalWall)w).getRoomUuid()).renderRay(m, gA, i, this.uuid, (wallTop < 0) ? 0 : wallTop);
					ot2 = System.nanoTime() - ot;
					otherTime += ot2;
					
				}				
				if(portalH < w.getHeight()){						
					Texture t = m.art.getTexture(w.getTextureNumber());
					int[] texture = t.getColumn(l[0].distance(intersect), dist);
					
					BufferedImage bi = new BufferedImage(1, (int)(texture.length * (w.getHeight() - portalH)), BufferedImage.TYPE_INT_ARGB);
					for(int y = 0; y < bi.getHeight(); y++){
						bi.setRGB(0, y, texture[y % texture.length]);
					}
					
					RescaleOp ro = new RescaleOp(
								new float[]{sf, sf, sf, 1f},
								new float[]{0f, 0f, 0f, 0f},
								null);
					if(sf > 0 || forceLightRender)gA[1].drawImage(ro.filter(bi, null), i, (int)Math.ceil(((wallTop + 1f) / 2f) * Main.RH), 1, (int)Math.ceil(Main.RH * lH * (w.getHeight()-portalH)), null);
				}
				m.timeWall.put(uuid, m.timeWall.get(uuid) + System.nanoTime() - curTimeWall - ot2);
			}
			if(intersect == null){
				m.timeWall.put(this.uuid, m.timeWall.get(uuid) + System.nanoTime() - curTimeWall);
			}
			
		}
		m.time.put(this.uuid, m.time.get(uuid) + System.nanoTime() - curTime - otherTime);
	}
	
	public int darkenColor(int c, float ra){
		int a = (c >> 24) & 0xFF;
		int r = (int)(((c >> 16) & 0xFF) * ra);
		int g = (int)(((c >> 8) & 0xFF) * ra);
		int b = (int)((c & 0xFF) * ra);
		
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}
