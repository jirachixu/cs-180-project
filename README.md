# CS 180 Project
## How to Run
You can test the server side methods and classes which compose the database by running RunLocalTest.java. You can test
the client side methods and classes by following the instructions at the bottom of this README. In general, first start 
the server and load the database by either entering "yes" or "no" and indicating the files to read from. Then start the
client and follow all prompts

## Submission
Phase 1 was submitted by Jared Bright on Brightspace on 04/01/2024.
Phase 2 was submitted by Caasi Boakye on Brightspace on 04/15/2024.
Phase 3 was submitted by Jared Bright on Brightspace on 04/28/2024

## Server-Side Classes

### Server
The Server class is where the server and all of its functionality is run. The Client class sends its inputs to the
server, which then sends those inputs to the database to process and get values and then send them back to the client. 
Implements Runnable via ServerInterface in order to make threads for each connected Client

#### Fields
| Field Name | Type            | Description                                                   |
|------------|-----------------|---------------------------------------------------------------|
| socket     | final Socket    | The client socket that will be used to create another thread. |
| database   | static Database | The database that every Server will call back to.             |

#### Methods
| Method Name                                                               | Return      | Description                                                             |
|---------------------------------------------------------------------------|-------------|-------------------------------------------------------------------------|
| run()                                                                     | void        | What runs when the thread is started. Calls other commands as prompted. |
| createNewUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser) | void        | Creates a new Profile using the input from the client.                  |
| login(ObjectInputStream inFromUser, ObjectOutputStream outToUser)         | void        | Logs in to a Profile using the input from the client.                   |
| main(String[] args)                                                       | static void | Creates the database and accepts clients in to different threads.       |
| logout(ObjectOutputStream outToUser)                                      | void        | Logs out of a profile using the inputs from the client.                 |
| deleteProfile(ObjectOutputStream outToUser)                               | void        | Deletes a profile using the inputs from the client.                     |
| editProfile(ObjectInputStream inFromUser, ObjectOutputStream outToUser)   | void        | Edits a profile using the inputs from the client.                       |
| searchUsers(ObjectInputStream inFromUser, ObjectOutputStream outToUser)   | void        | Sends profiles matching a query to client.                              |
| updateChats(ObjectInputStream inFromUser, ObjectOutputStream outToUser)   | void        | Sends chats to the client.                                              |
| sendMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser)   | void        | Sends a message from Client into the appropriate chat.                  |
| editMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser)   | void        | Edits a message from Client in the appropriate chat.                    |
| deleteMessage(ObjectInputStream inFromUser, ObjectOutputStream outToUser) | void        | Deletes a message from Client in the appropriate chat.                  |
| blockUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser)     | void        | Blocks a user.                                                          |
| unblockUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser)   | void        | Unblocks a user.                                                        |
| friendUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser)    | void        | Friends a user.                                                         |
| unfriendUser(ObjectInputStream inFromUser, ObjectOutputStream outToUser)  | void        | Unfriends a user.                                                       |
| viewProfile(ObjectInputStream inFromUser, ObjectOutputStream outToUser)   | void        | Views a profile.                                                        |


### Profile
The Profile class holds the user's information. This controls who can message the user, as well as who is friends with 
the user and who is blocked by the user. This is used by the Chat, Message, and Database classes.

#### Fields
| Field Name  | Type                | Description                                                                       |
|-------------|---------------------|-----------------------------------------------------------------------------------|
| username    | String              | The username of the profile. This is also used for logging in.                    |
| password    | String              | The password of the profile                                                       |
| displayName | String              | The display name of the profile (potentially add functionality to edit this?)     |
| receiveAll  | boolean             | Whether the profile can receive messages from everyone or only from their friends |
| friends     | ArrayList\<Profile> | The friends of the profile (potentially add functionality to add/remove friends?) |
| blocked     | ArrayList\<Profile> | Profiles which are blocked by this profile (cannot send messages to this profile) |

