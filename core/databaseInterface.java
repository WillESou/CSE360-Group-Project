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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseInterface {
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
			
			
			//Creating all user tables
			createUsersTable(connection);
			createRolesTable(connection);
			populateRolesTable(connection);
			createUserRolesTable(connection);
            createSkillsTable(connection);
            createUserSkillsTable(connection);
            createInviteCodesTable(connection);
            
            //Creating session tables
            createSessionTable(connection);
            createSessionStudentsTable(connection);
            
            
		} catch (SQLException e) {
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
    
    
    
    
}
