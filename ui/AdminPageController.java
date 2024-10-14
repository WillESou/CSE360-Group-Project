package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;

import core.ROLE;
import core.User;
import core.UserManager;

public class AdminPageController {
	
	//Delcaring need variables
	UserManager userMan;
	User selectedUser;
	
	
	//javaFX will initialize these variables
    @FXML
    private Button listUsersBtn;

    @FXML
    private Button editUsersBtn;

    @FXML
    private Button inviteUserBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private AnchorPane searchUserView;

    @FXML
    private BorderPane editUserView;
    
    
    @FXML
    private TextField selectUserField;

    @FXML
    private Button searchUser;

    @FXML
    private ListView<String> targetUserView;

    @FXML
    private Button resetUserBtn;

    @FXML
    private Button deleteUserBtn;

    @FXML
    private Button changleRolesBtn;

    @FXML
    private VBox roleControls;

    @FXML
    private TextField desiredRoleField;

    @FXML
    private Button addRoleBtn;

    @FXML
    private Button removeRoleBtn;

    @FXML
    private ListView<String> listUsersView;

    @FXML
    public void initialize() {
    	
    	userMan = new UserManager();
    	selectedUser = null;
    	
    	//selected user view intial text
    	ObservableList<String> content = FXCollections.observableArrayList();
    	content.add("No user selected, search for a user.");
    	targetUserView.setItems(content);
    	
    	//
    
    }

    //handleChangeRoles is called by the changeRolesBtn, it toggles the menu after checking to see if a user has been selected.

