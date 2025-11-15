import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataLayer {
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private String sql;

    // JDBC driver
    final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";   

    public DataLayer() {}

    public boolean connect(String databaseName, String userName, String password) {
        conn = null;

        String url = "jdbc:mysql://localhost/";
        url = url + databaseName + "?serverTimezone=UTC";
        try {
            Class.forName(DEFAULT_DRIVER);
            conn = DriverManager.getConnection(url, userName, password);
            System.out.println("DB Connected");
        }
        catch(ClassNotFoundException cnfe){
            System.out.println("No DB connection " + cnfe.getMessage());

            System.exit(0);
        }
        catch(SQLException sqle){
            System.out.println("No DB connection " + sqle.getMessage());
            System.exit(0);
        }//end of catch
        return (conn!=null);
    }

    public void closeConnection() {}

    /* user account management */

    public int registerUser(String username, String password, String userType, String userInfo) {
        int result = 0;
        
        return 1;
    }

    public int loginUser(String username, String password) {
        return 1;
    }

    // unsure how to 
    public int updateAccount(int accountID, String updatedInfo) {
        return 1;
    }

    public int deleteAccount(int accountID) {
        return 1;
    }

    public String hashPassword(String rawPassword) {
        // use BCrypt to hash maybe
        return "";
    }

    /* keyword management */

    public int addKeywords(String userType, int userID, List<String> keywords) {
        return 1;
    }
    
    public int updateKeywords(String userType, int userID, List<String> keywords) {
        return 1;
    }

    public int deleteKeywords(String userType, int userID, List<String> keywords) {
        return 1;
    }

    /* abstract management */

    public int addAbstract(String title, String text, List<Integer> professorIDs) {
        return 1;
    }

    public int updateAbstract(int abstractID, String title, String text, List<Integer> professorIDs) {
        return 1;
    }

    public int deleteAbstract(int abstractID) {
        return 1;
    }

    /* search functionality 
     * some search methods are limited to certain usertypes
    */


    public List<Integer> searchProfessorByKeywords(List<String> keywords) {
        List<Integer> professorIDs = new ArrayList<Integer>();
        return professorIDs;
    }

    public List<Integer> searchProfessorByAbstract(List<String> keywords) {
        List<Integer> professorIDs = new ArrayList<Integer>();
        return professorIDs;
    }

    public List<Integer> searchStudentsByKeywords(List<String> keywords) {
        List<Integer> studentIDs = new ArrayList<Integer>();
        return studentIDs;
    }

    // search across any user type using keywords
    public List<Integer> searchUsers(String searcherType, String targetType, List<String> keywords) {
        List<Integer> userIDs = new ArrayList<Integer>();
        return userIDs;
    }

    public String getProfessorContactInfo(int professorID) {
        return "";
    }

    public String getStudentContactInfo(int studentID) {
        return "";
    }

}