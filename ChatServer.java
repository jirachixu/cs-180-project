import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer implements ServerInterface, Runnable {
    private final Socket socket;
    static Database database;

    public ChatServer(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        ObjectInputStream inFromUser;
        ObjectOutputStream outToUser;
        Object command;

        try {
            // Connect the object streams together
            outToUser = new ObjectOutputStream(socket.getOutputStream());
            outToUser.flush();
            inFromUser = new ObjectInputStream(socket.getInputStream());

            command = inFromUser.readObject();

            while (command != null) {
                switch ((String) command) {    // Select operation to perform
                    case "createNewUser" -> createNewUser(inFromUser, outToUser);
                    case "login" -> {
                        // TODO: Make match syntax of client
                        if (database.login((String) inFromUser.readObject(), (String) inFromUser.readObject())) {
                            outToUser.writeObject(true);
                            outToUser.flush();
                        } else {
                            outToUser.writeObject(false);
                            outToUser.flush();
                        }
                    }
                }

                command = inFromUser.readObject();
            }

            inFromUser.close();
            outToUser.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);

        String profileIn;
        String chatsIn;
        String profileOut;
        String chatsOut;

        System.out.println("Would you like to load default files?");
        if (scan.nextLine().equalsIgnoreCase("yes")) {
            profileIn = "profileTest.txt";
            chatsIn = "chatTest.txt";
            profileOut = "profileTestOut.txt";
            chatsOut = "chatTestOut.txt";
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

        database = new Database(profileIn, chatsIn, profileOut, chatsOut);
        database.readProfile();
        database.readChat();


        ServerSocket serverSocket;
        serverSocket = new ServerSocket(8080);
        while (true) {
            System.out.println("Waiting for server connection");
            Socket socket = serverSocket.accept();
            System.out.println("Server connected");
            ChatServer server = new ChatServer(socket);
            new Thread(server).start();
        }
    }

    public void createNewUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String username;
            String password;
            String display;
            boolean receiveAll;
            boolean loop;

            do {    // Mirrors username input for account creation
                System.out.println("Awaiting valid username input");
                username = (String) inFromUser.readObject();
                loop = database.usernameFree(username);
                outToUser.writeBoolean(loop);
                outToUser.flush();
            } while(loop);

            System.out.println("Awaiting valid password input");
            password = (String) inFromUser.readObject();

            System.out.println("Awaiting valid display name input");
            display = (String) inFromUser.readObject();

            System.out.println("Awaiting valid receiveAll input");
            receiveAll = inFromUser.readBoolean();

            System.out.println("Received all inputs!");
            database.createProfile(username, password, display, receiveAll);

            System.out.println("Profile successfully created!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
