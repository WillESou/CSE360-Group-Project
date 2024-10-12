import java.sql.SQLException;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminPage {
	private User user;
	private Scene scene;
	private UserManager userMan = new UserManager();
	
	private ListView<String> userListView = new ListView<>();
	TextField targetUserField = new TextField();
	
	public AdminPage(User user) {
        this.user = user;
        
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(20);

        // User management section
        VBox userManagementBox = createSection("User Management");
        targetUserField.setPromptText("Enter username");
        
        HBox userActionButtons = new HBox(10);
        Button inviteUserBtn = new Button("Invite User");
        Button deleteUserBtn = new Button("Delete User");
        Button resetUserBtn = new Button("Reset User");
        
        deleteUserBtn.setOnAction(e -> handleDeleteUser());
        
        
        userActionButtons.getChildren().addAll(inviteUserBtn, deleteUserBtn, resetUserBtn);

        userManagementBox.getChildren().addAll(new Label("Target User:"), targetUserField, userActionButtons);

        // User list section
        VBox userListBox = createSection("User List");
        Button listUsersBtn = new Button("List Users");
        userListView.setPrefHeight(200);
        ScrollPane scrollPane = new ScrollPane(userListView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        listUsersBtn.setOnAction(e -> handleListUsers());
        
        
        
        userListBox.getChildren().addAll(listUsersBtn, scrollPane);

        // Role management section
        VBox roleManagementBox = createSection("Role Management");
        Button editRolesBtn = new Button("Edit Roles");
        roleManagementBox.getChildren().add(editRolesBtn);

        // Logout section
        HBox logoutBox = new HBox();
        vbox.getAlignment();
		logoutBox.setAlignment(Pos.CENTER_RIGHT);
        Button logoutBtn = new Button("Logout");
        logoutBox.getChildren().add(logoutBtn);

        vbox.getChildren().addAll(userManagementBox, userListBox, roleManagementBox, logoutBox);
        
        scene = new Scene(vbox, 800, 800);
    }

    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        section.getChildren().add(titleLabel);
        return section;
    }
	
	public void show() {
		Source.getPrimaryStage().setScene(scene);
        Source.getPrimaryStage().show();
	}
	
	
	private void handleDeleteUser() {
		String username = targetUserField.getText();
    	try {
			User targetUser = userMan.getUserByUsername(username);
			if(targetUser != null)
			{
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete user:" + username, ButtonType.YES, ButtonType.NO);
				if(alert.getResult() == ButtonType.YES) {
					userMan.deleteUser(username);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return;
	}
	
	private void handleResetUser() {
		
	}
	
	private void handleListUsers() {
		ArrayList<User> users;
		try {
			users = userMan.getAllUsers();
			for(User user : users) {
				userListView.getItems().add(user.getUsername());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleInviteUser() {
		
	}
	
	private void handleEditRolls() {
		
	}
	
	private void handleLogout() {
		
	}
	
	
	
}
