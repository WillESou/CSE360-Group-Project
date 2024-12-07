package core;

import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;

import ui.*;

public class EthanRequirements {
	
    private databaseInterface dbInterface;
    private UserManager userMan;
    private HelpArticleSystem art;

    @Before
    public void setUp() throws Exception {
    	
    	dbInterface = Source.getDatabase();
    	userMan = new UserManager();
    	art = new HelpArticleSystem();
    }
    
    @Test
    public void test3638() throws Exception {
    	User instructor = userMan.getUserByUsername("I");
    	
    	// Create a group with the instructor as the creator.
    	dbInterface.createTestGroup("TESTGROUP", true, instructor);
    	
    	// Because the instructor created the group, they should be an admin.
    	assertEquals(art.testAccess("TESTGROUP", instructor, dbInterface), 2);
    }
    
    @Test
    public void test40() throws Exception {
    	User admin = userMan.getUserByUsername("A");
    	
    	dbInterface.createTestGroup("GENGROUP", false, admin);
    	dbInterface.createTestGroup("ACCESSGROUP", true, admin);
    	
    	assertEquals(art.testAccess("GENGROUP", admin, dbInterface), 3);
    	assertEquals(art.testAccess("ACCESSGROUP", admin, dbInterface), 1);
    	assertEquals(art.testAccess("TESTGROUP", admin, dbInterface), -1);
    }
}
