package controller;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import content.Box;
import content.User;

/**
 * Implements helper functions for box logic. Gets boxes for Database for the home an rent page. Handles logic behind renting a box
 */
public class BoxController {
	
	private PostgreSQL postgres;
	private Connection cursor;
	private Statement statement = null;
	
	//Creates a Array of User Boxes
	public Box[] UserBoxes(User user) {
		List<Box> boxes = new ArrayList<Box>();
		connectToDatabase();
		String command = "SELECT * FROM box WHERE customer_id='" + user.getId() +"'";
		ResultSet results;
		boolean checkBox = true;
		try {
			results = statement.executeQuery(command);
			checkBox = results.next();
			while(checkBox) {
				int id = results.getInt("id");
				String plz = results.getString("plz");
				int area_id = results.getInt("area_id");
				boolean status_leased = results.getBoolean("status_leased");
				String location = results.getString("location");
				Date leased_until = results.getDate("leased_until");
				Array boxKeyArray = results.getArray("box_key");
				String[] box_key = (String[]) boxKeyArray.getArray();
				
				
				Box tmpBox = new Box(id,plz,area_id,status_leased,location,leased_until,box_key);
				boxes.add(tmpBox);
				checkBox = results.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconnectDatabase();
		
		Box[] out = new Box[boxes.size()];
		out = boxes.toArray(out);
		return out;
	}
	public Box getSingleBox(int id, int areaId, String plz) {
		connectToDatabase();
		Box box = null;
		String command = "SELECT * FROM box WHERE id='" + id +"' AND area_id = '"+ areaId +"' AND plz = '" + plz + "'";
		ResultSet results;
		boolean checkBox = true;
		try {
			results = statement.executeQuery(command);
			checkBox = results.next();
			if(checkBox) {
				boolean status_leased = results.getBoolean("status_leased");
				String location = results.getString("location");
				Date leased_until = results.getDate("leased_until");
				Array boxKeyArray = results.getArray("box_key");
				String[] box_key = (String[]) boxKeyArray.getArray();
				
				box = new Box(id,plz,areaId,status_leased,location,leased_until,box_key);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		disconnectDatabase();
		return box;
	}
	
	public boolean updateBoxKeys(Box box) {
		connectToDatabase();
		String command = "UPDATE box SET  box_key = '{" + box.getBoxKey()[0]+","+box.getBoxKey()[1]+"," + box.getBoxKey()[2] + "}'"
				+ "WHERE id='" + box.getId() +"' AND plz='"+ box.getPlz() + "' AND area_id='"+box.getAreaId()+"'";
		try {
			statement.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		disconnectDatabase();
		return true;
	}
	
	//Fetches an array of free boxes(leased == false) from the database
	public Box[] freeBoxes() {
		List<Box> boxes = new ArrayList<Box>();
		connectToDatabase();
		String command = "SELECT * FROM box WHERE status_leased ='0'";
		ResultSet results;
		boolean checkBox = true;
		try {
			results = statement.executeQuery(command);
			checkBox = results.next();
			while(checkBox) {
				int id = results.getInt("id");
				String plz = results.getString("plz");
				int area_id = results.getInt("area_id");
				boolean status_leased = results.getBoolean("status_leased");
				String location = results.getString("location");
				Date leased_until = results.getDate("leased_until");
				
				Box tmpBox = new Box(id,plz,area_id,status_leased,location,leased_until);
				boxes.add(tmpBox);
				checkBox = results.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconnectDatabase();
		Box[] out = new Box[boxes.size()];
		out = boxes.toArray(out);
		return out;
	}
	// Rents a box and sends box data to raspberry
	public boolean rentBox(User user, int areaId) {
		connectToDatabase();
		String command = "SELECT * FROM box WHERE area_id='" + areaId +"' AND status_leased ='0'";
		ResultSet results;
		int id = 0;
		Box box;
		try {
			results = statement.executeQuery(command);
			boolean checkBox = results.next();
			if(!checkBox) {
				System.out.println("ERROR: No Box available");
				return false;
			}
			id = results.getInt("id");
			String plz = results.getString("plz");
			//Gen today calendar date
			DateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 30);
			Date date = cal.getTime();
			String leasedUntil = dateFormat.format(date);
			
			String[] boxPin = genRandomBoxKey(4,3);
			
			box = new Box(id, plz, areaId, true, "" , date);
			command = "UPDATE box SET customer_id ='" + user.getId() +"', "
					+ "status_leased='1', leased_until ='"+ leasedUntil +"', box_key = '{" + boxPin[0]+","+boxPin[1]
					+ "," + boxPin[2] + "}'"
					+ "WHERE id='" + id +"' AND plz='"+ plz + "' AND area_id='"+areaId+"'";
			statement.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		//Sends Data over MQTT
		String topic = "rentBox/38678/" + areaId;
		List<Box> boxList = new ArrayList<Box>();
		boxList.add(box);
		
		UserController userController = new UserController();
		String key = userController.getUserPin(user);
		
		MqttController mqttController = new MqttController();
		mqttController.connectToBox(topic, boxList, key);
		
		
		disconnectDatabase();
		return true;
	}
	//Randomly generates 3 x-digit Pins
	public String[] genRandomBoxKey(int digits, int numberOfKeys) {
		String[] boxPin = new String[numberOfKeys];
		Random rand = new Random();
		for(int i=0; i< boxPin.length; i++) {
			String tmpKey = "";
			for(int j=0; j<digits; j++) {
				int n = rand.nextInt(10);
				tmpKey += n;
			}
			boxPin[i]= tmpKey;
		}
		return boxPin;
		
		
	}
	//Connect and Disconnect to/from Database
	private void connectToDatabase() {
		try {
			postgres = new PostgreSQL();
			cursor = postgres.connect();
			statement = cursor.createStatement();
		} catch (SQLException e) {
			System.out.println("ERROR2: " + e);
		}
	}
		
	private void disconnectDatabase() {
		try {
			statement.close();
			cursor.close();
		} catch (SQLException e) {
			System.out.println("ERROR3: " + e);
		}
			
	}
}
