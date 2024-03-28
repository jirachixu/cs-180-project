# CS 180 Project

## Server-Side Classes
### Database
#### Fields
| Field Name | Type                | Access Modifier | Description                           |
|-----------|---------------------|-----------------|---------------------------------------|
| profiles  | ArrayList\<Profile> | private         | ArrayList of profiles in the database |
| chats     | ArrayList\<Chat>    | private         | ArrayList of chats in the database    |
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
### Chat
#### Fields
| Field Name | Type                | Access Modifier | Description                                 |
|------------|---------------------|-----------------|---------------------------------------------|
| messages   | ArrayList\<Message> | private         | ArrayList of messages in the chat           |
| profiles   | ArrayList\<Profile> | private         | ArrayList of profiles involved in this chat |
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
### Profile
#### Fields
| Field Name  | Type                | Access Modifier | Description                                                                       |
|-------------|---------------------|-----------------|-----------------------------------------------------------------------------------|
| username    | String              | private         | The username of the profile. This is also used for logging in.                    |
| password    | String              | private         | The password of the profile                                                       |
| displayName | String              | private         | The display name of the profile (potentially add functionality to edit this?)     |
| receiveAll  | boolean             | private         | Whether the profile can receive messages from everyone or only from their friends |
| friends     | ArrayList\<Profile> | private         | The friends of the profile (potentially add functionality to add/remove friends?) |
| blocked     | ArrayList\<Profile> | private         | Profiles which are blocked by this profile (cannot send messages to this profile) |
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
## Client
