package com.db.edu.server.service;

import com.db.edu.exception.DuplicateNicknameException;
import com.db.edu.exception.MessageTooLongException;
import com.db.edu.exception.NicknameSettingException;
import com.db.edu.exception.RoomNameTooLongException;
import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.UsersController;
import com.db.edu.server.model.User;
import com.db.edu.server.storage.BufferStorage;
import com.db.edu.server.worker.ClientWorker;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.db.edu.server.storage.RoomStorage.getFileName;
import static java.nio.file.Files.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServiceTest {
    @BeforeAll
    public static void initialization() {
        mockStatic(UsersController.class);
    }

    private final String filename = "test.txt";
    private File file;

    @BeforeEach
    public void setUp() {
        file = new File(filename);
    }

    @AfterEach
    public void reset() {
        file.delete();
    }

    @Test
    public void serviceShouldThrowRuntimeExceptionWhenMessageIsTooLong() {
        Service sutService = new Service();
        String tooLongMessage = "To be, or not to be, that is the question:" +
                                "Whether 'tis nobler in the mind to suffer" +
                                "The slings and arrows of outrageous fortune, " +
                                "Or to take arms against a sea of troubles";

        User userStub = mock(User.class);

        assertThrows(MessageTooLongException.class, () -> sutService.saveAndSendMessage(tooLongMessage, userStub));
    }

    @Test
    public void serviceShouldAcceptMessageWhenMessageLengthIsAcceptable() throws MessageTooLongException {
        Service sutService = new Service();
        String acceptableMessage = "To be, or not to be, that is the question:" +
                                "Whether 'tis nobler in the mind to suffer";

        sutService.checkMessageLength(acceptableMessage);
    }

    @Test
    public void serviceShouldThrowUserNotIdentifiedExceptionWhenFileDoesNotExist() {
        Service sutService = new Service();

        User userStub = mock(User.class);

        assertThrows(UserNotIdentifiedException.class, () -> sutService.saveAndSendMessage("Hi!", userStub));
    }

    @Test
    public void serviceShouldThrowUserNotIdentifiedExceptionWhenUserNicknameDoesNotExist() {
        Service sutService = new Service();

        User userStub = mock(User.class);
        when(userStub.getNickname()).thenReturn(null);

        assertThrows(UserNotIdentifiedException.class, () -> sutService.saveAndSendMessage("Hi!", userStub));
    }

    @Test
    public void serviceShouldThrowDuplicateNicknameExceptionWhenUserNicknameExists() {
        Service sutService = new Service();
        User userStub = mock(User.class);

        when(UsersController.isNicknameTaken("Musk")).thenReturn(true);

        assertThrows(DuplicateNicknameException.class, () ->sutService.setUserNickname("Musk", userStub));
    }

    @Test
    public void serviceShouldNotThrowUserNotIdentifiedExceptionWhenUserNicknameExist() throws UserNotIdentifiedException {
        Service sutService = new Service();

        User userStub = mock(User.class);
        when(userStub.getNickname()).thenReturn("Musk");

        sutService.checkUserIdentified(userStub);
    }

    @Test
    public void serviceShouldFormatMessageCorrectlyWhenMessageExists() throws UserNotIdentifiedException {
        Service sutService = new Service();

        User userStub = mock(User.class);
        when(userStub.getNickname()).thenReturn("Musk");

        sutService.checkUserIdentified(userStub);
    }

    @Test
    public void serviceShouldSetUserNicknameCorrectlyWhenNicknameExists() throws NicknameSettingException {
        Service sutService = new Service();
        User user = new User(10, "general");
        ClientWorker clientWorkerStub = mock(ClientWorker.class);

        when(UsersController.isNicknameTaken("Musk")).thenReturn(false);
        when(clientWorkerStub.getUser()).thenReturn(user);
        UsersController.addUserConnection(10, clientWorkerStub);
        sutService.setUserNickname("Musk", user);

        assertEquals("Musk", user.getNickname());
    }

    @Test
    public void serviceShouldGetFileNameCorrectlyWhenGetsDiscussionId() {
        assertEquals("src/main/resources/room/228.txt", getFileName("228"));
    }

    @Test
    public void serviceShouldFormatMessageCorrectlyWhenGetsMessageAndNickname() {
        Service sutService = new Service();

        assertEquals("Musk: Hello Mars!" + " ("
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")",
                sutService.formatMessage("Musk", "Hello Mars!"));
    }

    @Test
    public void serviceShouldSetUserRoomWhenRoomIdDoesNotExist() throws RoomNameTooLongException {
        Service sutService = new Service();
        User user = new User(10, "general");

        sutService.setUserRoom("first room", user);

        assertEquals(1, user.getRoomId());
    }

    @Test
    public void serviceShouldResetUserRoomWhenRoomIdExists() throws RoomNameTooLongException {
        Service sutService = new Service();
        User user = new User(10, "general");

        sutService.setUserRoom("first room", user);
        sutService.setUserRoom("second room", user);

        assertEquals(2, user.getRoomId());
    }

    @Test
    public void serviceShouldSaveMessageWhenGetsMessageAndUser() throws IOException {
        Service sutService = new Service();
        Path filePath = Paths.get(filename);
        BufferedWriter bufferWrtr = new BufferedWriter(
                                    new OutputStreamWriter(
                                            new BufferedOutputStream(
                                                    new FileOutputStream(filename, false))));
        BufferStorage.save(filename, bufferWrtr);
        BufferedWriter bufferedWriter = sutService.getWriter(filename);

        sutService.saveMessage(bufferedWriter, "Hi!");

        String actualString = readAllLines(filePath)
                                        .toString()
                                        .substring(1, readAllLines(filePath).toString().length() - 1);

        assertEquals("Hi!", actualString);
    }
}
