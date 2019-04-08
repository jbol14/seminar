package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import content.Box;
/**
 * This class is a MQTT phao client which publishes box messages
 */
public class MqttController {
	
	String broker = "tcp://192.168.178.49:1883";
	String clientId;

	//Creates Random id, cause MQTT needs a unique name
	public MqttController() {
		clientId = UUID.randomUUID().toString();
	}
	public void connectToBox(String topic, List<Box> boxList, String key) {
		DateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
		JSONArray ja = new JSONArray();
		for(int i=0; i<boxList.size();i++) {
			JSONObject jo = new JSONObject();
			jo.put("ID", boxList.get(i).getId());
			jo.put("PIN", key);
			String leasedUntil = dateFormat.format(boxList.get(i).getDate());
			jo.put("Date", leasedUntil);
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
}
