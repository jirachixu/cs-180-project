import java.io.*;
import java.util.Scanner;

public interface ClientInterface extends Runnable {
    void createNewUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void login(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    int blockUser(Profile profile);
    int unblockUser(Profile profile);
    int friendUser(Profile profile);
    int unfriendUser(Profile profile);
    String sendMessage(Message message);
    String editMessage(Message message, String newMessage);
    String deleteMessage(Message message);
    int deleteProfile();
    void editProfile(String newDisplayName);
}
