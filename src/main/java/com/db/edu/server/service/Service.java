package com.db.edu.server.service;

import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.storage.BufferStorage;
import com.db.edu.server.storage.DiscussionStorage;
import com.db.edu.server.UsersController;
import com.db.edu.server.dao.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class Service {
    public void saveAndSendMessage(String message, int discussionId, User user) throws UserNotIdentifiedException {
        checkMessageLength(message);
        checkUserIdentified(user);
        String formattedMessage = formatMessage(user.getNickname(), message);
        BufferedWriter writer = getWriter(getFileName(discussionId));
        saveMessage(writer, formattedMessage);
        UsersController.sendMessageToAllUsers(formattedMessage, DiscussionStorage.getUsersById(discussionId));
    }

    public void getMessagesFromDiscussion(int discussionId, User user) throws UserNotIdentifiedException, IOException {
        checkUserIdentified(user);
        List<String> lines = Files.readAllLines(Paths.get(getFileName(discussionId)));
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
        return nickname + ": " + message + " (" + LocalDateTime.now() + ")";
    }

    public void setUserNickname(String nickname, User user) {
        user.setNickname(nickname);
    }

    String getFileName(int discussionId) {
        return "discussion" + discussionId + ".txt";
    }

    BufferedWriter getWriter(String fileName) {
        BufferedWriter writer = BufferStorage.getBufferedWriterByFileName(fileName);
        if (writer == null) {
            try {
                writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new BufferedOutputStream(
                                        new FileOutputStream(fileName))));
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