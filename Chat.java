import java.util.ArrayList;
import java.util.Arrays;

public class Chat {
    private ArrayList<Profile> profiles;
    private ArrayList<Message> messages;
    private long timestamp;
    private static final Object messageSentinel = new Object();    // Prevents race conditions on accessing messages

    public Chat(ArrayList<Profile> profiles) {
        this.profiles = profiles;
        messages = new ArrayList<>();
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

    public boolean sendMessage(Message message) {    // Add the message to the message array
        if (profiles.contains(message.getSender())) {    // Ensure that the chat includes the sender
            synchronized (messageSentinel) {    // Ensure that one only message is added at a time to the array
                messages.add(message);
            }
            timestamp = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    // Finds and edits a messages to change the contents of the method. Returns true if successful, returns false if the
    // message is not found. Message error thrown if message is deleted or if newContents are null/empty
    public boolean editMessage(Message message, String newContents) throws MessageError {
        try {
            int i = messages.indexOf(message);
            if (i < 0) {
                return false;
            } else {
                messages.get(i).edit(newContents);
                return true;
            }
        } catch (MessageError e) {
            throw new MessageError(e.getMessage());
        }
    }

    public boolean deleteMessage(Message message) {    // Finds and deletes a message from a chat
        int i = messages.indexOf(message);
        if (i < 0) {
            return false;
        } else {
            messages.get(i).delete();
            return true;
        }
    }
}