#### Methods
| Method Name                             | Return              | Description                                                                  |
|-----------------------------------------|---------------------|------------------------------------------------------------------------------|
| getUsername()                           | String              | Gets the username.                                                           |
| getPassword()                           | String              | Gets the password.                                                           |
| setPassword(String password)            | void                | Sets the password.                                                           |
| getDisplayName()                        | String              | Gets the display name.                                                       |
| setDisplayName(String displayName)      | void                | Sets the display name.                                                       |
| isReceiveAll()                          | boolean             | Gets whether the profile can receive messages from everyone or just friends. |
| getFriends()                            | ArrayList\<Profile> | Gets the profile's friends.                                                  |
| setReceiveAll(boolean receiveAll)       | void                | Sets the receive all status of a profile                                     |
| setFriends(ArrayList\<Profile> friends) | void                | Sets the profile's friends.                                                  |
| getBlocked()                            | ArrayList\<Profile> | Gets the profile's blocked profiles.                                         |
| setBlocked(ArrayList\<Profile> blocked) | void                | Sets the profile's blocked profiles.                                         |
| addFriend(Profile p)                    | boolean             | Adds a profile to the profile's friend ArrayList.                            |
| removeFriend(Profile p)                 | boolean             | Removes a profile from the profile's friend ArrayList.                       |
| block(Profile p)                        | boolean             | Adds a profile to the profile's blocked ArrayList.                           |
| unblock(Profile p)                      | boolean             | Removes a profile from the profile's blocked ArrayList.                      |
| equals(Object o)                        | boolean             | Checks if the Object is a profile, then checks if the usernames match.       |


### Message
The Message class handles the actual messages sent in a given chat. This contains the content of the message, as well as
the profile which sent the message and the profile which received the message. This is used by the Chat class.

#### Fields
| Field Name | Type          | Description                                                                |
|------------|---------------|----------------------------------------------------------------------------|
| sender     | final Profile | The profile that sent the message.                                         |
| receiver   | final Profile | The profile that receives the message.                                     |
| contents   | String        | The contents of the message.                                               |
| status     | int           | The current status of the message (0 = unedited, 1 = edited, 2 = deleted). |
| timestamp  | long          | The time in which the message was sent.                                    |

#### Methods
| Method Name                               | Return  | Description                                                                                                                 |
|-------------------------------------------|---------|-----------------------------------------------------------------------------------------------------------------------------|
| getSender()                               | Profile | Gets the sender of the message.                                                                                             |
| getReceiver()                             | Profile | Gets the receiver of the message.                                                                                           |
| getTimestamp()                            | long    | Gets the time the message was sent.                                                                                         |
| getStatus()                               | int     | Gets the deleted/edited status of the message.                                                                              |
| edit(String contents) throws MessageError | void    | Edits the content of the message                                                                                            |
| delete()                                  | void    | Deletes the message (sets its contents to null).                                                                            |
| getContents()                             | String  | Gets the contents of the message.                                                                                           |
| toString()                                | String  | Returns a string representation of all the data stored in the message                                                       |
| equals(Object o)                          | boolean | Checks if the object is a Message, and then checks if they have the same sender, receiver, status, timestamp, and contents. |


### Chat
The Chat class is where messages are sent and profiles interact with one another. This holds all the messages in a
chat and the two profiles involved in the chat. It also contains a timestamp in for later display purposes. This is used
by the Database class.

#### Fields
| Field Name | Type                | Description                                   |
|------------|---------------------|-----------------------------------------------|
| messages   | ArrayList\<Message> | ArrayList of messages in the chat             |
| profiles   | ArrayList\<Profile> | ArrayList of profiles involved in this chat   |
| timestamp  | long                | The time of the last sent message in the chat |

