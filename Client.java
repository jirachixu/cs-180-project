import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements ClientInterface {
    Profile profile;

    public void run() {    // TODO: Should probably become a run method
        Scanner scan = new Scanner(System.in);
        Socket socket;
        BufferedReader inFromServer;
        PrintWriter outToServer;
        ObjectInputStream objectInputStream;

        // TODO IO: SERVER CONNECTION SHOULD BE FIRST THING DONE ON RUN
        try {
            socket = new Socket("localhost",8080);
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToServer = new PrintWriter(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);    // TODO: Fail to connect error
        }

        profile = new Profile();    // TODO IO: Should be assigned empty profile by server

        try {
            while (profile.getUsername() == null) {    // Loop while account is still empty
                switch (Integer.parseInt(scan.nextLine())) {    // TODO: Replace with action listeners and buttons rather than a switch
                    case 1 -> createNewUser(scan, inFromServer, outToServer); //profile = createNewUser(scan, inFromServer, outToServer);
                    case 2 -> profile = login(scan, outToServer, objectInputStream);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            break;
        }    // TODO: Loop of main functionality
    }

    public void createNewUser(Scanner scan, BufferedReader br, PrintWriter pw) {    // Creates an account
        String username;
        String password;
        String display;
        String receiveAll;

        pw.println("createNewUser");
        pw.flush();
        boolean loop = true;

        do {    // Get and validate username with server
            try {
                // TODO: Replace with GUI input rather than command line input
                System.out.println("Enter your desired username: ");
                username = scan.nextLine();

                pw.println(username);    // TODO: Move all server communications further down and together?
                pw.flush();

                loop = !br.readLine().equals("true");

            } catch (IOException e) {    // If socket is lost exit method
                return;
            }
        } while (loop);

        loop = true;
        do {
            try {
                // TODO: Replace with GUI input rather than command line input
                do { // Check valid password
                    System.out.println("Enter your desired password");
                    password = scan.nextLine();
                } while(checkValidPassword(password));

                pw.println(password);    // TODO: Move all server communications further down and together?
                pw.flush();

                loop = !br.readLine().equals("true");

            } catch (IOException e) {    // If socket is lost exit method
                return;
            }
        } while (loop);

        display = scan.nextLine();
        pw.println(display);
        pw.flush();

        do {
            receiveAll = scan.nextLine();
        } while (!receiveAll.equals("true") && !receiveAll.equals("false"));

        pw.println(receiveAll);    // TODO: Move all server communications further down and together?
        pw.flush();

        boolean successful;
        try {
            successful = br.readLine().equals("true");
        } catch (IOException e) {    // If socket is lost exit method
            return;
        }

        if (successful) {
            profile = new Profile(username, password, display, Boolean.parseBoolean(receiveAll));
        }

        // TODO: unsuccessful message
        profile = new Profile();
    }

    private boolean checkValidPassword(String password) {
        boolean uppercase = false;
        boolean lowercase = false;
        boolean number = false;

        if (password.length() >= 8) {
            for (char character : password.toCharArray()) {
                if (Character.isDigit(character)) {
                    number = true;
                } else if (Character.isUpperCase(character)) {
                    uppercase = true;
                } else if (Character.isLowerCase(character)) {
                    lowercase = true;
                }
            }

        } else {
            System.out.println("Password must be at least 8 characters!");    // TODO GUI: Replace with GUI
            return false;
        }

        if (!uppercase) {
            System.out.println("Password must an uppercase character!");    // TODO GUI: Replace with GUI
            return false;
        } else if (!lowercase) {
            System.out.println("Password must a lowercase character!");    // TODO GUI: Replace with GUI
            return false;
        } else if (!number) {
            System.out.println("Password must a digit!");    // TODO GUI: Replace with GUI
            return false;
        } else {
            return true;
        }
    }

    // TODO: Fix profile login
    public Profile login(Scanner scan, PrintWriter pw, ObjectInputStream ois) throws IOException {    // Log into an account
        pw.println("login");
        pw.flush();
        String username = scan.nextLine();
        String password = scan.nextLine();

        pw.println(username);
        pw.flush();
        pw.println(password);
        pw.flush();

        Object o;
        try {
            o = ois.readObject();
            if (!(o instanceof Profile)) {
                throw new Exception();
            }
        } catch (Exception e) {
            // TODO: error message
            return new Profile();
        }

        return (Profile) o;
    }





    public int blockUser(Profile profile) {
        return 1;    // TODO
    }
    public int unblockUser(Profile profile) {
        return 1;    // TODO
    }
    public int friendUser(Profile profile) {
        return 1;    // TODO
    }
    public int unfriendUser(Profile profile) {
        return 1;    // TODO
    }
    public String sendMessage(Message message) {
        return null;    // TODO
    }
    public String editMessage(Message message, String newMessage) {
        return null;    // TODO
    }
    public String deleteMessage(Message message) {
        return null;    // TODO
    }
    public int deleteProfile() {
        return 1;    // TODO
    }
    public void editProfile(String newDisplayName) {
        return;    // TODO
    }
}
