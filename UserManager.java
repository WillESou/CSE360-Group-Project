
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserManager {
	// Create operation
    public void createUser(String username, String email, String name, String password) throws SQLException {
        String sql = "INSERT INTO USERS (USERNAME, EMAIL, NAME, PASSWORD) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseInterface.getConnection();
        	PreparedStatement pstmt = conn.prepareStatement(sql)){
        		pstmt.setString(1, username);
        		pstmt.setString(2, email);
        		pstmt.setString(3, name);
        		pstmt.setString(4, password); // TODO HASH PASS
            
        		int affectedRows = pstmt.executeUpdate();
        		if (affectedRows == 0) {
        			throw new SQLException("Creating user failed, no rows affected.");
        		}
        		System.out.println("User created successfully.");
        	
        } catch (SQLException e){
        	e.printStackTrace();
        }
}
    
    // Read operation
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("USERNAME"),
                        rs.getString("EMAIL"),
                        rs.getString("NAME"),
                        rs.getString("Password")
                    );
                } else {
                    return null; // User not found
                }
            }
        }
    }
    
    // Update operation
    public void updateUser(String username, String newEmail, String newName) throws SQLException {
        String sql = "UPDATE USERS SET EMAIL = ?, NAME = ? WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newName);
            pstmt.setString(3, username);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            System.out.println("User updated sucessfully");
        }
    }
    
    // Delete operation
    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM USERS WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        }
    }
    
    // Additional methods for user management
    
    public void assignRoleToUser(String username, String roleName) throws SQLException {
    	String sql = "INSERT INTO USER_ROLES (USER_ID, ROLE_ID) " +
                "SELECT U.ID, R.ID FROM USERS U, ROLES R " +
                "WHERE U.USERNAME = ? AND R.ROLE_NAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, roleName);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Assigning role to user failed, no rows affected.");
            }
        }
    }
    
    public ArrayList<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM USERS";
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = databaseInterface.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getString("USERNAME"),
                    rs.getString("EMAIL"),
                    rs.getString("NAME"),
                    rs.getString("Password")
                ));
            }
        }
        return users;
    }
}
