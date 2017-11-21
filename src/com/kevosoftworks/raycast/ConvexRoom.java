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
	
	float resComp = 16f;
	//float renderDist = 6400000f;
	float lightDist = 255f;
	float maxLight = 0.4f;
	
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
	
	public void renderRay(Map m, Graphics[] gA, int i, int originroom, int maxPixel){	
		if(!m.time.containsKey(uuid)){
			m.time.put(uuid, 0l);
			m.timeWall.put(uuid, 0l);
			m.timeCeil.put(uuid, 0l);
			m.timeFloor.put(uuid, 0l);
		}
		
		long curTime = System.nanoTime();
		long otherTime = 0;
		forceLightRender = true;
		Texture floor = m.art.getTexture(this.floorTexNum);
		Texture ceil = m.art.getTexture(this.ceilTexNum);
		Mipmap floorm = floor.getMipmap((maxPixel == 0 || this.uuid == originroom) ? 0 : FLOOR_MIPMAP);
		Mipmap ceilm = ceil.getMipmap((maxPixel == 0 || this.uuid == originroom) ? 0 : FLOOR_MIPMAP);
		
		Location pLoc1 = new Location(m.camera.getLocation().getX() + m.camera.direction.getX() - m.camera.plane.getX(), m.camera.getLocation().getY() + m.camera.direction.getY() - m.camera.plane.getY());
		Location pLoc2 = new Location(m.camera.getLocation().getX() + m.camera.direction.getX() + m.camera.plane.getX(), m.camera.getLocation().getY() + m.camera.direction.getY() + m.camera.plane.getY());
		Point2D pCoord1 = m.camera.getPoint2D(pLoc1);
		Point2D pCoord2 = m.camera.getPoint2D(pLoc2);
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
				Point2D iCoord = m.camera.getPoint2D(intersect);
				float dist = (float) (Math.abs(
							((iCoord.getX() - pCoord1.getX()) * -(pCoord2.getY() - pCoord1.getY())) + 
							((iCoord.getY() - pCoord1.getY()) * (pCoord2.getX() - pCoord1.getX()))
						) / Math.sqrt(
								Math.pow(pCoord2.getY() - pCoord1.getY(),2) + Math.pow(pCoord2.getX() - pCoord1.getX(),2)
						));
				float distFrac = (float)dist / lightDist;
				float sf = 1f - distFrac;
				if(sf > maxLight) sf = maxLight;
				if(sf < 0) sf = 0;
				
				float portalH = 0;
				
				//The 16f compensates for the resolution
				float lH = (resComp*(float)Main.RH / dist);
				if(lH > 255*Main.RH) lH = 255*Main.RH;
				int wallTop = (int)Math.floor((float)Main.RH * 0.5f - lH * 0.5f - (w.getHeight()-1)*lH);
				
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
					if(sf > 0 || forceLightRender)gA[1].drawImage(ro.filter(bi, null), i, wallTop, 1, (int)Math.ceil(lH * (w.getHeight()-portalH))+1, null);
				}
				m.timeWall.put(uuid, m.timeWall.get(uuid) + System.nanoTime() - curTimeWall - ot2);
				if(m.renderFloor){
					//render floors
					long curTimeFloor = System.nanoTime();
					int wH = (int)Math.floor((float)Main.RH * 0.5f - lH * 0.5f) + (int)Math.ceil(lH);
					if(wH < Main.RH){
						BufferedImage biF = new BufferedImage(1, Main.RH - wH, BufferedImage.TYPE_INT_ARGB);
						for(int y = wH + 1; y <= Main.RH; y++){
							float curDist = (float)Main.RH / (2f * (float)y - (float)Main.RH);
							if(curDist < 0) continue;
							float sf2 = 1f - (resComp * curDist)/lightDist;
							if(sf2 < 0){
								sf2 = 0;
								if(!forceLightRender) continue;
							}
							if(sf2 > maxLight) sf2 = maxLight;
							
							float weight = (float)curDist / (float)(dist / resComp);
							float curFloorX = (float) (weight * intersect.getX() + (1f-weight) * m.camera.getLocation().getX());
							float curFloorY = (float) (weight * intersect.getY() + (1f-weight) * m.camera.getLocation().getY());
							
							int floorTexX = (int)Math.abs((curFloorX * (float)floorm.width) % (float)floorm.width);
							int floorTexY = (int)Math.abs((curFloorY * (float)floorm.height) % (float)floorm.height);
							biF.setRGB(0, y - 1 - wH, darkenColor(floorm.pA[floorTexY][floorTexX], sf2));
						}
						gA[1].drawImage(biF, i, wH, 1, biF.getHeight(), null);
					}
					m.timeFloor.put(uuid, m.timeFloor.get(uuid) + System.nanoTime() - curTimeFloor);
					
					//render ceiling
					long curTimeCeil = System.nanoTime();
					if(w.getHeight() == 1){
						wH = (int)Math.floor((float)Main.RH * 0.5f - lH * 0.5f - (w.getHeight()-1f)*lH);
						if(wH - maxPixel > 0){
							BufferedImage biF = new BufferedImage(1, wH, BufferedImage.TYPE_INT_ARGB);
							for(int y = maxPixel; y < wH; y++){
								if(y >= biF.getHeight()) break;
								float curDist = -1f*(float)(Main.RH) / (2f * (float)y - (float)Main.RH);
								if(curDist < 0) continue;
								float sf2 = 1f - (resComp * curDist)/lightDist;
								if(sf2 < 0){
									sf2 = 0;
									if(!forceLightRender) continue;
								}
								if(sf2 > maxLight) sf2 = maxLight;
								
								float weight = (float)curDist / (float)(dist / resComp);
								float curFloorX = (float) (weight * intersect.getX() + (1f-weight) * m.camera.getLocation().getX())*1f;
								float curFloorY = (float) (weight * intersect.getY() + (1f-weight) * m.camera.getLocation().getY())*1f;
								
								int floorTexX = (int)Math.abs((curFloorX * (float)ceilm.width) % (float)ceilm.width);
								int floorTexY = (int)Math.abs((curFloorY * (float)ceilm.height) % (float)ceilm.height);
								try{
									biF.setRGB(0, y - maxPixel, darkenColor(ceilm.pA[floorTexY][floorTexX], sf2));
								} catch(ArrayIndexOutOfBoundsException e){
									System.out.println("y:" + y + "; maxP: " + maxPixel + "; biF: " + biF.getHeight());
								}
							}
							gA[1].drawImage(biF, i, maxPixel, 1, biF.getHeight(), null);
						}
					}
					m.timeCeil.put(uuid, m.timeCeil.get(uuid) + System.nanoTime() - curTimeCeil);
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
}
