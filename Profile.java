import java.util.ArrayList;

/*
 * TODO: all the setter methods and addFriend, removeFriend, block, unblock, etc. have to communicate with the client
 *  and get info (the method parameters) from the user, also probably needs to be displayed on the GUI
 */
public class Profile {
    private final String username;
    private String password;
    private String displayName;
    private boolean receiveAll;
    private ArrayList<Profile> friends;
    private ArrayList<Profile> blocked;
    //TODO
    public Profile(String username, String password, String displayName, boolean receiveAll,
                   ArrayList<Profile> friends, ArrayList<Profile> blocked) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.receiveAll = receiveAll;
        this.friends = friends;
        this.blocked = blocked;
    }

    public Profile(String username) {
        this.username = username;
        this.password = null;
        this.displayName = null;
        this.receiveAll = false;
        this.friends = null;
        this.blocked = null;
    }

    public Profile() {
        this.username = null;
        this.password = null;
        this.displayName = null;
        this.receiveAll = false;
        this.friends = null;
        this.blocked = null;
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
        return friends.add(p);
    }

    public boolean removeFriend(Profile p) {
        return friends.remove(p);
    }

    public boolean block(Profile p) {
        return blocked.add(p);
    }

    public boolean unblock(Profile p) {
        return blocked.remove(p);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Profile && ((Profile) o).getUsername().equals(username);
    }
}
