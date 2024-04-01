# CS 180 Project
## How to Run
Currently, the client side has not been implemented, so this can't be used as a proper app. You can test the server
side methods and classes which compose the database by running RunLocalTest.java.

## Submission
Phase 1 was submitted by Jared Bright on Brightspace on 04/01/2024.

## Server-Side Classes

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

## Testing
All of these classes are tested in RunLocalTest.java, where all methods are rigorously tested to make sure they function
properly and also so that they error properly for invalid inputs.