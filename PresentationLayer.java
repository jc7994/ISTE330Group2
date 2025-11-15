public class PresentationLayer {
    DataLayer dl = new DataLayer();
    String databaseName;
    String username;
    String password;

    public PresentationLayer() {

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


