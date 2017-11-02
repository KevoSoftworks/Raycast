package com.kevosoftworks.raycast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.kevosoftworks.raycast.vector.Vector2;
import com.kevosoftworks.raycast.wall.PortalWall;
import com.kevosoftworks.raycast.wall.Wall;

public class ConvexRoom{
	
	public int uuid;
	ArrayList<Wall> walls;
	
	public ConvexRoom(ArrayList<Wall> walls, int uuid){
		this.uuid = uuid;
		this.walls = walls;
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
			this.renderRay(m, gA, i, originroom);
		}
	}
	
	public void renderRay(Map m, Graphics[] gA, int i, int originroom){
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
				if(w instanceof PortalWall){
					PortalWall pw = (PortalWall) w;
					int ruid = pw.getRoomUuid();
					if(ruid != originroom) m.getRoom(((PortalWall)w).getRoomUuid()).renderRay(m, gA, i, this.uuid);
				} else {
					Point2D iCoord = m.camera.getPoint2D(intersect);
					float dist = (float) (Math.abs(
								((iCoord.getX() - pCoord1.getX()) * -(pCoord2.getY() - pCoord1.getY())) + 
								((iCoord.getY() - pCoord1.getY()) * (pCoord2.getX() - pCoord1.getX()))
							) / Math.sqrt(
									Math.pow(pCoord2.getY() - pCoord1.getY(),2) + Math.pow(pCoord2.getX() - pCoord1.getX(),2)
							));
					//The 16f compensates for the resolution
					float lH = (16f*(float)Main.RH / dist);
					if(lH > 255*Main.RH) lH = 255*Main.RH;
					
					BufferedImage bi = new BufferedImage(1, m.art.wall.height, BufferedImage.TYPE_INT_ARGB);
					int[] texture = m.art.wall.getColumn(l[0].distance(intersect));
					for(int y = 0; y < bi.getHeight(); y++){
						bi.setRGB(0, y, texture[y]);
					}
					((Graphics2D)gA[1]).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					gA[1].drawImage(bi, i, (int)((float)Main.RH * 0.5f - lH * 0.5f), 1, (int)lH, null);
					
					/*int rc = (int) (w.getColor().getRed() * (2*(float)lH / (float)Main.RH));
					int gc = (int) (w.getColor().getGreen() * (2*(float)lH / (float)Main.RH));
					int bc = (int) (w.getColor().getBlue() * (2*(float)lH / (float)Main.RH));
					
					if(rc > 255) rc = 255;
					if(rc < 0) rc = 0;
					if(gc > 255) gc = 255;
					if(gc < 0) gc = 0;
					if(bc > 255) bc = 255;
					if(bc < 0) bc = 0;
					
					gA[1].setColor(new Color(rc, gc, bc));
					gA[1].drawLine(i, (int)((float)Main.RH * 0.5f + lH * 0.5f), i, (int)((float)Main.RH * 0.5f - lH * 0.5f));*/
				}
			}
		}
	}
}
