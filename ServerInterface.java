import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ServerInterface extends Runnable {  // TODO
    void createNewUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser);
}
