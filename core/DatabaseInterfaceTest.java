package core;
import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;


public class DatabaseInterfaceTest {

	    private Connection connection;
	    private databaseInterface dbInterface;

	    @Before
	    public void setUp() throws Exception {
	        // Initialize in-memory database
	        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

	        // Create an instance of DatabaseInterface with overridden methods for testing
	        dbInterface = new databaseInterface();

	        // Create necessary tables
	        createTables();

	        // Insert test user
	        insertTestUser();
	    }

	    @After
	    public void tearDown() throws Exception {
	        // Close the connection
	        connection.close();
	    }

	    private void createTables() throws SQLException {
	        String createTablesSql = 
	            "CREATE TABLE IF NOT EXISTS USERS (" +
	            "ID INT AUTO_INCREMENT PRIMARY KEY, " +
	            "USERNAME VARCHAR(50) NOT NULL UNIQUE, " +
	            "PASSWORD VARCHAR(64) NOT NULL" +
	            ");" +
	            "CREATE TABLE IF NOT EXISTS GENERAL_QUESTIONS (" +
	            "QUESTION_ID INT, " +
	            "STUDENT_ID INT, " +
	            "PRIMARY KEY (QUESTION_ID, STUDENT_ID), " +
	            "FOREIGN KEY (STUDENT_ID) REFERENCES USERS(ID), " +
	            "question TEXT, " +
	            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
	            ");" +
	            "CREATE TABLE IF NOT EXISTS SPECIFIC_QUESTIONS (" +
	            "QUESTION_ID INT, " +
	            "STUDENT_ID INT, " +
	            "PRIMARY KEY (QUESTION_ID, STUDENT_ID), " +
	            "FOREIGN KEY (STUDENT_ID) REFERENCES USERS(ID), " +
	            "question TEXT, " +
	            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
	            ");";

	        // Execute the SQL statements
	        Statement stmt = connection.createStatement();
	        for (String sql : createTablesSql.split(";")) {
	            if (!sql.trim().isEmpty()) {
	                stmt.execute(sql);
	            }
	        }
	        stmt.close();
	    }

	    private void insertTestUser() throws SQLException {
	        String insertUserSql = "INSERT INTO USERS (USERNAME, PASSWORD) VALUES ('testuser', 'password');";
	        Statement stmt = connection.createStatement();
	        stmt.executeUpdate(insertUserSql);
	        stmt.close();
	    }

	    @Test
	    public void testAddGeneralQuestionValid() throws Exception {
	        dbInterface.addGeneralQuestion("testuser", "What is Java?");
	        // Verify that the question was added
	        verifyQuestionCount("GENERAL_QUESTIONS", 1);
	    }

	    @Test
	    public void testAddGeneralQuestionInvalidUser() {
	        try {
				dbInterface.addGeneralQuestion("invaliduser", "What is Java?");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fail("Expected SQLException was not thrown");
	    }

	    @Test
	    public void testAddGeneralQuestionEmptyBody() throws Exception {
	        dbInterface.addGeneralQuestion("testuser", "");
	        // Verify that the question was added
	        verifyQuestionCount("GENERAL_QUESTIONS", 1);
	    }

	    @Test(expected = NullPointerException.class)
	    public void testAddGeneralQuestionNullBody() throws Exception {
	        dbInterface.addGeneralQuestion("testuser", null);
	    }

	    @Test
	    public void testAddSpecificQuestionValid() throws Exception {
	        dbInterface.addSpecificQuestion("testuser", "Explain polymorphism.");
	        // Verify that the question was added
	        verifyQuestionCount("SPECIFIC_QUESTIONS", 1);
	    }

	    @Test
	    public void testAddSpecificQuestionInvalidUser() {
	        try {
	            dbInterface.addSpecificQuestion("invaliduser", "Explain polymorphism.");
	            fail("Expected SQLException was not thrown");
	        } catch (Exception e) {
	            assertTrue(e.getMessage().contains("User not found: invaliduser"));
	        }
	    }

	    @Test
	    public void testGetGeneralQuestionsNoQuestions() throws Exception {
	        List<String> questions = dbInterface.getGeneralQuestions();
	        assertNotNull(questions);
	        assertEquals(0, questions.size());
	    }

	    @Test
	    public void testGetGeneralQuestionsWithQuestions() throws Exception {
	        dbInterface.addGeneralQuestion("testuser", "What is Java?");
	        dbInterface.addGeneralQuestion("testuser", "Explain OOP.");
	        List<String> questions = dbInterface.getGeneralQuestions();
	        assertNotNull(questions);
	        assertEquals(2, questions.size());
	        assertTrue(questions.get(0).contains("Explain OOP."));
	        assertTrue(questions.get(1).contains("What is Java?"));
	    }

	    private void verifyQuestionCount(String tableName, int expectedCount) throws SQLException {
	        String query = "SELECT COUNT(*) FROM " + tableName + ";";
	        try (Statement stmt = connection.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            assertTrue(rs.next());
	            int count = rs.getInt(1);
	            assertEquals(expectedCount, count);
	        }
	    }

}

