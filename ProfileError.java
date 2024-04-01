/**
 * Team Project - Database
 *
 * This class is an exception class for
 * when a Profile does not exist and tries
 * to send a message.
 *
 * @author Jared, Ruiqi, Aneesh, Caasi (lab section 24)
 *
 * @version Mar 31, 2024
 *
 */

public class ProfileError extends Exception {
    public ProfileError(String message) {
        super(message);
    }
}
