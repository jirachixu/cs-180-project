import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements ClientInterface {
    public static void main(String[] args) {
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

        Profile profile = new Profile();    // TODO IO: Should be assigned empty profile by server

        try {
            while (profile.getUsername() == null) {    // Loop while account is still empty
                switch (Integer.parseInt(scan.nextLine())) {    // TODO: Replace with action listeners and buttons rather than a switch
                    case 1 -> profile = createNewUser(scan, inFromServer, outToServer);
                    case 2 -> profile = login(scan, outToServer, objectInputStream);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            break;
        }    // TODO: Loop of main functionality
    }

    private static Profile createNewUser(Scanner scan, BufferedReader br, PrintWriter pw) throws IOException {    // Creates an account
        String username;
        String password;
        String display;
        String receiveAll;

        pw.println("createNewUser");
        pw.flush();
        boolean loop = true;
        do {
            // TODO: Replace with GUI input rather than command line input
            System.out.println("Enter your desired username: ");
            username = scan.nextLine();
            pw.println(username);
            pw.flush();
            loop = !br.readLine().equals("true");
        } while (loop);

        loop = true;
        do {
            // TODO: Replace with GUI input rather than command line input
            System.out.println("Enter your desired password");
            password = scan.nextLine(); // TODO: check valid password
            pw.println(password);
            pw.flush();
            loop = !br.readLine().equals("true");
        } while (loop);

        display = scan.nextLine();
        pw.println(display);
        pw.flush();
        do {
            receiveAll = scan.nextLine();
        } while (!receiveAll.equals("true") && !receiveAll.equals("false"));
        pw.println(receiveAll);
        pw.flush();
        boolean successful = br.readLine().equals("true");

        if (successful) {
            return new Profile(username, password, display, Boolean.parseBoolean(receiveAll));
        }
        // TODO: unsuccessful message
        return new Profile();
    }

    private static Profile login(Scanner scan, PrintWriter pw, ObjectInputStream ois) throws IOException {    // Log into an account
        pw.println("login");
        pw.flush();
        String username = scan.nextLine();
        String password = scan.nextLine();

        pw.println(username);
        pw.flush();
        pw.println(password);
        pw.flush();

        Object o;
        try {
            o = ois.readObject();
            if (!(o instanceof Profile)) {
                throw new Exception();
            }
        } catch (Exception e) {
            // TODO: error message
            return new Profile();
        }

        return (Profile) o;
    }
}
