package com.db.edu.server.worker;

import com.db.edu.exception.CommandProcessException;
import com.db.edu.exception.DuplicateNicknameException;
import com.db.edu.exception.InvalidNicknameException;
import com.db.edu.exception.MessageTooLongException;
import com.db.edu.exception.NicknameSettingException;
import com.db.edu.exception.RoomNameTooLongException;
import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.model.User;
import com.db.edu.server.service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.containsString;

public class ClientWorkerTest {
    private PipedInputStream inputStream;
    private PipedOutputStream writer;
    private ByteArrayOutputStream outputStream;
    private Socket socketStub;
    private ClientWorker workerSut;
    private User userStub;
    private Service serviceStub;

    @BeforeEach
    public void setup() {
        outputStream = new ByteArrayOutputStream();
        socketStub = mock(Socket.class);
        writer = new PipedOutputStream();
        try {
            inputStream = new PipedInputStream(writer);
            when(socketStub.getInputStream()).thenReturn(inputStream);
            when(socketStub.getOutputStream()).thenReturn(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userStub = mock(User.class);
        serviceStub = mock(Service.class);
        workerSut = new ClientWorker(socketStub, userStub, serviceStub);
        workerSut.start();
    }

    @Test
    public void shouldSendGreeting() throws InterruptedException {
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("Welcome to the chat!"));
    }

    @Test
    public void shouldSendMessage() throws IOException, CommandProcessException, InterruptedException {
        String command = "/snd test_message" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        verify(serviceStub).saveAndSendMessage("test_message", userStub);
    }

    @Test
    public void shouldNotSendEmptyMessage() throws IOException, InterruptedException, CommandProcessException {
        String command = "/snd" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("message is empty"));
        verify(serviceStub, never()).saveAndSendMessage("", userStub);
    }

    @Test
    public void shouldHandleNotIdentifiedUser() throws IOException, InterruptedException, CommandProcessException {
        doThrow(UserNotIdentifiedException.class).when(serviceStub).saveAndSendMessage("123", userStub);
        String command = "/snd 123" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("You have not set your nickname yet"));
    }

    @Test
    public void shouldHandleTooLongMessage() throws IOException, InterruptedException, CommandProcessException {
        doThrow(MessageTooLongException.class).when(serviceStub).saveAndSendMessage("123", userStub);
        String command = "/snd 123" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("Your message is too long"));
    }

    @Test
    public void shouldRequestHistory() throws IOException, CommandProcessException, InterruptedException {
        String command = "/hist" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        verify(serviceStub).getMessagesFromRoom(userStub);
    }

    @Test
    public void shouldNotRequestHistoryWithArguments() throws IOException, CommandProcessException,
            InterruptedException {
        String command = "/hist 123" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("hist takes no arguments"));
        verify(serviceStub, never()).getMessagesFromRoom(userStub);
    }

    @Test
    public void shouldSetNickname() throws IOException, NicknameSettingException, InterruptedException {
        String command = "/chid 123" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        verify(serviceStub).setUserNickname("123", userStub);
    }

    @Test
    public void shouldNotSetEmptyNickname() throws IOException, NicknameSettingException, InterruptedException {
        String command = "/chid" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("nickname is empty"));
        verify(serviceStub, never()).setUserNickname("", userStub);
    }

    @Test
    public void shouldNotSetNicknameWithWhitespaces() throws IOException, NicknameSettingException,
            InterruptedException {
        String command = "/chid 123 123" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("nickname contains a whitespace character"));
        verify(serviceStub, never()).setUserNickname("123 123", userStub);
    }

    @Test
    public void shouldHandleDuplicateNicknames() throws IOException, NicknameSettingException, InterruptedException {
        doThrow(new DuplicateNicknameException("nickname is already taken!"))
                .when(serviceStub).setUserNickname("123", userStub);
        String command = "/chid 123" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("nickname is already taken"));
    }

    @Test
    public void shouldHandleTooLongNicknames() throws IOException, NicknameSettingException, InterruptedException {
        doThrow(new InvalidNicknameException("nickname is too long"))
                .when(serviceStub).setUserNickname("123", userStub);
        String command = "/chid 123" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("nickname is too long"));
    }

    @Test
    public void shouldChangeRoom() throws IOException, InterruptedException, RoomNameTooLongException {
        String command = "/chroom test" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        verify(serviceStub).setUserRoom("test", userStub);
    }

    @Test
    public void shouldNotChangeToRoomWithWhitespaces() throws IOException, InterruptedException,
            RoomNameTooLongException {
        String command = "/chroom test test" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("room name contains a whitespace character"));
        verify(serviceStub, never()).setUserRoom("test test", userStub);
    }

    @Test
    public void shouldHandleTooLongRoomName() throws IOException, InterruptedException,
            RoomNameTooLongException {
        doThrow(new RoomNameTooLongException())
                .when(serviceStub).setUserRoom("test", userStub);
        String command = "/chroom test" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("room name is too long"));
    }

    @Test
    public void shouldCallHelp() throws IOException, InterruptedException {
        String command = "/help" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("Available commands"));
    }

    @Test
    public void shouldNotProcessUnknownCommands() throws IOException, InterruptedException {
        String command = "random" + System.lineSeparator();
        writer.write(command.getBytes());
        Thread.sleep(2500);
        assertThat(outputStream.toString(), containsString("Unknown command"));
    }
}
