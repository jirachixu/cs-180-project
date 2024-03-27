import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable, ChatInterface {
    private final ArrayList<Profile> profiles;
    private ArrayList<Message> messages;
    private long timestamp;
    private static final Object messageSentinel = new Object();    // Prevents race conditions on accessing messages

    public Chat(Message message) {
        profiles = new ArrayList<Profile>();
        profiles.add(message.getSender());
        profiles.add(message.getReceiver());

        messages = new ArrayList<Message>();
        messages.add(message);

        timestamp = System.currentTimeMillis();
    }

    public ArrayList<Profile> getProfiles() {
        return profiles;
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
        synchronized (messageSentinel) {    // Ensure that one only message is added at a time to the array
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
