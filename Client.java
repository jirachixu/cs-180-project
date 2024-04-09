import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements ClientInterface {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Socket socket;
        BufferedReader inFromServer;
        PrintWriter outToServer;

        // TODO IO: SERVER CONNECTION SHOULD BE FIRST THING DONE ON RUN
        try {
            socket = new Socket("localhost",8080);
        } catch (IOException e) {
            throw new RuntimeException(e);    // TODO: Fail to connect error
        }

        Profile profile = new Profile();    // TODO IO: Should be assigned empty profile by server

        while (profile.getUsername() == null) {    // Loop while account is still empty
            switch (Integer.parseInt(scan.nextLine())) {    // TODO: Replace with action listeners and buttons rather than a switch
                case 1 -> profile = createNewUser(scan, socket);
                case 2 -> profile = login(scan, socket);
            }
        }

        while (true) {
            break;
        }    // TODO: Loop of main functionality
    }

    private static Profile createNewUser(Scanner scan, Socket socket) {    // Creates an account
        String username;
        String password;
        String display;
        boolean receiveAll;

        boolean loop = true;
        do {
            // TODO: Replace with GUI input rather than command line input
            System.out.println("Enter your desired username: ");
            username = scan.nextLine();

            loop = false;    // TODO IO: Server sends boolean to confirm username is valid
        } while (loop);

        loop = true;
        do {
            // TODO: Replace with GUI input rather than command line input
            System.out.println("Enter your desired password");
            password = scan.nextLine();

            loop = false;    // TODO IO: Server sends boolean to confirm password is valid
        } while (loop);

        display = scan.nextLine();
        receiveAll = Boolean.parseBoolean(scan.nextLine());

        return new Profile(username, password, display, receiveAll);
    }

    private static Profile login(Scanner scan, Socket socket) {    // Log into an account
        String username = scan.nextLine();
        String password = scan.nextLine();

        // TODO: Send username and password to server to find account

        Profile profile;
        do {
            profile = new Profile("TODO");    // TODO IO: Receive a profile from server
        } while (profile.getUsername() == null);

        return profile;
    }
}
