import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database implements DatabaseInterface {
    private String profileIn; // File that profiles are read in from
    private String chatIn; // File that chats are read in from
    private String profileOut; // File that profiles are read out to
    private String chatOut; // File that chats are read out to
    private ArrayList<Profile> profiles; // ArrayList of profiles
    private HashMap<String, Chat> chats; // HashMap (dictionary) of chats

    public Database(String profileIn, String chatIn, String profileOut, String chatOut) {
        this.profileIn = profileIn;
        this.chatIn = chatIn;
        this.profileOut = profileOut;
        this.chatOut = chatOut;
    }

    // Reads in all the profiles from the profile file.
    // I'm assuming profiles are written in with ObjectInputStream.
    public boolean readProfile() {
        try {
            boolean more = true;
            while (more) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(profileIn))) {
                    Profile prof = (Profile) ois.readObject();
                    if (prof != null) {
                        profiles.add(prof);
                    } else {
                        more = false;
                    }
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    // Reads in all the chats from the chat file.
    // I'm assuming chats are written in with ObjectInputStream.
    public boolean readChat() {
        try {
            boolean more = true;
            while (more) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(chatIn))) {
                    chats = (HashMap<String, Chat>) ois.readObject();
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean outputProfile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(profileOut))) {
            for (int i = 0; i < profiles.size(); i++) {
                oos.writeObject(profiles.get(i));
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean outputChat() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chatOut))) {
            oos.writeObject(chats);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean login(String username, String password) {
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getUsername().equals(username) && profiles.get(i).getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendMessage(Message message) {
        String key = message.getSender().getUsername() + message.getReceiver().getUsername();
        if (chats.containsKey(key)) {
            Chat chat = chats.get(key);
            chat.sendMessage(message);
        } else {
            chats.put(key, new Chat(message));
        }
    }

    public synchronized void editMessage(Message message, String newContent) throws MessageError {
        String key = message.getSender().getUsername() + message.getReceiver().getUsername();
        Chat chat = chats.get(key);
        chat.editMessage(message, newContent);
    }

    public synchronized void deleteMessage(Message message) throws MessageError {
        String key = message.getSender().getUsername() + message.getReceiver().getUsername();
        Chat chat = chats.get(key);
        chat.deleteMessage(message);
    }
}
