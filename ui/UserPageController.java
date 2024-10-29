package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;


import core.*;

/***
 * 
 * <p> UserPageController </p>
 * 
 * <p> Description: Class to implement UI for a Student/User from userpage.fxml. Currently only has a logout button.</p>
 * 
 * 
 * @author Ethan MacTough
 * 
 * @version 1.00	2024-10-28
 * 
 */

public class UserPageController {
	
	User selectedUser;
	
	// Implemented button
	@FXML
	private Button logoutBtn;

	// Logout Button implementation. Logs user out of system.
	@FXML
    private void handleLogout() {
        core.Source.getUIManager().loadLoginPage();
    }
}