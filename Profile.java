import java.io.Serializable;
import java.util.ArrayList;

/**
 * Team Project - Profile
 *
 * This class is the user on the server-side, and holds
 * all of their information. This includes their
 * credentials and also who can message them,
 * who they are friends with, who they have blocked,
 * and who has requested to be their friend.
 *
 * @author Jared, Caasi, Ruiqi, Aneesh (lab section 24)
 *
 * @version Mar 31, 2024
 *
 */

public class Profile implements Serializable, ProfileInterface {
    private final String username; // The username of the profile
    private String password; // The password of the profile
    private String displayName; // The display name of the profile (this can be changed)
    private boolean receiveAll; // Whether they can receive messages from everyone, or just friends
    private ArrayList<Profile> friends; // Their friends
    private ArrayList<Profile> blocked; // Who they have blocked (cannot send or receive messages)
    private ArrayList<Profile> requests; // Who has requested to be their friend

    public Profile(String username, String password, String displayName, boolean receiveAll,
                   ArrayList<Profile> friends, ArrayList<Profile> blocked, ArrayList<Profile> requests) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.receiveAll = receiveAll;
        this.friends = friends;
        this.blocked = blocked;
        this.requests = requests;
    }

    public Profile(String username, String password, String displayName, boolean receiveAll) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.receiveAll = receiveAll;
        this.friends = new ArrayList<Profile>();
        this.blocked = new ArrayList<Profile>();
        this.requests = new ArrayList<Profile>();
    }

    public Profile(String username) {
        this.username = username;
        this.password = null;
        this.displayName = null;
        this.receiveAll = false;
        this.friends = null;
        this.blocked = null;
        this.requests = null;
    }

    public Profile() {
        this.username = null;
        this.password = null;
        this.displayName = null;
        this.receiveAll = false;
        this.friends = null;
        this.blocked = null;
        this.requests = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isReceiveAll() {
        return receiveAll;
    }

    public boolean isFriends(Profile profile) {
        return friends.contains(profile);
    }

    public void setReceiveAll(boolean receiveAll) {
        this.receiveAll = receiveAll;
    }

    public ArrayList<Profile> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Profile> friends) {
        this.friends = friends;
    }

    public ArrayList<Profile> getBlocked() {
        return blocked;
    }

    public void setBlocked(ArrayList<Profile> blocked) {
        this.blocked = blocked;
    }

    public boolean addFriend(Profile p) {
        if (blocked.contains(p)) {
            return false;
        } if (friends.contains(p)) {
            return false;
        }
        return friends.add(p);
    }

    public boolean removeFriend(Profile p) {
        if (!friends.contains(p)) {
            return false;
        }
        return friends.remove(p);
    }

    public boolean block(Profile p) {
        if (blocked.contains(p)) {
            return false;
        }
        removeFriend(p);
        return blocked.add(p);
    }

    public boolean unblock(Profile p) {
        if (!blocked.contains(p)) {
            return false;
        }
        return blocked.remove(p);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Profile && ((Profile) o).getUsername().equals(username);
    }

    public String toString() {
        return null;    //TODO : This was preventing from running stuff
    }
}
