import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        serverSocket = new ServerSocket(8080);
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                inFromUser = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToUser = new PrintWriter(clientSocket.getOutputStream(), true);
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                System.out.println("Error connecting to the server!");
                return;
            }
        }
    }
}
