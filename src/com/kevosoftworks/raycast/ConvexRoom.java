package com.kevosoftworks.raycast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;

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
	float lightDist = 64f;
	float maxLight = 0.8f;
	
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
		//Cast Rays
		for(int i = 0; i <= Main.RW; i++){
			this.renderRay(m, gA, i, originroom, 0);
		}
	}
	
	public void renderRay(Map m, Graphics[] gA, int i, int originroom, int maxPixel){
		Texture floor = m.art.getTexture(this.floorTexNum);
		Texture ceil = m.art.getTexture(this.ceilTexNum);
		
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
			Location[] l = w.getLocations();
			Location intersect = m.getIntersectionLocation(rLoc, l);
			if(intersect != null){
				Point2D iCoord = m.camera.getPoint2D(intersect);
				float dist = (float) (Math.abs(
							((iCoord.getX() - pCoord1.getX()) * -(pCoord2.getY() - pCoord1.getY())) + 
							((iCoord.getY() - pCoord1.getY()) * (pCoord2.getX() - pCoord1.getX()))
						) / Math.sqrt(
								Math.pow(pCoord2.getY() - pCoord1.getY(),2) + Math.pow(pCoord2.getX() - pCoord1.getX(),2)
						));
				float sf = 1f - (float)dist / lightDist;
				if(sf > maxLight) sf = maxLight;
				if(sf < 0) sf = 0;
				
				float portalH = 0;
				
				//The 16f compensates for the resolution
				float lH = (resComp*(float)Main.RH / dist);
				if(lH > 255*Main.RH) lH = 255*Main.RH;
				int wallTop = (int)Math.floor((float)Main.RH * 0.5f - lH * 0.5f - (w.getHeight()-1)*lH);
				
				if(w instanceof PortalWall){
					PortalWall pw = (PortalWall) w;
					int ruid = pw.getRoomUuid();
					portalH = ((PortalWall) w).getPortalHeight();
					if(ruid != originroom && sf > 0) m.getRoom(((PortalWall)w).getRoomUuid()).renderRay(m, gA, i, this.uuid, (wallTop < 0) ? 0 : wallTop);
				}				
				if(portalH < w.getHeight()){						
					Texture t = m.art.getTexture(w.getTextureNumber());
					
					BufferedImage bi = new BufferedImage(1, (int)(t.height * (w.getHeight() - portalH)), BufferedImage.TYPE_INT_ARGB);
					int[] texture = t.getColumn(l[0].distance(intersect));
					for(int y = 0; y < bi.getHeight(); y++){
						bi.setRGB(0, y, texture[y % t.height]);
					}
					
					RescaleOp ro = new RescaleOp(
								new float[]{sf, sf, sf, 1f},
								new float[]{0f, 0f, 0f, 0f},
								null);
					if(sf > 0)gA[1].drawImage(ro.filter(bi, null), i, wallTop, 1, (int)Math.ceil(lH * (w.getHeight()-portalH))+1, null);
				}
				
				//render floors
				int wH = (int)Math.floor((float)Main.RH * 0.5f - lH * 0.5f) + (int)Math.ceil(lH);
				if(wH < Main.RH){
					BufferedImage biF = new BufferedImage(1, Main.RH - wH, BufferedImage.TYPE_INT_ARGB);
					for(int y = wH + 1; y <= Main.RH; y++){
						float curDist = (float)Main.RH / (2f * (float)y - (float)Main.RH);
						if(curDist < 0) continue;
						float sf2 = 1f - (resComp * curDist)/lightDist;
						if(sf2 < 0) continue;
						if(sf2 > maxLight) sf2 = maxLight;
						
						float weight = (float)curDist / (float)(dist / resComp);
						float curFloorX = (float) (weight * intersect.getX() + (1f-weight) * m.camera.getLocation().getX())*1f;
						float curFloorY = (float) (weight * intersect.getY() + (1f-weight) * m.camera.getLocation().getY())*1f;
						
						int floorTexX = (int)Math.abs((curFloorX * (float)floor.width) % (float)floor.width);
						int floorTexY = (int)Math.abs((curFloorY * (float)floor.height) % (float)floor.height);
						biF.setRGB(0, y - 1 - wH, darkenColor(floor.pA[floorTexX][floorTexY], sf2));
					}
					gA[1].drawImage(biF, i, wH, 1, biF.getHeight(), null);
				}
				
				//render ceiling
				if(w.getHeight() == 1){
					wH = (int)Math.floor((float)Main.RH * 0.5f - lH * 0.5f - (w.getHeight()-1f)*lH);
					if(wH - maxPixel > 0){
						BufferedImage biF = new BufferedImage(1, wH, BufferedImage.TYPE_INT_ARGB);
						for(int y = maxPixel; y < wH; y++){
							if(y >= biF.getHeight()) break;
							float curDist = -1f*(float)(Main.RH) / (2f * (float)y - (float)Main.RH);
							if(curDist < 0) continue;
							float sf2 = 1f - (resComp * curDist)/lightDist;
							if(sf2 < 0) continue;
							if(sf2 > maxLight) sf2 = maxLight;
							
							float weight = (float)curDist / (float)(dist / resComp);
							float curFloorX = (float) (weight * intersect.getX() + (1f-weight) * m.camera.getLocation().getX())*1f;
							float curFloorY = (float) (weight * intersect.getY() + (1f-weight) * m.camera.getLocation().getY())*1f;
							
							int floorTexX = (int)Math.abs((curFloorX * (float)floor.width) % (float)floor.width);
							int floorTexY = (int)Math.abs((curFloorY * (float)floor.height) % (float)floor.height);
							try{
								biF.setRGB(0, y - maxPixel, darkenColor(ceil.pA[floorTexX][floorTexY], sf2));
							} catch(ArrayIndexOutOfBoundsException e){
								System.out.println("y:" + y + "; maxP: " + maxPixel + "; biF: " + biF.getHeight());
							}
						}
						gA[1].drawImage(biF, i, maxPixel, 1, biF.getHeight(), null);
					}
				}
			}
		}
	}
	
	public int darkenColor(int c, float ra){
		int a = (c >> 24) & 0xFF;
		int r = (int)(((c >> 16) & 0xFF) * ra);
		int g = (int)(((c >> 8) & 0xFF) * ra);
		int b = (int)((c & 0xFF) * ra);
		
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}
