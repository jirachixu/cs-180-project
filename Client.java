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

        while (profile.getUsername() == null) {    // Loop while account is still empty
            switch (Integer.parseInt(scan.nextLine())) {    // TODO: Replace with action listeners and buttons rather than a switch
                case 1 -> createNewUser(scan, inFromServer, outToServer);
                case 2 -> login(scan, outToServer, objectInputStream);
            }
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

                pw.println(password);    // TODO: Move all server communications as packet?
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

        pw.println(receiveAll);    // TODO: Move all server communications as packet?
        pw.flush();

        boolean successful;
        try {
            successful = br.readLine().equals("true");
        } catch (IOException e) {    // If socket is lost exit method
            return;
        }

        if (successful) {    // Might need to make as packets s.t. copy gets put in database as well
            profile = new Profile(username, password, display, Boolean.parseBoolean(receiveAll));
        } else {
            System.out.println("Please try again!");
            profile = new Profile();
        }
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
    public void login(Scanner scan, PrintWriter pw, ObjectInputStream ois) {    // Log into an account
        pw.println("login");
        pw.flush();

        // TODO: Replace with GUI input rather than command line input
        System.out.println("Please enter username:");
        String username = scan.nextLine();
        System.out.println("Please enter password:");
        String password = scan.nextLine();

        pw.println(username);
        pw.flush();
        pw.println(password);
        pw.flush();

        Object o;
        try {
            o = ois.readObject();
            if (o instanceof Profile) {
                profile = (Profile) o;
            }
        } catch (Exception e) {
            // TODO: error message
            return;
        }
    }
    public int deleteProfile() {
        return 1;    // TODO
    }
    public void editProfile(String newDisplayName) {
        return;    // TODO
    }

    public int blockUser(Profile profile) {
        /** Add the profile passed to this.profile and then send this.profile to server to store the updated profile in
         * the database. Probably can just return void rather than string so updated in interface as well.
         */
        return 1;
    }
    public int unblockUser(Profile profile) {
        /** Reference blockUser with the appropriate lists within this.profile
         */
        return 1;
    }
    public int friendUser(Profile profile) {
        /** Reference blockUser with the appropriate lists within this.profile
         */
        return 1;    // TODO
    }
    public int unfriendUser(Profile profile) {
        /** Reference blockUser with the appropriate lists within this.profile
         */
        return 1;    // TODO
    }
    public String sendMessage(Message message) {
        /** Needs to take in the message and send it to the server. The server then needs to find the corresponding chat
         * and add the new message to the chat. Probably can just return void rather than string so updated in interface
         * as well.
         */
        return null;
    }
    public String editMessage(Message message, String newMessage) {
        /** Needs to take in the old message and send it to the server with the new message. The server then needs to
         * find the corresponding chat and message and use the edit method. Probably can just return void rather than
         * string so updated in interface as well.
         */
        return null;
    }
    public String deleteMessage(Message message) {
        /** Needs to take in the old message and send it to the server. The server then needs to find the corresponding
         * chat and message and use the delete method. Probably can just return void rather than string so updated in
         * interface as well.
         */
        return null;
    }
}
