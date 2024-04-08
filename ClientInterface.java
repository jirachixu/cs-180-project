public interface ClientInterface {
    public void run();
    public void createNewUser();
    public void login();
    public int blockUser(Profile profile);
    public int unblockUser(Profile profile);
    public int friendUser(Profile profile);
    public int unfriendUser(Profile profile);
    public String sendMessage(Message message);
    public String editMessage(Message message, String newMessage);
    public String deleteMessage(Message message);
    public int deleteProfile();
    public void editProfile(String newDisplayName);
}
