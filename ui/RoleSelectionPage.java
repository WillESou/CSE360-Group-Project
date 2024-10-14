package ui;


import javafx.scene.control.Label;
import core.ROLE;
import core.Source;
import core.User;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;



public class RoleSelectionPage {
	private User user;
	private Scene scene;
	
	public RoleSelectionPage(User user) {
		this.user = user;
		
		VBox vbox = new VBox();
		
		Label selectRoleText = new Label("Please select the role you would like to use for this session");
		vbox.getChildren().add(selectRoleText);
		if(this.user.hasRole(ROLE.ADMIN))
		{
			Button adminBtn = new Button("ADMIN");
			adminBtn.setOnAction(e -> handleAdminSelection());
			
			vbox.getChildren().add(adminBtn);	
		}
		if(this.user.hasRole(ROLE.INSTRUCTOR))
		{
			Button instructorBtn = new Button("INSTRUCTOR");
			instructorBtn.setOnAction(e -> handleInstructorSelection());
			vbox.getChildren().add(instructorBtn);	
		}
		if(this.user.hasRole(ROLE.STUDENT))
		{
			Button studentBtn = new Button("STUDENT");
			studentBtn.setOnAction(e -> handleStudentSelection());
			vbox.getChildren().add(studentBtn);	
		}
		
		scene = new Scene(vbox, 600, 400);
	}
	
	public void show() {
		Source.getPrimaryStage().setScene(scene);
        Source.getPrimaryStage().show();
	}
	
	
	private void handleAdminSelection(){
		AdminPage adminPage = new AdminPage(this.user);
		adminPage.show();
	}
	
	private void handleInstructorSelection() {
		
	}
	
	private void handleStudentSelection() {
		
	}
}
