import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database implements DatabaseInterface {
    private String profileIn; // File that profiles are read in from
    private String chatIn; // File that chats are read in from
    private String profileOut; // File that profiles are read out to
    private String chatOut; // File that chats are read out to
    private ArrayList<Profile> profiles = new ArrayList<>(); // ArrayList of profiles
    private HashMap<String, Chat> chats = new HashMap<>(); // HashMap (dictionary) of chats

    public Database(String profileIn, String chatIn, String profileOut, String chatOut) {
        this.profileIn = profileIn;
        this.chatIn = chatIn;
        this.profileOut = profileOut;
        this.chatOut = chatOut;
    }

    // For testing purposes
    public Database(String profileIn, String chatIn, String profileOut, String chatOut, ArrayList<Profile> profiles, HashMap<String, Chat> chats) {
        this.profileIn = profileIn;
        this.chatIn = chatIn;
        this.profileOut = profileOut;
        this.chatOut = chatOut;
        this.profiles = profiles;
        this.chats = chats;
    }

    // For testing purposes
    public ArrayList<Profile> getProfiles() {
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
                profiles = (ArrayList<Profile>) ois.readObject();
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

    public boolean login(String username, String password) {
        for (Profile profile : profiles) {
            if (profile.getUsername().equals(username) && profile.getPassword().equals(password)) {
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

    // I'm assuming that the default display name is the same as your username, then you can edit it as you wish
    public synchronized boolean createProfile(String username, String password) {
        Profile newProfile = new Profile(username, password, username, true,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        if (profiles.contains(newProfile)) {
            return false;
        }
        profiles.add(newProfile);
        return true;
    }

    public synchronized boolean editDisplayName(String username, String newDisplayName) {
        for (Profile p : profiles) {
            if (p.getUsername().equals(username)) {
                p.setDisplayName(newDisplayName);
                return true;
            }
        }
        return false;
    }

    // probably doesn't have to be synchronized because nobody's using your account password as you change it
    public boolean editPassword(String username, String newPassword) {
        for (Profile p : profiles) {
            if (p.getUsername().equals(username)) {
                p.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean editReceiveAll(String username, boolean newReceiveAll) {
        for (Profile p : profiles) {
            if (p.getUsername().equals(username)) {
                p.setReceiveAll(newReceiveAll);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteProfile(String username) {
       for (Profile p : profiles) {
           if (p.getUsername().equals(username)) {
               profiles.remove(p);
               return true;
           }
       }
       return false;
    }

    public synchronized ArrayList<Profile> findProfiles(String toFind) {
        ArrayList<Profile> searchResults = new ArrayList<>();
        String toFindIgnoreCase = toFind.toLowerCase();
        for (Profile p : profiles) {
            if (p.getUsername().toLowerCase().startsWith(toFindIgnoreCase)
                    || p.getDisplayName().toLowerCase().startsWith(toFindIgnoreCase)) {
                searchResults.add(p);
            }
        }
        return searchResults;
    }
}