#### Methods
| Method Name                                                          | Return              | Description                                                                |
|----------------------------------------------------------------------|---------------------|----------------------------------------------------------------------------|
| getProfiles()                                                        | ArrayList\<Profile> | Gets all the profiles in the chat.                                         |
| setProfiles(Profile profile1, Profile profile2)                      | void                | Sets the profiles in the chat.                                             |
| getMessages()                                                        | ArrayList\<Message> | Gets all the messages in the chat.                                         |
| getTimestamp()                                                       | long                | Gets the current timestamp of the chat (when the latest message was sent). |
| matchesProfiles(Profile profile1, Profile profile2)                  | boolean             | Checks if the two given profiles are in the profiles for the chat.         |
| sendMessage(Message message)                                         | void                | Sends a message to the chat.                                               |
| editMessage(Message message, String newContents) throws MessageError | void                | Edits a message in the chat.                                               |
| deleteMessage(Message message) throws MessageError                   | void                | Deletes a message in the chat.                                             |


### Database
The Database class takes in all the requests from the client and does the processing on the server side. It is also
responsible for saving and loading profiles and chats. This connects the Profile, Chat, and Message class on the server
side.

#### Fields
| Field Name | Type                | Description                           |
|------------|---------------------|---------------------------------------|
| profiles   | ArrayList\<Profile> | ArrayList of profiles in the database |
| chats      | ArrayList\<Chat>    | ArrayList of chats in the database    |

#### Methods
| Method Name                                                                             | Return             | Description                                                                                     |
|-----------------------------------------------------------------------------------------|--------------------|-------------------------------------------------------------------------------------------------|
| readProfile()                                                                           | boolean            | Reads in all of the profiles from the profile file (profileIn).                                 |
| readChat()                                                                              | boolean            | Reads in all of the chats from the chat file (chatIn).                                          |
| outputProfile()                                                                         | boolean            | Outputs all of the profiles to the profileOut file.                                             |
| outputChat()                                                                            | boolean            | Outputs all of the chats to the chatOut file.                                                   |
| clearDatabase()                                                                         | void               | Sets database contents to empty arraylists                                                      |
| login(String username, String password)                                                 | boolean            | If the username and password match any of the profiles, returns true. Otherwise, returns false. |
| sendMessage(Message message)                                                            | void               | Sends a message to a chat between two profiles.                                                 |
| editMessage(Message message)                                                            | void               | Edits a message in a chat between two profiles.                                                 |
| deleteMessage(Message message)                                                          | void               | Deletes a message in a chat between two profiles.                                               |
| createProfile(String username, String password, String displayName, boolean receiveAll) | boolean            | Creates a profile in the database                                                               |
| editDisplayName(String username, String newDisplayName)                                 | boolean            | Edits the display name of a profile within the database                                         |
| editPassword(String username, String newPassword)                                       | boolean            | Edits the password of a profile within the database                                             |
| editReceiveAll(String username, boolean newReceiveAll)                                  | boolean            | Edits the receive all status of a profile within the database                                   |
| deleteProfile(String username)                                                          | boolean            | Deletes a profile within the database                                                           |
| findProfiles(String toFind)                                                             | ArrayList<Profile> | Returns a list of all profiles matching the search term                                         |
| usernameFree(String username)                                                           | boolean            | Returns true if the username is free                                                            |
| getProfile(String username)                                                             | Profile            | Returns a profile of a given username                                                           |
| getUserChats(Profile profile)                                                           | ArrayList<Chat>    | Returns the chats that include profile                                                          |
| blockUser(String blockerUsername, String blockeeUsername)                               | boolean            | Blocks a user                                                                                   |
| unblockUser(String unblockerUsername, String unblockeeUsername)                         | boolean            | Unblocks a user                                                                                 |
| friendUser(String frienderUsername, String friendeeUsername)                            | boolean            | Friends a user                                                                                  |
| unfriendUser(String unfrienderUsername, String unfriendeeUsername)                      | boolean            | Unfriends a user                                                                                |


