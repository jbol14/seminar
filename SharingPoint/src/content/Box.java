package content;

import java.util.Date;
/**
 * Digital representation of one RaspberryPi box
 */
public class Box {

	private int id;
	private String plz;
	private int area_id;
	private User user;
	private boolean status_leased;
	private String location;
	private Date leased_until;
	private String[] box_key;
	
	public Box(int id, String plz, int area_id,boolean status_leased,String location, Date leased_until) {
		this.id = id;
		this.plz = plz;
		this.area_id = area_id;
		this.status_leased = status_leased;
		this.location = location;
		this.leased_until = leased_until;
	}
	public Box(int id, String plz, int area_id,boolean status_leased,String location, Date leased_until, String[] box_key) {
		this.id = id;
		this.plz = plz;
		this.area_id = area_id;
		this.status_leased = status_leased;
		this.location = location;
		this.leased_until = leased_until;
		this.box_key = box_key;
	}
	
	
	// Getter and Setter
	
	public int getId() {
		return id;
	}
	public String getPlz() {
		return plz;
	}
	public int getAreaId() {
		return area_id;
	}
	public User getUser() {
		return user;
	}
	public boolean getStatusLeased() {
		return status_leased;
	}
	public String getLocation() {
		return location;
	}
	public Date getDate() {
		return leased_until;
	}
	public String[] getBoxKey() {
		return box_key;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPlz(String plz) {
		this.plz = plz;
	}
	public void setAreaId(int area_id) {
		this.area_id = area_id;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setStatusLeased(boolean status_leased) {
		this.status_leased = status_leased;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setDate (Date leased_until) {
		this.leased_until = leased_until;
	}
	public void setBoxKey (String[] box_key) {
		this.box_key = box_key;
	}
}
