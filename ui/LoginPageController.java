package ui;

import java.sql.SQLException;

import core.ROLE;
import core.Source;
import core.User;
import core.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginPageController {
	private UserManager userMan = new UserManager();
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Button inviteCodeButton;

    @FXML
    private void initialize() {
        // This method is called automatically after the FXML file has been loaded
        messageLabel.setText("CORNY MESSAGE GOES HERE");
    }

    @FXML
    private void handleLogin() {
    	String username = usernameField.getText();
        String password = passwordField.getText();
    	
        if(username.isEmpty() || password.isEmpty())
        {
        	return;
        }
        
        
        try {
			
        	//If no users in database, make initial admin user
			if(userMan.getAllUsers().size() == 0)
			{
				
				//Create user with the given username and password
				userMan.createUser(username, "", "", password);
				//Assign that user admin
				userMan.assignRoleToUser(username, "ADMIN");
				
				//Alert user that they have created the intial user
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "You are the first user and have been assigned ADMIN. \n Please login again.", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			
			//If there are users, check the entered username and password
			if(userMan.checkPassword(username, password)) {
				User loggedInUser = userMan.getUserByUsername(username);
				for(String role : userMan.getRolesByUsername(username))
				{
					System.out.println(role);
					if(role.compareTo("ADMIN") == 0){
						System.out.println("USER HAS ROLE ADMIN");
						loggedInUser.addRole(ROLE.ADMIN);
					}
					if(role.compareTo("STUDENT") == 0) {
						System.out.println("USER HAS ROLE STUDENT");
						loggedInUser.addRole(ROLE.STUDENT);
					}
					if(role.compareTo("INSTRUCTOR") == 0) {
						System.out.println("USER HAS ROLE INSTRUCTOR");
						loggedInUser.addRole(ROLE.INSTRUCTOR);
					}
				}
				
				//Log user in with UIManager
				Source.getUIManager().logUserIn(loggedInUser);
				
				//Load the RoleSelectionPage
				Source.getUIManager().loadRoleSelectionPage();
				
				// OLD
				//RoleSelectionPage roleSelectionPage = new RoleSelectionPage(loggedInUser);
				//roleSelectionPage.show();
			} else {
				
				//Clear passwordField
				passwordField.clear();
				//Give alert to user
				Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Login", ButtonType.OK);
				alert.showAndWait();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    }

    @FXML
    private void handleInviteCode() {
        // Add your invite code logic here
        System.out.println("Invite code button clicked");
    }
}
