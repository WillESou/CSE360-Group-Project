
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * User Class
 * 
 * A class to hold information on a specific user.
 * 
 * PLEASE FINISH :)
 * Also we will either need to add getters for the private variables or make them public so their data can be accessed.
 */

public class User {
	
	// Enumeration to define the different Roles a user can have
	enum ROLE{
		ADMIN,
		STUDENT,
		INSTRUCTOR
	}
	
	// Enumeration to define the possible skill levels a user can have on a subject
	enum SKILLLEVEL{
		BEGINNER,
		INTERMEDIATE,
		ADVANCED
	}

	// User attributes
	public String username;
	public String email;
	public String name;
	public String prefName; // ADD
	
	// Because the password must be protected, it will be stored in a char array instead of a String
	private char password[];
	public Set<ROLE> roles;
	private HashMap<String, SKILLLEVEL> skills;
	
	// Constructor class to set variables for a new User
	public User(String newUsername, String newEmail, String newName, String newPassword) {
		
		username = newUsername;
		email = newEmail;
		name = newName;
		
		// Conversion of password String to char[]
		password = new char[newPassword.length()];
		for (int i = 0; i < newPassword.length(); i++) {
			password[i] = newPassword.charAt(i);
		}
		
		roles = new HashSet<>();
		skills = new HashMap<String, SKILLLEVEL>();
	}
	
	// Setter function for a user's Roles. Can only add one at a time
	public void addRole(ROLE role) {
		roles.add(role);
	}
	
	// Function to remove a user's Role, if it already contains it
	public void removeRole(ROLE role) {
		
		if (!roles.remove(role)) {
			// Placeholder print message if user does not have the role to remove
			System.out.println("User does not have this role.");
		}
	}
	
	// Function to return whether a user has a specific role
	public boolean hasRole(ROLE role) {
		return roles.contains(role);
	}
	
	// Setter function to add or replace the skill level of a certain topic
	public void setSkillLevel(String topic, SKILLLEVEL level) {
		
		if (skills.containsKey(topic)) {
			skills.replace(topic, level);
		} else {
			skills.put(topic, level);
		}
	}
	
	// Function to return the skill level associated with the skill's topic
	public SKILLLEVEL getSkillLevel(String topic) {
		
		return skills.get(topic);
	}
	
	// Currently unused function. Will eventually encode the password associated with the user
	private String hashPassword(String password) {
		return "";
	}
	
	//Function that checks a given username to the users username
	public boolean verifyUsername(String givenUsername){
		if(givenUsername.compareTo(this.username) == 0)
		{
			return true;
		}
		return false;
	}
	
	
	// Function that compares a tried password with the current password
	public boolean verifyPassword(String password) {
		
		char [] tempPass = new char[password.length()];
		for (int i = 0; i < password.length(); i++) {
			tempPass[i] = password.charAt(i);
		}
		
		if(tempPass.length != this.password.length)
		{
			return false;
		}
		for(int i = 0;i < tempPass.length;i++)
		{
			if(tempPass[i] != this.password[i])
			{
				return false;
			}
		}
		
		return true;
	}
	

	public boolean createSession(String sessionName) {
		
		return SessionManager.getInstance().createClass(sessionName, this);
	}
	
	public boolean enrollInSession(String sessionName) {
		
		return SessionManager.getInstance().enrollStudent(sessionName, this);
	}
	
	public boolean unenrollFromSession(String sessionName) {
		
		return SessionManager.getInstance().unenrollStudent(sessionName, this);
	}
}