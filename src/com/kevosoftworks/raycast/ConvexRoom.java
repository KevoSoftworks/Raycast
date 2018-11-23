package com.kevosoftworks.raycast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;

import com.kevosoftworks.raycast.art.Art;
import com.kevosoftworks.raycast.art.Mipmap;
import com.kevosoftworks.raycast.art.Texture;
import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.wall.PortalWall;
import com.kevosoftworks.raycast.wall.Wall;

public class ConvexRoom{
	//Room properties
	private int id = -1;
	private ArrayList<Wall> walls;
	private float zHeight = 0f;
	
	//Render settings
	private boolean renderWalls = true;
	private boolean renderFloor = true;
	private boolean renderCeil = true;
	private boolean renderLight = true;
	private boolean isCulling = true;
	
	//Light properties
	private float lightDistanceDropoff = 12f;
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
	
	public ConvexRoom(int id, ArrayList<Wall> walls, float zHeight){
		this.id = id;
		this.walls = walls;
		this.zHeight = zHeight;
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
		if(textureScale < 0.1f) this.floorTextureScale = 0.1f;
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
		if(textureScale < 0.1f) this.ceilTextureScale = 0.1f;
	}
	
	public void setZHeight(float zh){
		this.zHeight = zh;
	}
	
	public boolean hasFloor(){
		return this.hasFloor;
	}
	
	public Texture getFloorTexture(){
		return this.floorTexture;
	}
	
	public float getFloorTextureScale(){
		return this.floorTextureScale;
	}
	
	public boolean hasCeiling(){
		return this.hasCeil;
	}
	
	public Texture getCeilingTexture(){
		return this.ceilTexture;
	}
	
	public float getCeilingTextureScale(){
		return this.ceilTextureScale;
	}
	
	public int getId(){
		return this.id;
	}
	
	public Wall[] getWalls(){
		return this.walls.toArray(new Wall[walls.size()]);
	}
	
	public Wall getWall(int id){
		for(Wall w:walls){
			if(w.getId()==id) return w;
		}
		return null;
	}
	
	public int addWall(Wall w){
		this.walls.add(w);
		return w.getId();
	}
	
	public int getNewWallId(){
		if(this.walls.size() == 0) return 0;
		return this.walls.get(this.walls.size() - 1).getId() + 1;
	}
	
	public boolean removeWall(int id){
		for(Wall w:walls){
			if(w.getId() == id) return walls.remove(w);
		}
		return false;
	}
	
	public float getZHeight(){
		return this.zHeight;
	}
	
	public void render(Map m, Graphics[] gA, BufferedImage[] bI, int originroom){
		//gA[1].drawImage(m.art.getSkybox().getBufferedImage(), 0, 0, Main.RW, Main.RH, null);
		//Cast Rays
		for(int i = 0; i < Main.RW; i++){
			this.renderRay(m, gA, bI, i, originroom, originroom, Main.RH, 0, zHeight, 0, Main.RH);
		}
	}
	
