public class Message {
    private final Profile sender;    // The profile that sent the message
    private String contents;    // The contents of the message
    private int status;    // Current status of message: 0 = unedited, 1 = edited, 2 = deleted
    private long timestamp;    // The time in which the message was sent

    public Message(Profile sender, String contents) {
        this.sender = sender;
        this.contents = contents;
        timestamp = System.currentTimeMillis();
    }

    public Profile getSender() {
        return sender;
    }

    public void edit(String contents) {    // Replace the contents of the old message with the new one
        if (status != 2) {    // Ensure message is not deleted
            this.contents = contents;
            status = 1;
            timestamp = System.currentTimeMillis();
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
        return String.format("Message<sender=%s,status=%d,timestamp=%d,contents=%s>",
                sender.toString(), status, timestamp, contents);
    }
}
