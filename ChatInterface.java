import java.io.Serializable;
import java.util.ArrayList;

public interface ChatInterface extends Serializable {
    ArrayList<Profile> getProfiles();
    ArrayList<Message> getMessages();
    long getTimestamp();
    boolean matchesProfiles(Profile profile1, Profile profile2);
    void sendMessage(Message message);
    void editMessage(Message message, String newContents) throws MessageError;
    void deleteMessage(Message message)  throws MessageError;
}
