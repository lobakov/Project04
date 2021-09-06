package com.db.edu.service;

import com.db.edu.dao.Discussion;
import com.db.edu.dao.User;
import com.db.edu.storage.BufferStorage;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.db.edu.storage.DiscussionStorage.getDiscussionById;

public class Service {
    public void saveAndSendMessage(String message, int discussionId, User user) {
        if (message.length() > 150) {
            throw new RuntimeException();
        }
        if (user.getLogin() == null) {
            throw new UserNotIdentifiedException();
        }
        BufferedWriter writer = getWriter(getFileName(discussionId));
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedMessage = user.getLogin() + ": " + message + " (" + dateTime + ")";
        try {
            writer.write(formattedMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save message to file", e);
        }
        Discussion discussion = getDiscussionById(discussionId);
        UsersController.saveToUsers(discussion.getUsers());
    }

    public List<String> getMessagesFromDiscussion(int discussionId) {
        BufferedReader reader = getReader(getFileName(discussionId));
        Discussion discussion = getDiscussionById(discussionId);
        return reader.lines().collect(Collectors.toList());
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
}
