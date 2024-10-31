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
package ui;

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
import java.sql.SQLException;
import java.util.List;

import core.Article;
import core.BackupManager;
import core.Source;
import core.databaseInterface;
import core.ROLE;

/**
 * This class represents the main application for the Help Article Management System.
 * It provides a graphical user interface for managing help articles, including
 * creation, display, deletion, and backup/restore functionality.
 */
public class HelpArticleSystem{

    private static final String RED = "#FC3D21";
    private static final String BLACK = "#000000";
    private TableView<Article> articleTable;
    HBox searchBox;
    private databaseInterface dbMan;
    private BackupManager backupMan;
    
    /**
     * The main entry point for the JavaFX application.
     * 
     * @param primaryStage The primary stage for this application
     */
    public void show() {
        // Initialize database and backup managers
    	try {
			dbMan = new databaseInterface();
			backupMan = new BackupManager(dbMan);
		} catch (Exception e) {
			e.printStackTrace();
		}
    
        
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: " + BLACK + ";");

        Label titleLabel = new Label("ARTICLES");
        titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(RED));

        setupArticleTable();
        setupSearchBox();
        
        Button addButton = createStylizedButton("ADD ARTICLE");
        Button displayButton = createStylizedButton("DISPLAY ARTICLE");
        Button deleteButton = createStylizedButton("DELETE ARTICLE");
        Button refreshButton = createStylizedButton("REFRESH ARTICLE LIST");
        Button backupButton = createStylizedButton("BACKUP ARTICLES");
        Button restoreButton = createStylizedButton("RESTORE ARTICLES");
        Button deleteAllArticlesButton = createStylizedButton("ERASE ALL ARTICLES");
        Button quitButton = createStylizedButton("EXIT");
        
        addButton.setOnAction(e -> showArticleCreationScreen());
        displayButton.setOnAction(e -> displaySelectedArticle());
        deleteButton.setOnAction(e -> deleteSelectedArticle());
        refreshButton.setOnAction(e -> refreshArticleList());
        backupButton.setOnAction(e -> backupArticles());
        restoreButton.setOnAction(e -> restoreArticles());
        deleteAllArticlesButton.setOnAction(e -> deleteAllArticles());
        quitButton.setOnAction(e-> handleQuit());
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, displayButton, deleteButton, refreshButton, backupButton, restoreButton, deleteAllArticlesButton, quitButton);

        mainLayout.getChildren().addAll(titleLabel,searchBox, articleTable, buttonBox);

        Scene scene = new Scene(mainLayout, 1400, 1400);
        Source.getPrimaryStage().setScene(scene);
        Source.getPrimaryStage().show();
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
	
    
    private void setupSearchBox() {
        searchBox = new HBox(10);
        searchBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter keyword to search");
        searchField.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        searchField.setPrefWidth(200);
        
        Button searchButton = createStylizedButton("SEARCH");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));
        
        searchBox.getChildren().addAll(new Label("SEARCH BY KEYWORD:"), searchField, searchButton);
    }
    
    
    private void handleQuit() {
    	
    	switch(Source.getUIManager().getSelectedRole()) {
    		case core.ROLE.INSTRUCTOR:
    			Source.getUIManager().loadInstructorPage();
    			break;
    		case core.ROLE.STUDENT:
    			Source.getUIManager().loadUserPage();
    			break;
    		case core.ROLE.ADMIN:
    			Source.getUIManager().loadAdminPage();
    			break;
    		default:
    			System.out.println("ARTICLE SYSTEM QUIT BUT COULD NOT FIND VALID ROLE TO RETURN TO");
    			return;
    	}
    	
    }
    
    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            showAlert("Search Error", "Please enter a keyword to search.");
            return;
        }
        
        try {
            List<Article> searchResults = dbMan.searchByKeyword(keyword);
            articleTable.setItems(FXCollections.observableArrayList(searchResults));
            
            if (searchResults.isEmpty()) {
                showAlert("Search Results", "No articles found matching the keyword: " + keyword);
            }
        } catch (Exception e) {
            showAlert("Search Error", "An error occurred while searching: " + e.getMessage());
            e.printStackTrace();
        }
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

    
    private void deleteAllArticles() {
    	try {
			dbMan.clearAllArticles();
			refreshArticleList();
			showAlert("Deleted all articles", "Success!");
		} catch (SQLException e) {
			showAlert("Delete All Articles", "Error: Could not delete articles");
			e.printStackTrace();
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

}