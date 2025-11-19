import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PresentationLayer {
    DataLayer dl = new DataLayer();
    String databaseName;
    String username;
    String password;

    public PresentationLayer() {

        runConnectDatabase();
        System.out.print("Welcome to the Faculty Research Database! \n");
        User currentUser = null;
        while (currentUser == null) {
            currentUser = loginMenu();
        }

        System.out.println("Welcome, " + currentUser.getUsername() + "!");

        switch (currentUser.getUserType()) {
            case "professor": 
                professorMenu(currentUser);
                break;
            case "student":
                studentMenu(currentUser);
            case "public":
                publicMenu();
        }

        currentUser = loginMenu();

    }

    public void interestSearchMenu() {
        System.out.println("\"---------------SEARCH BY INTERESTS--------------\"");
        System.out.print("Please enter an interest to search from: ");
        String input = GetInput.readLine();


    }

    public void abstractSearchMenu() {
        System.out.println("\"---------------SEARCH BY ABSTRACTS--------------\"");
        System.out.print("Please enter a title to search from: ");
        String input = GetInput.readLine();


    }

    public User loginMenu() {
        User user = null;
        System.out.println("---------------LOGIN MENU--------------");
        System.out.println();
        boolean valid = false; // only valid once a user has logged in or registered

        while (!valid) {
            System.out.println("Please select an option: \n" +
                                "[0] Login through existing account \n" +
                                "[1] Register a new account \n" +
                                "[2] Exit the database \n");
            System.out.print("Selection: ");
            int input = GetInput.readInt();
            System.out.println();

            switch (input) {
                case 0: // login option
                    System.out.println("---------------LOGIN--------------");
                    System.out.print("username: ");
                    String loginUsername = GetInput.readWord();
                    System.out.print("password: ");
                    String loginPassword = GetInput.readWord();
                    try {
                        valid = dl.validateLogin(loginUsername, loginPassword);
                        if (valid) {
                            user = dl.getUserByUsername(loginUsername);
                        }
                    }
                    catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 1: // register option
                    System.out.println("---------------REGISTER--------------");
                    System.out.print("User Type (Professor, Student, or Public): ");
                    String userType = GetInput.readWord();
                    System.out.println();

                    System.out.print("First Name: ");
                    String firstName = GetInput.readWord();
                    System.out.println();

                    System.out.print("Last Name: ");
                    String lastName = GetInput.readWord();
                    System.out.println();

                    System.out.print("Email: ");
                    String email = GetInput.readWord();
                    System.out.println();

                    System.out.print("Username: ");
                    String registerUsername = GetInput.readWord();
                    System.out.println();

                    System.out.print("Password: ");
                    String registerPassword = GetInput.readWord();
                    System.out.println();

                    switch(userType) {
                        case "professor": 
                            User prof = new User();
                            System.out.print("Building Number: ");
                            String building = GetInput.readWord();
                            System.out.println();

                            System.out.print("Office Number: ");
                            String office = GetInput.readWord();
                            System.out.println();
                            prof.setUserType(userType);
                            prof.setUsername(registerUsername);

                            try {
                                if (dl.registerProfessor(registerUsername, registerPassword, firstName, lastName, email, building, office)) {
                                    user = prof;
                                }
                            } catch (SQLException e) {
                                System.out.println("Error registering professor: " + e.getMessage());
                            }
                            break;
                        case "student":
                            User student = new User();
                            System.out.print("GPA: ");
                            Double gpa = GetInput.readDouble();
                            System.out.println();

                            student.setUserType(userType);
                            student.setUsername(registerUsername);
                            try {
                                if (dl.registerStudent(registerUsername, registerPassword, firstName, lastName, email, gpa)) {
                                    user = student;
                                }
                            } catch (SQLException e) {
                                System.out.println("Error registering student: " + e.getMessage());
                            }
                            break;
                        case "public":
                            User publicAcc = new User();
                            
                            publicAcc.setUserType(userType);
                            publicAcc.setUsername(registerUsername);
                            try {
                                if (dl.registerPublic(registerUsername, registerPassword, firstName, lastName, email)) {
                                    user = publicAcc;
                                }
                            } catch (SQLException e) {
                                System.out.println("Error registering student: " + e.getMessage());
                            }
                            break;
                        default:
                            System.out.println("Invalid user type. Please enter the correct options.");
                            break;
                    }

                    if (user != null) {
                        valid = true;
                        return user;
                    }
                    break;
                case 2: // exit from database option
                    System.out.println("Exiting from database...");
                    dl.closeConnection();
                    System.exit(0);
                    return user;
                default:
                    System.out.println("Wrong input. Please choose a valid input.");
                    break;
            }
        }
        return user;
    }

    public void professorMenu(User professor) {
        System.out.println("---------------PROFESSOR MENU--------------");
        boolean running = true;
        while (running) {
            System.out.println("Please select an option: \n" +
                                "[0] Logout \n" +
                                "[1] Interests \n" +
                                "[2] Abstracts \n" +
                                "[3] Search by Interests \n" +
                                "[4] Search by Abstracts");
            System.out.print("Selection: ");
            int input = GetInput.readInt();
            System.out.println();

            switch (input) {
                case 0:
                    running = false;
                    break;
                case 1:
                    System.out.println("---------------YOUR INTERESTS--------------");
                    List<String> updateInterests = dl.getProfessorKeywords(professor.getAccountID());
                    for (String interest : updateInterests) {
                        System.out.println(interest);
                    }
                    boolean validUpdate = false;
                    while (!validUpdate) {
                        System.out.println("Please select an option: \n" +
                                "[0] Exit \n" +
                                "[1] Add Interests \n" +
                                "[2] Delete Interests");
                        System.out.println();
                        System.out.print("Selection: ");
                        int updateInput = GetInput.readInt();
                        System.out.println();

                        if (updateInput == 1) {
                            List<String> addInterests = Arrays.asList(GetInput.readLine().split(", "));
                            if (dl.addKeywords("professor", professor.getAccountID(), addInterests) == 1) {
                                System.out.println("Interests added.");
                            }
                            else {
                                System.out.println("Error adding interests.");
                            }
                        }
                        if (updateInput == 2) {
                            List<String> deleteInterests = Arrays.asList(GetInput.readLine().split(", "));
                            if (dl.deleteKeywords("professor", professor.getAccountID(), deleteInterests) == 1) {
                                System.out.println("Interests deleted.");
                            }
                            else {
                                System.out.println("Error deleting interests.");
                            }
                        }
                        else { 
                            System.out.println("Invalid input");
                        }
                        System.out.println("Back to Professor Menu...");
                    }
                    break;
                case 2:
                    System.out.println("---------------YOUR ABSTRACTS--------------");
                    List<String> abstracts = dl.getAbstract(professor.getAccountID());
                    for (String abstractTitle : abstracts) {
                        System.out.println(abstractTitle);
                    }

                    System.out.println("Please select an option: \n" +
                                "[0] Exit \n" +
                                "[1] Add Abstract \n");
                    System.out.println();
                    System.out.print("Selection: ");
                    int updateInput = GetInput.readInt();
                    System.out.println();

                    if (updateInput == 1) {
                        int addedAbstract = -1;
                        while (addedAbstract == -1) {
                            System.out.println("Title: ");
                            String title = GetInput.readLine();

                            System.out.println("Professor usernames: ");
                            List<String> professors = Arrays.asList(GetInput.readLine().split(", "));
                            addedAbstract = dl.addAbstract(title, professors);
                            if (addedAbstract != 1) {
                                System.out.println("Added abstract.");
                            }
                            else {
                                System.out.println("Error adding abstract.");
                            }
                            System.out.println("Back to Professor Menu...");

                            }
                    }
                    else { 
                        System.out.println("Invalid input");
                    }
                    System.out.println("Back to Professor Menu...");
                    break;
                case 3:
                    System.out.println("\"---------------SEARCH BY INTERESTS--------------\"");
                    abstractSearchMenu();
                    break;

                case 4:
                    interestSearchMenu();
                    break;

            }
        }


    }

    public void studentMenu(User student) {
        System.out.println("---------------STUDENT MENU--------------");
        boolean running = true;
        while (running) {
            System.out.println("Please select an option: \n" +
                                "[0] Logout \n" +
                                "[1] Interests \n" +
                                "[3] Search by Interests and Abstracts \n" +
                                "[4] See all Faculty Abstracts");
            System.out.print("Selection: ");
            int input = GetInput.readInt();
            System.out.println();

            switch (input) {
                case 0:
                    running = false;
                    break;
                case 1:
                    System.out.println("---------------YOUR INTERESTS--------------");
                    List<String> updateInterests = dl.getStudentKeywords(student.getAccountID());
                    for (String interest : updateInterests) {
                        System.out.println(interest);
                    }
                    boolean validUpdate = false;
                    while (!validUpdate) {
                        System.out.println("Please select an option: \n" +
                                "[0] Exit \n" +
                                "[1] Add Interests \n" +
                                "[2] Delete Interests");
                        System.out.println();
                        System.out.print("Selection: ");
                        int updateInput = GetInput.readInt();
                        System.out.println();

                        if (updateInput == 1) {
                            List<String> addInterests = Arrays.asList(GetInput.readLine().split(", "));
                            if (dl.addKeywords("student", student.getAccountID(), addInterests) == 1) {
                                System.out.println("Interests added.");
                            }
                            else {
                                System.out.println("Error adding interests.");
                            }
                        }
                        if (updateInput == 2) {
                            List<String> deleteInterests = Arrays.asList(GetInput.readLine().split(", "));
                            if (dl.deleteKeywords("student", student.getAccountID(), deleteInterests) == 1) {
                                System.out.println("Interests deleted.");
                            }
                            else {
                                System.out.println("Error deleting interests.");
                            }
                        }
                        else { 
                            System.out.println("Invalid input");
                        }
                        System.out.println("Back to Student Menu...");
                    }
                    break;
                case 2:
                    System.out.println("\"---------------SEARCH BY INTERESTS AND ABSTRACTS--------------\"");
                    // insert search function here
                    System.out.println("Back to Student Menu...");
                    break;

                case 3:
                    System.out.println("\"---------------ALL ABSTRACTS--------------\"");
                    // insert getAbstracts function here
                    System.out.println("Back to Student Menu...");

                    break;
            }
        }

    }

    public void publicMenu() {
        System.out.println("---------------PUBLIC MENU--------------");
        boolean running = true;
        while (running) {
            System.out.println("Please select an option: \n" +
                                "[0] Logout \n" +
                                "[1] Search by Interests and Abstracts \n" +
                                "[2] Insert Interests \n" +
                                "[3] Modify Interests \n" +
                                "[4] Search by Interests and Abstracts \n" +
                                "[5] See all Faculty Abstracts");
            System.out.print("Selection: ");
            int input = GetInput.readInt();
            System.out.println();

            switch (input) {
                case 0:
                    running = false;
                    break;
                case 1:
                    System.out.println("\"---------------SEARCH BY INTERESTS AND ABSTRACTS--------------\"");
                    // insert search function here
                    break;

                case 2: 
                    System.out.println("\"---------------INSERT INTERESTS--------------\"");
                    // insert insert interests function here   
                    List<String> addInterests = Arrays.asList(GetInput.readLine().split(", "));
                        if (dl.addKeywords("student", student.getAccountID(), addInterests) == 1) {
                            System.out.println("Interests added.");
                        }
                    System.out.print("Enter ");

                case 3:
                    System.out.println("\"---------------MODIFY INTERESTS--------------\"");
                    // insert modify interests function here   
                    System.out.print("Enter ");
                case 4:
                    System.out.println("\"---------------SEARCH BY INTERESTS AND ABSTRACTS--------------\"");
                    // insert search by interests and abstracts function here   
                    System.out.print("Please enter an interest to search from: ");
                    String guestInterest = GetInput.readLine();

                    System.out.print("Please enter a title to search from: ");
                    String guestAbstract = GetInput.readLine();

                    


                    System.out.print("Enter ");

                case 5:
                    System.out.println("\"---------------ALL ABSTRACTS--------------\"");
                    // insert getAbstracts function here   
                    System.out.print("Enter Professor ID: ");

                    List<String> abstracts = dl.getAbstract(profAccountID);
                    for (String i: abstracts){
                        System.out.println(i);
                    }

                    break;
                default: 
                    System.out.println("Invalid input.");
                    break;
            }
            System.out.println("Back to Public Menu...");
        }
    }

    public void runConnectDatabase() {
        int connected = -1;
        while (connected == -1) {
            System.out.print("Enter database name: ");
            databaseName = GetInput.readWord();

            System.out.print("Enter username: ");
            username = GetInput.readWord();

            System.out.print("Enter password: ");
            password = GetInput.readWord();

            if (dl.connect(databaseName, username, password)) {
                connected = 1;
            } else {
                connected = -1;
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Group 2");
        new PresentationLayer();
    }
}


