package com.db.edu.server;

import com.db.edu.server.worker.ClientWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UsersController {
    private static HashMap<Integer, ClientWorker> workerConnections = new HashMap<>();

    public static void sendMessageToUser(String message, Integer id) {
        workerConnections.get(id).sendMessage(message);
    }

    public static void sendAllMessagesToUser(List<String> messages, Integer id) {
        ClientWorker worker = workerConnections.get(id);

        for (String message : messages) {
            worker.sendMessage(message);
        }
    }

    public static void sendMessageToAllUsers(String message, Set<Integer> userIds) {
        for (int id : userIds) {
            workerConnections.get(id).sendMessage(message);
        }
    }

    public static void addUserConnection(int id, ClientWorker worker) {
        workerConnections.put(id, worker);
    }

    public static boolean isNicknameTaken(String nickname) {
        return workerConnections.values().stream()
                .map(worker -> worker.getUser().getNickname())
                .anyMatch(nickname::equals);
    }

    public static void deleteAllUsers() {
        for (ClientWorker worker: workerConnections.values()) {
            worker.stopRunning();
        }
    }

    public static void disconnectUser(int id) {
        ClientWorker worker = workerConnections.remove(id);
        worker.stopRunning();
    }
}
