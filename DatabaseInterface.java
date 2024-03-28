public interface DatabaseInterface {
    public boolean readProfile();
    public boolean readChat();
    public boolean outputProfile();
    public boolean outputChat();
    public boolean login(String username, String password);
    public void sendMessage(Message message);
    public void editMessage(Message message, String newContent) throws MessageError;
    public void deleteMessage(Message message) throws MessageError;
}
