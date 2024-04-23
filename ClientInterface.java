import java.io.*;
import java.util.Scanner;

public interface ClientInterface extends Runnable {
    void createNewUser(String username, String password, String displayName, boolean receiveAll,
                       ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void login(String username, String password, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void blockUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void unblockUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void friendUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void unfriendUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void sendMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void deleteMessage(Scanner scan, ObjectOutputStream outToServer);
    void deleteProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void updateChats(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void searchUsers(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void logout(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void viewProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
}
