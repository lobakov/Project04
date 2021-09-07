package com.db.edu.server.service;

import com.db.edu.exception.DuplicateNicknameException;
import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.storage.BufferStorage;
import com.db.edu.server.storage.RoomStorage;
import com.db.edu.server.UsersController;
import com.db.edu.server.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.db.edu.server.UsersController.isNicknameTaken;
import static com.db.edu.server.UsersController.sendMessageToUser;
import static com.db.edu.server.storage.RoomStorage.*;

public class Service {
    public void saveAndSendMessage(String message, User user) throws UserNotIdentifiedException {
        checkMessageLength(message);
        checkUserIdentified(user);
        String formattedMessage = formatMessage(user.getNickname(), message);
        BufferedWriter writer = getWriter(getFileName(user.getRoomName()));
        saveMessage(writer, formattedMessage);
        UsersController.sendMessageToAllUsers(formattedMessage, RoomStorage.getUsersById(user.getRoomId()));
    }

    public void getMessagesFromRoom(User user) throws UserNotIdentifiedException, IOException {
        checkUserIdentified(user);
        List<String> lines = Files.readAllLines(Paths.get(getFileName(user.getRoomName())));
        UsersController.sendAllMessagesToUser(lines, user.getId());
    }

    void saveMessage(BufferedWriter writer, String formattedMessage) {
        try {
            writer.write(formattedMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save message to file", e);
        }
    }

    String formatMessage(String nickname, String message) {
        return nickname + ": " + message + " (" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")";
    }

    public void setUserNickname(String nickname, User user) throws DuplicateNicknameException {
        if (isNicknameTaken(nickname)) {
            throw new DuplicateNicknameException();
        }
        user.setNickname(nickname);
        sendMessageToUser("Nickname successfully set!", user.getId());
    }

    public void setUserRoom(String roomName, User user) {
        if (user.getRoomId() != 0) {
            removeUserFromRoom(user.getId(), user.getRoomId());
        }
        Integer roomId = addUserToRoom(user.getId(), roomName);
        user.setRoomId(roomId);
        user.setRoomName(roomName);
        sendMessageToUser("Joined #" + roomName + "!", user.getId());
    }

    BufferedWriter getWriter(String fileName) {
        BufferedWriter writer = BufferStorage.getBufferedWriterByFileName(fileName);
        if (writer == null) {
            try {
                writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new BufferedOutputStream(
                                        new FileOutputStream(fileName, true))));
                BufferStorage.save(fileName, writer);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found", e);
            }
        }
        return writer;
    }

    void checkUserIdentified(User user) throws UserNotIdentifiedException {
        if (user.getNickname() == null) {
            throw new UserNotIdentifiedException();
        }
    }

    void checkMessageLength(String message) {
        if (message.length() > 150) {
            throw new RuntimeException();
        }
    }
}