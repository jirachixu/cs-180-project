import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements ServerInterface {
    private final Socket socket;
    static Database database;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // Connect the object streams together
            ObjectOutputStream outToUser= new ObjectOutputStream(socket.getOutputStream());
            outToUser.flush();
            ObjectInputStream inFromUser = new ObjectInputStream(socket.getInputStream());

            loop :
            while (true) {
                Object command = inFromUser.readObject();
                switch ((String) command) {    // Select operation to perform
                    case "createNewUser" -> createNewUser(inFromUser, outToUser);
                    case "login" -> login(inFromUser, outToUser);
                    case "logout" -> logout(outToUser);
                    case "deleteProfile" -> deleteProfile(inFromUser, outToUser);
                    case "editProfile" -> editProfile(inFromUser, outToUser);
                    case "exit" -> {break loop;}
                }
            }

            // Exit condition
            inFromUser.close();
            outToUser.close();

        } catch (Exception e) {
            System.out.println("Client disconnected");
        } finally {
            database.outputChat();
            database.outputProfile();
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Load database into server
        String profileIn;
        String chatsIn;
        String profileOut;
        String chatsOut;

        System.out.println("Would you like to load default files?");
        if (scan.nextLine().equalsIgnoreCase("yes")) {
            profileIn = "profileTest.txt";
            chatsIn = "chatTest.txt";
            profileOut = "profileTest.txt";
            chatsOut = "chatTest.txt";
        } else {
            System.out.println("Enter the name of the file from which to read profiles: ");
            profileIn = scan.nextLine();
            System.out.println("Enter the name of the file from which to read chats: ");
            chatsIn = scan.nextLine();
            System.out.println("Enter the name of the file to which to write profiles: ");
            profileOut = scan.nextLine();
            System.out.println("Enter the name of the file to which to write chats: ");
            chatsOut = scan.nextLine();
        }

        database = new Database(profileIn, chatsIn, profileOut, chatsOut);    // TODO: Try catch if files do not exist
        database.readProfile();
        database.readChat();

        // Open server socket
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            //noinspection InfiniteLoopStatement
            while (true) {
                System.out.println("Waiting for server connection");
                Socket socket = serverSocket.accept();
                System.out.println("Server connected");
                Server server = new Server(socket);
                new Thread(server).start();
            }
        } catch (Exception e) {
            System.out.println("Server socket failed to start");
        }
    }

    public void createNewUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String username;
            String password;
            String display;
            boolean receiveAll;
            boolean loop;

            // Mirrors input for createNewUser in Client
            do {
                username = (String) inFromUser.readObject();
                loop = database.usernameFree(username);
                outToUser.writeBoolean(loop);
                outToUser.flush();
            } while(loop);


            password = (String) inFromUser.readObject();
            display = (String) inFromUser.readObject();
            receiveAll = inFromUser.readBoolean();

            database.createProfile(username, password, display, receiveAll);

            outToUser.writeObject(database.getProfile(username));
            outToUser.flush();

            System.out.println("Profile successfully created and sent to user!");

        } catch (Exception e) {
            System.out.println("Error occurred while creating account");
        }
    }

    public void login(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        // Mirrors input for login in Client
        try {
            String username = (String) inFromUser.readObject();
            String password = (String) inFromUser.readObject();

            if (database.login(username, password)){
                System.out.println("Sending profile " + username);
                outToUser.writeObject(database.getProfile(username));
            } else {
                System.out.println("Profile not found");
                outToUser.writeObject(null);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while logging in");
        }
    }

    public void logout(ObjectOutputStream outToUser) {
        // Mirrors input for logout in Client
        try {
            outToUser.writeObject(new Profile());
            outToUser.flush();
            System.out.println("Client logged out");

        } catch (Exception e) {
            System.out.println("Error occurred while logging out");
        }
    }

    public void deleteProfile(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        // Mirrors input for deleteProfile in Client
        try {
            Profile profile = (Profile) inFromUser.readObject();
            database.deleteProfile(profile.getUsername());

            System.out.println("Profile " + profile.getUsername() + " deleted");
        } catch (Exception e) {
            System.out.println("Error occurred while deleting profile");
        }
    }

    public void editProfile(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        // Mirrors input for editProfile in Client
        try {
            String username = (String) inFromUser.readObject();
            String choice = (String) inFromUser.readObject();

            if (choice.equals("display")) {
                database.editDisplayName(username, (String) inFromUser.readObject());
                outToUser.writeObject(database.getProfile(username));
                outToUser.flush();

            } else if (choice.equalsIgnoreCase("password")) {
                System.out.println(database.getProfile(username).getPassword());    // TODO
                database.editPassword(username, (String) inFromUser.readObject());

                System.out.println(database.getProfile(username).getPassword());    // TODO

                outToUser.writeObject(database.getProfile(username));
                outToUser.flush();
                System.out.println(database.getProfile(username).getPassword());    // TODO
            }
        } catch (Exception e) {
            System.out.println("Error occurred while deleting profile");
        }
    }
}
