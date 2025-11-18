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

            System.out.println("No DB connection: not found " + cnfe.getMessage());

            System.exit(0);
        }
        catch(SQLException sqle){
            System.out.println("No DB connection: sql " + sqle.getMessage());
            System.exit(0);
        }//end of catch
        return (conn!=null);
    }

    public void closeConnection() {}

    /* user account management */

    public boolean registerStudent(String username, String password, String firstName, String lastName, String email, Double gpa) throws SQLException {
        try {
            String insertAccount = "INSERT INTO account(username, password, user_type) VALUES (?, ?, 'student')";
            PreparedStatement accountStmt = conn.prepareStatement(insertAccount, Statement.RETURN_GENERATED_KEYS);
            accountStmt.setString(1, username);
            accountStmt.setString(2, password);

            int rowsAffected = accountStmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = accountStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int accountID = generatedKeys.getInt(1);

                    String insertStudentSQL = "INSERT INTO student (account_id, first_name, last_name, email, gpa) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement studentStmt = conn.prepareStatement(insertStudentSQL);
                    studentStmt.setInt(1, accountID);
                    studentStmt.setString(2, firstName);
                    studentStmt.setString(3, lastName);
                    studentStmt.setString(4, email);
                    studentStmt.setDouble(5, gpa);

                    studentStmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering: " + e.getMessage());
        }
        return false;
    }

    public boolean registerProfessor(String username, String password, String firstName, String lastName, String email, String building, String office) throws SQLException {
        try {
            String insertAccount = "INSERT INTO account(username, password, user_type) VALUES (?, ?, 'student')";
            PreparedStatement accountStmt = conn.prepareStatement(insertAccount, Statement.RETURN_GENERATED_KEYS);
            accountStmt.setString(1, username);
            accountStmt.setString(2, password);

            int rowsAffected = accountStmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = accountStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int accountID = generatedKeys.getInt(1);

                    String insertProfessor = "INSERT INTO professor (account_id, first_name, last_name, email, building_number, office_number) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement profStmt = conn.prepareStatement(insertProfessor);
                    profStmt.setInt(1, accountID);
                    profStmt.setString(2, firstName);
                    profStmt.setString(3, lastName);
                    profStmt.setString(4, email);
                    profStmt.setString(5, building);
                    profStmt.setString(6, building);

                    profStmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering: " + e.getMessage());
        }
        return false;
    }

    public boolean registerPublic(String username, String password, String firstName, String lastName, String email) throws SQLException {
        try {
            String insertAccount = "INSERT INTO account(username, password, user_type) VALUES (?, ?, 'student')";
            PreparedStatement accountStmt = conn.prepareStatement(insertAccount, Statement.RETURN_GENERATED_KEYS);
            accountStmt.setString(1, username);
            accountStmt.setString(2, password);

            int rowsAffected = accountStmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = accountStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int accountID = generatedKeys.getInt(1);
                    String insertPublic = "INSERT INTO public (account_id, first_name, last_name, email) VALUES (?, ?, ?, ?)";
                    PreparedStatement publicStmt = conn.prepareStatement(insertPublic);
                    publicStmt.setInt(1, accountID);
                    publicStmt.setString(2, firstName);
                    publicStmt.setString(3, lastName);
                    publicStmt.setString(4, email);
                    publicStmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering public user: " + e.getMessage());
        }
        return false;

    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT account_id, user_type FROM account WHERE username = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int accountID = rs.getInt("account_id");
                    String userType = rs.getString("user_type");
                    return getUserByType(accountID, username, userType);
                }
                
            }
        }
        return null;
    }

    public boolean validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT password FROM account WHERE username = ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return password.equals(storedPassword);
                }
            }
        }
        return false;
    }

    private User getUserByType(int accountID, String username, String userType) throws SQLException {
        String sql;

        switch (userType) {
            case "student":
                sql = "SELECT first_name, last_name, email, gpa FROM student WHERE account_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountID);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            User student = new User();
                            student.setAccountID(accountID);
                            student.setUsername(username);
                            student.setUserType(userType);
                            return student;
                        }
                    }
                }
                break;
            case "professor":
                sql  = "SELECT first_name, last_name, building_number, office_number, email FROM professor WHERE account_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountID);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            User professor = new User();
                            professor.setAccountID(accountID);
                            professor.setUsername(username);
                            professor.setUserType(userType);
                            return professor;
                        }
                    }
                }
                break;
            case "public":
                sql = "SELECT first_name, last_name, email FROM public WHERE account_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountID);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            User publicUser = new User();
                            publicUser.setAccountID(accountID);
                            publicUser.setUsername(username);
                            publicUser.setUserType(userType);
                            return publicUser;
                        }
                    }
                }
                break;
        }
        return null;
    }


    public String hashPassword(String rawPassword) {
        // use BCrypt to hash maybe
        return "";
    }

    /* keyword management */

    public List<String> getProfessorKeywords(int accountID) {
        return null;
    }

    public List<String> getStudentKeywords(int accountID) {
        return null;
    }

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
    
    // ig we aint using this lol
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

    public List<String> getAbstract(int profAccountID) {
        // return list of abstract titles
        List<String> abstracts = new ArrayList<>();
        return abstracts;
    }

    public int addAbstract(String title, List<String> professorsUsernames) {
        // get the ids from 
        return 1;
    public int addAbstract(String title, String text, List<Integer> professorIDs) {
        int result = 0;
        try{
            sql = "INSERT INTO abstract(title, abstract_text) VALUES(?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, title);
            pstmt.setString(2, text);
            result = pstmt.executeUpdate();

            // get the generated abstract ID 
            ResultSet rs = pstmt.getGeneratedKeys();
            int abstractID = -1;
            if(rs.next()){
                abstractID = rs.getInt(1);
            }
            rs.close();

            sql = "INSERT INTO professor_abstract(professor_id, abstract_id) VALUES(?,?)";
            pstmt = conn.prepareStatement(sql);
            for(int profID : professorIDs){
                pstmt.setInt(1, profID);
                pstmt.setInt(2, abstractID);
                result += pstmt.executeUpdate();
            }

        }
        catch(SQLException e){
            System.out.println("Error adding abstract: " + e.getMessage());
        }
        return result;
    }

    public int updateAbstract(int abstractID, String title, String text, List<Integer> professorIDs) {
        int result = 0;
        try{
            conn.setAutoCommit(false); 
            sql = "UPDATE abstract SET title = ? , abstract_text = ? WHERE abstract_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, text);
            pstmt.setInt(3, abstractID);
            result = pstmt.executeUpdate();
            pstmt.close();

            sql = "DELETE FROM professor_abstract WHERE abstract_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, abstractID);
            pstmt.executeUpdate();
            pstmt.close();

            if (professorIDs != null && !professorIDs.isEmpty()) {
            sql = "INSERT INTO professor_abstract(account_id, abstract_id) VALUES(?, ?)";
            pstmt = conn.prepareStatement(sql);
            for (int profID : professorIDs) {
                pstmt.setInt(1, profID);
                pstmt.setInt(2, abstractID);
                pstmt.addBatch();
            }
            int[] batchResults = pstmt.executeBatch();
            for (int r : batchResults) result += r;
            pstmt.close();
            }

            conn.commit(); 
            conn.setAutoCommit(true);
        }
        catch(SQLException e){
            // potential error catch for commit rollback
            System.out.println("Error updating abstract: " + e.getMessage());
        }
        return result;
    }

    public int deleteAbstract(int abstractID) {
        int result = 0;
        try{
            sql = "DELETE FROM abstract WHERE abstract_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,abstractID);
            result = pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println("Error deleting abstract: " + e.getMessage());
        }
        return result;
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