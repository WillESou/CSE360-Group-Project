
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * User Class
 * 
 * A local representation of the user class. Is created by UserManager to manipulate user data locally, so the database only
 * has to be queried once.
 * 
 * by: William Sou
 */

public class User {
	
	// Enumeration to define the possible skill levels a user can have on a subject
	enum SKILLLEVEL{
		BEGINNER,
		INTERMEDIATE,
		ADVANCED
	}

	// User attributes
	private String username;
	private String email;
	private String name;
	private String prefName; 
	private boolean changed = false;
	public Set<ROLE> roles = new HashSet<>();
	private HashMap<String, SKILLLEVEL> skills = new HashMap<String, SKILLLEVEL>();

	
	
	//Constructor
	User(String username, String email, String name){
		this.username = username;
		this.email = email;
		this.name = name;
	}
	
	
	//Getters
	public String getUsername() {
		return this.username;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getName() { 
		return this.name;
	}
	
	public String getPrefName() {
		return this.prefName;
	}
	
	public Set<ROLE> getRoles() {
		return this.roles;
	}
	
	// Function to return the skill level associated with the skill's topic
	public SKILLLEVEL getSkillLevel(String topic) {
		return skills.get(topic);
	}
		
	
	//Setters
	public void setUsername(String newUser) {
		this.username = newUser;
		changed = true;
	}
	
	public void setEmail(String newEmail) {
		this.email = newEmail;
		changed = true;
	}
	
	public void setName(String newName) {
		this.name = newName;
		changed = true;
	}
	
	public void setPrefName(String newPrefName) {
		this.prefName = newPrefName;
		changed = true;
	}
	
	// Setter function to add or replace the skill level of a certain topic
		public void setSkillLevel(String topic, SKILLLEVEL level) {
			
			changed = true;
			
			if (skills.containsKey(topic)) {
				skills.replace(topic, level);
			} else {
				skills.put(topic, level);
			}
		}
		
	// Setter function for a user's Roles. Can only add one at a time
	public void addRole(ROLE role) {
		roles.add(role);
		changed = true;
	}
	
	// Function to remove a user's Role, if it already contains it
	public void removeRole(ROLE role) {
		
		if (!roles.remove(role)) {
			// Placeholder print message if user does not have the role to remove
			System.out.println("User does not have this role.");
		}else {
			changed = true;
		}
		
	}
	
	// Function to return whether a user has a specific role
	public boolean hasRole(ROLE role) {
		return roles.contains(role);
	}
	
	
	
	
}