## Client-Side Classes
### Client
The Client class is responsible for getting GUI inputs from the client and sending them to the server so that it can do
processing. It's fields hold onto the current profile that the server has given permission for it to have as well as the
chats as of the last refresh. Client implements Runnable so that later testing can let multiple client run at the same
time even if that testing is not currently done.

#### Fields
| Field Name           | Type                      | Description                                                 |
|----------------------|---------------------------|-------------------------------------------------------------|
| profile              | Profile                   | The profile being used for the current client.              |
| chats                | ArrayList<Chat>           | The chats that the current client is a part of.             |
| frame                | JFrame                    | The main frame that the GUI exists in                       |
| panelSplit           | JSplitPane                | The primary left and right split pane in the main interface |
| backButton           | JButton                   | A button for going back to the welcome screen               |
| friendButton         | JButton                   | A button for friending a user                               |
| blockButton          | JButton                   | A button for blocking a user                                |
| editButton           | JButton                   | A button for viewing edit profile panel                     |
| logoutButton         | JButton                   | A button for logging out                                    |
| sendButton           | JButton                   | A button for sending message                                |
| loginButton          | JButton                   | A button for logging in                                     |
| registerButton       | JButton                   | A button for registering an account                         |
| loginEnterButton     | JButton                   | A button for confirming login                               |
| registerEnterButton  | JButton                   | A button for confirming registration                        |
| receiveAll           | JCheckBox                 | A check box for registering options                         |
| editProfileButton    | JButton                   | A button for editing a profile                              |
| chatButton           | JButton                   | A button for chatting with a user                           |
| searchButton         | JButton                   | A button for searching users                                |
| viewButton           | JButton                   | A button for viewing a user                                 |
| userDisplaySelection | JComboBox<String>         | A drop down menu for selecting tab of users to view         |
| displayList          | DefaultListModel<Profile> | A list to hold the profiles to view                         |
| userDisplay          | JList<Profile>            | A list to hold the profiles to view                         |
| chatDisplay          | JList<String>             | A list for holding chats to display                         |
| chatDisplayList      | DefaultListModel<String>  | A list for holding chats to display                         |
| currentRecipient     | JTextArea                 | Header for a chat                                           |
| messageText          | JTextField                | Message to be sent                                          |
| usernameField        | JTextField                | Username to be inputted                                     |
| searchQuery          | JTextField                | Search query input field                                    |
| passwordField        | JPasswordField            | Password to be inputted                                     |
| displayNameField     | JTextField                | Display name to be inputted                                 |
| confirmPasswordField | JPasswordField            | Confirm a password                                          |
| editDisplayButton    | JTextField                | Confirms a change to display name                           |
| editPasswordButton   | JButton                   | Confirms a change to password                               |
| editReceiveAllButton | JButton                   | Confirms a change to receive all                            |
| deleteProfileButton  | JButton                   | Confirms deleting of a profile                              |
| activeChat           | Profile                   | Current user that a chat is being held with                 |
| actionListener       | ActionListener            | Binds to all buttons to run correct methods based on clicks |


