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
        int result = 0;
        try{
            // get keyword IDs from keyword table because table relations use IDs
            String keywordIdSql = "SELECT keyword_id FROM keyword WHERE keyword = ?";
            PreparedStatement keywordIdPstmt = conn.prepareStatement(keywordIdSql);
            List<Integer> keywordIds = new ArrayList<>();
            for(String kw : keywords){
                keywordIdPstmt.setString(1, kw);
                ResultSet rs = keywordIdPstmt.executeQuery();
                if(rs.next()){
                    keywordIds.add(rs.getInt("keyword_id"));
                }
                rs.close();
            }
            if(userType.equals("professor")){
                sql = "INSERT INTO professor_keyword(account_id, keyword_id) VALUES(?,?) ON DUPLICATE KEY UPDATE account_id = account_id";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for(int kwId : keywordIds){
                    pstmt.setInt(1, userID);
                    pstmt.setInt(2, kwId);
                    result += pstmt.executeUpdate();
                }
            }
            else if(userType.equals("student")){
                sql = "INSERT INTO student_keyword(account_id, keyword_id) VALUES(?,?) ON DUPLICATE KEY UPDATE account_id = account_id";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for(int kwId : keywordIds){
                    pstmt.setInt(1, userID);
                    pstmt.setInt(2, kwId);
                    result += pstmt.executeUpdate();
                }
            }
            else if(userType.equals("public")){
                sql = "INSERT INTO public_keyword(account_id, keyword_id) VALUES(?,?) ON DUPLICATE KEY UPDATE account_id = account_id";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for(int kwId: keywordIds){
                    pstmt.setInt(1, userID);
                    pstmt.setInt(2, kwId);
                    result += pstmt.executeUpdate();
                }
            }
            else{
                System.out.println("Invalid user type");
                return -1;
            }
        }
        catch(Exception e){
            System.out.println("Error adding keywords: " + e.getMessage());
        }
        return result;
    }
    
    // fix this method its not correct
    public int updateKeywords(String userType, int userID, List<String> keywords) {
        return 1;
    }

    public int deleteKeywords(String userType, int userID, List<String> keywords) {
        int result = 0; 
        try{
            // get keyword IDs from keyword table because table relations use IDs
            String keywordIdSql = "SELECT keyword_id FROM keyword WHERE keyword = ?";
            PreparedStatement keywordIdPstmt = conn.prepareStatement(keywordIdSql);
            List<Integer> keywordIds = new ArrayList<>();
            for(String kw : keywords){
                keywordIdPstmt.setString(1, kw);
                ResultSet rs = keywordIdPstmt.executeQuery();
                if(rs.next()){
                    keywordIds.add(rs.getInt("keyword_id"));
                }
                rs.close();
            }
            if(userType.equals("professor")){
                sql = "DELETE FROM professor_keyword WHERE account_id = ? AND keyword_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for(int kwId : keywordIds){
                    pstmt.setInt(1, userID);
                    pstmt.setInt(2, kwId);
                    result += pstmt.executeUpdate();
                }
            }
            else if(userType.equals("student")){
                sql = "DELETE FROM student_keyword WHERE account_id = ? AND keyword_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for(int kwId : keywordIds){
                    pstmt.setInt(1, userID);
                    pstmt.setInt(2, kwId);
                    result += pstmt.executeUpdate();
                }
            }
            else if(userType.equals("public")){
                sql = "DELETE FROM public_keyword WHERE account_id = ? AND keyword_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for(int kwId : keywordIds){
                    pstmt.setInt(1, userID);
                    pstmt.setInt(2, kwId);
                    result += pstmt.executeUpdate();
                }
            }
            else{
                System.out.println("Invalid user type");
                return -1;
            }
        }
        catch(Exception e){
            System.out.println("Error deleting keywords: " + e.getMessage());
        }
        return result;
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