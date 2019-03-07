package controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import controller.PostgreSQL;
import content.Box;
import content.User;

public class UserController {

	private PostgreSQL postgres;
	private Connection cursor;
	private Statement statement = null;
	String broker = "tcp://192.168.178.49:1883";
	String clientId;
	
	public UserController() {
		clientId = UUID.randomUUID().toString();
	}
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
				String address = results.getString("address");
				user = new User(id,email,address);
			}
		} catch (SQLException e) {
			Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, e);
		}
		
		disconnectDatabase();
		return user;
	}
	//Registers a User if the email is not in the Database.
	public boolean RegisterUser(String email, String password) {
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
		command = "INSERT INTO customer (email,password) VALUES('" + email + "','" + password + "')";
		try {
			statement.executeUpdate(command);
		} catch (SQLException e) {
			Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, e);
		}
		disconnectDatabase();
		return true;
	}	
	//Changes the private Key of the User
	public boolean changePrivateKey(String key, User user) {
		connectToDatabase();
		System.out.println("test3");
		String command = "UPDATE customer SET private_key ='" + key +"' WHERE id='" + user.getId() +"'";
		try {
			statement.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sendPrivateKeyChange(user, key);
		System.out.println("test1");
		disconnectDatabase();
		return true;
	}
	public String getPrivateKey(User user) {
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
	//creates and send Message to Mqtt Brocker. Used in .changePrivateKey(). Its only scaled to Clausthal-Zellerfeld with the plz 38678
	private boolean sendPrivateKeyChange(User user, String key) {
		BoxController boxController = new BoxController();
		Box[] boxes = boxController.UserBoxes(user);
		List<Integer> usedAreaId = new ArrayList<Integer>();
		Map<Integer,List<Box>> dict = new HashMap<Integer,List<Box>>();
		System.out.println("box1");
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
		System.out.println("box2");
		for(int i=0; i<usedAreaId.size();i++) {
			List<Integer> boxId = new ArrayList<Integer>();
			List<Box> tmpBox = dict.get(usedAreaId.get(i));
			for(int j=0; j<tmpBox.size();j++) {
				boxId.add(tmpBox.get(j).getId());
			}
			String topic = "newBox/38678/"+ usedAreaId.get(i);
			System.out.println("BoxBox");
			Integer[] arrBoxId = new Integer[boxId.size()];
			arrBoxId = boxId.toArray(arrBoxId);
			connectToBox(topic, arrBoxId, key);
		}	
		
		return true;
	}
	public void connectToBox(String topic, Integer[] boxId, String key) {
		System.out.println("box3");
		JSONArray ja = new JSONArray();
		for(int i=0; i<boxId.length;i++) {
			JSONObject jo = new JSONObject();
			jo.put("ID", boxId[i]);
			jo.put("PIN", key);
			ja.put(jo);
		}
		JSONObject mainObj = new JSONObject();
		mainObj.put("Box",ja);
		
		String content = mainObj.toString();
		try {
			MqttClient sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			System.out.println("paho-client connecting to broker: " + broker);
			sampleClient.connect(connOpts);
			System.out.println("paho-client connected to broker");
			System.out.println("paho-client publishing message: " + content + "in Topic:" + topic);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(2);
			message.setRetained(true);
			sampleClient.publish(topic, message);
			System.out.println("paho-client message published");
			sampleClient.disconnect();
			System.out.println("paho-client disconnected");
			sampleClient.close();
		} catch (MqttException me) {
			me.printStackTrace();
		}
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
