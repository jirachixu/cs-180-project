import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ServerInterface extends Runnable {
    void createNewUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void login(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void logout(ObjectOutputStream outToUser);
    void deleteProfile(ObjectInputStream inFromUser);
    void editProfile(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void searchUsers(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void updateChats(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void sendMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void editMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void deleteMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void blockUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void unblockUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void friendUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    void unfriendUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
}
