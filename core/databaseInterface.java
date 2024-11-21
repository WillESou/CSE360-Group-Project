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

import java.util.*;
import java.nio.*;

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

      createGroupTable(connection);
      createGenArticlesTable(connection);


      createInviteCodesTable(connection);

            
      //Creating questions tables
      createGeneralQuestionsTable(connection);
      createSpecificQuestionsTable(connection);
            
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
    
    
    private void createGeneralQuestionsTable(Connection conn) throws SQLException {
    	String sql = "CREATE TABLE IF NOT EXISTS GENERAL_QUESTIONS (" +
    				 "QUESTION_ID INT, " +
    				 "STUDENT_ID INT, " +
    				 "PRIMARY KEY (QUESTION_ID, STUDENT_ID), " +
    				 "FOREIGN KEY (STUDENT_ID) REFERENCES USERS(ID), " +
    				 "question TEXT, " +
    				 "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
    				 ")";
    	
    	executeUpdate(conn, sql, "GENERAL_QUESTIONS table");
    }
    
    private void createSpecificQuestionsTable(Connection conn) throws SQLException {
    	String sql = "CREATE TABLE IF NOT EXISTS SPECIFIC_QUESTIONS (" +
    				 "QUESTION_ID INT, " +
    				 "STUDENT_ID INT, " +
    				 "PRIMARY KEY (QUESTION_ID, STUDENT_ID), " +
    				 "FOREIGN KEY (STUDENT_ID) REFERENCES USERS(ID), " +
    				 "question TEXT, " +
    				 "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
    				 ")";
    	
    	executeUpdate(conn, sql, "SPECIFIC_QUESTIONS table");
    }
    
    
    
    public void addGeneralQuestion(String username, String qBody) throws Exception {
        // First get the user's ID since the table uses USER_ID as a foreign key
        String userIdQuery = "SELECT ID FROM USERS WHERE USERNAME = ?";
        int userId;
        
        if(qBody.isEmpty() || (qBody == null)) {
        	throw new Exception("addGeneralQuestion was called with blank question body!");
        }
        
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(userIdQuery)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("User not found: " + username);
            }
            userId = rs.getInt("ID");
        }
        
        // Get the next question ID
        String maxIdQuery = "SELECT MAX(QUESTION_ID) FROM GENERAL_QUESTIONS";
        int nextQuestionId = 1; // Default start if no questions exist
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(maxIdQuery);
            if (rs.next() && rs.getObject(1) != null) {
                nextQuestionId = rs.getInt(1) + 1;
            }
        }
        
        // Insert the new question
        String insertQuery = "INSERT INTO GENERAL_QUESTIONS (QUESTION_ID, STUDENT_ID, question) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, nextQuestionId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, encryptField(qBody));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating general question failed, no rows affected.");
            }
            System.out.println("General question added successfully with ID: " + nextQuestionId);
        }
    }
    
    public void addSpecificQuestion(String username, String qBody) throws Exception {
        // First get the user's ID since the table uses USER_ID as a foreign key
        String userIdQuery = "SELECT ID FROM USERS WHERE USERNAME = ?";
        int userId;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(userIdQuery)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("User not found: " + username);
            }
            userId = rs.getInt("ID");
        }
        
        // Get the next question ID
        String maxIdQuery = "SELECT MAX(QUESTION_ID) FROM SPECIFIC_QUESTIONS";
        int nextQuestionId = 1; // Default start if no questions exist
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(maxIdQuery);
            if (rs.next() && rs.getObject(1) != null) {
                nextQuestionId = rs.getInt(1) + 1;
            }
        }
        
        // Insert the new question
        String insertQuery = "INSERT INTO SPECIFIC_QUESTIONS (QUESTION_ID, STUDENT_ID, question) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, nextQuestionId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, encryptField(qBody));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating specific question failed, no rows affected.");
            }
            System.out.println("Specific question added successfully with ID: " + nextQuestionId);
        }
    }
    
    public List<String> getGeneralQuestions() throws Exception {
        List<String> questions = new ArrayList<>();
        String sql = "SELECT U.USERNAME, Q.question " +
                     "FROM GENERAL_QUESTIONS Q " +
                     "JOIN USERS U ON Q.STUDENT_ID = U.ID " +
                     "ORDER BY Q.created_at DESC";
                     
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String questionText = decryptField(rs.getString("question"));
                questions.add(username + "\n" + questionText);
            }
        }
        return questions;
    }

    public List<String> getSpecificQuestions() throws Exception {
        List<String> questions = new ArrayList<>();
        String sql = "SELECT U.USERNAME, Q.question " +
                     "FROM SPECIFIC_QUESTIONS Q " +
                     "JOIN USERS U ON Q.STUDENT_ID = U.ID " +
                     "ORDER BY Q.created_at DESC";
                     
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String questionText = decryptField(rs.getString("question"));
                questions.add(username + "\n" + questionText);
            }
        }
        return questions;
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
                + "id INT PRIMARY KEY, "
                + "title VARCHAR(255) UNIQUE NOT NULL, "
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
     * Creates a unique ID number based on an article's title. Because titles are unique, all ID values are unique.
     * 
     * @throws SQLException If there's an error executing the SQL statement
     */
    private int getID(char[] title) {

    	int id = Math.abs(Arrays.hashCode(title));
    	return id % 100000;
    }
    
    /**
     * Adds a new article to the database.
     * 
     * @param article The Article object to be added
     * @return The ID of the newly added article
     * @throws Exception If there's an error adding the article to the database
     */
    public int addArticle(Article article) throws Exception {
        String sql = "INSERT INTO help_articles (id, title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        	
        	int id = getID(article.getTitle());
            pstmt.setInt(1, id);
            pstmt.setString(2, encryptField(new String(article.getTitle())));
            pstmt.setString(3, encryptField(new String(article.getAuthors())));
            pstmt.setString(4, encryptField(new String(article.getAbstract())));
            pstmt.setString(5, encryptField(new String(article.getKeywords())));
            pstmt.setString(6, encryptField(new String(article.getBody())));
            pstmt.setString(7, encryptField(new String(article.getReferences())));

            addArtGroup(id, new String(article.getGroup()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating article failed, no rows affected.");
            }

            return id;
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
                    decryptField(rs.getString("references")).toCharArray(),
                    null
                );
                nArticle.setId(rs.getInt("id"));
                articles.add(nArticle);
            }
        }
        return articles;
    }
   

     /* Retrieves an article if it contains the given keyword
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
     * Creates the necessary tables in the database if they don't already exist.
     * 
     * @throws SQLException If there's an error executing the SQL statement
     */
    private void createGroupTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS group_access ("
        		+ "GROUP_NAME VARCHAR(255) NOT NULL, "
                + "USER_NAME VARCHAR(255) NOT NULL,"
        		/**
        		 * For any group, 0 means not an admin, 1 means admin,
        		 * 2 means instructor admin, and 3 means the group is general.
        		 */
                + "ACCESS INT"
        		+ ")";
        executeUpdate(conn, sql, "group_access table");
    }
    
    /**
     * Adds a group
     * 
     * @param groupName - The name of the group the article is added to
     * @param isSpecial - Whether the group added is a special access group
     * @throws Exception If there's an error adding the article to the database
     */
    public void createGroup(String groupName, boolean isSpecial) throws SQLException, Exception {
    	User writer = Source.getUIManager().getUser();
    	
    	String sql = "INSERT INTO group_access (GROUP_NAME, USER_NAME, ACCESS) VALUES (?, ?, ?)";
    	try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
    		pstmt.setString(1, encryptField(groupName));
    		
    		if (!isSpecial) { // General Group
    			pstmt.setString(2, ("ALL"));
    			pstmt.setInt(3, 3);
    		} else if (writer.hasRole(ROLE.INSTRUCTOR)) { // The creator is an instructor, and is therefore the first instructor added.
    			pstmt.setString(2, (writer.getUsername()));
    			pstmt.setInt(3, 2);
    		} else { // The creator is not an instructor, and cannot see the articles.
    			pstmt.setString(2, (writer.getUsername()));
    			pstmt.setInt(3, 1);
    		}
    		
    		pstmt.executeUpdate();
    	}
    }
    
    /**
     * Adds a user to a group.
     * 
     * @param userName - The name of the user to join a group
     * @param access - The access level that the user is given.
     * @param groupName - The name of the group the article is added to
     * @throws Exception If there's an error adding the article to the database
     */
    public void addUserGroup(User user, boolean admin, String groupName) throws SQLException, Exception {
    	
    	String sql = "INSERT INTO group_access (GROUP_NAME, USER_NAME, ACCESS) VALUES (?, ?, ?)";
    	try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
    		pstmt.setString(1, encryptField(groupName));
    		pstmt.setString(2, (user.getUsername()));
    		
    		int access = 0;
    		if (user.hasRole(ROLE.INSTRUCTOR) && admin) {
    			access = 2;
    		} else if (user.hasRole(ROLE.ADMIN) && admin) {
    			access = 1;
    		}
    		pstmt.setInt(3, access);
    		
    		pstmt.executeUpdate();
    	}
    }
    
    /**
     * Toggles admin functionality to a user
     * 
     * @param user - the user to be toggled
     * @param groupName - The name of the group
     * @throws Exception If there's an error adding the article to the database
     */
    public void toggleAdmin(User user, String groupName) throws SQLException, Exception {
    	
    	String sql = "UPDATE group_access SET GROUP_NAME = ?, USER_NAME = ?, ACCESS = ?";
    	try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
    		pstmt.setString(1, encryptField(groupName));
    		pstmt.setString(2, (user.getUsername()));
    		
    		int access = groupAccess(user.getUsername(), groupName);
    		if (access == 2 || access == 1) { // To make the user not an admin
    			access = 0;
    		} else if (access == 0 && user.hasRole(ROLE.INSTRUCTOR)) { // To make the instructor an admin
    			access = 2;
    		} else if (access == 0 && user.hasRole(ROLE.ADMIN)) { // To make the admin a generic admin to the group
    			access = 1;
    		} else { // If the user cannot be made admin
    			System.out.println("User Cannot be Made Admin.");
    		}
    		
    		pstmt.setInt(3, access);
    		pstmt.executeUpdate();
    	}
    }
    
    /**
     * Deletes a user to a group.
     * 
     * @param userName - The name of the user to be deleted
     * @param groupName - The name of the group the user is deleted from
     * @throws Exception If there's an error adding the article to the database
     */
    public void deleteUserFromGroup(String userName, String groupName) throws SQLException, Exception {
    	String sql = "DELETE FROM group_access WHERE GROUP_NAME = ? AND USER_NAME = ?";
    	try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
    		pstmt.setString(1, encryptField(groupName));
    		pstmt.setString(2, (userName));
    		
    		pstmt.executeUpdate();
    	}
    }
    
    /**
     * Checks to see if a user is currently enrolled in a group.
     * 
     * @param userName - The name of the user to join a group
     * @param groupName - The name of the group the article is added to
     * @throws Exception If there's an error adding the article to the database
     */
    public boolean userInGroup(String userName, String groupName) throws SQLException, Exception {
    	
    	String sql = "SELECT GROUP_NAME, USER_NAME, ACCESS FROM group_access";
    	 try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                	String decryptedGroup = decryptField(rs.getString("GROUP_NAME"));
                	String user = (rs.getString("USER_NAME"));
                	if ((decryptedGroup.equals(groupName) && 
                			(user.equals(userName) && rs.getInt("ACCESS") != 1)
                			|| user.equals("ALL"))) {
                		return true;
                	}
                }
    	 }
    	 return false;
    }
    
    /**
     * Returns the access number of a user in a group.
     * 
     * @param userName - The name of the user
     * @param groupName - The name of the group
     * @throws Exception If there's an error adding the article to the database
     */
    public int groupAccess(String userName, String groupName) throws SQLException, Exception {
    	
    	String sql = "SELECT GROUP_NAME, USER_NAME, ACCESS FROM group_access";
    	 try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                	String decryptedGroup = decryptField(rs.getString("GROUP_NAME"));
                	String user = (rs.getString("USER_NAME"));
                	
                	if (decryptedGroup.equals(groupName) && 
                			user.equals(userName)) {
                		/**
                		 * If the user is a part of the group, return their accessibility number.
                		 * 0 - Viewing Rights
                		 * 1 - Admin without Viewing Rights
                		 * 2 - Instructor with Admin Rights
                		 */
                		return (rs.getInt("ACCESS"));
                		
                	} else if (decryptedGroup.equals(groupName) &&
                			user.equals("ALL")) { // Access rights number 3 means the group is not special access.
                		return 3;
                	}
                }
    	 }
    	 return -1;
    }
    
    /**
     * Creates the necessary tables in the database if they don't already exist.
     * 
     * @throws SQLException If there's an error executing the SQL statement
     */
    private void createGenArticlesTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS group_articles ("
        		+ "GROUP_NAME VARCHAR(255) NOT NULL, "
                + "ID INT NOT NULL"
        		+ ")";
        executeUpdate(conn, sql, "group_articles table");
    }
    
    /**
     * Adds an article to a group.
     * 
     * @param id - The ID value of the article to be added
     * @param groupName - The name of the group the article is added to
     * @throws Exception If there's an error adding the article to the database
     */
    private void addArtGroup(int id, String groupName) throws SQLException, Exception {
    	String sql = "INSERT INTO group_articles (GROUP_NAME, ID) VALUES (?, ?)";
    	try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
    		pstmt.setString(1, encryptField(groupName));
    		pstmt.setInt(2, id);
    		
    		pstmt.executeUpdate();
    	}
    	System.out.println(groupName);
    }
    
    /*
     * Retrieves a list of all general groups, as well as groups the user has access to.
     * 
     * @return A list of all Group Name strings.
     * @throws Exception
     */
    public List<String> listGroups() throws Exception{
    	User currentUser = Source.getUIManager().getUser();
    	
    	List<String> ret = new ArrayList<String>();
    	ret.add("General");
    	String sql = "SELECT GROUP_NAME, USER_NAME, ACCESS FROM group_access";
    	
    	try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
               while (rs.next()) {
            	   if (!ret.contains(decryptField(rs.getString("GROUP_NAME")))) {
            		   if (userInGroup(currentUser.getUsername(), decryptField(rs.getString("GROUP_NAME")))
            				   || currentUser.hasRole(ROLE.ADMIN)) {
            			   ret.add(decryptField(rs.getString("GROUP_NAME")));
            		   }
            	   }
               }
    	}
    	return ret;
    }
    
    /**
     * Retrieves all articles in a group by name
     * 
     * @param groupName - The name of the group to filter from
     * @throws Exception
     */
    public List<Article> filterGroup(String groupName) throws SQLException, Exception {
    	User currentUser = Source.getUIManager().getUser();
    	
    	List<Article> matchingArticles = new ArrayList<>();
        String sql = "SELECT GROUP_NAME, ID FROM group_articles";
        
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
               while (rs.next()) {

                   String decryptedGroup = decryptField(rs.getString("GROUP_NAME")).toLowerCase();
                   
                   if (decryptedGroup.equals(groupName.toLowerCase())) {
                	   
                	   int access = groupAccess(currentUser.getUsername(), groupName);
                	   
                	   if (access != 1) { // If the user is not an admin in a special group, they may view the article.
                		   Article article = getArticle(rs.getInt("ID"));
                		   article.setId(rs.getInt("ID"));
                           matchingArticles.add(article);
                	   } else {
                		   Article article = getEncArticle(rs.getInt("ID"));
                		   article.setId(rs.getInt("ID"));
                           matchingArticles.add(article);
                	   }
                   }
               }
           }
           return matchingArticles;
    }
    
     /* Clears all articles from the database.
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
        sql = "DELETE FROM group_articles";
        try (Statement stmt = getConnection().createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Cleared " + rowsAffected + " groups from the database.");
        } catch (SQLException e) {
            System.err.println("Error clearing groups: " + e.getMessage());
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
    	String group = "General";
    	String sql = "SELECT * FROM group_articles WHERE ID = ?";
        try (PreparedStatement pstmtG = getConnection().prepareStatement(sql)) {
            pstmtG.setInt(1, id);
            try (ResultSet rsG = pstmtG.executeQuery()) {
                if (rsG.next()) {
                	group = decryptField(rsG.getString("GROUP_NAME"));
                }
            }
        }
        sql = "SELECT * FROM help_articles WHERE id = ?";
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
                        decryptField(rs.getString("references")).toCharArray(),
                        group.toCharArray()
                    );
                    nArticle.setId(rs.getInt("id"));
                    return nArticle;
                }
            }
        }
        return null;
    }
    
    /**
     * Retrieves an encrypted article by its ID.
     * 
     * @param id The ID of the article to retrieve
     * @return The Article object if found, null otherwise
     * @throws Exception If there's an error retrieving the article from the database
     */
    public Article getEncArticle(int id) throws Exception {
    	String group = "General";
    	String sql = "SELECT * FROM group_articles WHERE ID = ?";
        try (PreparedStatement pstmtG = getConnection().prepareStatement(sql)) {
            pstmtG.setInt(1, id);
            try (ResultSet rsG = pstmtG.executeQuery()) {
                if (rsG.next()) {
                	group = decryptField(rsG.getString("GROUP_NAME"));
                }
            }
        }
        sql = "SELECT * FROM help_articles WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Article nArticle = new Article(
                        decryptField(rs.getString("title")).toCharArray(),
                        decryptField(rs.getString("authors")).toCharArray(),
                        decryptField(rs.getString("abstract")).toCharArray(),
                        decryptField(rs.getString("keywords")).toCharArray(),
                        rs.getString("body").toCharArray(),
                        decryptField(rs.getString("references")).toCharArray(),
                        group.toCharArray()
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
        sql = "DELETE FROM group_articles WHERE ID = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.execute();
            System.out.println("Group with Article id: " + id + " deleted successfully");
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
    
    
