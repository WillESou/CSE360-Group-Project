/**
 * <p> Title: Help Article System </p>
 * 
 * <p> Description: Main application class for the Help Article Management System </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author William Sou
 * 
 * @version 1.0    2024-10-15    Initial implementation
 */
package core;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.util.List;

import core.databaseInterface;

/**
 * This class represents the main application for the Help Article Management System.
 * It provides a graphical user interface for managing help articles, including
 * creation, display, deletion, and backup/restore functionality.
 */
public class HelpArticleSystem extends Application {

    private static final String RED = "#FC3D21";
    private static final String BLACK = "#000000";
    private TableView<Article> articleTable;
    private databaseInterface dbMan;
    private BackupManager backupMan;
    
    /**
     * The main entry point for the JavaFX application.
     * 
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize database and backup managers
    	try {
			dbMan = new databaseInterface();
			backupMan = new BackupManager(dbMan);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	primaryStage.setTitle("Help Article Management System");
        
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: " + BLACK + ";");

        Label titleLabel = new Label("ARTICLES");
        titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(RED));

        setupArticleTable();

        Button addButton = createStylizedButton("ADD ARTICLE");
        Button displayButton = createStylizedButton("DISPLAY ARTICLE");
        Button deleteButton = createStylizedButton("DELETE ARTICLE");
        Button refreshButton = createStylizedButton("REFRESH ARTICLE LIST");
        Button backupButton = createStylizedButton("BACKUP ARTICLES");
        Button restoreButton = createStylizedButton("RESTORE ARTICLES");
        
        addButton.setOnAction(e -> showArticleCreationScreen());
        displayButton.setOnAction(e -> displaySelectedArticle());
        deleteButton.setOnAction(e -> deleteSelectedArticle());
        refreshButton.setOnAction(e -> refreshArticleList());
        backupButton.setOnAction(e -> backupArticles());
        restoreButton.setOnAction(e -> restoreArticles());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, displayButton, deleteButton, refreshButton, backupButton, restoreButton);

        mainLayout.getChildren().addAll(titleLabel, articleTable, buttonBox);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a styled button with the given text.
     * 
     * @param text The text to display on the button
     * @return A styled Button instance
     */
    private Button createStylizedButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + RED + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10px 20px;");
        return button;
    }
    
    /**
     * Sets up the table view for displaying articles.
     */
    private void setupArticleTable() {
        articleTable = new TableView<>();
        articleTable.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: white;");

        TableColumn<Article, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Article, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(new String(cellData.getValue().getTitle())));

        TableColumn<Article, String> authorColumn = new TableColumn<>("Author(s)");
        authorColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(new String(cellData.getValue().getAuthors())));

        articleTable.getColumns().addAll(idColumn,titleColumn, authorColumn);
    }
	
    /**
     * Shows the article creation screen.
     */
    private void showArticleCreationScreen() {
        ArticleCreationScreen creationScreen = new ArticleCreationScreen(dbMan);
        creationScreen.show();
    }

    /**
     * Displays the selected article in a new window.
     */
    private void displaySelectedArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
           try {
			ArticleDisplayScreen dispArticle = new ArticleDisplayScreen(dbMan.getArticle(selectedArticle.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
        } else {
            showAlert("No Article Selected", "Please select an article to view.");
        }
    }

    /**
     * Deletes the selected article from the database.
     */
    private void deleteSelectedArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
           try {
			dbMan.deleteArticle(selectedArticle.getId());
			refreshArticleList();
		} catch (Exception e) {
			showAlert("Delete Article", "Error: Could not delete article");
			e.printStackTrace();
		}
        } else {
            showAlert("No Article Selected", "Please select an article to delete.");
        }
    }
    
    /**
     * Refreshes the article list in the table view.
     */
    public void refreshArticleList() {
        try {
            List<Article> articles = dbMan.getAllArticles();
            articleTable.setItems(FXCollections.observableArrayList(articles));
        } catch (Exception e) {
            showAlert("Refresh Error", "Failed to refresh article list: " + e.getMessage());
        }
    }

    /**
     * Initiates the backup process for all articles.
     */
    private void backupArticles() {
    	System.out.println("INITIATING BACKUP");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Backup File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Files", "*.bak"));
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try {
            	System.out.println("CALLING BACKUP MANAGER");
                backupMan.backupArticles(file.getAbsolutePath());
                showAlert("Backup Successful", "Articles have been backed up successfully.");
            } catch (Exception e) {
                showAlert("Backup Failed", "Failed to backup articles: " + e.getMessage());
            }
        }
    }

    /**
     * Initiates the restore process for articles from a backup file.
     */
    private void restoreArticles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Backup File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Files", "*.bak"));
        File file = fileChooser.showOpenDialog(null);
        
        if (file != null) {
            try {
                backupMan.restoreArticles(file.getAbsolutePath());
                refreshArticleList();
                showAlert("Restore Successful", "Articles have been restored successfully.");
            } catch (Exception e) {
                showAlert("Restore Failed", "Failed to restore articles: " + e.getMessage());
            }
        }
    }

    /**
     * Displays an alert dialog with the given title and content.
     * 
     * @param title The title of the alert dialog
     * @param content The content message of the alert dialog
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * The main method that launches the JavaFX application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}