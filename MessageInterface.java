import java.io.Serializable;

public interface MessageInterface extends Serializable {
    Profile getSender();
    Profile getReceiver();
    void edit(String contents) throws MessageError;
    void delete();
    String getContents();
    long getTimestamp();
    int getStatus();
    boolean equals(Object o);
}
