public interface MessageInterface {
    Profile getSender();
    Profile getReceiver();
    void edit(String contents) throws MessageError;
    void delete();
    String getContents();
    boolean equals(Object o);
}