#### Methods
| Method Name                                                                                                                                         | Return             | Description                                                                                                        |
|-----------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|--------------------------------------------------------------------------------------------------------------------|
| main(String[] args)                                                                                                                                 | static void        | Creates a new Client thread.                                                                                       |
| run()                                                                                                                                               | void               | Organizes the calls to individual methods.                                                                         |
| createNewUser(String username, String password, String display, boolean receiveAll, ObjectInputStream inFromServer, ObjectOutputStream outToServer) | void               | Creates a new Profile for the client.                                                                              |
| logout(ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                                              | void               | Logs a profile out of client.                                                                                      |
| checkValidPassword(String password)                                                                                                                 | static boolean     | Checks if a given password is a valid password. (8 characters, uppercase, lowercase, number and special character) |
| login(String username, String password, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                             | void               | Logs into a Profile for the client.                                                                                |
| deleteProfile(ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                                       | void               | Deletes the profile currently logged into client                                                                   |
| editProfile(String input, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                           | void               | Edits the profile currently logged into client                                                                     |
| updateChats(ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                                         | void               | Updates all chats associated with client                                                                           |
| searchUsers(ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                                         | ArrayList<Profile> | Searches for a user and displays results                                                                           |
| blockUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                              | void               | Blocks a profile                                                                                                   |
| unblockUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                            | void               | Unlocks a profile                                                                                                  |
| friendUserString user, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                              | void               | Friends a profile                                                                                                  |
| unfriendUser(String user, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                           | void               | Unfriends a profile                                                                                                |
| canSend(Chat currentChat)                                                                                                                           | boolean            | Checks if a chat can be sent based on blocked/friend status                                                        |
| sendMessage(ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                                                         | void               | Sends a message to a chat.                                                                                         |
| getCurrentChat()                                                                                                                                    | Chat               | Gets the current active chat                                                                                       |
| editMessage(ObjectInputStream inFromServer, ObjectOutputStream outToServer, String chatId, int messageIndex)                                        | void               | Edits a message in a chat.                                                                                         |
| deleteMessage(ObjectOutputStream outToServer)                                                                                                       | void               | Deletes a message in a chat.                                                                                       |
| frameInitialization()                                                                                                                               | void               | Starts the frame for the GUI to be build in                                                                        |
| initialPanel()                                                                                                                                      | void               | The first panel seen when starting the program                                                                     |
| registerPanel                                                                                                                                       | void               | Loads the registration panel                                                                                       |
| loginPanel()                                                                                                                                        | void               | Loads the login panel                                                                                              |
| userPanel()                                                                                                                                         | JPanel             | Loads the side panel of users and searches                                                                         |
| getChatMessages()                                                                                                                                   | void               | Gets all the possible chats                                                                                        |
| chatPanel()                                                                                                                                         | JPanel             | Creates a panel for chats                                                                                          |
| updateChatDisplay(Profile user)                                                                                                                     | JList<String>      | Updates the Chat display                                                                                           |
| viewUserPanel()                                                                                                                                     | JPanel             | Loads the view user panel                                                                                          |
| primaryPanel()                                                                                                                                      | void               | Loads the primary panel to interface with user                                                                     |
| updateUserDisplay(ArrayList<Profile> profiles)                                                                                                      | void               | Updates the users that are displayed                                                                               |
| editProfilePane()                                                                                                                                   | JPanel             | Loads the edit profile panel                                                                                       |

## Testing
All of these classes are tested in RunLocalTest.java, where all methods are rigorously tested to make sure they function
properly and also so that they error properly for invalid inputs.
The Client class also includes a main method so that we could test it without having to test the Network I/O.

### OBSOLETE - Instructions for Detailed Testing of Network IO without GUI
_This is an obsolete procedure but maintained in the README in case there ever is a need to revert the program to before
the GUI had been implemented._

For testing of the network, perform all testing within the console of your device. Quotes indicates things that appear 
in terminal. They should not be included when inputting prompts.
1) If the files "profiles.txt" and "chats.txt" exist them, delete them, they will be rewritten through the course of 
testing.
2) Run "Server.java". When prompted to load default files, type "no". Then type in "profiles.txt", "chats.txt",
"profiles.txt", and finally "chats.txt". This will load the empty files. A message that files failed to read is likely
but this is expected because the files do not exist yet. The last message you should see is "Waiting for server 
connection" which indicates the server is idling while waiting for a client to connect.
3) In a different terminal run "Client.java". You should be prompted with "New createNewUser or login?". On the terminal
associated with Server, you should see two new messages "Server connected", "Waiting for server connection" which
indicates that the server successfully connected to the client and is awaiting more clients.
4) Back on the client terminal, type "createNewUser" in order to initiate the dialog for a new account. Enter the 
following: "User1", "Password123!", "Password123!", "User One", "true". This creates new account with username User1,
password Password123!, display name User One and set to receive all messages. You should now see "Enter action:". On 
this menu, actions can be executed by typing the appropriate command. Hitting return will load all conversations sent to
this user. View the profile you created by typing "viewProfile", "User1". This shows the display name and username of 
any given account username entered when prompted.
5) No other profiles exist yet so end the program by typing "exit". Server should continue running. Run "Client.java"
again and follow prompt 4 again with the following prompts: "User2", "Password123!", "Password123!", "User Two", "true"
6) Send a message from User2 to User1 by typing: "sendMessage", "User1", "This is my first message!". Press enter to
view the messages.
7) Either start a new Client terminal by running "Client.java" or switch users by typing "logout" and then login with the 
prompts to follow. For remainder of this testing, I will be using a second terminal so if you choose to only use one, 
logout and login as needed
8) Type "login", "User1", "Password123!" in order to log back into User1. View User1's messages by pressing enter. We 
want to reply to User2 so follow the steps from step 6 but with "User2" to send the following messages "No way this is
my first message too!", "This is super duper cool!". View messages by pressing enter, the three messages should be
visible now. Go back to the User2 terminal and press enter to see the new messages (if the terminal has been idling you 
may need to press enter again to wake it up).
9) Test the ability to edit messages by entering "editMessage" in the User1 terminal. You will be prompted with a chat index, this number is 
the same number viewed next to chat when viewing chats, type 0 to edit the messages in the first chat. Similarly, a 
prompt for message index will appear, type 2 to edit the message "This is super-duper cool!". For the new message, type
"This is super-duper extra cool! Wowzer!". View messages to see changes.
10) Delete messages by using the same indexing as used to edit messages. In the User1 terminal, type "deleteMessage", 
"0", "2" to delete the message that was just edited.
11) Now let's edit profiles. On User1's terminal, type "editProfile", "Receive All", "false". Display name and password 
can similarly be changed by following the prompts on the screen in the editProfile menu. Now when you press enter on the
User1 screen, note how no messages appear because User1 is not friends with User2. 
12) Add User2 as a friend by typing "friendUser", "User2". Now when you press enter, you can once again see the 
messages.
13) Blocking users can be done similarly. On the User2 terminal, let's have User2 block User1 (poor User1...they just
wanted a friend :( ). Do this by typing "blockUser", "User1". This will block User1 and so now when viewing User2's
messages, User1 will not show up because they have been blocked. 
14) Undo both of these processes by following the same steps as 12 and 13 but instead typing "unblockUser" and 
"unfriendUser". Now fix User2's rudeness by friending User1 by following step 12 but typing "User1". User1 accepts the 
apology and should friend them back by following the same process as step 12.
15) Now stop all running programs in any way you choose. Restart the server by following step 2. Log into User1 and 
User2 on two different terminals again (Their passwords are Password123!). View that their messages were saved even
after shutdown by viewing messages.
16) After being offline for a bit, User1 had some time to think and actually realized they think they need a break from 
social media and so are deleting there account. Do this by typing "deleteProfile" and then typing "yes". They are now 
gone forever and so can end their terminal by typing "exit".
17) User2 notices that all their messages with User1 disappeared (because their account and thus all chats associated 
with the account) and so they want to check if their account even still exists. Type "searchUsers" followed by "User1"
and note that their are no results. In contrast, if they search for themselves by typing User2, they show up and see
their profile in the form "Display Name-Username".

