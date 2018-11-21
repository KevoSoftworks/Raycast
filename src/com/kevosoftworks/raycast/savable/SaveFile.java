package com.kevosoftworks.raycast.savable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveFile{
	//Modified after com.kevosoftworks.ifgame.SaveFile
	
	String path;
	File dir;
	
	public SaveFile(String path){
		this.path = path;
		this.dir = new File(path);
		if(!this.dir.exists()){
			this.dir.mkdirs();
		}
	}
	
	public SavableMap loadMap(){
		try{
			File f = new File(dir, "map.dat");
			FileInputStream fi = new FileInputStream(f);
			byte[] b = new byte[fi.available()];
			fi.read(b);
			fi.close();
			return (SavableMap)deserialise(b);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean saveMap(SavableMap m){
		try{
			File f = new File(dir, "map.dat");
			if(!f.exists()){
				f.createNewFile();
			}
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(serialise(m));
			fo.close();
		} catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		return false;		
	}
	
	private byte[] serialise(java.lang.Object o) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(o);
	    return out.toByteArray();
	}
	private java.lang.Object deserialise(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}

}
