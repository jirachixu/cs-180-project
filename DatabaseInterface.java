import java.util.ArrayList;

public interface DatabaseInterface {
    boolean readProfile();
    boolean readChat();
    boolean outputProfile();
    boolean outputChat();
    boolean login(String username, String password);
    void sendMessage(Message message);
    void editMessage(Message message, String newContent) throws MessageError;
    void deleteMessage(Message message) throws MessageError;
    boolean createProfile(String username, String password, String displayName, boolean receiveAll);
    boolean deleteProfile(String username);
    boolean editDisplayName(String username, String newDisplayName);
    boolean editPassword(String username, String newPassword);
    boolean editReceiveAll(String username, boolean newReceiveAll);
    Profile getProfile(String username);
    ArrayList<Chat> getUserChats(Profile profile);
    boolean blockUser(String blockerUsername, String blockeeUsername);
    boolean unblockUser(String unblockerUsername, String unblockeeUsername);
    boolean friendUser(String frienderUsername, String friendeeUsername);
    boolean unfriendUser(String unfrienderUsername, String unfriendeeUsername);
    boolean usernameFree(String username);
    ArrayList<Profile> findProfiles(String toFind);
    void clearDatabase();
}
