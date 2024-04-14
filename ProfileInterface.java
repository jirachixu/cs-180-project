import java.util.ArrayList;

public interface ProfileInterface {
    String getUsername();
    String getPassword();
    void setPassword(String password);
    String getDisplayName();
    void setDisplayName(String displayName);
    boolean isReceiveAll();
    boolean isFriends(Profile profile);
    void setReceiveAll(boolean receiveAll);
    ArrayList<Profile> getFriends();
    void setFriends(ArrayList<Profile> friends);
    ArrayList<Profile> getBlocked();
    void setBlocked(ArrayList<Profile> blocked);
    boolean addFriend(Profile p);
    boolean removeFriend(Profile p);
    boolean block(Profile p);
    boolean unblock(Profile p);
    boolean equals(Object o);
}
