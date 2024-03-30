import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

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
        public void testMessageMethods() {
            Profile p1 = new Profile("first", "password", "world", true, null, null);
            Profile p2 = new Profile("second", "password", "hello", true, null, null);
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
            Profile p1 = new Profile("first", "password", "world", true, null, null);
            Profile p2 = new Profile("second", "password", "hello", true, null, null);
            Profile p3 = new Profile("third", "password", "goodbye", false, null, null);
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
        public void testProfileMethods() {

        }

        @Test(timeout = 1000)
        public void testDatabaseMethods() {

        }

    }
}
