package controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import controller.PostgreSQL;
import content.Box;
import content.User;
/**
 * UserController implements functions for user logic. Used for checking and register a user. Is also used to change the pin of the user
 */
public class UserController {

	private PostgreSQL postgres;
	private Connection cursor;
	private Statement statement = null;
	

	// Checks if User + Password is in the Database. If not, return NULL
	public User checkUser(String email, String password){
		connectToDatabase();
		
		String command = "SELECT * FROM customer WHERE email='" + email + "' AND password ='" + password+"'";
		ResultSet results;
		User user = null;
		boolean checkUser = true;
		try {
			results = statement.executeQuery(command);
			checkUser = results.next();
			if(checkUser) {
				int id = results.getInt("id");
				user = new User(id,email);
			}
		} catch (SQLException e) {
			Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, e);
		}
		
		disconnectDatabase();
		return user;
	}
	//Registers a User if the email is not in the Database.
	public boolean RegisterUser(String email, String password, String pin) {
		connectToDatabase();
		//Check if email exists
		String command = "SELECT * FROM customer WHERE email='" + email +"'";
		ResultSet results;
		boolean checkUser = true;
		try {
			results = statement.executeQuery(command);
			checkUser = results.next();
			if(checkUser) {
				return false;
			}
		}catch(SQLException e) {
			Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, e);
		}
		//Create new User
		command = "INSERT INTO customer (email,password,private_key) VALUES('" + email + "','" + password + "','" + pin +"')";
		try {
			statement.executeUpdate(command);
		} catch (SQLException e) {
			Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, e);
		}
		disconnectDatabase();
		return true;
	}	
	//Changes the pin of the User
	public boolean changeUserPin(String key, User user) {
		connectToDatabase();
		String command = "UPDATE customer SET private_key ='" + key +"' WHERE id='" + user.getId() +"'";
		try {
			statement.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sendUserPinChange(user, key);
		disconnectDatabase();
		return true;
	}
	//Fetches userpin form Database
	public String getUserPin(User user) {
		connectToDatabase();
		String command = "SELECT * FROM customer WHERE id='" + user.getId() +"'";
		ResultSet results;
		String key = null;
		try {
			results = statement.executeQuery(command);
			boolean check = results.next();
			if(!check) {
				return key;
			}
			key = results.getString("private_key");
		}catch(SQLException e) {
			Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, e);
		}
		disconnectDatabase();
		return key;	
	}
	//creates and send Message to Mqtt Brocker. Its only scaled to Clausthal-Zellerfeld with the plz 38678
	private boolean sendUserPinChange(User user, String key) {
		BoxController boxController = new BoxController();
		Box[] boxes = boxController.UserBoxes(user);
		List<Integer> usedAreaId = new ArrayList<Integer>();
		Map<Integer,List<Box>> dict = new HashMap<Integer,List<Box>>();
		for(int i=0; i<boxes.length; i++) {
			if(usedAreaId.contains(boxes[i].getAreaId())) {
				List<Box> tmpBox = dict.get(boxes[i].getAreaId());
				tmpBox.add(boxes[i]);
				dict.put(boxes[i].getAreaId(), tmpBox);	
			}else {
				usedAreaId.add(boxes[i].getAreaId());
				List<Box> tmpBox = new ArrayList<Box>();
				tmpBox.add(boxes[i]);
				dict.put(boxes[i].getAreaId(), tmpBox);
			}
		}
		
		for(int i=0; i<usedAreaId.size();i++) {
			List<Box> boxList = dict.get(usedAreaId.get(i));
			String topic = "newCode/38678/"+ usedAreaId.get(i);
			
			MqttController mqttController = new MqttController();
			mqttController.connectToBox(topic, boxList, key);
		}	
		
		return true;
	}
	
	//Connect to Database
	private void connectToDatabase() {
		try {
			postgres = new PostgreSQL();
			cursor = postgres.connect();
			statement = cursor.createStatement();
		} catch (SQLException e) {
			System.out.println("ERROR2: " + e);
		}
	}
	//Disconnect from Database
	private void disconnectDatabase() {
		try {
			statement.close();
			cursor.close();
		} catch (SQLException e) {
			System.out.println("ERROR3: " + e);
		}
		
	}
	
	
	
}
