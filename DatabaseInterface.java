public interface DatabaseInterface {
    public boolean readProfile();
    public boolean readChat();
    public boolean outputProfile();
    public boolean outputChat();
    public boolean login(String username, String password);
    public void sendMessage(Message message);
    public void editMessage(Message message, String newContent) throws MessageError;
    public void deleteMessage(Message message) throws MessageError;
    public boolean createProfile(String username, String password, String displayName, boolean receiveAll);
    public boolean deleteProfile(String username);
    public boolean editDisplayName(String username, String newDisplayName);
    public boolean editPassword(String username, String newPassword);
    public boolean editReceiveAll(String username, boolean newReceiveAll);
}
