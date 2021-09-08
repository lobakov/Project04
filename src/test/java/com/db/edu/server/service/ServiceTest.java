package com.db.edu.server.service;

import com.db.edu.exception.CommandProcessException;
import com.db.edu.exception.DuplicateNicknameException;
import com.db.edu.exception.MessageTooLongException;
import com.db.edu.exception.NicknameSettingException;
import com.db.edu.exception.RoomNameTooLongException;
import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.UsersController;
import com.db.edu.server.model.User;
import com.db.edu.server.storage.BufferStorage;
import com.db.edu.server.storage.RoomStorage;
import com.db.edu.server.worker.ClientWorker;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static java.nio.file.Files.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTest {
    @BeforeAll
    public static void initialization() {
        mockStatic(UsersController.class);
    }

    private final String filename = "test.txt";
    private File file;
    private Service sutService;
    private BufferStorage buffersStub;
    private RoomStorage roomsStub;
    private UsersController controllerStub;
    private User userStub;

    @BeforeEach
    public void setUp() {
        file = new File(filename);
        buffersStub = mock(BufferStorage.class);
        roomsStub = mock(RoomStorage.class);
        controllerStub = mock(UsersController.class);
        userStub = mock(User.class);
        sutService = new Service(buffersStub, roomsStub, controllerStub);
    }

    @AfterEach
    public void reset() {
        file.delete();
    }

    @Test
    public void shouldNotSendTooLongMessage() {
        String tooLongMessage = "To be, or not to be, that is the question:" +
                                "Whether 'tis nobler in the mind to suffer" +
                                "The slings and arrows of outrageous fortune, " +
                                "Or to take arms against a sea of troubles";

        User userStub = mock(User.class);

        assertThrows(MessageTooLongException.class, () -> sutService.saveAndSendMessage(tooLongMessage, userStub));
    }

    @Test
    public void shouldNotSendMessageFromNotIdentifiedUsers() {
        assertThrows(UserNotIdentifiedException.class, () -> sutService.saveAndSendMessage("Hi!", userStub));
    }

    @Test
    public void shouldSendMessageOfCorrectLength() throws CommandProcessException, IOException {
        String acceptableMessage = "To be, or not to be, that is the question:" +
                "Whether 'tis nobler in the mind to suffer";
        when(userStub.getNickname()).thenReturn("Musk");

        sutService.saveAndSendMessage(acceptableMessage, userStub);
        verify(controllerStub).sendMessageToAllUsers(any(String.class), anySet());
    }

    @Test
    public void shouldFormatSentMessage() throws CommandProcessException, IOException {
        String message = "123";
        when(userStub.getNickname()).thenReturn("Musk");
        String curYear = String.valueOf(LocalDateTime.now().getYear());

        sutService.saveAndSendMessage(message, userStub);
        verify(controllerStub).sendMessageToAllUsers(argThat(s ->
            s.contains("Musk") && s.contains(message) && s.contains(curYear)
        ), anySet());
    }

    @Test
    public void serviceShouldFormatMessageCorrectlyWhenGetsMessageAndNickname() {
        assertEquals("Musk: Hello Mars!" + " ("
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")",
                sutService.formatMessage("Musk", "Hello Mars!"));
    }

    @Test
    public void shouldNotSetDuplicateNickname() {
        when(controllerStub.isNicknameTaken("Musk")).thenReturn(true);

        assertThrows(DuplicateNicknameException.class, () -> sutService.setUserNickname("Musk", userStub));
    }

    @Test
    public void shouldIdentifyUserWithNickname() throws UserNotIdentifiedException {
        when(userStub.getNickname()).thenReturn("Musk");

        sutService.checkUserIdentified(userStub);
    }

    @Test
    public void serviceShouldNotPassNicknameWhenNicknameLengthIsTooLong() {
        assertTrue(sutService.isNicknameTooLong("nicknameThatIsMoreThan20Chars"));
    }

    @Test
    public void serviceShouldPassNicknameWhenNicknameLengthIsLessThan20Chars() {
        assertFalse(sutService.isNicknameTooLong("shortNick"));
    }

    @Test
    public void serviceShouldNotPassRoomNameWhenRoomNameLengthIsTooLong() {
        assertTrue(sutService.isRoomNameTooLong("roomNameThatIsMoreThan20Chars"));
    }

    @Test
    public void serviceShouldPassRoomNameWhenRoomNameLengthIsLessThan20Chars() {
        assertFalse(sutService.isRoomNameTooLong("shortRoomName"));
    }

    /* integration test
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
    */

    @Test
    public void shouldSetUserRoomIdAfterJoiningRoom() throws RoomNameTooLongException {
        sutService.setUserRoom("first room", userStub);

        verify(userStub).setRoomId(1);
    }

    /* integration test
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
    */
}
