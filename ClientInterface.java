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
    void sendMessage(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editMessage(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void deleteMessage(ObjectOutputStream outToServer);
    void deleteProfile(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void editProfile(String input, ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void updateChats(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    ArrayList<Profile> searchUsers(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    void logout(ObjectInputStream inFromServer, ObjectOutputStream outToServer);
    Chat getCurrentChat();
    void frameInitialization();
    void initialPanel();
    void registerPanel();
    void loginPanel();
    JPanel userPanel();
    void getChatMessages();
    JPanel chatPanel();
    JList<String> updateChatDisplay();
    JPanel viewUserPanel(Profile user);
    void primaryPanel();
    void updateUserDisplay(ArrayList<Profile> profiles);
    JPanel editProfilePanel();
    
}
