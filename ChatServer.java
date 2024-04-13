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
                switch ((String) command) {
                    case "createNewUser" -> {
                        // TODO: Make match syntax of client
                        if (database.createProfile((String) inFromUser.readObject(), (String) inFromUser.readObject(),
                                (String) inFromUser.readObject(), (Boolean) inFromUser.readObject())) {

                            outToUser.writeObject(true);
                            outToUser.flush();
                        } else {
                            outToUser.writeObject(false);
                            outToUser.flush();
                        }
                    }
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
        Scanner sc = new Scanner(System.in);

        String profileIn;
        String chatsIn;
        String profileOut;
        String chatsOut;

        System.out.println("Would you like to load default files?");
        if (sc.nextLine().equalsIgnoreCase("yes")) {
            profileIn = "profileTest.txt";
            chatsIn = "chatTest.txt";
            profileOut = "profileTestOut.txt";
            chatsOut = "chatTestOut.txt";
        } else {
            System.out.println("Enter the name of the file from which to read profiles: ");
            profileIn = sc.nextLine();
            System.out.println("Enter the name of the file from which to read chats: ");
            chatsIn = sc.nextLine();
            System.out.println("Enter the name of the file to which to write profiles: ");
            profileOut = sc.nextLine();
            System.out.println("Enter the name of the file to which to write chats: ");
            chatsOut = sc.nextLine();
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
}
