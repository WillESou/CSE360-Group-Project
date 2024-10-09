import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SessionManager {
    private static SessionManager instance;
    private Map<String, Session> sessions;

    private SessionManager() {
        sessions = new HashMap<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
 // Create a new class 
    public boolean createClass(String className, User admin) {
        if (!sessions.containsKey(className)) {
            sessions.put(className, new Session(className, admin));
            return true;  // Class created 
        }
        return false;  // Class already exists
    }

    // Enroll a student in a class
    public boolean enrollStudent(String className, User student) {
        Session enrollClass = sessions.get(className);
        if (enrollClass != null) {
            return enrollClass.addStudent(student);
        }
        return false;  // Class not found
    }

    // Unenroll a student from a class
    public boolean unenrollStudent(String className, User student) {
        Session unenrollClass = sessions.get(className);
        if (unenrollClass != null) {
            return unenrollClass.removeStudent(student);
        }
        return false;  // Class not found
    }

    // Get a list of all class names
    public Set<String> getAllClassNames() {
        return sessions.keySet();  // Returns all class names
    }
}
