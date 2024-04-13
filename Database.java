import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team Project - Database
 *
 * This class is the gateway between the server
 * and the client. It takes in all the requests
 * from the client-side and communicates them
 * through the server-side by calling
 * the appropriate classes and methods.
 *
 * @author Jared, Ruiqi, Aneesh, Caasi (lab section 24)
 *
 * @version Mar 31, 2024
 *
 */

public class Database implements DatabaseInterface {
    private String profileIn; // File that profiles are read in from
    private String chatIn; // File that chats are read in from
    private String profileOut; // File that profiles are read out to
    private String chatOut; // File that chats are read out to
    private HashMap<String, Profile> profiles = new HashMap<>(); // HashMap of profiles
    private HashMap<String, Chat> chats = new HashMap<>(); // HashMap (dictionary) of chats

    public Database(String profileIn, String chatIn, String profileOut, String chatOut) {
        this.profileIn = profileIn;
        this.chatIn = chatIn;
        this.profileOut = profileOut;
        this.chatOut = chatOut;
    }

    // For testing purposes
    public Database(String profileIn, String chatIn, String profileOut, String chatOut,
                    HashMap<String, Profile> profiles, HashMap<String, Chat> chats) {
        this.profileIn = profileIn;
        this.chatIn = chatIn;
        this.profileOut = profileOut;
        this.chatOut = chatOut;
        this.profiles = profiles;
        this.chats = chats;
    }

    // For testing purposes
    public HashMap<String, Profile> getProfiles() {
        return profiles;
    }

    // For testing purposes
    public HashMap<String, Chat> getChats() {
        return chats;
    }

    // Reads in all the profiles from the profile file.
    // I'm assuming profiles are written in with ObjectInputStream.
    public boolean readProfile() {
        try {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(profileIn))) {
                profiles = (HashMap<String, Profile>) ois.readObject();
            } catch (ClassNotFoundException e) {
                return false;
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
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(chatIn))) {
                chats = (HashMap<String, Chat>) ois.readObject();
            } catch (ClassNotFoundException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean outputProfile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(profileOut))) {
            oos.writeObject(profiles);
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

    public void clearDatabase() {
        profiles = null;
        chats = null;
    }

    public boolean login(String username, String password) {
        return profiles.containsKey(username) && profiles.get(username).getPassword().equals(password);
    }

    public synchronized void sendMessage(Message message) {
        String key = message.getSender().getUsername() + message.getReceiver().getUsername(); // The key
        if (chats.containsKey(key)) {
            Chat chat = chats.get(key); // The chat to send the message to
            chat.sendMessage(message);
        } else {
            chats.put(key, new Chat(message));
        }
    }

    public synchronized void editMessage(Message message, String newContent) throws MessageError {
        String key = message.getSender().getUsername() + message.getReceiver().getUsername(); // The key
        Chat chat = chats.get(key); // The chat to edit a message in
        chat.editMessage(message, newContent);
    }

    public synchronized void deleteMessage(Message message) throws MessageError {
        String key = message.getSender().getUsername() + message.getReceiver().getUsername(); // The key
        Chat chat = chats.get(key); // Chat to delete a message in
        chat.deleteMessage(message);
    }

    public synchronized boolean createProfile(String username, String password,
                                              String displayName, boolean receiveAll) {
        Profile newProfile = new Profile(username, password, displayName, receiveAll,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()); // The new profile being created
        if (profiles.containsKey(username)) {
            return false;
        }
        profiles.put(username, newProfile);
        return true;
    }

    public synchronized boolean editDisplayName(String username, String newDisplayName) {
        if (profiles.containsKey(username)) {
            profiles.get(username).setDisplayName(newDisplayName);
            return true;
        }
        return false;
    }

    // probably doesn't have to be synchronized because nobody's using your account password as you change it
    public boolean editPassword(String username, String newPassword) {
        if (profiles.containsKey(username)) {
            profiles.get(username).setPassword(newPassword);
            return true;
        }
        return false;
    }

    public synchronized boolean editReceiveAll(String username, boolean newReceiveAll) {
        if (profiles.containsKey(username)) {
            profiles.get(username).setReceiveAll(newReceiveAll);
            return true;
        }
        return false;
    }

    public synchronized boolean deleteProfile(String username) {
        return profiles.remove(username) != null;
    }

    public synchronized ArrayList<Profile> findProfiles(String toFind) {
        ArrayList<Profile> searchResults = new ArrayList<>(); // Results of the search
        String toFindIgnoreCase = toFind.toLowerCase(); // The string to find in lowercase
        for (Profile p : profiles.values()) {
            if (p.getUsername().toLowerCase().startsWith(toFindIgnoreCase)
                    || p.getDisplayName().toLowerCase().startsWith(toFindIgnoreCase)) {
                searchResults.add(p);
            }
        }
        return searchResults;
    }
    public synchronized boolean usernameFree(String username) {
        return profiles.get(username) != null;
    }
}
