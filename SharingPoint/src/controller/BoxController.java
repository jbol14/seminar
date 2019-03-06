package controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import content.Box;
import content.User;

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
				int plz = results.getInt("plz");
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
		// To do: Fall keine Boxen gefunden!
		Box[] out = new Box[boxes.size()];
		out = boxes.toArray(out);
		return out;
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
