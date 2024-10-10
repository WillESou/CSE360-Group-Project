import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Source extends Application{
	static User genericAdmin = new User("admin", "admin@asu.edu", "Joe", "pass123");
	static UserManager userMan = new UserManager();
	public static void main(String[] args) throws SQLException {
		databaseInterface db = new databaseInterface();
		
		
		userMan.createUser("admin", "admin@asu.edu", "Joe", "pass123");
		launch(args);
    }	
    
    public void start(Stage primaryStage) {
    	System.out.println("Started!");
        primaryStage.setTitle("CSE360 PLACEHOLDERNAME");
        
        //LOGIN PAGE
        TextField usernameField = new TextField("Enter Username");
        TextField passwordField = new TextField("Enter Password");
        
        Button btn = new Button();
        btn.setText("Login");
        btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	System.out.println("Attemping login!..");
               String user = usernameField.getText();
               String pass = passwordField.getText();
               System.out.println("Username is " + user);
               System.out.println("Password is " + pass);
               try {
				User attempt = userMan.getUserByUsername(user);
				if(attempt.verifyPassword(pass))
				{
					System.out.println("LOGIN SUCCESSFUL");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
            }
        });
        /*
        Button btn2 = new Button();
        btn2.setText("CLOSE WINDOW");
        btn2.setOnAction(new EventHandler<>() {
        	public void handle(ActionEvent event2) {
        		System.out.println("CLOSING");
        		primaryStage.close();
        	}
        });
        */
       
        VBox root = new VBox();
        Text loginTitle = new Text("Login");
        //btn2.setLayoutX(-100);
        //btn2.setLayoutY(-100);
        root.getChildren().add(loginTitle);
        root.getChildren().add(usernameField);
        root.getChildren().add(passwordField);
        root.getChildren().add(btn);
        //root.getChildren().add(btn2);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
}



