import java.io.Serializable;
import java.util.ArrayList;

/**
 * Team Project - Chat
 *
 * This class is where messages are sent for profiles
 * to interact with one another. It also has a timestamp
 * for later display purposes.
 *
 * @author Jared, Ruiqi, Aneesh, Caasi (lab section 24)
 *
 * @version Mar 31, 2024
 *
 */

public class Chat implements ChatInterface {
    private ArrayList<Profile> profiles;    // Users in the chat
    private ArrayList<Message> messages;    // All messages sent in the chat
    private long timestamp;    // Time of the last sent chat

    public Chat(Message message) {    // Creates message but only adds message if able to
        Profile sender = message.getSender();
        Profile receiver = message.getReceiver();
        profiles = new ArrayList<Profile>();
        profiles.add(message.getSender());
        profiles.add(message.getReceiver());
        messages = new ArrayList<Message>();
        timestamp = System.currentTimeMillis();

        if (receiver.isReceiveAll() || receiver.isFriends(sender)) {
            messages.add(message);
        }
    }

    public ArrayList<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Profile profile1, Profile profile2) {
        profiles.set(0, profile1);
        profiles.set(1, profile2);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Returns true if the profiles array contains both the sender and receiver
    public boolean matchesProfiles(Profile profile1, Profile profile2) {
        return profiles.contains(profile1) && profiles.contains(profile2);
    }

    public void sendMessage(Message message) {    // Add the message to the message array
        Profile sender = message.getSender(); // Sender of the message
        Profile receiver = message.getReceiver(); // Receiver of the message

        if (receiver.isReceiveAll() || receiver.isFriends(sender)) {
            messages.add(message);
            timestamp = System.currentTimeMillis();
        }
    }

    // Finds and edits a messages to change the contents of the method. Returns true if successful, returns false if the
    // message is not found. Message error thrown if message is deleted or if newContents are null/empty
    public void editMessage(Message message, String newContents) throws MessageError {
        try {
            int i = messages.indexOf(message);
            if (i < 0) {
                throw new MessageError("Message does not exist in Chat");
            } else {
                messages.get(i).edit(newContents);
            }
        } catch (MessageError e) {
            throw new MessageError(e.getMessage());
        }
    }

    public void deleteMessage(Message message) throws MessageError {    // Finds and deletes a message from a chat
        int i = messages.indexOf(message);
        if (i < 0) {
            throw new MessageError("Message does not exist in Chat");
        } else {
            messages.get(i).delete();
        }
    }
}
