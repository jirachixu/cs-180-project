# CS 180 Project

## Server-Side Classes
### Database
#### Fields
| Field Name | Type                | Access Modifier | Description                           |
|------------|---------------------|-----------------|---------------------------------------|
| profiles   | ArrayList\<Profile> | private         | ArrayList of profiles in the database |
| chats      | ArrayList\<Chat>    | private         | ArrayList of chats in the database    |
#### Methods
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
