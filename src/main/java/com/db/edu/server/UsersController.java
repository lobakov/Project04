package com.db.edu.server;

import com.db.edu.server.worker.ClientWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UsersController {
    private Map<Integer, ClientWorker> workerConnections;

    public UsersController() {
        workerConnections = new ConcurrentHashMap<>();
    }

    public void sendMessageToUser(String message, Integer id) {
        workerConnections.get(id).sendMessage(message);
    }

    public void sendAllMessagesToUser(List<String> messages, Integer id) {
        ClientWorker worker = workerConnections.get(id);

        for (String message : messages) {
            worker.sendMessage(message);
        }
    }

    public void sendMessageToAllUsers(String message, Set<Integer> userIds) {
        for (int id : userIds) {
            workerConnections.get(id).sendMessage(message);
        }
    }

    public void addUserConnection(int id, ClientWorker worker) {
        workerConnections.put(id, worker);
    }

    public boolean isNicknameTaken(String nickname) {
        return workerConnections.values().stream()
                .map(worker -> worker.getUser().getNickname())
                .anyMatch(nickname::equals);
    }

    public void deleteAllUsers() {
        for (ClientWorker worker: workerConnections.values()) {
            worker.stopRunning();
        }
    }

    public void disconnectUser(int id) {
        ClientWorker worker = workerConnections.remove(id);
        worker.stopRunning();
    }
}
