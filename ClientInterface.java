import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public interface ClientInterface extends Runnable {
    void createNewUser(String username, String password, String displayName, boolean receiveAll,
                       ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void login(String username, String password, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void blockUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void unblockUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void friendUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void unfriendUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void sendMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void deleteMessage(Scanner scan, ObjectOutputStream outToServer);
    void deleteProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editProfile(String input, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void updateChats(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    ArrayList<Profile> searchUsers(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void logout(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void viewProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
}
