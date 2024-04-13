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
        ServerSocket serverSocket;
        Socket clientSocket;
        BufferedReader inFromUser;
        PrintWriter outToUser;
        ObjectOutputStream outToClient;
        String fromUser;
        try {
            inFromUser = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToUser = new PrintWriter(socket.getOutputStream());
            fromUser = inFromUser.readLine();
            while (fromUser != null) {
                switch (fromUser) {
                    case "createNewUser" -> {
                        if (database.createProfile(inFromUser.readLine(), inFromUser.readLine(), inFromUser.readLine(),
                                Boolean.parseBoolean(inFromUser.readLine()))) {
                            outToUser.println("true");
                            outToUser.flush();
                        } else {
                            outToUser.println("false");
                            outToUser.flush();
                        }
                    }
                    case "login" -> {
                        if (database.login(inFromUser.readLine(), inFromUser.readLine())) {
                            outToUser.println("true");
                            outToUser.flush();
                        } else {
                            outToUser.println("false");
                            outToUser.flush();
                        }
                    }
                }
                fromUser = inFromUser.readLine();
            }
            inFromUser.close();
            outToUser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the file from which to read profiles: ");
        String profileIn = sc.nextLine();
        System.out.println("Enter the name of the file from which to read chats: ");
        String chatsIn = sc.nextLine();
        System.out.println("Enter the name of the file to which to write profiles: ");
        String profileOut = sc.nextLine();
        System.out.println("Enter the name of the file to which to write chats: ");
        String chatsOut = sc.nextLine();

        database = new Database(profileIn, chatsIn, profileOut, chatsOut);
        database.readProfile();
        database.readChat();

        ServerSocket serverSocket;
        serverSocket = new ServerSocket(8080);
        while (true) {
            Socket socket = serverSocket.accept();
            ChatServer server = new ChatServer(socket);
            new Thread(server).start();
        }
    }
}
