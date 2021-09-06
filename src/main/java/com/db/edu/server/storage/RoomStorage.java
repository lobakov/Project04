package com.db.edu.server.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomStorage {
    static private Map<String, Integer> namesToIds = new HashMap<>();
    static private Map<Integer, Set<Integer>> idsToMembers = new HashMap<>();
    static private AtomicInteger roomIds = new AtomicInteger(1);

    public static Set<Integer> getUsersById(int roomId) {
        if (idsToMembers.get(roomId) == null) {
            throw new RuntimeException("Room doesn't exist");
        }
        return idsToMembers.get(roomId);
    }

    public static Integer addUserToRoom(int userId, String roomName) {
        Integer roomId = namesToIds.computeIfAbsent(roomName, s -> roomIds.getAndIncrement());
        idsToMembers.putIfAbsent(roomId, new HashSet<>());
        idsToMembers.get(roomId).add(userId);
        return roomId;
    }

    public static void removeUserFromRoom(int userId, int roomId) {
        idsToMembers.get(roomId).remove(userId);
    }
}
