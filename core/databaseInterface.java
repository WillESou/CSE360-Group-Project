/***
 * 
 * <p> databaseInterface </p>
 * 
 * <p> Description: TODO.</p>
 * 
 * 
 * @author William Sou
 * 
 * @version 1.00	2024-10-18
 * 
 */

/**
 * TODO
 */
package core;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import core.Article;
import core.EncryptionHelper;
import core.EncryptionUtils;



import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseInterface {
	
	private EncryptionHelper encryptHelper;
	private Statement statement = null;
	
	
	private static Connection connection;
	private static String jdbcURL = "jdbc:h2:./programDatabase";
	private static String username = "sa";
	private static String password = "pass";
	public static Connection getConnection() throws SQLException {
	        if (connection == null || connection.isClosed()) {
	            connection = DriverManager.getConnection(jdbcURL, username, password);
	        }
	        return connection;
	}
	
	public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	public databaseInterface() {
		try(Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
			System.out.println("Connection to H2 database successful");
			this.encryptHelper = new EncryptionHelper(); 
			
			//Creating all user tables
			createUsersTable(connection);
			createRolesTable(connection);
			populateRolesTable(connection);
			createUserRolesTable(connection);
            createSkillsTable(connection);
            createUserSkillsTable(connection);

            createArticleTables(connection);

            createInviteCodesTable(connection);

            
            //Creating session tables
            createSessionTable(connection);
            createSessionStudentsTable(connection);
            
            
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//CREATING USER TABLE

	private void createUsersTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS USERS (" +
                     "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                     "USERNAME VARCHAR(50) NOT NULL UNIQUE, " +
                     "EMAIL VARCHAR(100) UNIQUE, " +
                     "FIRSTNAME VARCHAR(100), " +
                     "MIDDLENAME VARCHAR(100)," +
                     "LASTNAME VARCHAR(100), " +
                     "PREFERREDNAME VARCHAR(100)," +
                     "PASSWORD VARCHAR(64) NOT NULL" +  // Assuming SHA-256 hash
                     ")";
        executeUpdate(conn, sql, "USERS table");
    }

	 private void createUserRolesTable(Connection conn) throws SQLException {
	        String sql = "CREATE TABLE IF NOT EXISTS USER_ROLES (" +
	                     "USER_ID INT, " +
	                     "ROLE_ID INT, " +
	                     "PRIMARY KEY (USER_ID, ROLE_ID), " +
	                     "FOREIGN KEY (USER_ID) REFERENCES USERS(ID), " +
	                     "FOREIGN KEY (ROLE_ID) REFERENCES ROLES(ID)" +
	                     ")";
	        executeUpdate(conn, sql, "USER_ROLES table");
	    }
	//CREATING TABLE THAT TRACKS ROLLS
	private void createRolesTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS ROLES (" +
                     "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                     "ROLE_NAME VARCHAR(20) UNIQUE NOT NULL" +
                     ")";
        executeUpdate(conn, sql, "ROLES table");
    }

    private void populateRolesTable(Connection conn) throws SQLException {
        String[] roles = {"ADMIN", "STUDENT", "INSTRUCTOR"};
        String selectSql = "SELECT COUNT(*) FROM ROLES WHERE ROLE_NAME = ?";
        String insertSql = "INSERT INTO ROLES (ROLE_NAME) VALUES (?)";
        
        for (String role : roles) {
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, role);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, role);
                        insertStmt.executeUpdate();
                        System.out.println("Inserted role: " + role);
                    }
                } else {
                    System.out.println("Role already exists: " + role);
                }
            }
        }
    }


    //CREATING TABLE THAT TRACKS SKILL TYPES
    private void createSkillsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS SKILLS (" +
                     "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                     "SKILL_NAME VARCHAR(50) UNIQUE NOT NULL" +
                     ")";
        executeUpdate(conn, sql, "SKILLS table");
    }
    //CREATING TABLE THAT TRACKS WHICH USER HAS WHAT SKILL AND THEIR SKILL LEVEL
    private void createUserSkillsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS USER_SKILLS (" +
                     "USER_ID INT, " +
                     "SKILL_ID INT, " +
                     "SKILL_LEVEL ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'), " +
                     "PRIMARY KEY (USER_ID, SKILL_ID), " +
                     "FOREIGN KEY (USER_ID) REFERENCES USERS(ID), " +
                     "FOREIGN KEY (SKILL_ID) REFERENCES SKILLS(ID)" +
                     ")";
        executeUpdate(conn, sql, "USER_SKILLS table");
    }
    
    
    //CREATING TABLE THAT TRACKS INVITE CODES
    private void createInviteCodesTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS INVITE_CODES (" +
                     "CODE VARCHAR(16) PRIMARY KEY, " +
                     "ROLE VARCHAR(20) NOT NULL, " +  // The role this invite code grants
                     "CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
        executeUpdate(conn, sql, "INVITE_CODES table");
    }
    
    private void createSessionTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS SESSIONS (" +
                     "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                     "NAME VARCHAR(100) NOT NULL, " +
                     "ADMIN_ID INT NOT NULL, " +
                     "FOREIGN KEY (ADMIN_ID) REFERENCES USERS(ID)" +
                     ")";
        executeUpdate(conn, sql, "SESSIONS table");
    }

    private void createSessionStudentsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS SESSION_STUDENTS (" +
                     "SESSION_ID INT, " +
                     "STUDENT_ID INT, " +
                     "PRIMARY KEY (SESSION_ID, STUDENT_ID), " +
                     "FOREIGN KEY (SESSION_ID) REFERENCES SESSIONS(ID), " +
                     "FOREIGN KEY (STUDENT_ID) REFERENCES USERS(ID)" +
                     ")";
        executeUpdate(conn, sql, "SESSION_STUDENTS table");
    }
    
    
    //EXECUTES A STATEMENT, PROVIDES UNIQUE ERROR IF IT FAILS
    private void executeUpdate(Connection conn, String sql, String tableName) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println(tableName + " created or already exists.");
        }
    }
    
    
    
    
    /**
     * Creates the necessary tables in the database if they don't already exist.
     * 
     * @throws SQLException If there's an error executing the SQL statement
     */
    private void createArticleTables(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS help_articles ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "title VARCHAR(255) NOT NULL, "
                + "authors VARCHAR(1000) NOT NULL, "
                + "abstract TEXT, "
                + "keywords VARCHAR(500), "
                + "body TEXT, "
                + "references TEXT, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
        executeUpdate(conn, sql, "ARTICLES table");
    }
    
    /**
     * Adds a new article to the database.
     * 
     * @param article The Article object to be added
     * @return The ID of the newly added article
     * @throws Exception If there's an error adding the article to the database
     */
    public int addArticle(Article article) throws Exception {
        String sql = "INSERT INTO help_articles (title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, encryptField(new String(article.getTitle())));
            pstmt.setString(2, encryptField(new String(article.getAuthors())));
            pstmt.setString(3, encryptField(new String(article.getAbstract())));
            pstmt.setString(4, encryptField(new String(article.getKeywords())));
            pstmt.setString(5, encryptField(new String(article.getBody())));
            pstmt.setString(6, encryptField(new String(article.getReferences())));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating article failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating article failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves all articles from the database with basic information (id, title, authors).
     * 
     * @return A List of Article objects containing basic information
     * @throws Exception If there's an error retrieving articles from the database
     */
    public List<Article> getAllArticles() throws Exception {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT id, title, authors FROM help_articles";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Article nArticle = new Article(
                    decryptField(rs.getString("title")).toCharArray(),
                    decryptField(rs.getString("authors")).toCharArray()
                );
                nArticle.setId(rs.getInt("id"));
                articles.add(nArticle);
            }
        }
        return articles;
    }
   
    /**
     * Retrieves all articles from the database with complete information.
     * 
     * @return A List of Article objects containing all information
     * @throws Exception If there's an error retrieving articles from the database
     */
    public List<Article> getAllCompleteArticles() throws Exception {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT id, title, authors, abstract, keywords, body, references FROM help_articles";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Article nArticle = new Article(
                    decryptField(rs.getString("title")).toCharArray(),
                    decryptField(rs.getString("authors")).toCharArray(),
                    decryptField(rs.getString("abstract")).toCharArray(),
                    decryptField(rs.getString("keywords")).toCharArray(),
                    decryptField(rs.getString("body")).toCharArray(),
                    decryptField(rs.getString("references")).toCharArray()
                );
                nArticle.setId(rs.getInt("id"));
                articles.add(nArticle);
            }
        }
        return articles;
    }
   
    /**
     * Retrieves an article if it contains the given keyword
     * 
     * @param keyword
     * @return
     * @throws Exception
     */
    
    public List<Article> searchByKeyword(String keyword) throws Exception {
        List<Article> matchingArticles = new ArrayList<>();
        String sql = "SELECT id, title, authors, keywords FROM help_articles";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Decrypt and check keywords
                String decryptedKeywords = decryptField(rs.getString("keywords")).toLowerCase();
                if (decryptedKeywords.contains(keyword.toLowerCase())) {
                    Article article = new Article(
                        decryptField(rs.getString("title")).toCharArray(),
                        decryptField(rs.getString("authors")).toCharArray()
                    );
                    article.setId(rs.getInt("id"));
                    matchingArticles.add(article);
                }
            }
        }
        return matchingArticles;
    }
    
    
    
    
    
    /**
     * Clears all articles from the database.
     * 
     * @throws SQLException If there's an error executing the SQL statement
     */
    public void clearAllArticles() throws SQLException {
        String sql = "DELETE FROM help_articles";
        try (Statement stmt = getConnection().createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Cleared " + rowsAffected + " articles from the database.");
        } catch (SQLException e) {
            System.err.println("Error clearing articles: " + e.getMessage());
            throw e;
        }
    }
   
    /**
     * Retrieves a specific article from the database by its ID.
     * 
     * @param id The ID of the article to retrieve
     * @return The Article object if found, null otherwise
     * @throws Exception If there's an error retrieving the article from the database
     */
    public Article getArticle(int id) throws Exception {
        String sql = "SELECT * FROM help_articles WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Article nArticle = new Article(
                        decryptField(rs.getString("title")).toCharArray(),
                        decryptField(rs.getString("authors")).toCharArray(),
                        decryptField(rs.getString("abstract")).toCharArray(),
                        decryptField(rs.getString("keywords")).toCharArray(),
                        decryptField(rs.getString("body")).toCharArray(),
                        decryptField(rs.getString("references")).toCharArray()
                    );
                    nArticle.setId(rs.getInt("id"));
                    return nArticle;
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing article in the database.
     * 
     * @param id The ID of the article to update
     * @param article The updated Article object
     * @throws Exception If there's an error updating the article in the database
     */
    public void updateArticle(int id, Article article) throws Exception {
        String sql = "UPDATE help_articles SET title = ?, authors = ?, abstract = ?, keywords = ?, body = ?, references = ? WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, encryptField(new String(article.getTitle())));
            pstmt.setString(2, encryptField(new String(article.getAuthors())));
            pstmt.setString(3, encryptField(new String(article.getAbstract())));
            pstmt.setString(4, encryptField(new String(article.getKeywords())));
            pstmt.setString(5, encryptField(new String(article.getBody())));
            pstmt.setString(6, encryptField(new String(article.getReferences())));
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes an article from the database by its ID.
     * 
     * @param id The ID of the article to delete
     * @throws Exception If there's an error deleting the article from the database
     */
    public void deleteArticle(int id) throws Exception {
        String sql = "DELETE FROM help_articles WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.execute();
            System.out.println("Article id: " + id + " deleted successfully");
        }
    }
   
    /**
     * Encrypts a field value for secure storage in the database.
     * 
     * @param field The field value to encrypt
     * @return The encrypted field value as a Base64 encoded string
     * @throws Exception If there's an error during encryption
     */
    private String encryptField(String field) throws Exception {
        byte[] iv = EncryptionUtils.getInitializationVector(field.toCharArray());
        byte[] encrypted = encryptHelper.encrypt(field.getBytes(), iv);
        return Base64.getEncoder().encodeToString(encrypted) + ":" + Base64.getEncoder().encodeToString(iv);
    }

    /**
     * Decrypts an encrypted field value retrieved from the database.
     * 
     * @param encryptedField The encrypted field value as a Base64 encoded string
     * @return The decrypted field value
     * @throws Exception If there's an error during decryption
     */
    private String decryptField(String encryptedField) throws Exception {
        String[] parts = encryptedField.split(":");
        byte[] encrypted = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] decrypted = encryptHelper.decrypt(encrypted, iv);
        return new String(decrypted);
    }
    }
    
    
