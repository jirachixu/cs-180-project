# CS 180 Project

## Server-Side Classes
### Database
#### Fields
| Field Name | Type                | Access Modifier | Description                           |
|-----------|---------------------|-----------------|---------------------------------------|
| profiles  | ArrayList\<Profile> | private         | ArrayList of profiles in the database |
| chats     | ArrayList\<Chat>    | private         | ArrayList of chats in the database    |
#### Methods
| Method Name                             | Type              | Description                                                                                     |
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

## Client
