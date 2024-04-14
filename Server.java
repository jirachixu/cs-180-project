import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server implements Runnable, ServerInterface {
    private final Socket socket;
    static Database database;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // Connect the object streams together
            ObjectOutputStream outToUser= new ObjectOutputStream(socket.getOutputStream());
            outToUser.flush();
            ObjectInputStream inFromUser = new ObjectInputStream(socket.getInputStream());

            loop :
            while (true) {
                Object command = inFromUser.readObject();
                switch ((String) command) {    // Select operation to perform
                    case "createNewUser" -> createNewUser(inFromUser, outToUser);
                    case "login" -> login(inFromUser, outToUser);
                    case "logout" -> logout(outToUser);
                    case "deleteProfile" -> deleteProfile(inFromUser);
                    case "editProfile" -> editProfile(inFromUser, outToUser);
                    case "searchUsers" -> searchUsers(inFromUser, outToUser);
                    case "sendMessage" -> sendMessage(inFromUser, outToUser);
                    case "editMessage" -> editMessage(inFromUser, outToUser);
                    case "deleteMessage" -> deleteMessage(inFromUser, outToUser);
                    case "updateChats" -> updateChats(inFromUser, outToUser);
                    case "blockUser" -> blockUser(inFromUser, outToUser);
                    case "unblockUser" -> unblockUser(inFromUser, outToUser);
                    case "friendUser" -> friendUser(inFromUser, outToUser);
                    case "unfriendUser" -> unfriendUser(inFromUser, outToUser);
                    case "exit" -> {break loop;}
                }
            }

            // Exit condition
            inFromUser.close();
            outToUser.close();

        } catch (Exception e) {
            System.out.println("Client disconnected");
        } finally {
            database.outputChat();
            database.outputProfile();
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Load database into server
        String profileIn;
        String chatsIn;
        String profileOut;
        String chatsOut;

        System.out.println("Would you like to load default files?");
        if (scan.nextLine().equalsIgnoreCase("yes")) {
            profileIn = "profileData.txt";
            chatsIn = "chatData.txt";
            profileOut = "profileData.txt";
            chatsOut = "chatData.txt";
        } else {
            System.out.println("Enter the name of the file from which to read profiles: ");
            profileIn = scan.nextLine();
            System.out.println("Enter the name of the file from which to read chats: ");
            chatsIn = scan.nextLine();
            System.out.println("Enter the name of the file to which to write profiles: ");
            profileOut = scan.nextLine();
            System.out.println("Enter the name of the file to which to write chats: ");
            chatsOut = scan.nextLine();
        }

        database = new Database(profileIn, chatsIn, profileOut, chatsOut);
        if (!database.readProfile()) {
            System.out.println("Server failed to read profiles. Starting new file of same name");
        } else if (!database.readChat()) {
            System.out.println("Server failed to read chats. Starting new file of same name");
        }

        // Open server socket
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            //noinspection InfiniteLoopStatement
            while (true) {    // Starts a new thread for every client
                System.out.println("Waiting for server connection");
                Socket socket = serverSocket.accept();
                System.out.println("Server connected");
                Server server = new Server(socket);
                new Thread(server).start();
            }
        } catch (Exception e) {
            System.out.println("Server socket failed to start");
        } finally {
            database.outputProfile();
            database.outputChat();
        }
    }

    public void createNewUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String username;
            String password;
            String display;
            boolean receiveAll;
            boolean loop;

            // Mirrors input for createNewUser in Client
            do {
                username = (String) inFromUser.readObject();
                loop = database.usernameFree(username);
                outToUser.writeBoolean(loop);
                outToUser.flush();
            } while(loop);


            password = (String) inFromUser.readObject();
            display = (String) inFromUser.readObject();
            receiveAll = inFromUser.readBoolean();

            database.createProfile(username, password, display, receiveAll);

            outToUser.writeUnshared(database.getProfile(username));
            outToUser.flush();

            System.out.println("Profile successfully created and sent to user!");

        } catch (Exception e) {
            System.out.println("Error occurred while creating account");
        } finally {
            database.outputProfile();
        }
    }

    public void login(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        // Mirrors input for login in Client
        try {
            String username = (String) inFromUser.readObject();
            String password = (String) inFromUser.readObject();

            if (database.login(username, password)){
                System.out.println("Sending profile " + username);
                outToUser.writeUnshared(database.getProfile(username));
            } else {
                System.out.println("Profile not found");
                outToUser.writeUnshared(null);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while logging in");
        }
    }

    public void logout(ObjectOutputStream outToUser) {
        // Mirrors input for logout in Client
        try {
            outToUser.writeUnshared(new Profile());
            outToUser.flush();
            System.out.println("Client logged out");

        } catch (Exception e) {
            System.out.println("Error occurred while logging out");
        }
    }

    public void deleteProfile(ObjectInputStream inFromUser) {
        // Mirrors input for deleteProfile in Client
        try {
            Profile profile = (Profile) inFromUser.readObject();
            database.deleteProfile(profile.getUsername());

            System.out.println("Profile " + profile.getUsername() + " deleted");
        } catch (Exception e) {
            System.out.println("Error occurred while deleting profile");
        } finally {
            database.outputProfile();
        }
    }

    public void editProfile(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        // Mirrors input for editProfile in Client
        try {
            String username = (String) inFromUser.readObject();
            String choice = (String) inFromUser.readObject();

            if (choice.equals("display")) {
                database.editDisplayName(username, (String) inFromUser.readObject());

                outToUser.reset();
                outToUser.writeUnshared(database.getProfile(username));
                outToUser.flush();

            } else if (choice.equalsIgnoreCase("password")) {
                database.editPassword(username, (String) inFromUser.readObject());

                outToUser.reset();
                outToUser.writeUnshared(database.getProfile(username));
                outToUser.flush();

            } else if (choice.equalsIgnoreCase("receiveAll")) {
                database.editReceiveAll(username, inFromUser.readBoolean());

                outToUser.reset();
                outToUser.writeUnshared(database.getProfile(username));
                outToUser.flush();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while deleting profile");
        } finally {
            database.outputProfile();
        }
    }

    public void searchUsers(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String query = (String) inFromUser.readObject();

            outToUser.reset();
            outToUser.writeUnshared(database.findProfiles(query));
            outToUser.flush();

        } catch (Exception e) {
            System.out.println("Error occurred while searching users");
        }
    }

    public void updateChats(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            Profile profile = (Profile) inFromUser.readObject();
            ArrayList<Chat> usersChats = database.getUserChats(profile);

            for(int i = usersChats.size() - 1; i >=0; i--) {
                Chat chat = usersChats.get(i);
                Profile receiver = chat.getProfiles().get(0);

                if (receiver.equals(profile)) {
                    receiver = chat.getProfiles().get(1);
                }

                if (profile.getBlocked().contains(receiver)) {
                    usersChats.remove(i);
                } else if (!profile.isReceiveAll() && !profile.getFriends().contains(receiver)) {
                    usersChats.remove(i);
                }
            }

            outToUser.reset();
            outToUser.writeUnshared(usersChats);
            outToUser.flush();

        } catch (Exception e) {
            System.out.println("Error occurred while updating chats");
        }
    }

    public void sendMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            Profile receiver;
            do {    // TODO: Should be unnecessary in phase 3 but needed now for testing
                receiver = database.getProfile((String) inFromUser.readObject());    // Search for receiver by username
                outToUser.writeUnshared(receiver);    // Send to sender
                outToUser.flush();
            } while (receiver == null);

            database.sendMessage((Message) inFromUser.readObject());    // Receives and sends the messages

        } catch (Exception e) {
            System.out.println("Error occurred while sending message");
        } finally {
            database.outputChat();
        }
    }

    public void editMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            Message toEdit = (Message) inFromUser.readObject();
            database.editMessage(toEdit, (String) inFromUser.readObject());
        } catch (Exception e) {
            System.out.println("Error occurred while editing message");
        } finally {
            database.outputChat();
        }
    }

    public void deleteMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            database.deleteMessage((Message) inFromUser.readObject());
        } catch (Exception e) {
            System.out.println("Error occurred while deleting message");
        } finally {
            database.outputChat();
        }
    }

    public void blockUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String blocker = (String) inFromUser.readObject();
            String toBlock = (String) inFromUser.readObject();
            database.blockUser(blocker, toBlock);

            outToUser.reset();
            outToUser.writeUnshared(database.getProfile(blocker));
            outToUser.flush();

        } catch (Exception e) {
            System.out.println("Error occurred while blocking profile");
        } finally {
            database.outputProfile();
        }
    }

    public void unblockUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String unblocker = (String) inFromUser.readObject();
            String toUnblock = (String) inFromUser.readObject();
            database.unblockUser(unblocker, toUnblock);

            outToUser.reset();
            outToUser.writeUnshared(database.getProfile(unblocker));
            outToUser.flush();

        } catch (Exception e) {
            System.out.println("Error occurred while unblocking message");
        } finally {
            database.outputProfile();
        }
    }

    public void friendUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String friender = (String) inFromUser.readObject();
            String toFriend = (String) inFromUser.readObject();
            database.friendUser(friender, toFriend);

            outToUser.reset();
            outToUser.writeUnshared(database.getProfile(friender));
            outToUser.flush();

        } catch (Exception e) {
            System.out.println("Error occurred while friending profile");
        } finally {
            database.outputProfile();
        }
    }

    public void unfriendUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) {
        try {
            String unfriender = (String) inFromUser.readObject();
            String toUnfriend = (String) inFromUser.readObject();
            database.unfriendUser(unfriender, toUnfriend);

            outToUser.reset();
            outToUser.writeUnshared(database.getProfile(unfriender));
            outToUser.flush();

        } catch (Exception e) {
            System.out.println("Error occurred while unfriending profile");
        } finally {
            database.outputProfile();
        }
    }
}
