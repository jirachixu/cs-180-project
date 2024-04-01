/**
 * Team Project - MessageError
 *
 * This class is an exception class for when
 * a message has an invalid sender/receiver or
 * when it cannot be edited or sent.
 *
 * @author Jared, Ruiqi, Aneesh, Caasi (lab section 24)
 *
 * @version Mar 31, 2024
 *
 */

public class MessageError extends Exception {
    public MessageError(String message) {
        super(message);
    }
}
