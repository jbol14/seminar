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
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.PostgreSQL;
import content.User;

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
	
	public boolean changePrivateKey(String key, User user) {
		connectToDatabase();
		String command = "UPDATE customer SET private_key ='" + key +"' WHERE id='" + user.getId() +"'";
		try {
			statement.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconnectDatabase();
		return true;
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