	public void renderRay(Map m, Graphics[] gA, BufferedImage[] bI, int i, int playerroom, int originroom, int floorRenderPxStop, int ceilRenderPxStart, float roomZHeight, int yMin, int yMax){
		if(yMin < 0) yMin = 0;
		if(yMax > Main.RH) yMax = Main.RH;
		float deltaZ = this.zHeight - roomZHeight; //Positive: room shifts up; negative: room shifts down w.r.t. originRoom
		if(!m.time.containsKey(id)){
			m.time.put(id, 0l);
			m.timeWall.put(id, 0l);
			m.timeCeil.put(id, 0l);
			m.timeFloor.put(id, 0l);
		}
		
		long curTime = System.nanoTime();
		long otherTime = 0;
		
		Vector2 fovLeft = new Vector2(m.camera.direction.getX() - m.camera.plane.getX(), m.camera.direction.getY() - m.camera.plane.getY());
		Vector2 fovRight = new Vector2(m.camera.direction.getX() + m.camera.plane.getX(), m.camera.direction.getY() + m.camera.plane.getY());
		//Remap x-coordinate to [-1, 1] interval
		float cameraX = (float) (2f * ((float)i/Main.RW) - 1f);
		Vector2 rayDir = new Vector2(
					m.camera.direction.getX() + m.camera.plane.getX() * cameraX,
					m.camera.direction.getY() + m.camera.plane.getY() * cameraX
				);
		float arcAngle = rayDir.normalised().dot(m.camera.direction);
		Location rLoc = new Location(m.camera.getLocation().getX() + rayDir.getX(), m.camera.getLocation().getY() + rayDir.getY());
		for(Wall w:walls){
			if(w.getNormal().dot(fovLeft) >= 0 && w.getNormal().dot(fovRight) >= 0) continue;
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
				float portalStart = 0f;
				float portalStop = 0f;
				
				float lH = (1 / dist);
				if(lH > 32) lH = 32;
				float wallTop = 0.5f*lH + ((w.getHeight() - 1f + deltaZ) * lH);
				
				long ot2 = 0;
				if(w instanceof PortalWall){
					PortalWall pw = (PortalWall) w;
					portalH = ((PortalWall) w).getPortalHeight();
					portalStart = pw.getPortalStart();
					portalStop = pw.getPortalStop();
				}
				
				int renderTopY = Math.round(Main.RH * (0.5f - wallTop));
				int renderBottomY = Math.round(0.5f*Main.RH + (-wallTop/lH + (w.getHeight()))*Main.RH*lH);
				int renderDeltaY = renderBottomY - renderTopY;
				//Consensus: If renderDeltaY = Main.RH -> 1 wall texture height (e.g. 64px) should fill the screen
				int renderPortalStartY = Math.round(0.5f*Main.RH + (-wallTop/lH + (w.getHeight() - portalStart))*Main.RH*lH);
				int renderPortalStopY = Math.round(0.5f*Main.RH + (-wallTop/lH + (w.getHeight() - portalStop))*Main.RH*lH);
				
				if(renderPortalStartY > yMax) renderPortalStartY = yMax;
				if(renderPortalStopY < yMin) renderPortalStopY = yMin;
				
				int floorRenderPxStart = renderBottomY;
				int ceilRenderPxStop = renderTopY;
				
				if(w instanceof PortalWall){
					PortalWall pw = (PortalWall) w;
					int ruid = pw.getRoomId();
					
					if(ruid == originroom) continue;
					
					long ot = System.nanoTime();
					if(ruid != originroom && (sf > 0 || !isCulling)) m.getRoom(((PortalWall)w).getRoomId()).renderRay(m, gA, bI, i, playerroom, this.id, floorRenderPxStart, ceilRenderPxStop, roomZHeight, renderPortalStopY, renderPortalStartY);
					ot2 = System.nanoTime() - ot;
					otherTime += ot2;
				}
				
				if(portalH < w.getHeight()){
					int[] texture = w.getTexture().getColumn(l[0].distance(intersect), dist);
					
					int topPxClip = -renderTopY;
					
					// Screen / texture
					float ffac = (float)(renderDeltaY) / (float)(texture.length * (w.getHeight()));
								
					for(int y = yMin; y < yMax; ++y){
						if(!((sf > 0 || !isCulling) && renderWalls)) break;
						if(portalH > 0 && (y < renderPortalStartY && y >= renderPortalStopY)) continue;
						if(y < renderTopY || y >= renderBottomY) continue;
						bI[1].setRGB(i, y, darkenColor(texture[((int)Math.abs((float)(y+topPxClip) / ffac)) % texture.length], sf));
					}
				}
				
				m.timeWall.put(id, m.timeWall.get(id) + System.nanoTime() - curTimeWall - ot2);

				if(hasFloor && floorRenderPxStart < yMax){
					long floorTime = System.nanoTime();
					this.renderPlane(intersect, m, bI, i, dist, sf, yMin, yMax, floorRenderPxStart, floorRenderPxStop, 1f - 2f*deltaZ);
					m.timeFloor.put(id, m.timeFloor.get(id) + System.nanoTime() - floorTime);
				}
				
				if(hasCeil && ceilRenderPxStop > yMin){
					long ceilTime = System.nanoTime();
					this.renderPlane(intersect, m, bI, i, dist, sf, yMin, yMax, ceilRenderPxStart, ceilRenderPxStop, -(1f + 2f*(w.getHeight()-1f+deltaZ)));
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
	
	//Floor: 1f - 2f*deltaZ
	//Ceil: -(1f + 2f*(w.getHeight()-1f+deltaZ))
	//TODO: make this better
	private void renderPlane(Location intersect, Map m, BufferedImage[] bI, int i, float dist, float sf, int yMin, int yMax, int planeStart, int planeStop, float planeConst){
		if(planeStart < yMin) planeStart = yMin;
		if(planeStop > yMax) planeStop = yMax;
		if(planeStop - planeStart > 0){
			for(int yPx = planeStart; yPx < planeStop; ++yPx){
				float fY = planeConst * (float)Main.RH / (2f * (float)yPx - (float)Main.RH);
				float refY = fY / dist;
				
				Mipmap tex = ceilTexture.getMipmap(ceilTexture.getMipmapIndex(fY));
				int[][] texPixelArray = tex.getPixelArray();
				
				float curX = (refY) * intersect.getX() + (1f-refY) * m.camera.getLocation().getX();
				float curY = (refY) * intersect.getY() + (1f-refY) * m.camera.getLocation().getY();
				
				int texX = (int)Math.abs((curX * (float)tex.width / ceilTextureScale) % tex.width);
				int texY = (int)Math.abs((curY * (float)tex.height / ceilTextureScale) % tex.height);
				
				float sf2 = (refY) * sf + (1f-refY) * lightMaximumBrightness; 
				if(sf2 <= 0) continue;
				if(sf2 > lightMaximumBrightness || !renderLight) sf2 = lightMaximumBrightness;
				
				bI[1].setRGB(i, yPx, darkenColor(texPixelArray[texX][texY], sf2));
			}
		}
	}
}
