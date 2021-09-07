package com.db.edu.server.storage;

import java.io.File;
import java.util.*;
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

    public static void loadAllRooms() {
        File folder = new File("src/main/resources/room");
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            String roomName = file.getName();
            roomName = roomName.substring(0, roomName.length() - 4);
            Integer roomId = namesToIds.computeIfAbsent(roomName, s -> roomIds.getAndIncrement());
            idsToMembers.putIfAbsent(roomId, new HashSet<>());
        }
    }

    public static String getFileName(String roomName) {
        return "src/main/resources/room/" + roomName + ".txt";
    }

    public static void removeUserFromRoom(int userId, int roomId) {
        idsToMembers.get(roomId).remove(userId);
    }
}
