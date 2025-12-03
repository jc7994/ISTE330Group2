/*
    Group Project Deliverable 2 - ISTE 330
	
    Group 2:
        Chen, Jennifer
        Donalds, Chris
        Earle, Rhys
        Gee, Kristen
        Gomes, Marissa
        Labranche, Roosevelt
 
	ISTE 330
	FALL 2025
*/

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.mysql.cj.protocol.Resultset;

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
            String insertAccount = "INSERT INTO account(username, password, user_type) VALUES (?, ?, 'professor')";
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
                    profStmt.setString(6, office);

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
            String insertAccount = "INSERT INTO account(username, password, user_type) VALUES (?, ?, 'public')";
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

    public int getAccountIDByUsername(String username) {
        String sql = "SELECT account_id FROM account WHERE username = ?";
        int accountID = -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                accountID = rs.getInt("account_id");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account ID " + e.getMessage());
        }
        return accountID;
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
        try{
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(rawPassword.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch(Exception e ){
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /* keyword management */

    /**
     * @param accountID         The account id of the profressor.
     * @return                  List of profressor keywords.
    */
    public List<String> getProfessorKeywords(int accountID) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT k.keyword from professor_keyword pk JOIN keyword k ON pk.keyword_id = k.keyword_id WHERE pk.account_id = ? ORDER BY k.keyword ASC";
        // Not sure if the ORDER BY is necessary but its still good QoL

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("keyword"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting professor keywords: " + e.getMessage());
        }

        return result;
    } // end of getProfessorKeywords.

    /**
     * @param accountID         The account id of the student.
     * @return                  List of student keywords.
    */
    public List<String> getStudentKeywords(int accountID) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT k.keyword from student_keyword sk JOIN keyword k ON sk.keyword_id = k.keyword_id WHERE sk.account_id = ? ORDER BY k.keyword ASC";
        // Not sure if the ORDER BY is necessary but its still good QoL

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("keyword"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting student keywords: " + e.getMessage());
        }

        return result;
    } // end of getStudentKeywords.

    
    public int addKeywordsToAbstract(int abstract_id, List<String> keywords) {
        int result = 0;
        try {
            String selectKeywordSql = "SELECT keyword_id FROM keyword WHERE keyword = ? ";
            PreparedStatement selectStmt = conn.prepareStatement(selectKeywordSql);

            String insertKeywordSql = "INSERT INTO (keyword) VALUES (?)";
            PreparedStatement insertKeywordStmt = conn.prepareStatement(insertKeywordSql);

            String insertAbstractKeywordSql = "INSERT INTO abstract_keyword (abstract_id, keyword_id) VALUES (?, ?) " + 
                "ON DUPLICATE KEY UPDATE abstract_id = abstract_id";
            PreparedStatement insertAbstractKeywordStmt = conn.prepareStatement(insertAbstractKeywordSql);

            for (String kw : keywords) {
                int keywordID = -1;
                selectStmt.setString(1, kw);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    keywordID = rs.getInt("keyword_id");
                } else {
                    insertKeywordStmt.setString(1, kw);
                    insertKeywordStmt.executeUpdate();
                    ResultSet generatedKeys = insertKeywordStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        keywordID = generatedKeys.getInt(0);
                    }
                    generatedKeys.close();
                }
                rs.close();

                if (keywordID != -1) {
                    insertAbstractKeywordStmt.setInt(1 , abstract_id);
                    insertAbstractKeywordStmt.setInt(2, keywordID);
                    result += insertAbstractKeywordStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding keywords to abstract: " + e.getMessage());
            return -1;
        }
        return result;
    }

    public int addKeywords(String userType, int userID, List<String> keywords) {
        int result = 0;
        try{
            // get keyword IDs from keyword table because table relations use IDs
            String keywordIdSql = "SELECT keyword_id FROM keyword WHERE keyword = ?";
            PreparedStatement keywordIdPstmt = conn.prepareStatement(keywordIdSql);

            String insertKeywordSql = "INSERT INTO keyword (keyword) VALUES (?)";
            PreparedStatement insertKeywordPstmt = conn.prepareStatement(insertKeywordSql, Statement.RETURN_GENERATED_KEYS);

            List<Integer> keywordIds = new ArrayList<>();
            for(String kw : keywords){
                keywordIdPstmt.setString(1, kw);
                ResultSet rs = keywordIdPstmt.executeQuery();
                int keywordId;
                if(rs.next()){
                    keywordIds.add(rs.getInt("keyword_id"));
                }
                else {
                    insertKeywordPstmt.setString(1, kw);
                    insertKeywordPstmt.executeUpdate();
                    ResultSet keyRs = insertKeywordPstmt.getGeneratedKeys();
                    if (keyRs.next()) {
                        keywordId = keyRs.getInt(1);
                        keywordIds.add(keywordId);
                        
                    }
                    keyRs.close();
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
            return -1;
        }
        return result;
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

    public String getAbstractTitleFromID(int abstract_id) {
        String title = "";
        String sql = "SELECT abstract.title FROM abstract WHERE abstract.abstract_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, abstract_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                title = rs.getString("title");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Error getting abstract title " + e.getMessage());
        }
        return title;

    }

    public List<String> getAbstract(int profAccountID) {
        // return list of abstract titles
        List<String> abstracts = new ArrayList<>();
        sql="SELECT abstract.title FROM professor "; 
        sql+="JOIN professor_abstract ON professor.account_id = professor_abstract.account_id ";
        sql+="JOIN abstract ON professor_abstract.abstract_id = abstract.abstract_id ";
        sql+="WHERE professor.account_id = ? ORDER BY abstract.title ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profAccountID);
            
            ResultSet rs = stmt.executeQuery(); 
            while (rs.next()) {
                abstracts.add(rs.getString("abstract.title"));
            }
            
        }
        catch(Exception e){
            System.out.println("Error getting abstracts: "+e);
        }
        return abstracts;
    }

    public int addAbstract(String title, String text, List<Integer> professorIDs) {
        int abstractID = -1;
        try{
            sql = "INSERT INTO abstract(title, abstract_text) VALUES(?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, title);
            pstmt.setString(2, text);
            pstmt.executeUpdate();

            // get the generated abstract ID 
            ResultSet rs = pstmt.getGeneratedKeys();
            
            if(rs.next()){
                abstractID = rs.getInt(1);
            }
            rs.close();

            sql = "INSERT INTO professor_abstract(account_id, abstract_id) VALUES(?,?)";
            pstmt = conn.prepareStatement(sql);
            for(int profID : professorIDs){
                pstmt.setInt(1, profID);
                pstmt.setInt(2, abstractID);
                pstmt.executeUpdate();
            }

        }
        catch(SQLException e){
            System.out.println("Error adding abstract: " + e.getMessage());
        }
        return abstractID;
    }

    public int updateAbstract(int abstractID, String title, String text, List<Integer> professorIDs) {
        int result = 0;
        try{
            conn.setAutoCommit(false); 
            String sql = "UPDATE abstract SET title = ? , abstract_text = ? WHERE abstract_id = ?";
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

    // TODO: return list of professorIDs that match all the keywords
    public List<Integer> searchProfessorByKeywords(List<String> keywords) {
        List<Integer> professorIDs = new ArrayList<Integer>();
        StringBuilder sql = new StringBuilder( " SELECT DISTINCT pk.account_id " +
        "FROM professor_keyword pk JOIN keyword k USING(keyword_id) "  + 
        "WHERE k.keyword IN (");

        sql.append("?,".repeat(keywords.size()));
        sql.setLength(sql.length() - 1);
        sql.append(")");

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            for(int i = 0; i < keywords.size(); i++){
                pstmt.setString(i+1, keywords.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                professorIDs.add(rs.getInt(1));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return professorIDs;
    }

    // TODO: return list of studentIDs that match all the keywords
    public List<Integer> searchStudentsByKeywords(List<String> keywords) {
        List<Integer> studentIDs = new ArrayList<Integer>();

        if (keywords.isEmpty()){
            return studentIDs;
        }

        try {
            String inClause = "";
            for (int i = 0; i < keywords.size(); i++) {
                inClause += "?";
                if (i < keywords.size() - 1) {
                    inClause += ", ";
                }
            }
            String sql = "SELECT student_keyword.account_id "
            + "FROM keyword JOIN student_keyword ON keyword.keyword_id = student_keyword.keyword_id "
            + "WHERE keyword.keyword IN(" + inClause + ") GROUP BY student_keyword.account_id";    

            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int i = 0; i < keywords.size(); i++) {
                stmt.setString(i + 1, keywords.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                studentIDs.add(rs.getInt("account_id"));
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException sqle){
            System.out.println("Error searching for students by keywords: " + sqle.getMessage());
        }
        return studentIDs;
    }

    // TODO: return a dictionary of <professorIDs, List<abstractIDs>>
    //public List<Integer> searchProfessorByAbstract(List<String> keywords) {
    public Map<Integer, List<Integer>> searchProfessorByAbstract(List<String> keywords) {
        // List<Integer> professorIDs = new ArrayList<Integer>();
        // return professorIDs;
        Map<Integer, List<Integer>> profAbstracts = new HashMap<>();
        try{
            String sql = "SELECT account_id, abstract.abstract_id FROM professor_abstract ";
            sql+= "JOIN abstract ON professor_abstract.abstract_id = abstract.abstract_id ";
            sql+= "JOIN abstract_keyword ON abstract.abstract_id = abstract_keyword.abstract_id ";
            sql+= "JOIN keyword on abstract_keyword.keyword_id = keyword.keyword_id ";
            sql+= "WHERE keyword.keyword IN ( ";
            for(int i=0;i<keywords.size();i++ ){
                String keyword= "'" + keywords.get(i) + "'";
                if(i!=keywords.size()-1){
                    sql+=keyword+", ";
                }
                else{
                    sql+=keyword;
                }
            }
            sql+=") ";
            sql+= "ORDER BY account_id, abstract.abstract_id";
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ResultSet rs = ptmt.executeQuery();

            while (rs.next()){
                int profID = rs.getInt("professor_abstract.account_id");
                int abstractID = rs.getInt("abstract.abstract_id");

                List<Integer> abstractList;
                if (profAbstracts.containsKey(profID)){
                    abstractList = profAbstracts.get(profID);
                }
                else{
                    abstractList = new ArrayList<>();
                    profAbstracts.put(profID,abstractList);
                }

                abstractList.add(abstractID);


            }
        
        } 
        catch (SQLException sqle) {
            System.out.println("Error getting abstract details: " + sqle.getMessage());
        }
        return profAbstracts;
    }
    
    public String getAllAbstracts() {
        StringBuilder output = new StringBuilder();

        try {
            String sql = "SELECT abstract_id, title FROM abstract";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int abstractID = rs.getInt("abstract_id");
                String title = rs.getString("title");
                output.append("Title: ").append(title).append("\n");

                String keysql = 
                    "SELECT k.keyword FROM keyword k JOIN abstract_keyword ak ON k.keyword_id = ak.keyword_id WHERE ak.abstract_id = ?";
                PreparedStatement kstmt = conn.prepareStatement(keysql);
                kstmt.setInt(1, abstractID);
                ResultSet keyrs = kstmt.executeQuery();

                StringBuilder keywordList = new StringBuilder();
                while (keyrs.next()) {
                    if (keywordList.length() > 0) {
                        keywordList.append(", ");
                    }
                    keywordList.append(keyrs.getString("keyword"));
                }
                keyrs.close();

                if (keywordList.length() == 0) {
                    output.append("Keywords: None\n");
                } else {
                    output.append("Keywords: ").append(keywordList).append("\n");
                }

                String profSql = 
                    "SELECT p.first_name, p.last_name " +
                    "FROM professor p " + 
                    "JOIN professor_abstract pa ON p.account_id = pa.account_id " +
                    "WHERE pa.abstract_id = ?";
                PreparedStatement profStmt = conn.prepareStatement(profSql);
                profStmt.setInt(1, abstractID);
                ResultSet profRs = profStmt.executeQuery();

                StringBuilder profList = new StringBuilder();
                while (profRs.next()) {
                    if (profList.length() > 0) {
                        profList.append(", ");
                    }
                    profList.append(profRs.getString("first_name")).append(" ").append(profRs.getString("last_name"));
                }
                profRs.close();

                if (profList.length() == 0) {
                    output.append("Professors: None\n");
                } else {
                    output.append("Professors: ").append(profList).append("\n");
                }
                output.append("\n");
            }
            rs.close();
        } catch (Exception e) {
            return "Error getting abstract details: " + e.getMessage();
        }
        return output.toString();
    }

    // TODO: return the IDs of all students who match at least one keyword with at least one keyword from the prof's abstracts
    public List<Integer> getStudentMatches(int professorID) {
        List<Integer> studentIDs = new ArrayList<Integer>();
        try{
            String sql = "SELECT DISTINCT account.account_id FROM student "
            + "JOIN account ON student.account_id = account.account_id "
            + "JOIN student_keyword ON student.account_id = student_keyword.account_id "
            + "JOIN keyword ON student_keyword.keyword_id = keyword.keyword_id "
            + "JOIN professor_keyword ON keyword.keyword_id = professor_keyword.keyword_id "
            + "WHERE professor_keyword.keyword_id "
            + "IN(SELECT keyword_id FROM professor_keyword WHERE professor_keyword.account_id = ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,professorID);
            rs = pstmt.executeQuery();
            while (rs.next()){
                studentIDs.add(rs.getInt("account_id"));
            }
        }
        catch (SQLException sqle){
            System.out.println("Error matching students with professor's keywords: " + sqle.getMessage());
        }

        return studentIDs;
    }

    // TODO: return the IDs of all professors who match at least one keyword from their abstract with at least one of the students' keywords
    public List<Integer> getProfessorMatches(int studentID) {
        List<Integer> professorIDs = new ArrayList<Integer>();
        try{
            String sql = "SELECT DISTINCT account.account_id FROM professor_abstract "
            + "JOIN professor ON professor_abstract.account_id = professor.account_id "
            + "JOIN account ON professor.account_id = account.account_id "
            + "JOIN professor_keyword ON professor.account_id = professor_keyword.account_id "
            + "WHERE professor_keyword.keyword_id "
            + "IN(SELECT keyword_id FROM student_keyword WHERE student_keyword.account_id = ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,studentID);
            rs = pstmt.executeQuery();
            while (rs.next()){
                professorIDs.add(rs.getInt("account_id"));
            }
        }
        catch (SQLException sqle){
            System.out.println("Error matching professors with student's keywords: " + sqle.getMessage());
        }

        return professorIDs;
    }

    // implement this with the getProfessorMatches in presentation layer
    public String getProfessorContactInfo(int professorID) {
        String result = "";
        String profName = "";
        String profEmail = "";
        String profBuilding = "";
        String profOffice = "";
        try{
            sql = "SELECT first_name, last_name, email, building_number, office_number FROM professor WHERE account_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,professorID);
            rs = pstmt.executeQuery();

            if (rs.next()){
                profEmail = rs.getString("email");
                profBuilding = rs.getString("building_number");
                profOffice = rs.getString("office_number");
                profName = rs.getString("first_name");
                profName += " " + rs.getString("last_name");
            }
            pstmt.close();
            rs.close();
        }
        catch(SQLException e){
            System.out.println("Error getting contact info: " + e.getMessage());
        }
        result = "Professor ID " + professorID + "\n\tContact Info:";
        result += "\n\t\tName: " + profName;
        result += "\n\t\tEmail: " + profEmail;
        result += "\n\t\tBuilding: " + profBuilding;
        result += "\n\t\tOffice: " + profOffice;

        return result;
    }

    // implement this with the getStudentMatches in presentation layer
    public String getStudentContactInfo(int studentID) {
        String result = "";
        String studentEmail = "";
        try{
            sql = "SELECT email FROM student WHERE account_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,studentID);
            rs = pstmt.executeQuery();

            if (rs.next()){
                studentEmail = rs.getString("email");
            }
            pstmt.close();
            rs.close();
        } catch(SQLException e){
            System.out.println("Error getting contact info: " + e.getMessage());
        }
        result = "Student ID " + studentID + " Contact Info: \nEmail: " + studentEmail;

        return result;
    }

}