



import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginPage {
	private Scene scene;
	private UserManager userMan = new UserManager();
	
	public LoginPage() {
		GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        TextField username = new TextField();
        PasswordField password = new PasswordField();
        Button loginButton = new Button("Login");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(loginButton, 1, 2);

        loginButton.setOnAction(e -> handleLogin(username.getText(), password.getText()));

        scene = new Scene(grid, 300, 200);
	}
	
	
	
	
	
	
	private void handleLogin(String username, String password){
		try {
			
			if(userMan.getAllUsers().size() == 0)
			{
				userMan.createUser(username, "", "", password);
				userMan.assignRoleToUser(username, "ADMIN");
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "You are the first user and have been assigned ADMIN. \n Please login again.", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			
			
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
				
				RoleSelectionPage roleSelectionPage = new RoleSelectionPage(loggedInUser);
				roleSelectionPage.show();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Login", ButtonType.OK);
				alert.showAndWait();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void show() {
		Source.getPrimaryStage().setScene(scene);
        Source.getPrimaryStage().show();
		
	}
	
}
