package BoxTeam.MQTT;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;


public class Client implements MqttCallback {
	static String broker = "tcp://192.168.178.49:1883";
	static String clientId = "server_rec";
	
	private PostgreSQL postgres;
	private Connection cursor;
	private Statement statement = null;
	
	public static void main(String[] args) {	
		// Creating a MQTT Client using Eclipse Paho
		Client test = new Client();
		test.connect();
	}
	public void connect() {
		try {
			MqttClient sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			sampleClient.setCallback(this);
			System.out.println("paho-client connecting to broker: " + broker);
			sampleClient.connect(connOpts);
			System.out.println("paho-client connected to broker");
			sampleClient.subscribe("invKey/38678/2");
			
			
		} catch (MqttException me) {
			me.printStackTrace();
		}

	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
		// TODO Auto-generated method stub
		System.out.println("Topic:" + topic);
		//Split Topic
		String delims = "[/]";
		String[] head = topic.split(delims);
		
		JSONObject obj = new JSONObject(message.toString());
		int id = obj.getInt("ID");
		JSONArray boxKey = obj.getJSONArray("BoxPIN");
		updateBoxKey(boxKey,id,head[1],head[2]);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	private void updateBoxKey(JSONArray boxKey, int id, String plz, String areaId) {
		connectToDatabase();
		String command = "UPDATE box SET  box_key = '{" + boxKey.getString(0) +","+boxKey.getString(1)+"," + boxKey.getString(2) + "}'"
				+ "WHERE id='" + id +"' AND plz='"+ plz + "' AND area_id='"+ areaId+"'";
		try {
			statement.executeUpdate(command);
			System.out.println("Sucessfully Updated BoxKey");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconnectDatabase();
	}
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