    @FXML
   private void handleChangeRoles() {
    	
    	if(selectedUser != null) {
    		roleControls.setVisible(!roleControls.isVisible());
            roleControls.setDisable(!roleControls.isDisabled());
    	}else {
    		Alert alert = new Alert(Alert.AlertType.ERROR, "No user was specified \n please search for a user. ", ButtonType.OK);
        	alert.showAndWait();
    	}
    }

    
    @FXML
    private void handleRemoveRole() {
        String desiredRole = desiredRoleField.getText();
        
        if(desiredRole.isEmpty()) {
        	Alert alert = new Alert(Alert.AlertType.ERROR, "No role was specified, please type in the role you would like to remove", ButtonType.OK);
        	alert.showAndWait();
        	return;
        }else {
        	desiredRole = desiredRole.toUpperCase();
        	
        	
        	//TODO overload user role methods to accept strings
        	switch(desiredRole) {
        	case "ADMIN":
        		if(selectedUser.hasRole(ROLE.ADMIN)) {
        			selectedUser.removeRole(ROLE.ADMIN);
        		}
        		break;
        	case "INSTRUCTOR":
        		if(selectedUser.hasRole(ROLE.INSTRUCTOR)) {
        			selectedUser.removeRole(ROLE.INSTRUCTOR);
        		}
        		break;
        	case "STUDENT":
        		if(selectedUser.hasRole(ROLE.STUDENT)) {
        			selectedUser.removeRole(ROLE.STUDENT);
        		}
        		break;
        	default:
        		Alert alert = new Alert(Alert.AlertType.ERROR, "Not a valid role, please enter a valid role", ButtonType.OK);
        		alert.showAndWait();
        		return;
        	}

        	try {
				userMan.removeRoleFromUser(selectedUser.getUsername(), desiredRole);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	updateTargetUserView();
        	
        }
        
        
    }
    
    @FXML
    private void handleAddRole() {
    	String desiredRole = desiredRoleField.getText();
        
        if(desiredRole.isEmpty()) {
        	Alert alert = new Alert(Alert.AlertType.ERROR, "No role was specified, please type in the role you would like to add", ButtonType.OK);
        	alert.showAndWait();
        	return;
        }else {
        	desiredRole = desiredRole.toUpperCase();
        	System.out.println(desiredRole);
        	switch(desiredRole) {
        	case "ADMIN":
        		if(!selectedUser.hasRole(ROLE.ADMIN)) {
        			selectedUser.addRole(ROLE.ADMIN);
        		}
        		break;
        	case "INSTRUCTOR":
        		if(!selectedUser.hasRole(ROLE.INSTRUCTOR)) {
        			selectedUser.addRole(ROLE.INSTRUCTOR);
        		}
        		break;
        	case "STUDENT":
        		if(!selectedUser.hasRole(ROLE.STUDENT)) {
        			selectedUser.addRole(ROLE.STUDENT);
        		}
        		break;
        	default:
        		Alert alert = new Alert(Alert.AlertType.ERROR, "Not a valid role, please enter a valid role", ButtonType.OK);
        		alert.showAndWait();
        		return;
        	}

        	try {
				userMan.assignRoleToUser(selectedUser.getUsername(), desiredRole);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	updateTargetUserView();
        	
        }
    	
    	
    	
    	
    	
    }
    
    
    @FXML
    private void handleDeleteUser() {
    	if(selectedUser != null) {
    		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedUser.getUsername(), ButtonType.YES,ButtonType.NO);
    		alert.setTitle("DELETING USER");
    		alert.showAndWait();
    		if(alert.getResult() == ButtonType.YES) {
    			try {
					userMan.deleteUser(selectedUser.getUsername());
		    		Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, selectedUser.getUsername() + " was deleted!", ButtonType.OK);
		    		alert2.showAndWait();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}else {
    			return;
    		}

    		
    		
    	}else {
    		Alert alert = new Alert(Alert.AlertType.ERROR, "No user was specified \n please search for a user. ", ButtonType.OK);
        	alert.showAndWait();
    	}
    }

    @FXML
    private void handleEditUsers() {
        
    	if(!listUsersView.isDisabled()) {
        	listUsersView.setDisable(true);
        	listUsersView.setVisible(false);
        }
    	
    	
    	editUserView.setDisable(false);
        editUserView.setVisible(true);
    }

    @FXML
    private void handleInviteUser() {
        // Method stub
    }

    @FXML
    private void handleListUsers() {
    	
    	//Disable the editUserView if its showing
    	if(!editUserView.isDisabled()) {
    		editUserView.setDisable(true);
    		editUserView.setVisible(false);
        }
    	
    	//Make the listUsersView visible
    	listUsersView.setDisable(false);
    	listUsersView.setVisible(true);
    	
    	//List all users
    	//TODO MAKE QUERY THE RETURNS ARRAYLIST OF STRINGS CONTAINING
    	//ALL USERS NAMES
    	
    	try {
			ArrayList<User> users = userMan.getAllUsers();
			ObservableList<String> content = FXCollections.observableArrayList();
			
		
			for(User i : users) {
				
				content.add(i.getUsername());
			}
		
		listUsersView.setItems(content);
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	
    }

    @FXML
    private void handleLogout() {
        core.Source.getUIManager().loadLoginPage();
    }


    @FXML
    private void handleResetUser() {
        // Method stub
    }

    @FXML
    private void handleSearchUser() {
        String selectedUsername = selectUserField.getText();
        selectedUser = null;
        ObservableList<String> content = FXCollections.observableArrayList();
        
        if(selectedUsername.isEmpty()) {
        	return;
        }
        try {
        	//GET USER
        	selectedUser = userMan.getUserByUsername(selectedUsername);
        	
			if(selectedUser ==  null)
			{
				content.add("USER NOT FOUND!");
				targetUserView.setItems(content);
				return;
			}else {
				
				content.add("USERNAME: " + selectedUser.getUsername());
				content.add("EMAIL: " + selectedUser.getEmail());
				content.add("NAME: " + selectedUser.getName());
				content.add("PREFFERED NAME: " + selectedUser.getPrefName());
				content.add("ROLES:");
				for(core.ROLE role : selectedUser.getRoles()){
					
					if(role == core.ROLE.ADMIN) {
						content.add("ADMIN");
					}else if(role == core.ROLE.INSTRUCTOR) {
						content.add("INSTRUCTOR");
					}else if(role == core.ROLE.STUDENT) {
						content.add("STUDENT");
					}
				}
				targetUserView.setItems(content);
				return;
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
    }
    
    
    private void updateTargetUserView() {
        ObservableList<String> content = FXCollections.observableArrayList();
        content.add("USERNAME: " + selectedUser.getUsername());
		content.add("EMAIL: " + selectedUser.getEmail());
		content.add("NAME: " + selectedUser.getName());
		content.add("PREFFERED NAME: " + selectedUser.getPrefName());
		content.add("ROLES:");
		for(core.ROLE role : selectedUser.getRoles()){
			
			if(role == core.ROLE.ADMIN) {
				content.add("ADMIN");
			}else if(role == core.ROLE.INSTRUCTOR) {
				content.add("INSTRUCTOR");
			}else if(role == core.ROLE.STUDENT) {
				content.add("STUDENT");
			}
		}
		targetUserView.setItems(content);
    }
    
}
