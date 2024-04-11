import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer implements ServerInterface {
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

        Database database = new Database(profileIn, chatsIn, profileOut, chatsOut);
        database.readProfile();
        database.readChat();

        ServerSocket serverSocket;
        Socket clientSocket;
        BufferedReader inFromUser;
        PrintWriter outToUser;
        ObjectOutputStream outToClient;
        serverSocket = new ServerSocket(8080);
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                inFromUser = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToUser = new PrintWriter(clientSocket.getOutputStream());
                outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                new Thread(new ClientHandler(clientSocket)).start();
                String fromUser = inFromUser.readLine();
                switch (fromUser) {
                    case "createNewUser":
                        String username;
                        do {
                            username = inFromUser.readLine();
                        } while (database.getProfiles().containsKey(username));
                        outToUser.println("true");
                        outToUser.flush();
                        String password = inFromUser.readLine();
                        outToUser.println("true");
                        outToUser.flush();
                        String displayName = inFromUser.readLine();
                        String receiveAll = inFromUser.readLine();
                        if (database.createProfile(username, password, displayName,
                                Boolean.parseBoolean(receiveAll))) {
                            outToUser.println("true");
                            outToUser.flush();
                            break;
                        }
                        outToUser.println("false");
                        outToUser.flush();
                        break;
                    case "login":
                        String user = inFromUser.readLine();
                        String pwd = inFromUser.readLine();
                        if (database.login(user, pwd)) {
                            outToClient.writeObject(database.getProfiles().get(user));
                            outToClient.flush();
                            break;
                        }
                        outToClient.writeObject(null);
                        outToClient.flush();
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error connecting to the server!");
                return;
            }
        }
    }
}
