package com.dreaminsteam.jarcade.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class DatabaseController {
	
	private static Map<String, DatabaseController> controllerMap = new HashMap<String, DatabaseController>();
	
	private String filename;
	private ObjectContainer database;
	
	private DatabaseController(String filename){
		this.filename = filename;
		this.database = null;
	}
	
	public void store(Object object){
		database.store(object);
	}
	
	public void storeIfNotThere(Object object){
		if(!database.queryByExample(object).hasNext()){
			store(object);
		}
	}
	
	public <T> List<T> findByExample(T example){
		ObjectSet<T> query = database.queryByExample(example);
		return query.stream().collect(Collectors.toList());
	}
	
	private void connectToDatabase(){
		database = Db4o.openFile(filename);
	}
	
	public void closeConnection(){
		database.close();
	}
	
	public static synchronized DatabaseController accessDatabase(String filename){
		DatabaseController databaseController = controllerMap.get(filename);
		if(databaseController == null){
			databaseController = new DatabaseController(filename);
			databaseController.connectToDatabase();
			controllerMap.put(filename, databaseController);
		}
		return databaseController;
	}
	
	public static synchronized void shutdownControllersGracefully(){
		controllerMap.values().stream().forEach((databaseController) -> {databaseController.closeConnection();});
	}
}
