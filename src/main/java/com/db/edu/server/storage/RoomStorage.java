package com.db.edu.server.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomStorage {
    static private Map<String, Integer> namesToIds = new HashMap<>();
    static private Map<Integer, Set<Integer>> idsToMembers = new HashMap<>();
    static private AtomicInteger roomIds = new AtomicInteger(1);
    static private final String roomFolder = "src/main/resources/room/";

    /**
     * Upload all the names of rooms from the rooms files directory to the map.
     */
    public static void loadAllRooms() {
        Path path = Paths.get(roomFolder);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File folder = new File(roomFolder);
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            String roomName = file.getName();
            roomName = roomName.substring(0, roomName.length() - 4);
            Integer roomId = namesToIds.computeIfAbsent(roomName, s -> roomIds.getAndIncrement());
            idsToMembers.putIfAbsent(roomId, new HashSet<>());
        }
    }

    public static Set<Integer> getUsersByRoomId(int roomId) {
        return idsToMembers.get(roomId);
    }

    public static Integer addUserToRoom(int userId, String roomName) {
        Integer roomId = namesToIds.computeIfAbsent(roomName, s -> roomIds.getAndIncrement());
        idsToMembers.putIfAbsent(roomId, new HashSet<>());
        idsToMembers.get(roomId).add(userId);
        return roomId;
    }

    public static String getFileName(String roomName) {
        return roomFolder + roomName + ".txt";
    }

    public static void removeUserFromRoom(int userId, int roomId) {
        idsToMembers.get(roomId).remove(userId);
    }

    public static Map<String, Integer> getNamesToIds() {
        return namesToIds;
    }

    public static Map<Integer, Set<Integer>> getIdsToMembers() {
        return idsToMembers;
    }
}
