package com.dreaminsteam.jarcade.database;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;

public class DatabaseController {

	public static DatabaseController instance;
	
	private String filename;
	private ObjectContainer database;
	
	private DatabaseController(String filename){
		this.filename = filename;
		this.database = null;
	}
	
	private void connectToDatabase(){
		database = Db4o.openFile(filename);
	}
	
}
