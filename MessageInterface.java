public interface MessageInterface {
    public Profile getSender();
    public Profile getReceiver();
    public void edit(String contents) throws MessageError;
    public void delete();
    public String getContents();
    public boolean equals(Object o);
}
