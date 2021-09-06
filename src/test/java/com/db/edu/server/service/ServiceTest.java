package com.db.edu.server.service;

import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.dao.User;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void serviceShouldThrowRuntimeExceptionWhenMessageIsTooLong() {
        Service sutService = new Service();
        String tooLongMessage = "To be, or not to be, that is the question:" +
                                "Whether 'tis nobler in the mind to suffer" +
                                "The slings and arrows of outrageous fortune, " +
                                "Or to take arms against a sea of troubles";

        User userStub = mock(User.class);

        assertThrows(RuntimeException.class, () -> sutService.saveAndSendMessage(tooLongMessage, 228, userStub));
    }

    @Test
    public void serviceShouldThrowUserNotIdentifiedExceptionWhenFileDoesNotExist() {
        Service sutService = new Service();

        User userStub = mock(User.class);

        assertThrows(UserNotIdentifiedException.class, () -> sutService.saveAndSendMessage("Hi!", 228, userStub));
    }

    @Test
    public void serviceShouldThrowUserNotIdentifiedExceptionWhenUserNicknameDoesNotExist() {
        Service sutService = new Service();

        User userStub = mock(User.class);
        when(userStub.getNickname()).thenReturn(null);

        assertThrows(UserNotIdentifiedException.class, () -> sutService.saveAndSendMessage("Hi!", 228, userStub));
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
    public void serviceShouldSetUserNicknameCorrectlyWhenNicknameExists() {
        Service sutService = new Service();
        User user = new User(10);

        sutService.setUserNickname("Musk", user);

        assertEquals("Musk", user.getNickname());
    }

    @Test
    public void serviceShouldGetFileNameCorrectlyWhenGetsDiscussionId() {
        Service sutService = new Service();

        assertEquals("discussion228.txt", sutService.getFileName(228));
    }

    @Test
    public void serviceShouldFormatMessageCorrectlyWhenGetsMessageAndNickname() {
        Service sutService = new Service();

        assertEquals("Musk: Hello Mars!" + " (" + LocalDateTime.now() + ")", sutService.formatMessage("Musk", "Hello Mars!"));
    }
}
