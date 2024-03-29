import java.io.Serializable;

public class Message implements Serializable, MessageInterface {
    private final Profile sender;    // The profile that sent the message
    private final Profile receiver;    // The profile that receives the message
    private String contents;    // The contents of the message
    private int status;    // Current status of message: 0 = unedited, 1 = edited, 2 = deleted
    private long timestamp;    // The time in which the message was sent

    public Message(Profile sender, Profile receiver, String contents) throws ProfileError, MessageError {
        if (sender == null) {    // Checks to be done if ProfileError
            throw new ProfileError("Message has no sender");
        }

        if (receiver == null) {    // Checks to be done if ProfileError
            throw new ProfileError("Message has no receiver");
        }

        if (contents == null || contents.isEmpty()) {
            throw new MessageError("Message contents cannot be empty");
        }

        this.receiver = receiver;
        this.sender = sender;
        this.contents = contents;

        status = 0;
        timestamp = System.currentTimeMillis();
    }

    public Profile getSender() {
        return sender;
    }

    public Profile getReceiver() {
        return receiver;
    }

    public long getTimestamp() { return timestamp; }

    public int getStatus() { return status; }

    public void edit(String contents) throws MessageError {    // Replace the contents of the old message with the new one
        if (status != 2) {    // Ensure message is not deleted
            if (contents == null || contents.isEmpty()) {
                throw new MessageError("Message contents cannot be empty");
            }

            this.contents = contents;
            status = 1;
            timestamp = System.currentTimeMillis();
        } else {
            throw new MessageError("Deleted messages cannot be edited");
        }
    }

    public void delete() {    // Replace the contents of the old message with the new one
        this.contents = null;
        status = 2;
        timestamp = System.currentTimeMillis();
    }

    public String getContents() {    // Converts message to displayable format
        return contents;
    }
    
    @Override
    public String toString() {    // Converts message to storable format
        return String.format("Message<sender=%s,receiver=%s,status=%d,timestamp=%d,contents=%s>",
                sender.toString(), receiver.toString(), status, timestamp, contents);
    }

    @Override
    public boolean equals(Object o) {    // Returns true only if all fields match
        return o instanceof Message &&
                ((Message) o).getSender().equals(sender) &&
                ((Message) o).getReceiver().equals(receiver) &&
                ((Message) o).status == this.status &&
                ((Message) o).timestamp == this.timestamp &&
                ((Message) o).getContents().equals(contents);
    }
}
