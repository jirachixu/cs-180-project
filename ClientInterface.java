import java.io.*;
import java.util.Scanner;

public interface ClientInterface extends Runnable {
    void createNewUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void login(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void blockUser(Scanner scan, ObjectOutputStream outToServer);
    int unblockUser(Profile profile);
    int friendUser(Profile profile);
    int unfriendUser(Profile profile);
    void sendMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void deleteMessage(Scanner scan, ObjectOutputStream outToServer);
    void deleteProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
}
