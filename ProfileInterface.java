import java.util.ArrayList;

public interface ProfileInterface {
    public String getUsername();
    public String getPassword();
    public void setPassword(String password);
    public String getDisplayName();
    public void setDisplayName(String displayName);
    public boolean isReceiveAll();
    public void setReceiveAll(boolean receiveAll);
    public ArrayList<Profile> getFriends();
    public void setFriends(ArrayList<Profile> friends);
    public ArrayList<Profile> getBlocked();
    public void setBlocked(ArrayList<Profile> blocked);
    public boolean addFriend(Profile p);
    public boolean removeFriend(Profile p);
    public boolean block(Profile p);
    public boolean unblock(Profile p);
    public boolean equals(Object o);
}
