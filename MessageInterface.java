public interface MessageInterface {
    Profile getSender();
    Profile getReceiver();
    void edit(String contents) throws MessageError;
    void delete();
    String getContents();
    long getTimestamp();
    int getStatus();
    boolean equals(Object o);
}
