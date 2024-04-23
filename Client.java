import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Team Project - Client
 *
 * This class is the class that interacts with the user. All methods are connected directly into methods within Server
 * in order to handle network IO.
 *
 * @author Jared, Caasi, Ruiqi, Aneesh (lab section 24)
 *
 * @version April 14, 2024
 *
 */
public class Client implements ClientInterface {
    // Object specific to client
    Profile profile;
    ArrayList<Chat> chats;

    // GUI objects
    // Display objects
    JFrame frame;
    JTextArea userMessages;

    // User Inputs
    JButton logoutButton;
    JButton sendButton;
    JButton editButton;
    JButton deleteButton;
    JButton loginButton;
    JButton registerButton;
    JButton loginEnterButton;
    JButton registerEnterButton;
    JCheckBox receiveAll;
    JButton editProfileButton;
    JButton chatButton;
    JButton viewButton;
    JComboBox userDisplaySelection;
    DefaultListModel<String> displayList;
    JTextField messageText;
    JTextField usernameField;
    JPasswordField passwordField;
    JTextField displayNameField;

    // Network IO stuff
    ObjectInputStream inFromServer;
    ObjectOutputStream outToServer;
    Scanner scan; // FIXME: Placeholder
    ArrayList<Profile> searchResults = new ArrayList<>() {};    // FIXME: Temporary

    public Client() {
        profile = null;
        chats = null;

        frameInitialization();
        initialPanel();
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sendButton) {
                sendMessage(scan, inFromServer, outToServer);
            }

            if (e.getSource() == editButton) {
                editMessage(scan, inFromServer, outToServer);
            }

            if (e.getSource() == deleteButton) {
                deleteMessage(scan, outToServer);
            }

            if (e.getSource() == loginButton) {
                loginPanel();
            }

            if (e.getSource() == logoutButton) {
                logout(inFromServer, outToServer);
                initialPanel();
            }

            if (e.getSource() == registerButton) {
                registerPanel();
            }

            if (e.getSource() == loginEnterButton) {
                if (!(usernameField.getText() == null && !(passwordField.getPassword() == null))) {
                    if (!usernameField.getText().isEmpty() && !(passwordField.getPassword().length < 1)) {
                        login(usernameField.getText(), new String(passwordField.getPassword()), inFromServer, outToServer);
                        return;
                    }
                }

                JOptionPane.showMessageDialog(frame,
                        "Please enter a valid username/password",
                        "Invalid Username/Password", JOptionPane.ERROR_MESSAGE);

            }

