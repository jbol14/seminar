package controller;

/**
 *	Helper class for PostgreSQL logic
 */
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQL {

	String db_url = "jdbc:postgresql://localhost/Boxbase";
	String db_name = "postgres";
	String db_pass = "pass";

	Connection cursor = null;

	Connection connect() {

		try {

			cursor = DriverManager.getConnection(db_url, db_name, db_pass);
			System.out.println("Connection established!");

		}

		catch (SQLException ex) {
			Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, ex);
		}

		return cursor;
	}

	void setURL(String s) {

		db_url = s;
		System.out.println("URL changed!");

	}

	void setName(String n) {

		db_name = n;
		System.out.println("New name set!");
	}

	void setPass(String p) {

		db_pass = p;
		System.out.println("Password changed!");

	}

	public PostgreSQL() {
	};

	public PostgreSQL(String new_URL) {

		this.db_url = new_URL;

	}

}
