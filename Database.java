import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Team Project - Database
 *
 * This class is the gateway between the server
 * and the client. It takes in all the requests
 * from the client-side and communicates them
 * through the server-side by calling
 * the appropriate classes and methods.
 *
 * @author Jared, Caasi, Ruiqi, Aneesh (lab section 24)
 *
 * @version April 14, 2024
 *
 */

public class Database implements DatabaseInterface {
    private String profileIn; // File that profiles are read in from
    private String chatIn; // File that chats are read in from
    private String profileOut; // File that profiles are read out to
    private String chatOut; // File that chats are read out to
    private HashMap<String, Profile> profiles = new HashMap<>(); // HashMap of profiles
    private HashMap<String, Chat> chats = new HashMap<>(); // HashMap (dictionary) of chats
    private final static Object gatekeeper = new Object();

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
        synchronized (gatekeeper) {
            return profiles;
        }
    }

    // For testing purposes
    public HashMap<String, Chat> getChats() {
        synchronized (gatekeeper) {
            return chats;
        }
    }

    // Reads in all the profiles from the profile file.
    // I'm assuming profiles are written in with ObjectInputStream.
    public boolean readProfile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(profileIn))) {
            synchronized (gatekeeper) {
                profiles = (HashMap<String, Profile>) ois.readObject();
                return true;
            }
        } catch (Exception e) {
            profiles = new HashMap<String, Profile>();
            return false;
        }
    }

    // Reads in all the chats from the chat file.
    // I'm assuming chats are written in with ObjectInputStream.
    public boolean readChat() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(chatIn))) {
            synchronized (gatekeeper) {
                chats = (HashMap<String, Chat>) ois.readObject();
                return true;
            }
        } catch (Exception e) {
            chats = new HashMap<String, Chat>();
            return false;
        }
    }

    public boolean outputProfile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(profileOut, false))) {
            synchronized (gatekeeper) {
                oos.writeObject(profiles);
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean outputChat() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chatOut, false))) {
            synchronized (gatekeeper) {
                oos.writeObject(chats);
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void clearDatabase() {
        synchronized (gatekeeper) {
            profiles = new HashMap<>();
            chats = new HashMap<>();
        }
    }

    public boolean login(String username, String password) {
        synchronized (gatekeeper) {
            return profiles.containsKey(username) && profiles.get(username).getPassword().equals(password);
        }
    }

    public void sendMessage(Message message) {
        String key1 = message.getSender().getUsername() + message.getReceiver().getUsername(); // The key
        String key2 = message.getReceiver().getUsername() + message.getSender().getUsername(); // The key

        synchronized (gatekeeper) {
            if (chats.containsKey(key1)) {
                Chat chat = chats.get(key1); // The chat to send the message to
                chat.sendMessage(message);
                chats.remove(key1);
                chats.put(key1, chat);
            }  else if (chats.containsKey(key2)) {
                Chat chat = chats.get(key2); // The chat to send the message to
                chat.sendMessage(message);
                chats.remove(key2);
                chats.put(key2, chat);
            } else {
                chats.put(key1, new Chat(message));
            }
        }
    }

    public void editMessage(Message message, String newContent) throws MessageError {
        String key1 = message.getSender().getUsername() + message.getReceiver().getUsername(); // The key
        String key2 = message.getReceiver().getUsername() + message.getSender().getUsername(); // The key
        synchronized (gatekeeper) {
            if (chats.containsKey(key1)) {
                Chat chat = chats.get(key1); // The chat to send the message to
                chat.editMessage(message, newContent);
                chats.remove(key1);
                chats.put(key1, chat);
            }  else if (chats.containsKey(key2)) {
                Chat chat = chats.get(key2); // The chat to send the message to
                chat.editMessage(message, newContent);
                chats.remove(key2);
                chats.put(key2, chat);
            }
        }
    }

    public void deleteMessage(Message message) throws MessageError {
        String key1 = message.getSender().getUsername() + message.getReceiver().getUsername(); // The key
        String key2 = message.getReceiver().getUsername() + message.getSender().getUsername(); // The key
        synchronized (gatekeeper) {
            if (chats.containsKey(key1)) {
                Chat chat = chats.get(key1); // The chat to send the message to
                chat.deleteMessage(message);
                chats.remove(key1);
                chats.put(key1, chat);
            }  else if (chats.containsKey(key2)) {
                Chat chat = chats.get(key2); // The chat to send the message to
                chat.deleteMessage(message);
                chats.remove(key2);
                chats.put(key2, chat);
            }
        }
    }

    public boolean createProfile(String username, String password, String displayName, boolean receiveAll) {
        Profile newProfile = new Profile(username, password, displayName, receiveAll); // The new profile being created
        synchronized (gatekeeper) {
            if (profiles.containsKey(username)) {
                return false;
            } else {
                profiles.put(username, newProfile);
                return true;
            }
        }
    }

    public boolean editDisplayName(String username, String newDisplayName) {
        synchronized (gatekeeper) {
            if (profiles.containsKey(username)) {
                profiles.get(username).setDisplayName(newDisplayName);
                return true;
            } else {
                return false;
            }
        }
    }

    // probably doesn't have to be synchronized because nobody's using your account password as you change it
    public boolean editPassword(String username, String newPassword) {
        synchronized (gatekeeper) {
            if (profiles.containsKey(username)) {
                profiles.get(username).setPassword(newPassword);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean editReceiveAll(String username, boolean newReceiveAll) {
        synchronized (gatekeeper) {
            if (profiles.containsKey(username)) {
                profiles.get(username).setReceiveAll(newReceiveAll);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean deleteProfile(String username) {
        synchronized (gatekeeper) {
            if (profiles.containsKey(username)) {
                // Using an iterator to be able to go over the hashmap without causing errors
                Iterator<Map.Entry<String, Chat>> iterator = chats.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Chat> entry = iterator.next();
                    String key = entry.getKey();
                    if (key.contains(username)) {
                        iterator.remove();
                    }
                }
                profiles.remove(username);
                return true;
            } else {
                return false;
            }
        }
    }

    public ArrayList<Profile> findProfiles(String toFind) {
        ArrayList<Profile> searchResults = new ArrayList<>(); // Results of the search
        String toFindIgnoreCase = toFind.toLowerCase(); // The string to find in lowercase
        synchronized (gatekeeper) {
            for (Profile p : profiles.values()) {
                if (p.getUsername().toLowerCase().startsWith(toFindIgnoreCase)
                        || p.getDisplayName().toLowerCase().startsWith(toFindIgnoreCase)) {
                    searchResults.add(p);
                }
            }
        }
        return searchResults;
    }

    public boolean usernameFree(String username) {
        synchronized (gatekeeper) {
            return profiles.get(username) == null;
        }
    }

    public Profile getProfile(String username) {
        synchronized (gatekeeper) {
            return profiles.get(username);
        }
    }

    public ArrayList<Chat> getUserChats(Profile profile) {
        ArrayList<Chat> userChats = new ArrayList<Chat>();
        synchronized (gatekeeper) {
            if (!chats.isEmpty()) {
                for (String key : chats.keySet()) {
                    if (key.contains(profile.getUsername())) {
                        Chat toSend = chats.get(key);
                        System.out.println("got one");

                        if (!toSend.getMessages().isEmpty()) {
                            Message lastMessage = toSend.getMessages().get(0);

                            // Ensure profiles are the most recent ones in the database for display purposes
                            toSend.setProfiles(profiles.get(lastMessage.getReceiver().getUsername()),
                                    profiles.get(lastMessage.getSender().getUsername()));

                            userChats.add(chats.get(key));
                        }
                    }
                }
            } else {
                return userChats;
            }
        }
        System.out.println(userChats.get(0).getProfiles().get(0).getUsername());
        System.out.println(userChats.get(0).getMessages().get(0));
        return userChats;
    }

    public boolean blockUser(String blockerUsername, String blockeeUsername) {
        synchronized (gatekeeper) {
            Profile blocker = profiles.get(blockerUsername);
            Profile blockee = profiles.get(blockeeUsername);

            if (blocker == null || blockee == null) {
                return false;
            } else {
                return blocker.block(blockee);
            }
        }
    }

    public boolean unblockUser(String unblockerUsername, String unblockeeUsername) {
        synchronized (gatekeeper) {
            Profile unblocker = profiles.get(unblockerUsername);
            Profile unblockee = profiles.get(unblockeeUsername);

            if (unblocker == null || unblockee == null) {
                return false;
            } else {
                return unblocker.unblock(unblockee);
            }
        }
    }

    public boolean friendUser(String frienderUsername, String friendeeUsername) {
        synchronized (gatekeeper) {
            Profile friender = profiles.get(frienderUsername);
            Profile friendee = profiles.get(friendeeUsername);

            if (friender == null || friendee == null) {
                return false;
            } else {
                return friender.addFriend(friendee);
            }
        }
    }

    public boolean unfriendUser(String unfrienderUsername, String unfriendeeUsername) {
        synchronized (gatekeeper) {
            Profile unfriender = profiles.get(unfrienderUsername);
            Profile unfriendee = profiles.get(unfriendeeUsername);

            if (unfriender == null || unfriendee == null) {
                return false;
            } else {
                return unfriender.removeFriend(unfriendee);
            }
        }
    }
}