            if (e.getSource() == registerEnterButton) {
                if (!usernameField.getText().isEmpty() && !(passwordField.getPassword().length < 1)
                        && !(usernameField.getText() == null && !(passwordField.getPassword() == null))
                        && !displayNameField.getText().isEmpty()) {
                    if (checkValidPassword(new String(passwordField.getPassword()))) {
                        createNewUser(usernameField.getText(), new String(passwordField.getPassword()),
                                displayNameField.getText(), receiveAll.isSelected(), inFromServer, outToServer);

                    } else {// TODO: Handle cases that username does not exist
                        JOptionPane.showMessageDialog(frame,
                                "Invalid Password!",
                                "Invalid Username/Password", JOptionPane.ERROR_MESSAGE);
                    }
                    if (profile != null) {
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Username already exists!",
                                "Invalid Username/Password", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    // TODO: Handle cases that username does not exist
                    JOptionPane.showMessageDialog(frame,
                            "Username/Password/Display Name cannot be empty",
                            "Invalid Username/Password/Display Name", JOptionPane.ERROR_MESSAGE);
                    frame.dispose();
                }
            }

            if (e.getSource() == userDisplaySelection) {
                ArrayList<Profile> displayProfiles = null;
                switch ((String) userDisplaySelection.getSelectedItem()) {
                    case "Friends" -> displayProfiles = profile.getFriends();
                    case "Blocked" -> displayProfiles = profile.getBlocked();
                    case "Search" -> displayProfiles = searchUsers(scan, inFromServer, outToServer);
                }

                updateUserDisplay(displayProfiles);

                frame.revalidate();
                frame.repaint();

                System.out.println(userDisplaySelection.getSelectedItem());
            }
        }
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Client());
    }

    public void run() {
        scan = new Scanner(System.in);    // TODO: Replace with GUI
        SwingWorker<Void, Void> connectionWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Socket socket = new Socket("localhost", 8080)) {    // Network connection
                    // Setup network connection
                    inFromServer = new ObjectInputStream(socket.getInputStream());
                    outToServer = new ObjectOutputStream(socket.getOutputStream());
                    outToServer.flush();
                    profile = new Profile();

                    loop:
                    while (true) {
                        while (profile.getUsername() == null) {    // Loop while account is still empty
                            // loginOrRegister();
                        }

                        System.out.println("Enter action:");    // TODO GUI
                        switch (scan.nextLine()) {    // TODO GUI: Action listeners and buttons rather than a switch
                            // TODO: Replace with appropriate method calls
                            case "sendMessage" -> sendMessage(scan, inFromServer, outToServer);
                            case "editMessage" -> editMessage(scan, inFromServer, outToServer);
                            case "deleteMessage" -> deleteMessage(scan, outToServer);
                            case "logout" -> logout(inFromServer, outToServer);
                            case "searchUsers" -> searchUsers(scan, inFromServer, outToServer);
                            case "blockUser" -> blockUser(scan, inFromServer, outToServer);
                            case "unblockUser" -> unblockUser(scan, inFromServer, outToServer);
                            case "friendUser" -> friendUser(scan, inFromServer, outToServer);
                            case "unfriendUser" -> unfriendUser(scan, inFromServer, outToServer);
                            case "editProfile" -> editProfile(scan, inFromServer, outToServer);
                            case "deleteProfile" -> deleteProfile(scan, inFromServer, outToServer);
                            case "viewProfile" -> viewProfile(scan, inFromServer, outToServer);
                            case "exit" -> {
                                break loop;
                            }
                            default -> updateChats(inFromServer, outToServer);
                        }
                    }

                    // Exit process
                    outToServer.writeUnshared("exit");
                    outToServer.flush();
                    outToServer.close();
                    inFromServer.close();

                } catch (IOException e) {
                    System.out.println("Failed to connect to server");    // TODO GUI: Fail to connect error
                }
                return null;
            }
        };
        connectionWorker.execute();
    }

    // Backend functionality methods
    public void createNewUser(String username, String password, String display, boolean receiveAll,
                              ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("createNewUser");
            outToServer.flush();

            boolean loop = true;
            // Get and validate username with server
            try {
                outToServer.reset();
                outToServer.writeUnshared(username);
                outToServer.flush();

                loop = inFromServer.readBoolean();
                if (loop) {
                    return;
                }
            } catch (IOException e) {    // If socket is lost exit method
                return;
            }

            try {
                outToServer.writeUnshared(password);
                outToServer.flush();

            } catch (IOException e) {    // If socket is lost exit method
                return;
            }

            // Get user display name
            outToServer.writeUnshared(display);
            outToServer.flush();

            // Get user receive all preference
            outToServer.writeBoolean(receiveAll);
            outToServer.flush();

            this.profile = (Profile) inFromServer.readObject();
        } catch (Exception e) {
            System.out.println("An error occurred while trying to create an account");
        }
    }

    public void logout(ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("logout");
            outToServer.flush();

            this.profile = (Profile) inFromServer.readObject();
        } catch (Exception e) {
            System.out.println("Failed to Logout");
        }
    }

    private static boolean checkValidPassword(String password) {    // Helper method to check if a password is valid
        boolean uppercase = false;
        boolean lowercase = false;
        boolean number = false;

        if (password.length() >= 8) {
            for (char character : password.toCharArray()) {
                if (Character.isDigit(character)) {
                    number = true;
                } else if (Character.isUpperCase(character)) {
                    uppercase = true;
                } else if (Character.isLowerCase(character)) {
                    lowercase = true;
                }
            }
        } else {
            return false;
        }
        return uppercase && lowercase && number;
    }

    public void login(String username, String password, ObjectInputStream inFromServer, ObjectOutputStream outToServer) { // Log in
        try {
            outToServer.writeUnshared("login");
            outToServer.flush();

            outToServer.writeUnshared(username);
            outToServer.flush();

            outToServer.writeUnshared(password);
            outToServer.flush();

            Object o = inFromServer.readObject();

            if (o != null) {
                profile = (Profile) o;
                primaryPanel();

            } else {
               JOptionPane.showMessageDialog(frame,
                       "Username or password is not correct",
                       "Boiler Chat", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while trying to login",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        System.out.println("Are you sure you want to delete this account?");
        if (scan.nextLine().equalsIgnoreCase("yes")) {
            try {
                outToServer.writeUnshared("deleteProfile");
                outToServer.flush();

                outToServer.writeUnshared(profile);
                outToServer.flush();

                logout(inFromServer, outToServer);
            } catch (Exception e) {
                System.out.println("Failed to delete account");
            }
        }
    }

    public void editProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("editProfile");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("What would you like to change: Display Name? Password? Receive All?");
            String input = scan.nextLine();

            if (input.equalsIgnoreCase("Display Name")) {
                outToServer.writeUnshared("display");
                outToServer.flush();

                String newDisplay;
                do {    // Get user input for new username
                    System.out.println("What you you liked to be called?");
                    newDisplay = scan.nextLine();
                } while (newDisplay.isEmpty());

                outToServer.writeUnshared(newDisplay);
                outToServer.flush();

                profile = (Profile) inFromServer.readObject();

            } else if (input.equalsIgnoreCase("Password")) {
                outToServer.writeUnshared("password");
                outToServer.flush();

                String newPassword;

                do {     // Enter old password before editing password
                    System.out.println("Enter your current password");
                } while (!scan.nextLine().equals(profile.getPassword()));

                do {     // Check valid password
                    System.out.println("Enter your desired password");
                    newPassword = scan.nextLine();

                    System.out.println("Enter your desired password again");
                } while (!scan.nextLine().equals(newPassword) || !checkValidPassword(newPassword));

                outToServer.writeUnshared(newPassword);
                outToServer.flush();

                profile = (Profile) inFromServer.readObject();
            } else if (input.equalsIgnoreCase("Receive All")) {
                outToServer.writeUnshared("receiveAll");
                outToServer.flush();

                System.out.println("Would you like to receive messages from everyone (true/false)?");
                outToServer.writeBoolean(Boolean.parseBoolean(scan.nextLine()));
                outToServer.flush();

                profile = (Profile) inFromServer.readObject();
            }
        } catch (Exception e) {
            System.out.println("Failed to edit account");
        }
    }

    public void updateChats(ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("updateChats");
            outToServer.flush();

            outToServer.writeUnshared(profile);
            outToServer.flush();

            chats = (ArrayList<Chat>) inFromServer.readObject();

            int j = 0;
            for (Chat chat : chats) {
                int i = 0;
                System.out.println("Chat " + j++);
                for (Message message : chat.getMessages()) {
                    String fromTag;
                    // Ensure message has the most up-to-date display information
                    if (message.getSender().getUsername().equals(chat.getProfiles().get(0).getUsername())) {
                        fromTag = chat.getProfiles().get(0).getDisplayName();
                    } else {
                        fromTag = chat.getProfiles().get(1).getDisplayName();
                    }

                    System.out.println("\t" + i++ + " - " + fromTag + ": " + message.getContents());
                }

                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("An error occurred when updating chats");
        }
    }

    public ArrayList<Profile> searchUsers(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("searchUsers");
            outToServer.flush();

//            System.out.println("Enter your search query:");
//            String query;
//            do {
//                query = scan.nextLine();
//            } while(query.isEmpty());
//
//            outToServer.writeUnshared(query);
            outToServer.writeUnshared("u");
            outToServer.flush();

            ArrayList<Profile> results = (ArrayList<Profile>) inFromServer.readObject();

            for (Profile option : results) {
                System.out.println(option.getDisplayName() + "-" + option.getUsername());
            }

            return results;

        } catch (Exception e) {
            System.out.println("An error occurred while searching for users");
            return new ArrayList<Profile>();
        }
    }

    public void blockUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("blockUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to block?");
            String toBlock;
            do {
                toBlock = scan.nextLine();
            } while(toBlock.isEmpty());

            outToServer.writeUnshared(toBlock);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while blocking user");
        }
    }

    public void unblockUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("unblockUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to unblock?");
            String toUnblock;
            do {
                toUnblock = scan.nextLine();
            } while(toUnblock.isEmpty());

            outToServer.writeUnshared(toUnblock);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while unblocking user");
        }
    }

    public void friendUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("friendUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to friend?");
            String toFriend;
            do {
                toFriend = scan.nextLine();
            } while(toFriend.isEmpty());

            outToServer.writeUnshared(toFriend);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while friending user");
        }
    }

    public void unfriendUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("unfriendUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            System.out.println("Who would you like to unfriend?");
            String toUnfriend;
            do {
                toUnfriend = scan.nextLine();
            } while(toUnfriend.isEmpty());

            outToServer.writeUnshared(toUnfriend);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            System.out.println("An error occurred while unfriending user");
        }
    }

    public void sendMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    outToServer.writeUnshared("sendMessage");
                    outToServer.flush();
                    Profile receiver;

                    do {
                        String personToSend = JOptionPane.showInputDialog(null, "Who would you like to send a message to?", "Send Message", JOptionPane.QUESTION_MESSAGE);
                        outToServer.writeUnshared(personToSend);    // Send profile name to server
                        outToServer.flush();

                        receiver = (Profile) inFromServer.readObject();
                    } while (receiver == null);

                    String contents;
                    do {
                        contents = messageText.getText(); // Get message contents
                    } while (contents.isEmpty());

                    // Send the message to the server
                    outToServer.writeUnshared(new Message(profile, receiver, contents));
                    outToServer.flush();

                } catch (Exception e) {
                    System.out.println("An error occurred when sending the message");
                }
                return null;
            }

            protected void done() {
                userMessages.append(messageText.getText() + "\n");
            }
        };
        worker.execute();
    }

    public void editMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("editMessage");
            outToServer.flush();

            // TODO GUI
            Message toEdit;
            do {    // Get a valid method from chats
                int chatIndex;
                do {
                    System.out.println("Enter chat index");
                    try {
                        chatIndex = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                int messageIndex;
                do {
                    System.out.println("Enter message index");
                    try {
                        messageIndex = Integer.parseInt(scan.nextLine());

                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                try {
                    toEdit = chats.get(chatIndex).getMessages().get(messageIndex);

                    if (!toEdit.getSender().equals(profile)) {
                        System.out.println("You can only edit messages you sent!");
                        toEdit = null;
                    }

                } catch (Exception e) {
                    System.out.println("Indices must be within bounds");
                    toEdit = null;
                }
            } while (toEdit == null);

            System.out.println("Old message: " + toEdit.getContents());

            outToServer.reset();
            outToServer.writeUnshared(toEdit);
            outToServer.flush();

            System.out.println("What would you like to edit the message to?");
            String contents;
            do {
                contents = scan.nextLine();
            } while (contents.isEmpty());

            outToServer.writeUnshared(contents);
            outToServer.flush();

        } catch (Exception e) {
            System.out.println("An error occurred when editing the message");
        }
    }

    public void deleteMessage(Scanner scan, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("deleteMessage");
            outToServer.flush();

            // TODO GUI
            Message toDelete;
            do {    // Get a valid method from chats
                int chatIndex;
                do {
                    System.out.println("Enter chat index");
                    try {
                        chatIndex = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                int messageIndex;
                do {
                    System.out.println("Enter message index");
                    try {
                        messageIndex = Integer.parseInt(scan.nextLine());

                        break;
                    } catch (Exception e) {
                        System.out.println("Chat index must be an integer");
                    }
                } while (true);

                try {
                    toDelete = chats.get(chatIndex).getMessages().get(messageIndex);

                    if (!toDelete.getSender().equals(profile)) {
                        System.out.println("You can only edit messages you sent!");
                        toDelete = null;
                    }

                } catch (Exception e) {
                    System.out.println("Indices must be within bounds");
                    toDelete = null;
                }
            } while (toDelete == null);

            outToServer.reset();
            outToServer.writeUnshared(toDelete); // Send the message to delete
            outToServer.flush();

        } catch (Exception e) {
            System.out.println("An error occurred while trying to delete the message: " + e.getMessage());
        }
    }

    public void viewProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("viewProfile");
            outToServer.flush();

            System.out.println("Who would you like to view?");
            String toView;
            do {
                toView = scan.nextLine();
            } while(toView.isEmpty());

            outToServer.writeUnshared(toView);
            outToServer.flush();

            Profile viewMe = (Profile) inFromServer.readObject();

            if (viewMe != null) {
                System.out.printf("Display name: %s\nUsername: %s\n", viewMe.getUsername(), viewMe.getDisplayName());
            } else {
                System.out.println("User does not exist!");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while viewing user");
        }
    }

    // GUI related methods
    public void frameInitialization() {    // Initializes the frame for the GUI to be built in
        frame = new JFrame("Boiler Chat");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public void initialPanel() {
        JPanel initialPanel = new JPanel(new GridBagLayout());
        initialPanel.add(new JLabel(), new GridBagConstraints());

        // Create login button
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Add action listeners
        loginButton.addActionListener(actionListener);
        registerButton.addActionListener(actionListener);

        // Add buttons to panel
        initialPanel.add(loginButton);
        initialPanel.add(registerButton);

        frame.getContentPane().removeAll();
        frame.setContentPane(initialPanel);
        frame.revalidate();
    }

    public void registerPanel() {
        // TODO: Back button to take back to previous panel. Confirm password field

        // Create registration panel and set constraints
        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints registerPanelConstraints = new GridBagConstraints();
        registerPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        registerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

        // Create new button and attach action listener for registering
        registerEnterButton = new JButton("Register");
        registerEnterButton.addActionListener(actionListener);

        // Create fields for username, password, display name and receive all input
        usernameField = new JTextField("", 20);
        passwordField = new JPasswordField("", 20);
        displayNameField = new JTextField("", 20);
        receiveAll = new JCheckBox("Receive messages from all users (not only friends)?");

        // Create labels for text boxes
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");
        JLabel displayNameLabel = new JLabel("Display Name: ");
        JLabel passwordRequirements = new JLabel("Password must have 8 characters and contain at least one" +
                " uppercase, lowercase, and number.");
        passwordRequirements.setForeground(Color.GRAY);
        passwordRequirements.setFont(new Font(passwordRequirements.getFont().getFontName(), Font.ITALIC, 8));

        // Create a panel for the username information
        JPanel usernamePanel = new JPanel(new GridBagLayout());
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // Create a panel for the password information
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // Create a panel for the display name input
        JPanel displayNamePanel = new JPanel(new GridBagLayout());
        displayNamePanel.add(displayNameLabel);
        displayNamePanel.add(displayNameField);

        // Add all the smaller panels into the main panel with extra panels for spacing
        registerPanel.add(usernamePanel, registerPanelConstraints);
        registerPanel.add(new JPanel(), registerPanelConstraints);
        registerPanel.add(passwordPanel, registerPanelConstraints);
        registerPanel.add(passwordRequirements, registerPanelConstraints);
        registerPanel.add(new JPanel(), registerPanelConstraints);
        registerPanel.add(displayNamePanel, registerPanelConstraints);
        registerPanel.add(new JPanel(), registerPanelConstraints);
        registerPanel.add(receiveAll, registerPanelConstraints);
        registerPanel.add(new JPanel(), registerPanelConstraints);
        registerPanel.add(registerEnterButton);

        // Change the frame to the registration panel
        frame.getContentPane().removeAll();
        frame.setContentPane(registerPanel);
        frame.revalidate();
    }

    public void loginPanel() {
        // TODO: Back button to take back to previous panel

        // Create new login panel and set constraints
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints loginPanelConstraints = new GridBagConstraints();
        loginPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        loginPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

        // Create a button and add an action listener for logging in
        loginEnterButton = new JButton("Login");
        loginEnterButton.addActionListener(actionListener);

        // Create fields for username and password
        usernameField = new JTextField("", 20);
        passwordField = new JPasswordField("", 20);
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");

        // Create panel for username input
        JPanel usernamePanel = new JPanel(new GridBagLayout());
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // Create panel for password input
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // Add panels to main panel with spacing
        loginPanel.add(usernamePanel, loginPanelConstraints);
        loginPanel.add(new JPanel(), loginPanelConstraints);
        loginPanel.add(passwordPanel, loginPanelConstraints);
        loginPanel.add(new JPanel(), loginPanelConstraints);
        loginPanel.add(loginEnterButton);

        // Change the frame to the login panel
        frame.getContentPane().removeAll();
        frame.setContentPane(loginPanel);
        frame.revalidate();
    }

    public void primaryPanel() {
        // Create the main panel
        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(new GridLayout());

        // Split the left and right panes
        JSplitPane panelSplit = new JSplitPane();
        panelSplit.setDividerLocation(200);
        panelSplit.enable(false);

        // Create the area for a chat
        JPanel chatArea = new JPanel(new BorderLayout());

        // Create the area for user inputs
        JPanel inputPanel = new JPanel(new GridBagLayout());

        messageText = new JTextField("", 57);    // Text field for message input
        deleteButton = new JButton();    // Button to delete messages
        editButton = new JButton();    // Button to edit messages
        sendButton = new JButton();    // Button to send message

        // Set text labels of buttons
        deleteButton.setText("Delete");
        editButton.setText("Edit");
        sendButton.setText("Send");

        // Add the buttons into the panel
        inputPanel.add(deleteButton);
        inputPanel.add(editButton);
        inputPanel.add(sendButton);
        inputPanel.add(messageText);

        chatArea.add(inputPanel, BorderLayout.SOUTH);    // Add the input panel into the chat area

        // Create the text area to be added to chat area
        JTextPane chatDisplay = new JTextPane();
        chatDisplay.setText("User 1: This is my first message\n");    // FIXME: This is the method used to change the message being displayed

        JScrollPane chatScroll = new JScrollPane(chatDisplay);

        chatArea.add(chatScroll, BorderLayout.CENTER);    // Add the display area into the chat area

        panelSplit.setRightComponent(chatArea);    // Add chat area to right half of panel


        // Create the user area
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());

        userDisplaySelection = new JComboBox(new String[] {"Friends", "Blocked", "Search"});    // Create a dropdown menu for selecting who is displayed
        userDisplaySelection.addActionListener(actionListener);

        userPanel.add(userDisplaySelection, BorderLayout.NORTH);    // Add the selection of display into the user panel

        // Create the display of users
        ArrayList<Profile> displayProfiles = null;
        switch ((String) userDisplaySelection.getSelectedItem()) {
            case "Friends" -> displayProfiles = profile.getFriends();
            case "Blocked" -> displayProfiles = profile.getBlocked();
            case "Search" -> displayProfiles = profile.getFriends();
        }

        displayList = new DefaultListModel<>();

        updateUserDisplay(displayProfiles);
        JList userDisplay = new JList(displayList);

        userPanel.add(userDisplay, BorderLayout.CENTER);    // Add the display into the user panel

        JPanel userButtons = new JPanel(new GridLayout(4, 1));    // Create a panel for buttons
        userPanel.add(userButtons, BorderLayout.SOUTH);

        editProfileButton = new JButton();
        chatButton = new JButton();
        viewButton = new JButton();
        logoutButton = new JButton();

        logoutButton.addActionListener(actionListener);

        chatButton.setText("Chat with User");
        viewButton.setText("View User");
        editProfileButton.setText("Edit your Profile");
        logoutButton.setText("Logout");

        userButtons.add(chatButton);
        userButtons.add(viewButton);
        userButtons.add(editProfileButton);
        userButtons.add(logoutButton);

        panelSplit.setLeftComponent(userPanel);    // Add the user panel into the left half of the main panel

        primaryPanel.add(panelSplit);    // Add the split panel into the main panel

        // Change the frame to the login panel
        frame.getContentPane().removeAll();
        frame.setContentPane(primaryPanel);
        frame.revalidate();
    }

    public void updateUserDisplay(ArrayList<Profile> profiles) {
        displayList.removeAllElements();

        if (profiles != null) {
            for (Profile toShow : profiles) {
                displayList.addElement(toShow.getDisplayName());
            }
        }
    }


    private String displayChat(Chat chat) {
        // TODO: Utilize this function to display a chat object

        String senderDisplay;
        String receiverDisplay;
        String display;

        if(chat.getProfiles().get(0).equals(profile)) {
            senderDisplay = chat.getProfiles().get(0).getDisplayName();
            receiverDisplay = chat.getProfiles().get(1).getDisplayName();
        } else {
            senderDisplay = chat.getProfiles().get(1).getDisplayName();
            receiverDisplay = chat.getProfiles().get(0).getDisplayName();
        }

        String result = "";

        for (Message message : chat.getMessages()) {
            display = message.getSender().equals(profile) ? senderDisplay : receiverDisplay;

            result = result + String.format("%s: %s\n", display, message.getContents());
        }
        result.strip();

        return result;
    }
}