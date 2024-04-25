import javax.swing.*;
import javax.swing.border.Border;
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
    JSplitPane panelSplit;

    // User Inputs
    JButton backButton;    // TODO: Put this on login
    JButton friendButton;
    JButton blockButton;
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
    JButton searchButton;
    JButton viewButton;
    JComboBox<String> userDisplaySelection;
    DefaultListModel<Profile> displayList;
    JList<Profile> userDisplay;
    JList<String> chatDisplay = new JList<>();
    DefaultListModel<String> chatDisplayList = new DefaultListModel<>();
    JTextField messageText;
    JTextField usernameField;
    JTextField searchQuery;
    JPasswordField passwordField;
    JTextField displayNameField;
    JPasswordField confirmPasswordField;

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
                        updateChats(inFromServer, outToServer);
                        chatDisplay.clearSelection();
                        chatDisplayList.clear();
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
                    if (!(new String(confirmPasswordField.getPassword()))
                            .equals(new String(passwordField.getPassword()))) {
                        JOptionPane.showMessageDialog(frame,
                                "Password and Confirm Password must be the same!",
                                "Invalid Password", JOptionPane.ERROR_MESSAGE);
                    }
                    if (checkValidPassword(new String(passwordField.getPassword()))) {
                        createNewUser(usernameField.getText(), new String(passwordField.getPassword()),
                                displayNameField.getText(), receiveAll.isSelected(), inFromServer, outToServer);
                        updateChats(inFromServer, outToServer);
                        chatDisplay.clearSelection();
                        chatDisplayList.clear();

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
                    case "Friends" :
                        searchQuery.setText("");
                        searchQuery.setEditable(false);
                        searchButton.setEnabled(false);
                        displayProfiles = profile.getFriends();
                        break;

                    case "Blocked" :
                        searchQuery.setText("");
                        searchQuery.setEditable(false);
                        searchButton.setEnabled(false);
                        displayProfiles = profile.getBlocked();
                        break;

                    case "Search" :
                        searchQuery.setText("");
                        searchQuery.setEditable(true);
                        searchButton.setEnabled(true);
                        displayProfiles = searchUsers(inFromServer, outToServer);
                        break;
                }

                updateUserDisplay(displayProfiles);
            }

            if (e.getSource() == searchQuery || e.getSource() == searchButton) {
                updateUserDisplay(searchUsers(inFromServer, outToServer));
            }

            if (e.getSource() == friendButton) {
                int i = userDisplay.getSelectedIndex();
                if (i > -1) {
                    Profile user = displayList.getElementAt(i);

                    if (profile.getFriends().contains(user)) {
                        unfriendUser(user.getUsername(), inFromServer, outToServer);
                    } else {
                        friendUser(user.getUsername(), inFromServer, outToServer);
                    }

                    ArrayList<Profile> displayProfiles = switch ((String) userDisplaySelection.getSelectedItem()) {
                        case "Friends" -> profile.getFriends();
                        case "Blocked" -> profile.getBlocked();
                        case "Search" -> searchUsers(inFromServer, outToServer);
                        default -> null;
                    };

                    updateUserDisplay(displayProfiles);
                    panelSplit.setRightComponent(chatPanel());
                }
            }

            if (e.getSource() == blockButton) {
                int i = userDisplay.getSelectedIndex();
                if (i > -1) {
                    Profile user = displayList.getElementAt(i);

                    if (profile.getBlocked().contains(user)) {
                        unblockUser(user.getUsername(), inFromServer, outToServer);
                    } else {
                        blockUser(user.getUsername(), inFromServer, outToServer);
                    }

                    ArrayList<Profile> displayProfiles = switch ((String) userDisplaySelection.getSelectedItem()) {
                        case "Friends" -> profile.getFriends();
                        case "Blocked" -> profile.getBlocked();
                        case "Search" -> searchUsers(inFromServer, outToServer);
                        default -> null;
                    };

                    updateUserDisplay(displayProfiles);
                    panelSplit.setRightComponent(chatPanel());
                }
            }

            if (e.getSource() == viewButton) {
                int i = userDisplay.getSelectedIndex();
                if (i > -1) {
                    Profile toView = displayList.getElementAt(i);
                    panelSplit.setRightComponent(viewUserPanel(toView));    // Add the user panel into the left half of the main panel
                }
            }
            if (e.getSource() == chatButton) {
                int i = userDisplay.getSelectedIndex();
                if (i != -1) {
                    Profile recipient = displayList.getElementAt(i);
                    chatDisplay.clearSelection();
                    chatDisplayList.clear();
                    getChatMessages(recipient);
                    chatDisplay = new JList<>(chatDisplayList);
                    panelSplit.setRightComponent(chatPanel());
                }
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
                            //case "logout" -> logout(inFromServer, outToServer);
                            //case "searchUsers" -> searchUsers(inFromServer, outToServer);
                            //case "blockUser" -> blockUser(scan, inFromServer, outToServer);
                            //case "unblockUser" -> unblockUser(scan, inFromServer, outToServer);
                            //case "friendUser" -> friendUser(scan, inFromServer, outToServer);
                            //case "unfriendUser" -> unfriendUser(inFromServer, outToServer);
                            case "editProfile" -> editProfile(scan, inFromServer, outToServer);
                            case "deleteProfile" -> deleteProfile(scan, inFromServer, outToServer);
                            //case "viewProfile" -> viewProfile(scan, inFromServer, outToServer);
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
                    JOptionPane.showMessageDialog(null,
                            "Failed to connect to server!",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
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
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    outToServer.writeUnshared("updateChats");
                    outToServer.flush();

                    outToServer.writeUnshared(profile);
                    outToServer.flush();

                    chats = (ArrayList<Chat>) inFromServer.readObject();
                } catch (Exception e) {
                    System.out.println("An error occurred when updating chats");
                }
                return null;
            }
        };
        worker.execute();
    }

    public ArrayList<Profile> searchUsers(ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            String query = searchQuery.getText();
            if (!query.isEmpty()) {
                outToServer.writeUnshared("searchUsers");
                outToServer.flush();

                outToServer.writeUnshared(query);
                outToServer.flush();

                return (ArrayList<Profile>) inFromServer.readObject();
            } else {
                return new ArrayList<Profile>();
            }
        } catch (Exception e) {
            System.out.println("An error occurred while searching for users");
            return new ArrayList<Profile>();
        }
    }

    public void blockUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("blockUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            outToServer.writeUnshared(user);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while blocking user",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void unblockUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("unblockUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            outToServer.writeUnshared(user);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while unblocking user",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void friendUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("friendUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            outToServer.writeUnshared(user);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while friending user",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void unfriendUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("unfriendUser");
            outToServer.flush();

            outToServer.writeUnshared(profile.getUsername());
            outToServer.flush();

            outToServer.writeUnshared(user);
            outToServer.flush();

            profile = (Profile) inFromServer.readObject();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while unfriending user",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            Message toAdd;
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    outToServer.writeUnshared("sendMessage");
                    outToServer.flush();
                    Profile receiver;

                    do {
                        receiver = displayList.getElementAt(userDisplay.getSelectedIndex());
                        outToServer.writeUnshared(receiver);    // Send profile name to server
                        outToServer.flush();
                    } while (receiver == null);

                    String contents;
                    do {
                        contents = messageText.getText(); // Get message contents
                    } while (contents.isEmpty());

                    // Send the message to the server
                    toAdd = new Message(profile, receiver, contents);
                    outToServer.writeUnshared(toAdd);
                    outToServer.flush();
                    updateChats(inFromServer, outToServer);
                    messageText.setText("");

                } catch (Exception e) {
                    System.out.println("An error occurred when sending the message");
                }
                return null;
            }

            protected void done() {
                chatDisplayList.addElement(toAdd.getSender() + ": " + toAdd.getContents());
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
        frame.setResizable(false);
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
        // TODO: Back button to take back to previous panel

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
        confirmPasswordField = new JPasswordField("", 20);
        displayNameField = new JTextField("", 20);
        receiveAll = new JCheckBox("Receive messages from all users (not only friends)?");

        // Create labels for text boxes
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
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

        // Create a panel for confirming password
        JPanel confirmPasswordPanel = new JPanel(new GridBagLayout());
        confirmPasswordPanel.add(confirmPasswordLabel);
        confirmPasswordPanel.add(confirmPasswordField);

        // Create a panel for the display name input
        JPanel displayNamePanel = new JPanel(new GridBagLayout());
        displayNamePanel.add(displayNameLabel);
        displayNamePanel.add(displayNameField);

        // Add all the smaller panels into the main panel with extra panels for spacing
        registerPanel.add(usernamePanel, registerPanelConstraints);
        registerPanel.add(new JPanel(), registerPanelConstraints);
        registerPanel.add(passwordPanel, registerPanelConstraints);
        registerPanel.add(new JPanel(), registerPanelConstraints);
        registerPanel.add(confirmPasswordPanel, registerPanelConstraints);
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

    public JPanel userPanel() {
        // Create the user area
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel lowerSearchPanel = new JPanel(new BorderLayout());

        // Create a dropdown menu for selecting who is displayed
        userDisplaySelection = new JComboBox<String>(new String[] {"Friends", "Blocked", "Search"});
        userDisplaySelection.addActionListener(actionListener);

        // Create a text field for searching users
        searchQuery = new JTextField("", 12);
        searchQuery.setEditable(false);
        searchQuery.addActionListener(actionListener);

        // Create a button for searching users
        searchButton = new JButton();
        searchButton.setText("Search");
        searchButton.setEnabled(false);
        searchButton.addActionListener(actionListener);

        // Fill out the lower section of the search panel
        lowerSearchPanel.add(searchButton, BorderLayout.WEST);
        lowerSearchPanel.add(searchQuery, BorderLayout.EAST);

        // Fill out the remainder of the search panel
        searchPanel.add(userDisplaySelection, BorderLayout.NORTH);    // Add the selection of display into the user panel
        searchPanel.add(lowerSearchPanel, BorderLayout.SOUTH);    // Add the selection of display into the user panel

        // Get the list of users to display
        ArrayList<Profile> displayProfiles = null;
        switch ((String) userDisplaySelection.getSelectedItem()) {
            case "Friends" :
                searchQuery.setText("");
                searchQuery.setEditable(false);
                searchButton.setEnabled(false);
                displayProfiles = profile.getFriends();
                break;

            case "Blocked" :
                searchQuery.setText("");
                searchQuery.setEditable(false);
                searchButton.setEnabled(false);
                displayProfiles = profile.getBlocked();
                break;

            case "Search" :
                searchQuery.setText("");
                searchQuery.setEditable(true);
                searchButton.setEnabled(true);
                displayProfiles = searchUsers(inFromServer, outToServer);
                break;
        }

        displayList = new DefaultListModel<>();
        updateUserDisplay(displayProfiles);
        userDisplay = new JList<>(displayList);

        // Create a panel of buttons
        JPanel userButtons = new JPanel(new GridLayout(4, 1));    // Create a panel for buttons

        editProfileButton = new JButton();
        chatButton = new JButton();
        viewButton = new JButton();
        logoutButton = new JButton();

        chatButton.addActionListener(actionListener);
        viewButton.addActionListener(actionListener);
        editProfileButton.addActionListener(actionListener);
        logoutButton.addActionListener(actionListener);

        chatButton.setText("Chat");
        viewButton.setText("View");
        editProfileButton.setText("Edit your Profile");
        logoutButton.setText("Logout");

        userButtons.add(chatButton);
        userButtons.add(viewButton);
        userButtons.add(editProfileButton);
        userButtons.add(logoutButton);

        // Add all the sub-panels into the main panel
        userPanel.add(searchPanel, BorderLayout.NORTH);
        userPanel.add(userDisplay, BorderLayout.CENTER);
        userPanel.add(userButtons, BorderLayout.SOUTH);

        return userPanel;
    }

    public void getChatMessages(Profile recipient) {
        Profile sender = this.profile;
        Chat selectedChat = null;
        for (Chat chat : chats) {
            if (chat.matchesProfiles(sender, recipient)) {
                selectedChat = chat;
            }
        }
        if (selectedChat == null) {
            return;
        }
        for (int j = 0; j < selectedChat.getMessages().size(); j++) {
            chatDisplayList.addElement(selectedChat.getMessages().get(j).getSender() + ": "
                    + selectedChat.getMessages().get(j).getContents());
        }
    }

    public JPanel chatPanel() {
        // Create the area for a chat
        JPanel chatArea = new JPanel(new BorderLayout());

        // Create the area for user inputs
        JPanel inputPanel = new JPanel(new GridBagLayout());

        messageText = new JTextField("", 57);    // Text field for message input
        sendButton = new JButton("Send");    // Button to send message
        sendButton.addActionListener(actionListener);
        editButton = new JButton("Edit");    // Button to edit messages
        editButton.addActionListener(actionListener);
        deleteButton = new JButton("Delete");    // Button to delete messages
        deleteButton.addActionListener(actionListener);

        // Add the buttons into the panel
        inputPanel.add(messageText);
        inputPanel.add(sendButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);


        chatArea.add(inputPanel, BorderLayout.SOUTH);    // Add the input panel into the chat area

        // Create the text area to be added to chat area
        JScrollPane chatScroll = new JScrollPane(chatDisplay); // Put chat into a scroll panel
        chatArea.add(chatScroll, BorderLayout.CENTER);    // Add the display area into the chat area

        return chatArea;
    }

    public JPanel viewUserPanel(Profile user) {
        // Create the area for a chat
        JPanel viewUserPanel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();

        JLabel displayLabel = new JLabel("Display Name: " + user.getUsername());
        JLabel usernameLabel = new JLabel("Username: " + user.getUsername());

        displayLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));


        friendButton = new JButton();
        blockButton = new JButton();

        if (profile.getFriends().contains(user)) {
            friendButton.setText("Unfriend");
        } else {
            friendButton.setText("Friend");
        }

        if (profile.getBlocked().contains(user)) {
            blockButton.setText("Unblock");
        } else {
            blockButton.setText("Block");
        }


        friendButton.addActionListener(actionListener);
        blockButton.addActionListener(actionListener);

        gbc.gridx = 0;
        gbc.gridy = 0;
        viewUserPanel.add(displayLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        viewUserPanel.add(usernameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        viewUserPanel.add(friendButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        viewUserPanel.add(blockButton, gbc);

        return viewUserPanel;
    }


    public void primaryPanel() {
        // Create the main panel
        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(new GridLayout());

        // Split the left and right panes
        panelSplit = new JSplitPane();
        panelSplit.setDividerLocation(200);
        panelSplit.setEnabled(false);

        panelSplit.setLeftComponent(userPanel());    // Add the user panel into the left half of the main panel
        panelSplit.setRightComponent(chatPanel());    // Add chat area to right half of panel

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
                displayList.addElement(toShow);
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
        result = result.strip();

        return result;
    }
}