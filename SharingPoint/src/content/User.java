package content;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String email;
	private String address = null;
	//private PersonalKey pKey;
	//private GuestKey gKey;
	
	public User(int id,String email) {
		this.id = id;
		this.email = email;
	}
	public User(int id,String email, String address) {
		this.id = id;
		this.email = email;
		this.address = address;
	}
	//create Constructor for pKey and gKey 
	
	
	public int getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public String getAddress() {
		return address;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
}
