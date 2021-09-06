package com.db.edu.server.service;

import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.storage.BufferStorage;
import com.db.edu.server.storage.DiscussionStorage;
import com.db.edu.server.UsersController;
import com.db.edu.server.dao.User;

import java.io.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class Service {
    public void saveAndSendMessage(String message, int discussionId, User user) throws UserNotIdentifiedException {
        if (message.length() > 150) {
            throw new RuntimeException();
        }
        checkUserIdentified(user);
        BufferedWriter writer = getWriter(getFileName(discussionId));
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedMessage = user.getNickname() + ": " + message + " (" + dateTime + ")";
        try {
            writer.write(formattedMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save message to file", e);
        }
        UsersController.sendMessageToAllUsers(formattedMessage, DiscussionStorage.getUsersById(discussionId));
    }

    public void getMessagesFromDiscussion(int discussionId, User user) throws UserNotIdentifiedException {
        checkUserIdentified(user);
        BufferedReader reader = getReader(getFileName(discussionId));
        UsersController.sendAllMessagesToUser(reader.lines().collect(Collectors.toList()), user.getId());
    }

    public void setUserNickname(String nickname, User user) {
        user.setNickname(nickname);
    }

    private String getFileName(int discussionId) {
        return "discussion" + discussionId + ".txt";
    }

    private BufferedWriter getWriter(String fileName) {
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

    private BufferedReader getReader(String fileName) {
        BufferedReader reader = BufferStorage.getBufferedReaderByFileName(fileName);
        if (reader == null) {
            try {
                reader = new BufferedReader(
                        new InputStreamReader(
                                new BufferedInputStream(
                                        new FileInputStream(fileName))));
                BufferStorage.save(fileName, reader);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found", e);
            }
        }
        return reader;
    }

    private void checkUserIdentified(User user) throws UserNotIdentifiedException {
        if (user.getNickname() == null) {
            throw new UserNotIdentifiedException();
        }
    }
}