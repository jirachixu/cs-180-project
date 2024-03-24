import java.util.ArrayList;
import java.util.Arrays;

public class Chat {
    private Profile[] profiles;
    private ArrayList<Message> messages;
    private long timestamp;
    private static final Object messageSentinel = new Object();    // Prevents race conditions on accessing messages

    public Chat(Profile[] profiles) {
        this.profiles = profiles;
        messages = new ArrayList<>();
        timestamp = System.currentTimeMillis();
    }

    public Profile[] getProfiles() {
        return profiles;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean sendMessage(Message message) {    // Add the message to the message array
        if (Arrays.asList(profiles).contains(message.getSender())) {    // Ensure that the chat includes the sender
            synchronized (messageSentinel) {
                messages.add(message);
            }
            timestamp = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }



}
