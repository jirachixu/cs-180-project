import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements ClientInterface, Runnable {
    // Object specific to client
    Profile profile;
    ArrayList<Chat> chats;

    public Client() {
        profile = null;
        chats = null;
    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }

    public void run() {
        Scanner scan = new Scanner(System.in);    // TODO: Replace with GUI

        // Network IO objects
        Socket socket;
        ObjectInputStream inFromServer;
        ObjectOutputStream outToServer;


        try {    // Network connection
            socket = new Socket("localhost",8080);
            inFromServer = new ObjectInputStream(socket.getInputStream());
            outToServer = new ObjectOutputStream(socket.getOutputStream());
            outToServer.flush();

        } catch (IOException e) {
            System.out.println("Failed to connect");
            return;    // TODO GUI: Fail to connect error
        }

        profile = new Profile();    // TODO IO: Should be assigned empty profile by server

        while (profile.getUsername() == null) {    // Loop while account is still empty
            System.out.println("New createNewUser or login?");    // TODO GUI: Main interface
            switch (scan.nextLine()) {    // TODO GUI: Replace with action listeners and buttons rather than a switch
                case "createNewUser" -> createNewUser(scan, inFromServer, outToServer);
                case "login" -> login(scan, inFromServer, outToServer);
            }
        }

        int i;
        while (true) {
            System.out.println("Enter action:");    // TODO GUI: Main interface
            switch (scan.nextLine()) {    // TODO GUI: Replace with action listeners and buttons rather than a switch
                case "sendMessage" -> i = 1;
                case "editMessage" -> i = 2;
            }
        }
    }

    public void createNewUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        String username;
        String password;
        String display;
        String receiveAll;
        try {
            outToServer.writeObject("createNewUser");
            outToServer.flush();

            boolean loop = true;
            do {    // Get and validate username with server
                try {
                    // TODO: Replace with GUI input rather than command line input
                    System.out.println("Enter your desired username: ");
                    username = scan.nextLine();

                    if (username.isEmpty()) {    // Username must not be empty
                        continue;
                    }

                    outToServer.writeObject(username);
                    outToServer.flush();

                    loop = inFromServer.readBoolean();

                } catch (IOException e) {    // If socket is lost exit method
                    return;
                }
            } while (loop);

            try {
                // TODO: Replace with GUI input rather than command line input
                do { // Check valid password
                    System.out.println("Enter your desired password");
                    password = scan.nextLine();
                } while (!checkValidPassword(password));

                outToServer.writeObject(password);
                outToServer.flush();

            } catch (IOException e) {    // If socket is lost exit method
                return;
            }

            // Get user display name
            // TODO: Replace with GUI input rather than command line input
            System.out.println("What you you liked to be called?");
            do {
                display = scan.nextLine();
            } while (display.isEmpty());
            outToServer.writeObject(display);
            outToServer.flush();

            // Get user receive all preference
            // TODO: Replace with GUI input rather than command line input
            do {
                System.out.println("Would you like to receive messages from everyone (true/false)?");
                receiveAll = scan.nextLine();
            } while (!receiveAll.equals("true") && !receiveAll.equals("false"));

            outToServer.writeBoolean(Boolean.parseBoolean(receiveAll));
            outToServer.flush();

            profile = new Profile(username, password, display, Boolean.parseBoolean(receiveAll));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkValidPassword(String password) {    // Helper method to check if a password is valid
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

    public void login(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {    // Log into an account
        try {
            outToServer.writeObject("login");
            outToServer.flush();

            // TODO: Replace with GUI input rather than command line input
            String username;
            do {
                System.out.println("Please enter username:");
                username = scan.nextLine();
            } while (username.isEmpty());
            outToServer.writeObject(username);
            outToServer.flush();

            String password;
            do {
                System.out.println("Please enter password:");
                password = scan.nextLine();
            } while (password.isEmpty());
            outToServer.writeObject(password);
            outToServer.flush();

            Object o = inFromServer.readObject();
            if (o != null) {
                profile = (Profile) o;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
