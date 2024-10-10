import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class Source extends Application {

	// Enumeration for the different window states possible
	enum STATE{
		LOGIN,
		CREATEACCOUNT,
		CHOOSEROLE,
		STUDENT,
		INSTRUCTOR,
		ADMIN,
		FIRSTLOGIN,
		EDITROLES,
		DEL,
		RESET,
		RESETUSER
	}
	
    // Public fields for user interface components
    private TextField usernameField;
    private TextField passwordField;
    private TextField emailField;
    private TextField nameField;
    private TextField confirmPasswordField;
    private TextField deleteField;
    private Button loginButton;
    private Button createAccountButton;
    private Stage primaryStage;
    private User currentUser;
    private Label label_Password = new Label("Enter password:");
    private Label label_Username = new Label("Enter username:");
    private Label label_Email = new Label("Enter email:");
    private Label label_Name = new Label("Enter your name (first, middle, last):");
    private Label label_Admin = new Label("Enter a username:");
    private Label OTP = new Label("");
	private Label resetP = new Label("Enter New Password:");
    
    
    // User list to store created users
    public static databaseInterface interF = new databaseInterface();
    public static UserManager userMan = new UserManager();

    @Override
    public void start(Stage primaryStage) {
        // Add the generic admin to the list of users
        this.primaryStage = primaryStage;
        
        primaryStage.setTitle("Account Setup");
    	
        // Initialize the UI components
        usernameField = new TextField();
        passwordField = new TextField();
        loginButton = new Button("Login");
        createAccountButton = new Button("Create Account");
    	
    	// Handle the login action	
        loginButton.setOnAction(event -> {
			try {
				handleLogin();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

        // Handle the account creation action
        createAccountButton.setOnAction(event -> changeScene(STATE.CREATEACCOUNT));

        // Layout setup
        VBox layout = new VBox();
        layout.getChildren().addAll(label_Username, usernameField, label_Password, passwordField, loginButton, createAccountButton);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void changeScene(STATE state) {
    	
    	// Variables that will be used in more than one state
    	Scene scene = null;
    	VBox layout = null;
    	Button logOut = new Button("Log Out");
    	Label label_Confirm = new Label("Confirm Password:");
    	Button back = new Button("Back");
    	Button firstLogin = new Button("Login");
    	List<String> roleList = null;
    	int numOfRoles = 0;
    	// FSM to handle window selection
        switch(state) {
        
	        // State 0: Login screen
	        case STATE.LOGIN: 	
	        	
	        	currentUser = null;
	        	primaryStage.setTitle("Login");
	        	
	            // Initialize the UI components
	            usernameField = new TextField();
	            passwordField = new TextField();
	            loginButton = new Button("Login");
	            createAccountButton = new Button("Create Account");
	        	
	        	// Handle the login action	
	            loginButton.setOnAction(event -> {
					try {
						handleLogin();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

	            // Handle the account creation action
	            createAccountButton.setOnAction(event -> changeScene(STATE.CREATEACCOUNT));

	            // Layout setup
	            layout = new VBox();
	            layout.getChildren().addAll(label_Username, usernameField, label_Password, passwordField, loginButton, createAccountButton);

	            scene = new Scene(layout, 300, 200);
	            primaryStage.setScene(scene);
	            primaryStage.show();
		        break;
		    
	        case STATE.FIRSTLOGIN:
	        	
				emailField = new TextField();
				nameField = new TextField();
				
	        	firstLogin.setOnAction(event -> {
					try {
						firstLogin();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
	        	
	        	layout = new VBox();
	        	layout.getChildren().addAll(label_Email, emailField, label_Name, nameField, firstLogin);
	        	
	        	scene = new Scene(layout, 300, 200);
	            primaryStage.setScene(scene);
	            primaryStage.show();
		        break;
	        // State 1: Account creation screen
	        case STATE.CREATEACCOUNT:
	        	
	        	primaryStage.setTitle("Create an Account");
	        	
				// Initialization
				usernameField = new TextField();
				passwordField = new TextField();
				confirmPasswordField = new TextField();
				createAccountButton = new Button("Create Account");
		
				// Handle the account 
				createAccountButton.setOnAction(event -> {
					try {
						handleCreateAccount();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				back.setOnAction(event -> changeScene(STATE.LOGIN));
		
				// Layout setup
				layout = new VBox();
				layout.getChildren().addAll(label_Username, usernameField, label_Password, passwordField, label_Confirm, confirmPasswordField, createAccountButton, back);
		
				scene = new Scene(layout, 300, 300);
				primaryStage.setScene(scene);
				primaryStage.show();
	        	break;
	        	
	        // State 2: Choosing a role for the user to use
	        // ONCE WILL ADDS SQL FUNCTIONALITY YOU CAN ADD LOGIC TO CHOOSE WHETHER OR NOT TO
	        // USE THIS STATE
	        case STATE.CHOOSEROLE:
	        	
			try {
				roleList = userMan.getRolesByUsername(currentUser.username);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        	if (roleList.contains("ADMIN")){ numOfRoles += 1;}
	        	if (roleList.contains("STUDENT")){ numOfRoles += 10;}
	        	if (roleList.contains("INSTRUCTOR")){ numOfRoles += 100;}
	        	
	        	if (numOfRoles == 1) {
	        		changeScene(STATE.ADMIN);
	        		return;
	        	}
	        	if (numOfRoles == 10 || numOfRoles == 0) {
	        		changeScene(STATE.STUDENT);
	        		return;
	        	}
	        	if (numOfRoles == 100) {
	        		changeScene(STATE.INSTRUCTOR);
	        		return;
	        	}
	        	
	        	primaryStage.setTitle("Choose Role:");
	        	
	        	// Initialize the UI components
	        	Button chooseAdmin = new Button("Admin");
	        	Button chooseStudent = new Button("Student");
	        	Button chooseInstructor = new Button("Instructor");
	        	
	        	chooseAdmin.setOnAction(event -> changeScene(STATE.ADMIN));
	        	chooseStudent.setOnAction(event -> changeScene(STATE.STUDENT));
	        	chooseInstructor.setOnAction(event -> changeScene(STATE.INSTRUCTOR));
	        	
	        	layout = new VBox();
//	        	layout.getChildren().addAll(chooseAdmin, chooseStudent, chooseInstructor);
	        	if (currentUser.roles.contains(User.ROLE.ADMIN)) { layout.getChildren().addAll(chooseAdmin); }
	        	if (currentUser.roles.contains(User.ROLE.STUDENT)) { layout.getChildren().addAll(chooseStudent); }
	        	if (currentUser.roles.contains(User.ROLE.INSTRUCTOR)) { layout.getChildren().addAll(chooseInstructor); }
	        	
	        	scene = new Scene(layout, 300, 200);
	        	primaryStage.setScene(scene);
	        	primaryStage.show();
	        	break;
	        	
	        // State 3: Student role
	        // Only function is to log out
	        case STATE.STUDENT:
	        	
	        	primaryStage.setTitle(currentUser.username + ": " + state.toString());
	        	
	        	logOut.setOnAction(event -> changeScene(STATE.LOGIN));
	        	
	        	layout = new VBox();
	        	layout.getChildren().addAll(logOut);
	        	
	        	scene = new Scene(layout, 300, 200);
	        	primaryStage.setScene(scene);
	        	primaryStage.show();
	        	break;
	        
	        // State 4: Instructor role
	        // Only function is to log out
	        case STATE.INSTRUCTOR:
	        	
	        	primaryStage.setTitle(currentUser.username + ": " + state.toString());
	        	
	        	logOut.setOnAction(event -> changeScene(STATE.LOGIN));
	        	
	        	layout = new VBox();
	        	layout.getChildren().addAll(logOut);
	        	
	        	scene = new Scene(layout, 300, 200);
	        	primaryStage.setScene(scene);
	        	primaryStage.show();
	        	break;
	        	
	        // State 5: Admin role
	        case STATE.ADMIN:
	        	primaryStage.setTitle(currentUser.username + ": " + state.toString());
	        	
	        	
	        	Button inviteUserBtn = new Button("Invite User");
	        	Button listUserBtn = new Button("List Users");
	        	Button delUserBtn = new Button("Delete User");
	        	Button resetUserBtn = new Button("Reset User");
	        	Button editRoleBtn = new Button("Edit Roles");
	        	Label users = new Label("press 'List Users' to display all users");
	        	
	        	
	        	logOut.setOnAction(event -> changeScene(STATE.LOGIN));
	        	editRoleBtn.setOnAction(event -> changeScene(STATE.EDITROLES));
	        	delUserBtn.setOnAction(event -> changeScene(STATE.DEL));
	        	listUserBtn.setOnAction(event -> {
	        		try {
	        			users.setText(listUsers());
	        		} catch (SQLException e) {
	        			e.printStackTrace();
	        		}
	        	});
	        	
	        	resetUserBtn.setOnAction(event -> changeScene(STATE.RESET));
	        	
	        	layout = new VBox();
	        	layout.getChildren().addAll(inviteUserBtn,users,listUserBtn,delUserBtn,resetUserBtn,editRoleBtn,logOut);
	       
	        	scene = new Scene(layout, 600, 400);
	        	primaryStage.setScene(scene);
	        	primaryStage.show();
	        	
	        	
	        	break;
	        case STATE.EDITROLES:
	        	primaryStage.setTitle(currentUser.username + ": " + state.toString());
	        	Label usernameLabel = new Label("Target username");
	        	TextField usernameField = new TextField();
	        	
	        	Label roleLabel = new Label("Role to add/remove");
	        	TextField roleField = new TextField();
	      
	        	Button listRolesBtn = new Button("List Roles");
	        	Label rolesLabel = new Label("Press 'List Roles' to display users roles");
	        	
	        	Button addRoleBtn = new Button("Add Role");
	        	Button removeRoleBtn = new Button("Remove Role");
	        	Button adminPanelBtn = new Button("Back to admin panel");
	        	
	        	
	        	addRoleBtn.setOnAction(event -> {
	        		try {
	        			userMan.assignRoleToUser(usernameField.getText(), roleField.getText());
	        		} catch (SQLException e) {
	        			e.printStackTrace();
	        		}
	        	});
	        	
	        	removeRoleBtn.setOnAction(event -> {
	        		try {
	        			userMan.removeRoleFromUser(usernameField.getText(), roleField.getText());
	        		} catch (SQLException e) {
	        			e.printStackTrace();
	        		}
	        	});
	        	
	        	listRolesBtn.setOnAction(event -> {
	        		try {
	        			rolesLabel.setText(listRoles(usernameField.getText()));
	        			VBox updatedLayout = new VBox();
	        			updatedLayout.getChildren().addAll(usernameLabel,usernameField,roleLabel,roleField,rolesLabel,listRolesBtn,addRoleBtn,removeRoleBtn,adminPanelBtn);
	        			Scene updatedScene = new Scene(updatedLayout, 600, 400);
	        			primaryStage.setScene(updatedScene);
	        			primaryStage.show();
	        		} catch (SQLException e) {
	        			e.printStackTrace();
	        		}
	        	});
	        	
	        	
	        	
	        	adminPanelBtn.setOnAction(event -> changeScene(STATE.ADMIN));
	        	
	        	
	        	layout = new VBox();
	        	
	        	layout.getChildren().addAll(usernameLabel,usernameField,roleLabel,roleField,rolesLabel,listRolesBtn,addRoleBtn,removeRoleBtn,adminPanelBtn);
	        	scene = new Scene(layout, 600, 400);
	        	primaryStage.setScene(scene);
	        	primaryStage.show();
	        
	        case STATE.DEL:
	        	
	        	primaryStage.setTitle("Delete User");
	        	
	        	deleteField = new TextField();
	        	
	        	Button delete = new Button("Delete");
	        	CheckBox check = new CheckBox("Are You Sure?");
	        	
	        	delete.setOnAction(event -> {if (check.isSelected())
					try {
						deleteUser();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}});
	        	back.setOnAction(event -> changeScene(STATE.ADMIN));
	        	
	        	layout = new VBox();
	        	layout.getChildren().addAll(label_Admin, deleteField, check, delete, back);
	        	scene = new Scene(layout, 300, 200);
	        	primaryStage.setScene(scene);
	        	primaryStage.show();
	        		
	        	break;
	        case STATE.RESET:
	        	
	        	primaryStage.setTitle("Reset User");
	        	
	        	usernameField = new TextField();
	        	
	        	Button reset = new Button("Reset");
	        	
	        	reset.setOnAction(event -> {
					try {
						OTP = new Label(resetUser());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
	        	back.setOnAction(event -> changeScene(STATE.ADMIN));
	        	
	        	layout = new VBox();
	        	layout.getChildren().addAll(label_Admin, usernameField, reset, OTP, back);
	        	scene = new Scene(layout, 300, 200);
	        	primaryStage.setScene(scene);
	        	primaryStage.show();
	        	break;
	        case STATE.RESETUSER:
	        	
	        	primaryStage.setTitle("Reset Account");
	        	
	        	passwordField = new TextField();
	        	
	        	back.setOnAction(event -> changeScene(STATE.LOGIN));
	        	loginButton.setOnAction(event -> {
					try {
						resetAccount();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
	        	
	        	break;
        }
    }

    private void handleLogin() throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simulate login process (this links to your User class)
        for (User user : userMan.getAllUsers()) {
            if (user.verifyUsername(username)) {
                if (user.verifyPassword(password)) {
                	if (user.email == null) {
                		System.out.println("First Time Login");
                		currentUser = user;
                		changeScene(STATE.FIRSTLOGIN);
                		return;
                	}
                    System.out.println("Login successful!");
                    currentUser = user;
                    changeScene(STATE.CHOOSEROLE);
                    return;
                    
                } else {
                    System.out.println("Incorrect password!");
                }
                return; // Exit after finding the user
            }
        }
        System.out.println("Username not found!");
    }
    
    private void handleCreateAccount() throws SQLException {
		String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        boolean isFirst = false;

        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        	System.out.println("Failed to Create the Account\nDid not fill out all the required information");
        	return;
        }
        
        if (!password.equals(confirmPassword)) {
        	System.out.println("Failed to Create the Account\nPassword does not match");
        	return;
        }

        // Create a new user and add it to the user list
        if (userMan.getAllUsers().isEmpty()) {
        	isFirst = true;
        }
        userMan.createUser(username, null, "", password);
        if (isFirst) {
        	userMan.assignRoleToUser(username, "ADMIN");
        }
        System.out.println("Account created for " + username);
        changeScene(STATE.LOGIN);
    }

    
    private void firstLogin() throws SQLException {
    	String email = emailField.getText();
    	String name = nameField.getText();
    	
    	if(email.isEmpty() || name.isEmpty()) {
        	System.out.println("Failed to Verify the Account\nDid not fill out all the required information");
        	return;
        }
    	
    	userMan.updateUser(currentUser.username, email, name);
    	System.out.println("Account Verified.");
    	changeScene(STATE.CHOOSEROLE);
    }
    
    private String listUsers() throws SQLException {
    	ArrayList<User> users = userMan.getAllUsers();
    	String retStr = "";
    	
    	for(User i : users)
    	{
    		retStr += i.username + "\n";
    	}
    	
    	return retStr;
    	
    }

    private String listRoles(String username) throws SQLException {
    	List<String> roles = userMan.getRolesByUsername(username);
    	
    	String retStr = "";
    	
    	for(String i : roles)
    	{
    		retStr += i + "\n";
    	}
    	return retStr;
    }
    private String resetUser() throws SQLException {
    	String username = usernameField.getText();
    	Random rand = new Random();
    	String pass = Integer.toString(rand.nextInt(100000));
    	
    	for (User user : userMan.getAllUsers()) {
            if (user.verifyUsername(username)) {
            	userMan.updateUser(username, userMan.getUserByUsername(username).email, pass, userMan.getUserByUsername(username).name);
            	System.out.println("Reset Successful! Send temporary password to user.");
            	OTP = new Label("One Time Password: " + pass);
            	changeScene(STATE.RESET);
            	return pass;
            }
    	}
    	return null;
    }
    private void resetAccount() throws SQLException {
    	String pass = passwordField.getText();
    	
    	userMan.updateUser(currentUser.username, currentUser.email, pass, currentUser.name);
    }
    private void deleteUser() throws SQLException {
    	String username = deleteField.getText();
    	
    	for (User user : userMan.getAllUsers()) {
            if (user.verifyUsername(username)) {
            	userMan.deleteUser(username);
            	System.out.println("User Successfully Deleted.");
            	changeScene(STATE.DEL);
            	return;
            }
        }
    	System.out.println("User does not exist.");
    	changeScene(STATE.DEL);
    	return;
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
