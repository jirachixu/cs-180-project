import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ServerInterface extends Runnable {
    public static void main(String[] args) {
        // idk if im supposed to put anything here
    }
    public void createNewUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    public void login(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    public void logout(ObjectOutputStream outToUser);
    public void deleteProfile(ObjectInputStream inFromUser);
    public void editProfile(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    public void searchUsers(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    public void updateChats(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    public void sendMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    public void editMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
    public void deleteMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
}
