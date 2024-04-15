# CS 180 Project
## How to Run
You can test the server side methods and classes which compose the database by running RunLocalTest.java. You can test
the client side methods and classes by following the instructions at the bottom of this README. In general, first start 
the server and load the database by either entering "yes" or "no" and indicating the files to read from. Then start the
client and follow all prompts

## Submission
Phase 1 was submitted by Jared Bright on Brightspace on 04/01/2024.
Phase 2 was submitted by Jared Bright on Brightspace on 04/15/2024.

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
| Method Name                             | Return            | Description                                                                                     |
|-----------------------------------------|-------------------|-------------------------------------------------------------------------------------------------|
| readProfile()                           | boolean           | Reads in all of the profiles from the profile file (profileIn).                                 |
| readChat()                              | boolean           | Reads in all of the chats from the chat file (chatIn).                                          |
| outputProfile()                         | boolean           | Outputs all of the profiles to the profileOut file.                                             |
| outputChat()                            | boolean           | Outputs all of the chats to the chatOut file.                                                   |
| login(String username, String password) | boolean           | If the username and password match any of the profiles, returns true. Otherwise, returns false. |
| sendMessage(Message message)            | synchronized void | Sends a message to a chat between two profiles.                                                 |
| editMessage(Message message)            | synchronized void | Edits a message in a chat between two profiles.                                                 |
| deleteMessage(Message message)          | synchronized void | Deletes a message in a chat between two profiles.                                               |



## Client-Side Classes
### Client
The Client class is responsible for getting GUI inputs from the client and sending them to the server so that it can do
processing.

#### Fields
| Field Name | Type             | Description                                     |
|------------|------------------|-------------------------------------------------|
| profile    | Profile          | The profile being used for the current client.  |
| chats      | ArrayList\<Chat> | The chats that the current client is a part of. |

#### Methods
| Method Name                                                                                                                | Return | Description                           |
|----------------------------------------------------------------------------------------------------------------------------|--------|---------------------------------------|
| createNewUser(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                | void   | Creates a new Profile for the client. |
| login(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                        | void   | Logs in to a Profile for the client.  |
| sendMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                  | void   | Sends a message to a chat.            |
| editMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer, String chatId, int messageIndex) | void   | Edits a message in a chat.            |
| deleteMessage(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                | void   | Deletes a message in a chat.          |
| deleteProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                | void   | Deletes the Profile the client is on. |
| editProfile(Scanner scan, ObjectInputStream inFromServer, ObjectOutputStream outToServer)                                  | void   | Edits the Profile the client is on.   |

## Testing
All of these classes are tested in RunLocalTest.java, where all methods are rigorously tested to make sure they function
properly and also so that they error properly for invalid inputs.
The Client class also includes a main method so that we could test it without having to test the Network I/O.

### Instructions for Detailed Testing of Network IO
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