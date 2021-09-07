package com.db.edu.server.service;

import com.db.edu.exception.DuplicateNicknameException;
import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.UsersController;
import com.db.edu.server.model.User;
import com.db.edu.server.worker.ClientWorker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.db.edu.server.storage.RoomStorage.getFileName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceTest {

    @Test
    public void serviceShouldThrowRuntimeExceptionWhenMessageIsTooLong() {
        Service sutService = new Service();
        String tooLongMessage = "To be, or not to be, that is the question:" +
                                "Whether 'tis nobler in the mind to suffer" +
                                "The slings and arrows of outrageous fortune, " +
                                "Or to take arms against a sea of troubles";

        User userStub = mock(User.class);

        assertThrows(RuntimeException.class, () -> sutService.saveAndSendMessage(tooLongMessage, userStub));
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
    public void serviceShouldSetUserNicknameCorrectlyWhenNicknameExists() throws DuplicateNicknameException {
        Service sutService = new Service();
        User user = new User(10, "general");
        ClientWorker clientWorkerStub = mock(ClientWorker.class);

        UsersController.addUserConnection(10, clientWorkerStub);
        sutService.setUserNickname("Musk", user);

        assertEquals("Musk", user.getNickname());
    }

    @Test
    public void serviceShouldGetFileNameCorrectlyWhenGetsDiscussionId() {
        Service sutService = new Service();

        assertEquals("src/main/resources/228.txt", getFileName("228"));
    }

    @Test
    public void serviceShouldFormatMessageCorrectlyWhenGetsMessageAndNickname() {
        Service sutService = new Service();

        assertEquals("Musk: Hello Mars!" + " ("
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")",
                sutService.formatMessage("Musk", "Hello Mars!"));
    }
}