This was a brief walkthrough of all the Network IO methods. Feel free to experiment around, everything should protected
against invalid inputs so that shouldn't be a concern. In addition, if you load the default profiles by typing "yes" on
the first prompt when loading the servers, you'll open a server that already has an Admin with password "Admin123!" and 
User1 - User4 with password "Password123!". Some messages have already been sent and some users are already friends so
feel free to experiment!.

### Instructions for Detailed Testing of Network IO with GUI
For testing of the entire program, perform all testing within the console of your device. Quotes indicates things that 
appear in terminal or on the GUI. They should not be included when inputting prompts unless otherwise noted.

1) If the files "profiles.txt" and "chats.txt" exist them, delete them, they will be rewritten through the course of
testing.
2) Run "Server.java". When prompted in terminal to load default files, type "no". Then type in "profiles.txt", 
"chats.txt", "profiles.txt", and finally "chats.txt". This will load the empty files. A message that files failed to 
read is likely but this is expected because the files do not exist yet. The last message you should see is "Waiting for 
server connection" which indicates the server is idling while waiting for a client to connect.
3) In a different terminal run "Client.java". A GUI should appear with a login and register button. On the terminal
associated with Server, you should see two new messages "Server connected", "Waiting for server connection" which
indicates that the server successfully connected to the client and is awaiting more clients.
4) On the client GUI, click "Register" in order to initiate creating a new account. Enter the following into the fields:
"User1", "Password123!", "Password123!", "UserOne" and do NOT click the checkbox for receive all. Then click
"Register". This creates new account with username User1, password Password123!, display name UserOne and set to not
receive all messages. 
5) The GUI should now appear. Confirm no other users exist yet by going to the drop-down menu currently displaying
"Friends" in the upper left and change it to "Search". In the text box below the drop-down menu, type "U" and click
"Search". There should be no changes as no other users currently exist.
6) Click "Logout" and create a new process in the same process as step 3 but using: "User2", "Password123!", 
"Password123!", "UserToo" and DO click the checkbox for receive all. Note the typo is intentional in the display name.
7) Fix the typo in the username by clicking "Edit your Profile", typing "User Two" in the top box and clicking the 
button next to the text field. Note the new change appearing in the frame title and in the panel. 
8) User2 wants to friend User1 so search for "U" using the same procedure in step 5 but now "User One" should appear in 
the list below. Select "UserOne", click the "View" button in the lower right and then select "Friend". The GUI should
revert to the original panel but now User One should appear in the friend list.
9) User2 wants to try and send a message so in they click "UserOne", click "Chat" and type "This is my first message!". 
When they hit send, they get an error because User1 is set to receive only from friends and User1 is not friends
with User2 even though User2 is friends with User1 (friending is a single direction connection).
10) End the program by clicking the x in the upper right, then ending the server by stopping the program. Restart the 
server following step 2. No error should appear this time.
11) This time click "Login" and enter "User1" "Password123!" to log into User1's account. Search for User2 and add them
as a friend following step 8. Now sends the messages "Sorry about that!" and "Hi their!" to User2. User1 realizes their 
typo and clicks on their message, clicks "Edit", types "Hi there", and clicks ok to fix their typo.
12) Log out and log in to User2 in the same GUI. Click on "UserOne" and select "Chat" to view the chat's User1 sent.
User2 doesn't like how nonchalant User1 is and so replies "It's not ok" and "Stupid". They realize maybe they shouldn't
have said "Stupid" so they quickly select the message and then click delete to undo the sending.
13) Just to be safe. Then also block User1 by viewing User1's profile and selecting block. Now they no longer see User1
in the friends list but they do see them in the blocked list.
14) Log out of User2 and log back into User1. User1 secretly has superpowers and knows everything ever said about them
and they are very sad, so they try to send ":(" but because they have been blocked they can't send the message.
14) User1 is so sad they decide they want to leave social media forever and so they delete their profile, so they click
"Edit your Profile" and "Delete Profile", confirming their choices as needed.
15) Log back into User2 and go to the blocked tab. Notice how User1 still exists in case User1 ever decides to come back
but not search for User1 and notices how they no longer exist.

This was a brief walkthrough of all the Network IO methods. Feel free to experiment around, everything should be 
protected against invalid inputs so that shouldn't be a concern. In addition, if you load the default profiles by typing
"yes" on the first prompt when loading the servers, you'll open a server that already has User1 - User4 with password 
"Password123!". Some messages have already been sent and some users are already friends so feel free to experiment!.