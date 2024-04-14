import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Runnable, ClientInterface {
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

        try (Socket socket = new Socket("localhost", 8080)) {    // Network connection
            // Setup network connection
            ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
            outToServer.flush();

            profile = new Profile();

            int i = 0;  // TODO: PLACEHOLDER TO AVOID ERRORS
            loop :
            while (true) {
                while (profile.getUsername() == null) {    // Loop while account is still empty
                    System.out.println("New createNewUser or login?");    // TODO GUI: Login interface
                    switch (scan.nextLine()) {    // TODO GUI: Action listeners and buttons rather than a switch
                        case "createNewUser" -> createNewUser(scan, inFromServer, outToServer);
                        case "login" -> login(scan, inFromServer, outToServer);
                        case "exit" -> {break loop;}
                    }
                }

                System.out.println("Enter action:");    // TODO GUI
                switch (scan.nextLine()) {    // TODO GUI: Action listeners and buttons rather than a switch
                    // TODO: Replace with appropriate method calls
                    case "sendMessage" -> sendMessage(scan, inFromServer, outToServer);
                    case "editMessage" -> editMessage(scan, inFromServer, outToServer);
                    case "deleteMessage" -> deleteMessage(scan, outToServer);
                    case "logout" -> logout(inFromServer, outToServer);
                    case "searchUsers" -> searchUsers(scan, inFromServer, outToServer);
                    case "blockUser" -> blockUser(scan, inFromServer, outToServer);
                    case "unblockUser" -> unblockUser(scan, inFromServer, outToServer);
                    case "friendUser" -> i = 7;
                    case "unfriendUser" -> i = 8;
                    case "editProfile" -> editProfile(scan, inFromServer, outToServer);
                    case "deleteProfile" -> deleteProfile(scan, inFromServer, outToServer);
                    case "exit" -> {break loop;}
                    default -> updateChats(inFromServer, outToServer);
                }
            }

            // Exit process
            outToServer.writeUnshared("exit");
            outToServer.flush();
            outToServer.close();
            inFromServer.close();

        } catch (IOException e) {
            System.out.println("Failed to connect to server");    // TODO GUI: Fail to connect error
        }
    }

    public void createNewUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        String username;
        String password;
        String display;
        String receiveAll;

        try {
            outToServer.writeUnshared("createNewUser");
            outToServer.flush();

            boolean loop = true;
            do {    // Get and validate username with server
                try {
                    // TODO GUI
                    System.out.println("Enter your desired username: ");
                    username = scan.nextLine();

                    if (username.isEmpty()) {    // Username must not be empty
                        continue;
                    }

                    outToServer.writeUnshared(username);
                    outToServer.flush();

                    loop = inFromServer.readBoolean();

                } catch (IOException e) {    // If socket is lost exit method
                    return;
                }
            } while (loop);

            try {
                // TODO GUI
                do { // Check valid password
                    System.out.println("Enter your desired password");
                    password = scan.nextLine();

                    System.out.println("Enter your desired password again");
                } while (!scan.nextLine().equals(password) || !checkValidPassword(password));

                outToServer.writeUnshared(password);
                outToServer.flush();

            } catch (IOException e) {    // If socket is lost exit method
                return;
            }

            // Get user display name
            // TODO GUI
            System.out.println("What you you liked to be called?");
            do {
                display = scan.nextLine();
            } while (display.isEmpty());
            outToServer.writeUnshared(display);
            outToServer.flush();

            // Get user receive all preference
            // TODO GUI
            do {
                System.out.println("Would you like to receive messages from everyone (true/false)?");
                receiveAll = scan.nextLine();
            } while (!receiveAll.equals("true") && !receiveAll.equals("false"));

            outToServer.writeBoolean(Boolean.parseBoolean(receiveAll));
            outToServer.flush();

            this.profile = (Profile) inFromServer.readObject();
        } catch (Exception e) {
            System.out.println("An error occurred while trying to create an account");
        }
    }

    public void logout(ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("logout");
            outToServer.flush();

            this.profile = (Profile) inFromServer.readObject();
        } catch (Exception e) {
            System.out.println("Failed to Logout");
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
            outToServer.writeUnshared("login");
            outToServer.flush();

            // TODO: Replace with GUI input rather than command line input
            String username;
            do {
                System.out.println("Please enter username:");
                username = scan.nextLine();
            } while (username.isEmpty());
            outToServer.writeUnshared(username);
            outToServer.flush();

            String password;
            do {
                System.out.println("Please enter password:");
                password = scan.nextLine();
            } while (password.isEmpty());
            outToServer.writeUnshared(password);
            outToServer.flush();

            Object o = inFromServer.readObject();
            if (o != null) {
                profile = (Profile) o;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while trying to login");
        }
    }

    public void deleteProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        System.out.println("Are you sure you want to delete this account?");
        if (scan.nextLine().equalsIgnoreCase("yes")) {
            try {
                outToServer.writeUnshared("deleteProfile");
                outToServer.flush();

                outToServer.writeUnshared(profile);
                outToServer.flush();

                logout(inFromServer, outToServer);
            } catch (Exception e) {
                System.out.println("Failed to delete account");
            }
        }
    }

    public void editProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("editProfile");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("What would you like to change: Display Name? Password? Receive All?");
            String input = scan.nextLine();

            if (input.equalsIgnoreCase("Display Name")) {
                outToServer.writeUnshared("display");
                outToServer.flush();

                String newDisplay;
                do {    // Get user input for new username
                    System.out.println("What you you liked to be called?");
                    newDisplay = scan.nextLine();
                } while (newDisplay.isEmpty());

                outToServer.writeUnshared(newDisplay);
                outToServer.flush();

                profile = (Profile) inFromServer.readObject();

            } else if (input.equalsIgnoreCase("Password")) {
                outToServer.writeUnshared("password");
                outToServer.flush();

                String newPassword;

                do {     // Enter old password before editing password
                    System.out.println("Enter your current password");
                } while (!scan.nextLine().equals(profile.getPassword()));

                do {     // Check valid password
                    System.out.println("Enter your desired password");
                    newPassword = scan.nextLine();

                    System.out.println("Enter your desired password again");
                } while (!scan.nextLine().equals(newPassword) || !checkValidPassword(newPassword));

                outToServer.writeUnshared(newPassword);
                outToServer.flush();

                profile = (Profile) inFromServer.readObject();
            } else if (input.equalsIgnoreCase("Receive All")) {
                outToServer.writeUnshared("receiveAll");
                outToServer.flush();

                System.out.println("Would you like to receive messages from everyone (true/false)?");
                outToServer.writeBoolean(Boolean.parseBoolean(scan.nextLine()));
                outToServer.flush();

                profile = (Profile) inFromServer.readObject();
            }
        } catch (Exception e) {
            System.out.println("Failed to edit account");
        }
    }

    public void updateChats(ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("updateChats");
            outToServer.flush();

            outToServer.writeUnshared(profile);
            outToServer.flush();

            chats = (ArrayList<Chat>) inFromServer.readObject();

            int j = 0;
            for (Chat chat : chats) {
                int i = 0;
                System.out.println("Chat " + j++);
                for (Message message : chat.getMessages()) {
                    String fromTag;
                    // Ensure message has the most up-to-date display information
                    if (message.getSender().getUsername().equals(chat.getProfiles().get(0).getUsername())) {
                        fromTag = chat.getProfiles().get(0).getDisplayName();
                    } else {
                        fromTag = chat.getProfiles().get(1).getDisplayName();
                    }

                    System.out.println("\t" + i++ + " - " + fromTag + ": " + message.getContents());
                }

                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("An error occurred when updating chats");
        }
    }

    public void searchUsers(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("searchUsers");
            outToServer.flush();

            System.out.println("Enter your search query:");
            String query;
            do {
                query = scan.nextLine();
            } while(query.isEmpty());

            outToServer.writeUnshared(query);
            outToServer.flush();

            ArrayList<Profile> results = (ArrayList<Profile>) inFromServer.readObject();

            for (Profile option : results) {
                System.out.println(option.getDisplayName() + "-" + option.getUsername());
            }

        } catch (Exception e) {
            System.out.println("An error occurred while searching for users");
        }
    }

    public void blockUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("blockUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to block?");
            String toBlock;
            do {
                toBlock = scan.nextLine();
            } while(toBlock.isEmpty());

            outToServer.writeUnshared(toBlock);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while blocking user");
        }
    }
    public void unblockUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("unblockUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to unblock?");
            String toUnblock;
            do {
                toUnblock = scan.nextLine();
            } while(toUnblock.isEmpty());

            outToServer.writeUnshared(toUnblock);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while unblocking user");
        }
    }
    public void friendUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("friendUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to friend?");
            String toFriend;
            do {
                toFriend = scan.nextLine();
            } while(toFriend.isEmpty());

            outToServer.writeUnshared(toFriend);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while friending user");
        }
    }
    public void unfriendUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("unfriendUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to unfriend?");
            String toUnfriend;
            do {
                toUnfriend = scan.nextLine();
            } while(toUnfriend.isEmpty());

            outToServer.writeUnshared(toUnfriend);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while unfriending user");
        }
    }
    public void sendMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("sendMessage");
            outToServer.flush();
            Profile receiver;

            do {
                System.out.println("Who would you like to send a message to?");
                outToServer.writeUnshared(scan.nextLine());    // Send profile name to server
                outToServer.flush();

                receiver = (Profile) inFromServer.readObject();
            } while (receiver == null);

            System.out.println("What would you like to say?");
            String contents;
            do {
                contents = scan.nextLine();    // Get message contents
            } while(contents.isEmpty());

            // Send the message to the server
            outToServer.writeUnshared(new Message(profile, receiver, contents));
            outToServer.flush();

        } catch (Exception e) {
            System.out.println("An error occurred when sending the message");
        }
    }

    public void editMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("editMessage");
            outToServer.flush();

            // TODO GUI
            Message toEdit;
            do {    // Get a valid method from chats
                int chatIndex;
                do {
                    System.out.println("Enter chat index");
                    try {
                        chatIndex = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                int messageIndex;
                do {
                    System.out.println("Enter message index");
                    try {
                        messageIndex = Integer.parseInt(scan.nextLine());

                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                try {
                    toEdit = chats.get(chatIndex).getMessages().get(messageIndex);

                    if (!toEdit.getSender().equals(profile)) {
                        System.out.println("You can only edit messages you sent!");
                        toEdit = null;
                    }

                } catch (Exception e) {
                    System.out.println("Indices must be within bounds");
                    toEdit = null;
                }
            } while (toEdit == null);

            System.out.println("Old message: " + toEdit.getContents());

            outToServer.reset();
            outToServer.writeUnshared(toEdit);
            outToServer.flush();

            System.out.println("What would you like to edit the message to?");
            String contents;
            do {
                contents = scan.nextLine();
            } while (contents.isEmpty());

            outToServer.writeUnshared(contents);
            outToServer.flush();

        } catch (Exception e) {
            System.out.println("An error occurred when editing the message");
        }
    }

    public void deleteMessage(Scanner scan, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("deleteMessage");
            outToServer.flush();

            // TODO GUI
            Message toDelete;
            do {    // Get a valid method from chats
                int chatIndex;
                do {
                    System.out.println("Enter chat index");
                    try {
                        chatIndex = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                int messageIndex;
                do {
                    System.out.println("Enter message index");
                    try {
                        messageIndex = Integer.parseInt(scan.nextLine());

                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                try {
                    toDelete = chats.get(chatIndex).getMessages().get(messageIndex);

                    if (!toDelete.getSender().equals(profile)) {
                        System.out.println("You can only edit messages you sent!");
                        toDelete = null;
                    }

                } catch (Exception e) {
                    System.out.println("Indices must be within bounds");
                    toDelete = null;
                }
            } while (toDelete == null);

            outToServer.reset();
            outToServer.writeUnshared(toDelete); // Send the message to delete
            outToServer.flush();

        } catch (Exception e) {
            System.out.println("An error occurred while trying to delete the message: " + e.getMessage());
        }
    }
}