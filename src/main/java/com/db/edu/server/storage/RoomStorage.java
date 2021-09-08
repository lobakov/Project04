package com.db.edu.server.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomStorage {
    private Map<String, Integer> namesToIds;
    private Map<Integer, Set<Integer>> idsToMembers;
    private AtomicInteger roomIds;
    private String roomFolder;

    public RoomStorage() {
        namesToIds = new ConcurrentHashMap<>();
        idsToMembers = new ConcurrentHashMap<>();
        roomIds = new AtomicInteger(1);
        roomFolder = "";
    }

    /**
     * Upload all the names of rooms from the rooms files directory to the map.
     */
    public void loadAllRooms(File folder) {
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            String roomName = file.getName();
            roomName = roomName.substring(0, roomName.length() - 4);
            Integer roomId = namesToIds.computeIfAbsent(roomName, s -> roomIds.getAndIncrement());
            idsToMembers.putIfAbsent(roomId, new HashSet<>());
        }
    }

    public File getRoomFolder(String roomFolder) {
        this.roomFolder = roomFolder;
        Path path = Paths.get(roomFolder);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(roomFolder);
    }

    public Set<Integer> getUsersByRoomId(int roomId) {
        return idsToMembers.get(roomId);
    }

    public Integer addUserToRoom(int userId, String roomName) {
        Integer roomId = namesToIds.computeIfAbsent(roomName, s -> roomIds.getAndIncrement());
        idsToMembers.putIfAbsent(roomId, new HashSet<>());
        idsToMembers.get(roomId).add(userId);
        return roomId;
    }

    public String getFileName(String roomName) {
        return roomFolder + roomName + ".txt";
    }

    public void removeUserFromRoom(int userId, int roomId) {
        idsToMembers.get(roomId).remove(userId);
    }

    public Map<String, Integer> getNamesToIds() {
        return namesToIds;
    }

    public Map<Integer, Set<Integer>> getIdsToMembers() {
        return idsToMembers;
    }
}
