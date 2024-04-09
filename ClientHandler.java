import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;
        String user;
        String pass;
        try (Socket socket = this.socket) {
            message = reader.readLine();
            while (message != null) {
                switch (message) {
                    case "1":
                        user = reader.readLine();
                        pass = reader.readLine();
                        writer.println(String.format("CREATE_USER;%s;%s", user, pass));
                        writer.println();
                        break;
                    case "2":
                        user = reader.readLine();
                        pass = reader.readLine();
                        writer.println(String.format("LOGIN;%s;%s", user, pass));
                        writer.println();
                        break;
                }
                message = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
