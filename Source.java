import javafx.application.Application;
import javafx.stage.Stage;


public class Source extends Application {
	
	databaseInterface database = new databaseInterface();
	private static Stage primaryStage;
	
	public void start(Stage primaryStage) {
		Source.primaryStage = primaryStage;
		primaryStage.setTitle("Help Program");
		
		//Login page
		LoginPage loginPage = new LoginPage();
		loginPage.show();
		
		
	}
    
	 public static Stage getPrimaryStage() {
	        return primaryStage;
	    }
	
    public static void main(String[] args) {
        launch(args);
    }
}
