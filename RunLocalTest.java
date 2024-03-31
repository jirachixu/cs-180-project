import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(Enclosed.class)
public class RunLocalTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("No errors - Tests ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    public static class TestCase {
        @Test(timeout = 1000)
        public void MessageClassDeclarationTest() {
            Class<?> clazz;
            int modifiers;
            Class<?> superclass;
            Class<?>[] superinterfaces;

            clazz = Message.class;
            modifiers = clazz.getModifiers();
            superclass = clazz.getSuperclass();
            superinterfaces = clazz.getInterfaces();

            Assert.assertTrue("Ensure that Message is public!",
                    Modifier.isPublic(modifiers));
            Assert.assertFalse("Ensure that Message is not abstract!",
                    Modifier.isAbstract(modifiers));
            Assert.assertEquals("Ensure that Message implements MessageInterface and Serializable!",
                    2, superinterfaces.length);
        }

        @Test(timeout = 1000)
        public void ChatClassDeclarationTest() {
            Class<?> clazz;
            int modifiers;
            Class<?> superclass;
            Class<?>[] superinterfaces;

            clazz = Chat.class;
            modifiers = clazz.getModifiers();
            superclass = clazz.getSuperclass();
            superinterfaces = clazz.getInterfaces();

            Assert.assertTrue("Ensure that Chat is public!",
                    Modifier.isPublic(modifiers));
            Assert.assertFalse("Ensure that Chat is not abstract!",
                    Modifier.isAbstract(modifiers));
            Assert.assertEquals("Ensure that Chat implements ChatInterface and Serializable!",
                    2, superinterfaces.length);
        }

        @Test(timeout = 1000)
        public void ProfileClassDeclarationTest() {
            Class<?> clazz;
            int modifiers;
            Class<?> superclass;
            Class<?>[] superinterfaces;

            clazz = Profile.class;
            modifiers = clazz.getModifiers();
            superclass = clazz.getSuperclass();
            superinterfaces = clazz.getInterfaces();

            Assert.assertTrue("Ensure that Profile is public!",
                    Modifier.isPublic(modifiers));
            Assert.assertFalse("Ensure that Profile is not abstract!",
                    Modifier.isAbstract(modifiers));
            Assert.assertEquals("Ensure that Profile implements ProfileInterface and Serializable!",
                    2, superinterfaces.length);
        }

        @Test(timeout = 1000)
        public void DatabaseClassDeclarationTest() {
            Class<?> clazz;
            int modifiers;
            Class<?> superclass;
            Class<?>[] superinterfaces;

            clazz = Database.class;
            modifiers = clazz.getModifiers();
            superclass = clazz.getSuperclass();
            superinterfaces = clazz.getInterfaces();

            Assert.assertTrue("Ensure that Database is public!",
                    Modifier.isPublic(modifiers));
            Assert.assertFalse("Ensure that Database is not abstract!",
                    Modifier.isAbstract(modifiers));
            Assert.assertEquals("Ensure that Database implements DatabaseInterface!",
                    1, superinterfaces.length);
        }


        @Test(timeout = 1000)
        public void testProfileMethods() {

            // Create profiles using all profile constructors
            // Create a list with a friend in it
            Profile friend = new Profile("friend", "friend", "friend", false);
            ArrayList<Profile> friends = new ArrayList<>();
            friends.add(friend);

            // Create a list with a blocked user in it
            Profile blocked = new Profile("blocked", "blocked", "blocked", true);
            ArrayList<Profile> block = new ArrayList<>();
            block.add(blocked);

            // Create a list with a pending friend in it
            Profile requested = new Profile("friend", "friend", "friend", false);
            ArrayList<Profile> request = new ArrayList<>();
            request.add(requested);

            // Test all getters for constructor with all parameters
            Profile p1 = new Profile("hello", "password", "hello world", true, friends, block, request);
            assertEquals("Make sure getUsername() works properly!", "hello", p1.getUsername());
            assertEquals("Make sure getPassword() works properly!", "password", p1.getPassword());
            assertEquals("Make sure getDisplayName() works properly!", "hello world", p1.getDisplayName());
            assertTrue("Make sure isReceiveAll() works properly!", p1.isReceiveAll());
            assertEquals("Make sure getFriends() works properly!", friends, p1.getFriends());
            assertEquals("Make sure getBlocked() works properly!", block, p1.getBlocked());

            // Test all getters for constructor with no parameters
            Profile p2 = new Profile();
            assertNull("Make sure getUsername() works properly!", p2.getUsername());
            assertNull("Make sure getPassword() works properly!", p2.getPassword());
            assertNull("Make sure getDisplayName() works properly!", p2.getDisplayName());
            assertFalse("Make sure isReceiveAll() works properly!", p2.isReceiveAll());
            assertNull("Make sure getFriends() works properly!", p2.getFriends());
            assertNull("Make sure getBlocked() works properly!", p2.getBlocked());



            p1.setDisplayName("goodbye world");
            assertEquals("Make sure setDisplayName() works properly!", "goodbye world", p1.getDisplayName());
            p1.setPassword("skibidi toilet");
            assertEquals("Make sure setPassword() works properly!", "skibidi toilet", p1.getPassword());
            p1.setReceiveAll(false);
            assertFalse("Make sure setReceiveAll() works properly!", p1.isReceiveAll());
            p1.setFriends(null);
            assertNull("Make sure setFriends() works properly!", p1.getFriends());
            p1.setBlocked(null);
            assertNull("Make sure setBlocked() works properly!", p1.getBlocked());

            p1.setFriends(friends);
            p1.setBlocked(block);

            Profile p3 = new Profile("gamer", "not gamer", "super gamer", true);
            Profile p4 = new Profile("i am losing my mind", "test cases are so annoying to write", "help me", false);

            p1.requestFriend(p3);
            p3.acceptRequest(p1);

            ArrayList<Profile> expectedFriends = new ArrayList<>();
            expectedFriends.add(friend);
            expectedFriends.add(p3);

            for (int i = 0; i < 2; i++) {
                assertEquals("Make sure addFriend() works properly!", expectedFriends.get(i), p1.getFriends().get(i));
            }

            p1.removeFriend(friend);
            assertEquals("Make sure removeFriend() works properly!", p3, p1.getFriends().get(0));

            p1.block(p4);
            ArrayList<Profile> expectedBlocked = new ArrayList<>();
            expectedBlocked.add(blocked);
            expectedBlocked.add(p4);

            for (int i = 0; i < 2; i++) {
                assertEquals("Make sure block() works properly!", expectedBlocked.get(i), p1.getBlocked().get(i));
            }

            p1.unblock(blocked);
            assertEquals("Make sure unblock() works properly!", p4, p1.getBlocked().get(0));

            assertNotEquals("Make sure equals() works properly!", p3, p1);
            Profile p5 = new Profile("hello", "this is it", "im not doing database testing you guys can cry over that", false, null, null, null);
            assertEquals("Make sure equals() works properly!", p5, p1);
        }


        @Test(timeout = 1000)
        public void testMessageMethods() {
            Profile p1 = new Profile("first", "password", "world", true);
            Profile p2 = new Profile("second", "password", "hello", true);
            Message expectedMessage = null;
            long timestamp = 0;

            try {
                expectedMessage = new Message(p1, p2, "hello world!");
                timestamp = expectedMessage.getTimestamp();
            } catch (MessageError e) {
                Assert.assertTrue("Message threw an unexpected MessageError\n" +
                        "Make sure the Message constructor is correct!", false);
            } catch (ProfileError e) {
                Assert.assertTrue("Message threw an unexpected ProfileError\n" +
                        "Make sure the Message constructor is correct!", false);
            }

            Profile actSender = expectedMessage.getSender();
            Profile actReceiver = expectedMessage.getReceiver();

            assertEquals("Ensure that getSender() returns the correct value!", p1, actSender);
            assertEquals("Ensure that getReceiver() returns the correct value!", p2, actReceiver);
            assertEquals("Ensure that getContent() returns the correct value!", "hello world!",
                    expectedMessage.getContents());
            assertEquals("Ensure that getTimestamp() returns the correct value!", timestamp,
                    expectedMessage.getTimestamp());

            try {
                expectedMessage.edit("goodbye world!");
            } catch (MessageError e) {
                Assert.assertTrue("Message threw an unexpected MessageError\n" +
                        "Make sure the edit() method is correct!", false);
            }

            assertEquals("Ensure that the edit method works correctly!",
                    "goodbye world!", expectedMessage.getContents());

            Message unequalMessage = null;
            Message equalMessage = null;

            try {
                unequalMessage = new Message(p2, p1, "womp womp");
                equalMessage = new Message(p1, p2, expectedMessage.getContents());
            } catch (MessageError e) {
                Assert.assertTrue("Message threw an unexpected MessageError\n" +
                        "Make sure the Message constructor is correct!", false);
            } catch (ProfileError e) {
                Assert.assertTrue("Message threw an unexpected ProfileError\n" +
                        "Make sure the Message constructor is correct!", false);
            }

            assertEquals("Make sure the equals method is implemented properly!",
                    expectedMessage, equalMessage);
            assertNotEquals("Make sure the equals method is implemented properly!",
                    expectedMessage, unequalMessage);

            assertEquals("Make sure the toString method is implement correctly!",
                    String.format("Message<sender=%s,receiver=%s,status=%d,timestamp=%d,contents=%s>", p1, p2,
                            expectedMessage.getStatus(), expectedMessage.getTimestamp(), expectedMessage.getContents()),
                    expectedMessage.toString());

            expectedMessage.delete();
            assertNull("Make sure the delete method is implemented correctly!", expectedMessage.getContents());
            assertEquals("Make sure the delete method is implemented correctly!", 2, expectedMessage.getStatus());
            assertNotEquals("Make sure the delete method is implemented correctly!", timestamp, expectedMessage.getTimestamp());
        }

        @Test(timeout = 1000)
        public void testChatMethods() {
            Profile p1 = new Profile("first", "password", "world", true, null, null, null);
            Profile p2 = new Profile("second", "password", "hello", true, null, null, null);
            Profile p3 = new Profile("third", "password", "goodbye", false, null, null, null);
            ArrayList<Profile> profiles = new ArrayList<>();
            profiles.add(p1);
            profiles.add(p2);
            Message m1 = null;

            try {
                m1 = new Message(p1, p2, "hello world!");
            } catch (MessageError e) {
                Assert.assertTrue("Message threw an unexpected MessageError\n" +
                        "Make sure the Message constructor is correct!", false);
            } catch (ProfileError e) {
                Assert.assertTrue("Message threw an unexpected ProfileError\n" +
                        "Make sure the Message constructor is correct!", false);
            }

            ArrayList<Message> messages = new ArrayList<>();
            messages.add(m1);

            Chat expectedChat = new Chat(m1);


            long timestamp = expectedChat.getTimestamp();

            assertEquals("Ensure that getProfiles() returns the correct value!", profiles, expectedChat.getProfiles());
            assertEquals("Ensure that getMessages() returns the correct value!", messages, expectedChat.getMessages());
            assertEquals("Ensure that getTimestamp() returns the correct value!", timestamp, expectedChat.getTimestamp());
            assertTrue("Ensure that matchesProfiles() returns the correct value!", expectedChat.matchesProfiles(p1, p2));
            assertFalse("Ensure that matches Profiles() returns the correct value!", expectedChat.matchesProfiles(p2, p3));

            try {
                expectedChat.sendMessage(new Message(p1, p2, "im the next message!"));
            } catch (MessageError e) {
                Assert.assertTrue("Message threw an unexpected MessageError\n" +
                        "Make sure the Message constructor is correct!", false);
            } catch (ProfileError e) {
                Assert.assertTrue("Message threw an unexpected ProfileError\n" +
                        "Make sure the Message constructor is correct!", false);
            }

            assertEquals("Ensure that sendMessage() is properly updating the messages array!", 2, expectedChat.getMessages().size());
            assertNotEquals("Ensure that sendMessage() is properly updating the timestamp!", timestamp, expectedChat.getTimestamp());

            Message m3 = null;

            try {
                m3 = new Message(p3, p2, "this isn't in the expectedChat!");
            } catch (MessageError e) {
                Assert.assertTrue("Message threw an unexpected MessageError\n" +
                        "Make sure the Message constructor is correct!", false);
            } catch (ProfileError e) {
                Assert.assertTrue("Message threw an unexpected ProfileError\n" +
                        "Make sure the Message constructor is correct!", false);
            }

            try {
                expectedChat.editMessage(m1, "goodbye world!");
            } catch (MessageError e) {
                Assert.assertTrue("editMessage() threw an unexpected MessageError\n" +
                        "Make sure editMessage() is correct!", false);
            }

            assertEquals("Ensure that editMessage() is properly updating the message!", "goodbye world!", m1.getContents());

            Message finalM = m3;
            assertThrows(MessageError.class, () -> expectedChat.editMessage(finalM, "i shouldn't work!"));

            try {
                expectedChat.deleteMessage(m1);
            } catch (MessageError e) {
                Assert.assertTrue("deleteMessage() threw an unexpected MessageError\n" +
                        "Make sure deleteMessage() is correct!", false);
            }

            assertEquals("Ensure that deleteMessage() is properly updating the message!", 2, m1.getStatus());
            assertThrows(MessageError.class, () -> expectedChat.deleteMessage(finalM));
        }

        @Test(timeout = 1000)
        public void testDatabaseMethods() {
            ArrayList<Profile> expectedProfiles = new ArrayList<>();

            Profile p1 = new Profile("first", "password", "world", true, null, null, null);
            Profile p2 = new Profile("second", "password", "hello", true, null, null, null);
            Profile p3 = new Profile("third", "password", "boom", true, null, null, null);

            expectedProfiles.add(p1);
            expectedProfiles.add(p2);
            expectedProfiles.add(p3);

            Message m1 = null;
            Message m2 = null;
            Message m3 = null;

            try {
                m1 = new Message(p1, p2, "hello world!");
                m2 = new Message(p2, p3, "goodbye world!");
                m3 = new Message(p1, p3, "it's a wonderful day outside!");
            } catch (MessageError e) {
                Assert.assertTrue("Message threw an unexpected MessageError\n" +
                        "Make sure the Message constructor is correct!", false);
            } catch (ProfileError e) {
                Assert.assertTrue("Message threw an unexpected ProfileError\n" +
                        "Make sure the Message constructor is correct!", false);
            }

            HashMap<String, Chat> expectedChats = new HashMap<>();

            Chat c1 = new Chat(m1);
            Chat c2 = new Chat(m2);
            Chat c3 = new Chat(m3);

            expectedChats.put(c1.getProfiles().get(0).getUsername() + c1.getProfiles().get(1).getUsername(), c1);
            expectedChats.put(c2.getProfiles().get(0).getUsername() + c2.getProfiles().get(1).getUsername(), c2);
            expectedChats.put(c3.getProfiles().get(0).getUsername() + c3.getProfiles().get(1).getUsername(), c3);

            Database database = new Database("profileTest.txt", "chatTest.txt", "profileOutTest.txt", "chatOutTest.txt");

            database.readProfile();
            database.readChat();

            ArrayList<Profile> actualProfiles = database.getProfiles();
            HashMap<String, Chat> actualChats = database.getChats();
            for (int i = 0; i < expectedProfiles.size(); i++) {
                assertEquals("Make sure readProfile() works properly!", expectedProfiles.get(i), actualProfiles.get(i));
            }
            for (Map.Entry<String, Chat> entry : expectedChats.entrySet()) {
                assertEquals("Make sure readChat() works properly!", entry.getValue().getProfiles().get(0).getUsername(), actualChats.get(entry.getKey()).getProfiles().get(0).getUsername());
            }
        }
    }
}
