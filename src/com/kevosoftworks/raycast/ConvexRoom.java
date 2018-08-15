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
	//Room properties
	private int id = -1;
	private ArrayList<Wall> walls;
	
	//Render settings
	private boolean renderWalls = true;
	private boolean renderFloor = true;
	private boolean renderCeil = true;
	private boolean renderLight = true;
	private boolean isCulling = true;
	
	//Light properties
	private float lightDistanceDropoff = 16f;
	private float lightMaximumBrightness = 0.6f;
	
	//Floor properties
	private boolean hasFloor = false;
	private Texture floorTexture = null;
	private float floorTextureScale = 1f;
	
	//Ceiling properties
	private boolean hasCeil = false;
	private Texture ceilTexture = null;
	private float ceilTextureScale = 1f;
	
	
	public ConvexRoom(int id, ArrayList<Wall> walls){
		this.id = id;
		this.walls = walls;
	}
	
	public void setFloorProperties(boolean hasFloor){
		this.setFloorProperties(hasFloor, null, 1f);
	}
	
	public void setFloorProperties(boolean hasFloor, Texture floorTexture){
		this.setFloorProperties(hasFloor, floorTexture, 1f);
	}
	
	public void setFloorProperties(boolean hasFloor, Texture floorTexture, float textureScale){
		this.hasFloor = hasFloor;
		this.floorTexture = floorTexture;
		this.floorTextureScale = textureScale;
	}
	
	public void setCeilingProperties(boolean hasCeil){
		this.setCeilingProperties(hasCeil, null, 1f);
	}
	
	public void setCeilingProperties(boolean hasCeil, Texture ceilTexture){
		this.setCeilingProperties(hasCeil, ceilTexture, 1f);
	}
	
	public void setCeilingProperties(boolean hasCeil, Texture ceilTexture, float textureScale){
		this.hasCeil = hasCeil;
		this.ceilTexture = ceilTexture;
		this.ceilTextureScale = textureScale;
	}
	
	public int getId(){
		return this.id;
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
		for(int i = 0; i < Main.RW; i++){
			this.renderRay(m, gA, i, originroom, Main.RH, 0);
		}
	}
	
	public void renderRay(Map m, Graphics[] gA, int i, int originroom, int floorRenderPxStop, int ceilRenderPxStart){
		renderWalls = true;
		if(!m.time.containsKey(id)){
			m.time.put(id, 0l);
			m.timeWall.put(id, 0l);
			m.timeCeil.put(id, 0l);
			m.timeFloor.put(id, 0l);
		}
		
		long curTime = System.nanoTime();
		long otherTime = 0;
		
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
				float distFrac = (float)dist / lightDistanceDropoff;
				float sf = 1f - distFrac;
				if(sf > lightMaximumBrightness || !renderLight) sf = lightMaximumBrightness;
				if(sf < 0) sf = 0;
				
				float portalH = 0;
				
				float lH = (1 / dist);
				if(lH > 32) lH = 32;
				float wallTop = 0.5f*lH + ((w.getHeight() - 1f) * lH);
				
				int floorRenderPxStart = Math.round(0.5f*Main.RH + (-wallTop/lH + (w.getHeight()))*Main.RH*lH);
				int ceilRenderPxStop = Math.round(Main.RH * (0.5f - wallTop));
				
				long ot2 = 0;
				if(w instanceof PortalWall){
					PortalWall pw = (PortalWall) w;
					int ruid = pw.getRoomUuid();
					portalH = ((PortalWall) w).getPortalHeight();
					long ot = System.nanoTime();
					if(ruid != originroom && (sf > 0 || !isCulling)) m.getRoom(((PortalWall)w).getRoomUuid()).renderRay(m, gA, i, this.id, floorRenderPxStart, ceilRenderPxStop);
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
					if((sf > 0 || !isCulling) && renderWalls)gA[1].drawImage(ro.filter(bi, null), i, renderTopY, i+1, renderBottomY,0,0,1,bi.getHeight(), null);
				}
				
				m.timeWall.put(id, m.timeWall.get(id) + System.nanoTime() - curTimeWall - ot2);
				
				if(hasFloor){
					long floorTime = System.nanoTime();
					if(floorRenderPxStart > Main.RH) continue;
					if(floorRenderPxStop > Main.RH) floorRenderPxStop = Main.RH;
					if(floorRenderPxStop - floorRenderPxStart > 0){
						BufferedImage biF = new BufferedImage(1, floorRenderPxStop - floorRenderPxStart, BufferedImage.TYPE_INT_ARGB);
						for(int yPx = floorRenderPxStart; yPx < floorRenderPxStop; ++yPx){
							float fY = (float) Main.RH / (2f * (float)yPx - (float)Main.RH);
							float refY = fY / dist;
							
							Mipmap tex = floorTexture.getMipmap(floorTexture.getMipmapIndex(0.8f*fY));
							int[][] texPixelArray = tex.getPixelArray();
							
							float curX = (refY) * intersect.getX() + (1f-refY) * m.camera.getLocation().getX();
							float curY = (refY) * intersect.getY() + (1f-refY) * m.camera.getLocation().getY();
							
							int texX = (int)Math.abs((curX * (float)tex.width / floorTextureScale) % tex.width);
							int texY = (int)Math.abs((curY * (float)tex.height / floorTextureScale) % tex.height);
							
							float sf2 = (refY) * sf + (1f-refY) * lightMaximumBrightness; 
							if(sf2 < 0) sf2 = 0;
							if(sf2 > lightMaximumBrightness || !renderLight) sf2 = lightMaximumBrightness;
							
							biF.setRGB(0, yPx - floorRenderPxStart, darkenColor(texPixelArray[texX][texY], sf2));
						}
						gA[1].drawImage(biF, i, floorRenderPxStart, i+1, floorRenderPxStop, 0, 0, 1, biF.getHeight(), null);
					}
					m.timeFloor.put(id, m.timeFloor.get(id) + System.nanoTime() - floorTime);
				}
				
				if(hasCeil){
					long ceilTime = System.nanoTime();
					if(ceilRenderPxStop < 0) continue;
					if(ceilRenderPxStart < 0) ceilRenderPxStart = 0;
					if(ceilRenderPxStop - ceilRenderPxStart > 0){
						int pxPerWallSeg = Math.round(renderDeltaY / w.getHeight());
						BufferedImage biF = new BufferedImage(1, ceilRenderPxStop - ceilRenderPxStart, BufferedImage.TYPE_INT_ARGB);
						for(int yPx = ceilRenderPxStart; yPx < ceilRenderPxStop; ++yPx){
							float fY = (float) -(1f + 2f*(w.getHeight()-1f))*Main.RH / (2f * (float)yPx - (float)Main.RH);
							float refY = fY / dist;
							
							Mipmap tex = ceilTexture.getMipmap(ceilTexture.getMipmapIndex(0.8f*fY));
							int[][] texPixelArray = tex.getPixelArray();
							
							float curX = (refY) * intersect.getX() + (1f-refY) * m.camera.getLocation().getX();
							float curY = (refY) * intersect.getY() + (1f-refY) * m.camera.getLocation().getY();
							
							int texX = (int)Math.abs((curX * (float)tex.width / ceilTextureScale) % tex.width);
							int texY = (int)Math.abs((curY * (float)tex.height / ceilTextureScale) % tex.height);
							
							float sf2 = (refY) * sf + (1f-refY) * lightMaximumBrightness; 
							if(sf2 < 0) sf2 = 0;
							if(sf2 > lightMaximumBrightness || !renderLight) sf2 = lightMaximumBrightness;
							
							biF.setRGB(0, yPx - ceilRenderPxStart, darkenColor(texPixelArray[texX][texY], sf2));
						}
						gA[1].drawImage(biF, i, ceilRenderPxStart, i+1, ceilRenderPxStop, 0, 0, 1, biF.getHeight(), null);
					}
					m.timeCeil.put(id, m.timeCeil.get(id) + System.nanoTime() - ceilTime);
				}
			}
			if(intersect == null){
				m.timeWall.put(this.id, m.timeWall.get(id) + System.nanoTime() - curTimeWall);
			}
			
		}
		m.time.put(this.id, m.time.get(id) + System.nanoTime() - curTime - otherTime);
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
		return this.hasCeil;
	}
}
