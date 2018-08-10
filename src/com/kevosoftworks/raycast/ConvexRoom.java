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
	
	boolean forceLightRender = false;
	
	static final int FLOOR_MIPMAP = 2;
	
	final boolean hasFloor = true;
	final boolean hasCeiling = false;
	
	int[] floorPxStart = new int[Main.RW];
	Mipmap floor = null;
	
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
		floor = m.art.getTexture(this.floorTexNum).getMipmap(0);
		gA[1].drawImage(m.art.getSkybox().getBufferedImage(), 0, 0, Main.RW, Main.RH, null);
		//Cast Rays
		for(int i = 0; i < Main.RW; i++){
			this.renderRay(m, gA, i, originroom, Main.RH);
		}
	}
	
	public void renderRay(Map m, Graphics[] gA, int i, int originroom, int floorPxStop){	
		if(!m.time.containsKey(uuid)){
			m.time.put(uuid, 0l);
			m.timeWall.put(uuid, 0l);
			m.timeCeil.put(uuid, 0l);
			m.timeFloor.put(uuid, 0l);
		}
		
		if(floor == null) floor = m.art.getTexture(this.floorTexNum).getMipmap(0);
		
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
		float arcAngle = rayDir.normalised().dot(m.camera.direction);
		Location rLoc = new Location(m.camera.getLocation().getX() + rayDir.getX(), m.camera.getLocation().getY() + rayDir.getY());
		for(Wall w:walls){
			long curTimeWall = System.nanoTime();
			Location[] l = w.getLocations();
			//We still want to do some culling here.
			
			Location intersect = m.getIntersectionLocation(rLoc, l);
			if(intersect != null){
				float dist = m.camera.getLocation().distance(intersect);
				if(dist < 0) continue;
				dist = dist * arcAngle;
				float distFrac = (float)dist / lightDist;
				float sf = 1f - distFrac;
				if(sf > maxLight) sf = maxLight;
				if(sf < 0) sf = 0;
				
				float portalH = 0;
				
				float lH = (1 / dist);
				if(lH > 32) lH = 32;
				float wallTop = 0.5f*lH + ((w.getHeight() - 1f) * lH);
				
				floorPxStart[i] = Math.round(0.5f*Main.RH + (-wallTop/lH + (w.getHeight()))*Main.RH*lH);
				
				long ot2 = 0;
				if(w instanceof PortalWall){
					PortalWall pw = (PortalWall) w;
					int ruid = pw.getRoomUuid();
					portalH = ((PortalWall) w).getPortalHeight();
					long ot = System.nanoTime();
					if(ruid != originroom && (sf > 0 || forceLightRender)) m.getRoom(((PortalWall)w).getRoomUuid()).renderRay(m, gA, i, this.uuid, floorPxStart[i]);
					ot2 = System.nanoTime() - ot;
					otherTime += ot2;
					
				} else {
					m.zIndex[i] = dist;
					m.zLocation[i] = intersect;
				}
				
				int renderTopY = Math.round(Main.RH * (0.5f - wallTop));
				int renderBottomY = Math.round(0.5f*Main.RH + (-wallTop/lH + (w.getHeight() - portalH))*Main.RH*lH);
				int renderDeltaY = renderBottomY - renderTopY;
				//Consensus: If renderDeltaY = Main.RH -> 1 wall texture height (e.g. 64px) should fill the screen
				
				if(portalH < w.getHeight()){
					Texture t = m.art.getTexture(w.getTextureNumber());
					int[] texture = t.getColumn(l[0].distance(intersect), dist);
					
					int topPxClip = (renderTopY < 0) ? -renderTopY : 0;					
					// Screen / texture
					float ffac = (float)(renderDeltaY) / (float)(texture.length * (w.getHeight() - portalH));
					
					int imageHeight = (topPxClip > 0) ? renderBottomY : renderDeltaY;
					if(imageHeight < 1) continue;
					
					BufferedImage bi = new BufferedImage(1, (imageHeight < Main.RH) ? imageHeight : Main.RH, BufferedImage.TYPE_INT_ARGB);
					for(int y = 0; y < bi.getHeight(); y++){
						bi.setRGB(0, y, texture[((int)(((float)y+topPxClip) / ffac)) % texture.length]);
					}
					
					RescaleOp ro = new RescaleOp(
								new float[]{sf, sf, sf, 1f},
								new float[]{0f, 0f, 0f, 0f},
								null);
					renderTopY = renderTopY > 0 ? renderTopY : 0;
					renderBottomY = renderBottomY < Main.RH ? renderBottomY : Main.RH;
					if(sf > 0 || forceLightRender)gA[1].drawImage(ro.filter(bi, null), i, renderTopY, i+1, renderBottomY,0,0,1,bi.getHeight(), null);
				}
				
				m.timeWall.put(uuid, m.timeWall.get(uuid) + System.nanoTime() - curTimeWall - ot2);
				
				if(hasFloor){
					long floorTime = System.nanoTime();
					if(floorPxStart[i] > Main.RH) continue;
					if(floorPxStop > Main.RH) floorPxStop = Main.RH;
					if(floorPxStop - floorPxStart[i] > 0){
						BufferedImage biF = new BufferedImage(1, floorPxStop - floorPxStart[i], BufferedImage.TYPE_INT_ARGB);
						for(int yPx = floorPxStart[i]; yPx < floorPxStop; ++yPx){
							float fY = (float) Main.RH / (2f * (float)yPx - (float)Main.RH);
							float refY = fY / dist;
							
							float curX = (refY) * intersect.getX() + (1f-refY) * m.camera.getLocation().getX();
							float curY = (refY) * intersect.getY() + (1f-refY) * m.camera.getLocation().getY();
							
							int texX = (int)Math.abs((curX * (float)floor.width) % floor.width);
							int texY = (int)Math.abs((curY * (float)floor.height) % floor.height);
							
							float sf2 = (refY) * sf + (1f-refY) * maxLight; 
							if(sf2 < 0) sf2 = 0;
							if(sf2 > maxLight) sf2 = maxLight;
							
							biF.setRGB(0, yPx - floorPxStart[i], darkenColor(floor.pA[texX][texY], sf2));
						}
						gA[1].drawImage(biF, i, floorPxStart[i], i+1, floorPxStop, 0, 0, 1, biF.getHeight(), null);
					}
					m.timeFloor.put(uuid, m.timeFloor.get(uuid) + System.nanoTime() - floorTime);
				}
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
	
	public boolean hasFloor(){
		return this.hasFloor;
	}
	
	public boolean hasCeiling(){
		return this.hasCeiling;
	}
}
