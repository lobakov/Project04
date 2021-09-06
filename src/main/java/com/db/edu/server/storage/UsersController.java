package com.db.edu.server.storage;

import com.db.edu.server.worker.ClientWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersController {

    private static Map<Integer, ClientWorker> workers = new HashMap<>();

    public static void sendAllMessagesToUser(List<String> messages, Integer id) {
    }

    public static void sendMessageToAllUsers(String message, List<Integer> userIds) {
    }

    public static void addUserConnection(int id, ClientWorker worker) {
    }
}
