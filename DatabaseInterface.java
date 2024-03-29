public interface DatabaseInterface {
    public boolean readProfile();
    public boolean readChat();
    public boolean outputProfile();
    public boolean outputChat();
    public boolean login(String username, String password);
    public void sendMessage(Message message);
    public void editMessage(Message message, String newContent) throws MessageError;
    public void deleteMessage(Message message) throws MessageError;
    public void createUser(String username, String password, String displayName);
    public boolean deleteUser(String username);
    public boolean editUser(String username, String newDisplayName);
}
