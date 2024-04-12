import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public interface ClientInterface {
    public void run();
    public void createNewUser(Scanner scan, BufferedReader br, PrintWriter pw);
    public void login(Scanner scan, PrintWriter pw, ObjectInputStream ois);
    public int blockUser(Profile profile);
    public int unblockUser(Profile profile);
    public int friendUser(Profile profile);
    public int unfriendUser(Profile profile);
    public String sendMessage(Message message);
    public String editMessage(Message message, String newMessage);
    public String deleteMessage(Message message);
    public int deleteProfile();
    public void editProfile(String newDisplayName);
}
