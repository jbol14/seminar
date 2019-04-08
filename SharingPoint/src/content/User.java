package content;

import java.io.Serializable;
/**
 * Digital representation of one User
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String email;
	
	public User(int id,String email) {
		this.id = id;
		this.email = email;
	}
	
	
	
	//getter and setter
	
	public int getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
}
