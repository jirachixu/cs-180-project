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

//FIXME: sending/editing/deleting messages is still scuffed
public class Client implements ClientInterface {
    // Object specific to client
    Profile profile;
    ArrayList<Chat> chats;

    // GUI objects
    // Display objects
    JFrame frame;
    JSplitPane panelSplit;

    // User Inputs
    JButton backButton;
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
    JTextArea currentRecipient = new JTextArea();
    JTextField messageText;
    JTextField usernameField;
    JTextField searchQuery;
    JPasswordField passwordField;
    JTextField displayNameField;
    JPasswordField confirmPasswordField;
    JButton editDisplayButton;
    JButton editPasswordButton;
    JButton editReceiveAllButton;
    JButton deleteProfile;


    // Network IO stuff
    Profile activeChat;
    ObjectInputStream inFromServer;
    ObjectOutputStream outToServer;
    Scanner scan; // FIXME: Placeholder


    public Client() {
        profile = null;
        chats = null;

        frameInitialization();
        initialPanel();
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == editProfileButton) {
                panelSplit.setRightComponent(editProfilePanel());
            }

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
                        login(usernameField.getText(), new String(passwordField.getPassword()),
                                inFromServer, outToServer);

                        if (profile.getUsername() != null) {
                            updateChats(inFromServer, outToServer);
                            chatDisplay.clearSelection();
                            chatDisplayList.clear();
                            primaryPanel();
                        }
                    }
                }
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
                        primaryPanel();

                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Invalid Password!",
                                "Invalid Username/Password", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Username/Password/Display Name cannot be empty",
                            "Invalid Username/Password/Display Name", JOptionPane.ERROR_MESSAGE);
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
                    activeChat = displayList.getElementAt(i);
                    chatDisplay = updateChatDisplay();
                    panelSplit.setRightComponent(chatPanel());
                }
            }

            if (e.getSource() == backButton) {
                initialPanel();
            }

            if (e.getSource() == editDisplayButton) {
                editProfile("Display Name", inFromServer, outToServer);
                panelSplit.setRightComponent(editProfilePanel());
                frame.setTitle(String.format("Boiler Chat - %s(%s)", profile.getDisplayName(), profile.getUsername()));
            }

            if (e.getSource() == receiveAll) {
                editProfile("Receive All", inFromServer, outToServer);
                panelSplit.setRightComponent(editProfilePanel());
            }

            if (e.getSource() == editPasswordButton) {
                editProfile("Password", inFromServer, outToServer);
                panelSplit.setRightComponent(editProfilePanel());
            }
        }
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Client());
    }

    public void run() {
        scan = new Scanner(System.in);    // TODO: Replace with GUI
        activeChat = new Profile();
        SwingWorker<Void, Void> connectionWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Socket socket = new Socket("localhost", 8080)) {    // Network connection
                    // Setup network connection
                    inFromServer = new ObjectInputStream(socket.getInputStream());
                    outToServer = new ObjectOutputStream(socket.getOutputStream());
                    outToServer.flush();
                    profile = new Profile();

                    while (true) {    // Loop forever and update chats in background
                        if (profile.getUsername() != null) {
                            System.out.println("Update chat");
                            updateChats(inFromServer, outToServer);
                            chatDisplay = updateChatDisplay();
                        }

                        Thread.sleep(2500);
                    }

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

            // Get and validate username with server
            outToServer.reset();
            outToServer.writeUnshared(username);
            outToServer.flush();

            boolean loop = inFromServer.readBoolean();
            if (loop) {
                JOptionPane.showMessageDialog(frame,
                        "Username already exists!",
                        "Boiler Chat", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                outToServer.writeUnshared(password);
                outToServer.flush();

            } catch (IOException e) {    // If socket is lost exit method
                JOptionPane.showMessageDialog(frame,
                        "An error occurred while trying to create an account!",
                        "Boiler Chat", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while trying to create an account!",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void logout(ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("logout");
            outToServer.flush();

            this.profile = (Profile) inFromServer.readObject();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to logout",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
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

    public void login(String username, String password, ObjectInputStream inFromServer,
                      ObjectOutputStream outToServer) { // Log in
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

            } else {
                profile = new Profile();
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


    // TODO: Update to GUI
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
                JOptionPane.showMessageDialog(frame,
                        "Failed to delete account",
                        "Boiler Chat", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // TODO: Update to GUI
    public void editProfile(String input, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        try {
            if (input.equalsIgnoreCase("Display Name")) {
                String newDisplay = displayNameField.getText();

                if (!newDisplay.isEmpty()) {
                    outToServer.writeUnshared("editProfile");
                    outToServer.flush();

                    outToServer.writeUnshared(profile.getUsername());
                    outToServer.flush();

                    outToServer.writeUnshared("display");
                    outToServer.flush();

                    outToServer.writeUnshared(newDisplay);
                    outToServer.flush();

                    profile = (Profile) inFromServer.readObject();
                }

            } else if (input.equalsIgnoreCase("Password")) {
                String newPassword = String.valueOf(passwordField.getPassword());
                String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

                if (!newPassword.isEmpty() && newPassword.equals(confirmPassword)) {
                    outToServer.writeUnshared("editProfile");
                    outToServer.flush();

                    outToServer.writeUnshared(profile.getUsername());
                    outToServer.flush();

                    outToServer.writeUnshared("password");
                    outToServer.flush();

                    outToServer.writeUnshared(newPassword);
                    outToServer.flush();

                    profile = (Profile) inFromServer.readObject();
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Password is not valid",
                            "Boiler Chat", JOptionPane.ERROR_MESSAGE);
                }
            } else if (input.equalsIgnoreCase("Receive All")) {
                outToServer.writeUnshared("editProfile");
                outToServer.flush();

                outToServer.writeUnshared(profile.getUsername());
                outToServer.flush();

                outToServer.writeUnshared("receiveAll");
                outToServer.flush();

                outToServer.writeBoolean(receiveAll.isSelected());
                outToServer.flush();

                profile = (Profile) inFromServer.readObject();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to edit account",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(frame,
                            "An error occurred when updating chats",
                            "Boiler Chat", JOptionPane.ERROR_MESSAGE);
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

                ArrayList<Profile> results = (ArrayList<Profile>) inFromServer.readObject();
                results.remove(profile);

                return results;
            } else {
                return new ArrayList<Profile>();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while searching for users",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);

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
        Profile receiver;
        receiver = activeChat;

        String contents;
        contents = messageText.getText(); // Get message contents

        try {
            if (receiver.getUsername() != null && !contents.isEmpty()) {
                outToServer.writeUnshared("sendMessage");
                outToServer.flush();

                // Send the message to the server
                Message toAdd = new Message(profile, receiver, contents);

                outToServer.writeUnshared(toAdd);
                outToServer.flush();

                updateChats(inFromServer, outToServer);
                messageText.setText("");

                chatDisplayList.addElement(toAdd.getSender() + ": " + toAdd.getContents());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred when sending the message",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Chat getCurrentChat() {
        Chat selectedChat = null;
        Profile recipient = activeChat;
        for (Chat chat : chats) {
            if (chat.matchesProfiles(this.profile, recipient)) {
                selectedChat = chat;
                break;
            }
        }
        return selectedChat;
    }

    public void editMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer) {
        Chat selectedChat = getCurrentChat();
        int i = chatDisplay.getSelectedIndex();

        System.out.println(i);

        if (i > -1) {
            String selection = chatDisplayList.getElementAt(i);
            String sender = selection.substring(0, selection.indexOf(" "));
            String messageContent = selection.substring(selection.indexOf(" ") + 1);

            try {
                outToServer.writeUnshared("editMessage");
                outToServer.flush();

                Message toEdit = null;

                for (int j = 0; j < selectedChat.getMessages().size(); j++) {
                    if (messageContent.equals(selectedChat.getMessages().get(j).getContents())) {
                        toEdit = selectedChat.getMessages().get(j);
                        break;
                    }
                }

                do {    // Get a valid method from chats
                    if (!toEdit.getSender().equals(profile)) {
                        JOptionPane.showMessageDialog(null,
                                "You can only edit messages that you sent!", "Edit Error",
                                JOptionPane.ERROR_MESSAGE);
                        toEdit = null;
                    }
                } while (toEdit == null);

                outToServer.reset();
                outToServer.writeUnshared(toEdit);
                outToServer.flush();

                String contents = JOptionPane.showInputDialog(null,
                        "What would you like to edit the message to?", "Edit", JOptionPane.QUESTION_MESSAGE);

                outToServer.writeUnshared(contents);
                outToServer.flush();

                updateChats(inFromServer, outToServer);
                chatDisplayList.set(chatDisplay.getSelectedIndex(), sender + " " + contents);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("An error occurred when editing the message");
            }
        }
    }

    public void deleteMessage(Scanner scan, ObjectOutputStream outToServer) {
        try {
            outToServer.writeUnshared("deleteMessage");
            outToServer.flush();

            Chat selectedChat = getCurrentChat();
            String selection = chatDisplayList.getElementAt(chatDisplay.getSelectedIndex());
            String sender = selection.substring(0, selection.indexOf(" "));
            String messageContent = selection.substring(selection.indexOf(" ") + 1);

            Message toDelete = null;

            for (int i = 0; i < selectedChat.getMessages().size(); i++) {
                if (messageContent.equals(selectedChat.getMessages().get(i).getContents())) {
                    toDelete = selectedChat.getMessages().get(i);
                    break;
                }
            }

            do {    // Get a valid method from chats
                if (!toDelete.getSender().equals(profile)) {
                    JOptionPane.showMessageDialog(null, "You can only delete messages you sent!",
                            "Delete Error", JOptionPane.ERROR_MESSAGE);
                    toDelete = null;
                }
            } while (toDelete == null);

            outToServer.reset();
            outToServer.writeUnshared(toDelete); // Send the message to delete
            outToServer.flush();

            updateChats(inFromServer, outToServer);
            chatDisplayList.remove(chatDisplay.getSelectedIndex());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while trying to delete the message",
                    "Boiler Chat", JOptionPane.ERROR_MESSAGE);
        }
    }

    // FIXME: Obselete method
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
        frame.setTitle("Boiler Chat");
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
        // Create registration panel and set constraints
        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints registerPanelConstraints = new GridBagConstraints();
        registerPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        registerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints registerButtonConstraints = new GridBagConstraints();
        registerButtonConstraints.gridwidth = GridBagConstraints.REMAINDER;

        // Create new button and attach action listener for registering
        registerEnterButton = new JButton("Register");
        registerEnterButton.addActionListener(actionListener);
        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

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
        registerPanel.add(registerEnterButton, registerButtonConstraints);
        registerPanel.add(new JPanel(), registerPanelConstraints);
        registerPanel.add(backButton);

        // Change the frame to the registration panel
        frame.getContentPane().removeAll();
        frame.setContentPane(registerPanel);
        frame.revalidate();
    }

    public void loginPanel() {
        // Create new login panel and set constraints
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints loginPanelConstraints = new GridBagConstraints();
        loginPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        loginPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints loginButtonConstraints = new GridBagConstraints();
        loginButtonConstraints.gridwidth = GridBagConstraints.REMAINDER;

        // Create a button and add an action listener for logging in
        loginEnterButton = new JButton("Login");
        loginEnterButton.addActionListener(actionListener);
        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

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
        loginPanel.add(loginEnterButton, loginButtonConstraints);
        loginPanel.add(new JPanel(), loginPanelConstraints);
        loginPanel.add(backButton);

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
        Chat selectedChat = getCurrentChat();
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

        currentRecipient.setEditable(false);

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
        chatArea.add(currentRecipient, BorderLayout.NORTH);

        // Create the text area to be added to chat area
        JScrollPane chatScroll = new JScrollPane(chatDisplay); // Put chat into a scroll panel
        chatArea.add(chatScroll, BorderLayout.CENTER);    // Add the display area into the chat area

        return chatArea;
    }

    public JList<String> updateChatDisplay() {
        if (activeChat.getUsername() != null) {
            int i = chatDisplay.getSelectedIndex();
            currentRecipient.setText(activeChat.getDisplayName() + " (" + activeChat.getUsername() + ")");
            chatDisplay.clearSelection();
            chatDisplayList.clear();
            getChatMessages(activeChat);
            chatDisplay = new JList<>(chatDisplayList);
            chatDisplay.setSelectedIndex(i);
        }

        return chatDisplay;
    }

    public JPanel viewUserPanel(Profile user) {
        // Create the area for a chat
        JPanel viewUserPanel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();

        JLabel displayLabel = new JLabel("Display Name: " + user.getUsername());
        JLabel usernameLabel = new JLabel("Username: " + user.getUsername());

        displayLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 20));


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
        frame.setTitle(String.format("Boiler Chat - %s(%s)", profile.getDisplayName(), profile.getUsername()));

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

    public JPanel editProfilePanel() {
        JPanel editProfilePanel = new JPanel(new GridBagLayout());

        var gbc = new GridBagConstraints();

        JLabel displayLabel = new JLabel("Display Name: " + profile.getDisplayName());
        JLabel passwordLabel = new JLabel("Password:");
        JLabel receiveAllLabel = new JLabel("Receive All: " + profile.isReceiveAll());

        displayLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        receiveAllLabel.setFont(new Font("Tahoma", Font.BOLD, 15));

        editDisplayButton = new JButton();
        editPasswordButton = new JButton();
        editReceiveAllButton = new JButton();

        editDisplayButton.addActionListener(actionListener);
        editPasswordButton.addActionListener(actionListener);
        editReceiveAllButton.addActionListener(actionListener);

        editDisplayButton.setText("Edit");
        editPasswordButton.setText("Edit");
        editReceiveAllButton.setText("Edit");

        displayNameField = new JTextField("", 15);
        passwordField = new JPasswordField("", 15);
        confirmPasswordField = new JPasswordField("", 15);

        receiveAll = new JCheckBox("Receive messages from all users (not only friends)?");
        receiveAll.setSelected(profile.isReceiveAll());
        receiveAll.addActionListener(actionListener);

        JLabel passwordRequirements = new JLabel("Password must have 8 characters and contain at least one" +
                " uppercase, lowercase, and number.");
        passwordRequirements.setForeground(Color.GRAY);
        passwordRequirements.setFont(new Font(passwordRequirements.getFont().getFontName(), Font.ITALIC, 8));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        editProfilePanel.add(displayLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        editProfilePanel.add(new JLabel("New Display Name: "), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        editProfilePanel.add(displayNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        editProfilePanel.add(editDisplayButton, gbc);

        gbc.gridy = 3;
        editProfilePanel.add(new JLabel(" "), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        editProfilePanel.add(passwordLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        editProfilePanel.add(new JLabel("New Password: "), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        editProfilePanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        editProfilePanel.add(new JLabel("Reenter New Password: "), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        editProfilePanel.add(confirmPasswordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        editProfilePanel.add(passwordRequirements, gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        editProfilePanel.add(editPasswordButton, gbc);

        gbc.gridy = 9;
        editProfilePanel.add(new JLabel(" "), gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        editProfilePanel.add(receiveAllLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        editProfilePanel.add(receiveAll, gbc);

        return editProfilePanel;
    }
}