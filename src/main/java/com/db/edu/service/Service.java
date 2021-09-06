package com.db.edu.service;

import com.db.edu.dao.Discussion;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.db.edu.storage.DiscussionStorage.getDiscussionById;

public class Service {
    private BufferedWriter writer;
    private BufferedReader reader;

    public Service(String path) {
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new BufferedOutputStream(
                                    new FileOutputStream(path))));
            reader = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(path)))))

        } catch (FileNotFoundException ex) {
            throw new RuntimeException("File not found", ex);
        }
    }

    public void saveAndSendMessage(String message, int discussionId) {
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedMessage = dateTime + ": " + message;
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
        Discussion discussion = getDiscussionById(discussionId);
        return reader.lines().collect(Collectors.toList());
    }
}
