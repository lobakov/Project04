package com.db.edu.server.storage;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import static com.db.edu.server.storage.RoomStorage.*;
import static org.junit.jupiter.api.Assertions.*;

class RoomStorageTest {
    String roomPath = "src/test/resources/room/";
    private RoomStorage rooms;

    @BeforeEach
    void setUp() {
        rooms = new RoomStorage();
    }

    @Test
    void mapsMustGenerateWhenLoadAllRooms() throws IOException {
        Files.createDirectories(Paths.get(roomPath));
        if(!Files.exists(Paths.get(roomPath + "/room1.txt"))){
            Files.createFile(Paths.get(roomPath + "/room1.txt"));
        }

        if(!Files.exists(Paths.get(roomPath + "/room2.txt"))){
            Files.createFile(Paths.get(roomPath + "/room2.txt"));
        }

        rooms.loadAllRooms(rooms.getRoomFolder("src/test/resources/room/"));

        assertEquals(rooms.getNamesToIds().keySet(), new HashSet<>(Arrays.asList("room1", "room2")));
        assertEquals(2, rooms.getIdsToMembers().size());

        Files.delete(Paths.get(roomPath + "/room1.txt"));
        Files.delete(Paths.get(roomPath + "/room2.txt"));
        Files.delete(Paths.get(roomPath));
    }

    @Test
    void mapsMustGenerateWhenFolderDoesntExist() {

        rooms.loadAllRooms(rooms.getRoomFolder("src/test/resources/room/"));

        assertEquals(rooms.getNamesToIds().keySet(), new HashSet<>());
        assertEquals(0, rooms.getIdsToMembers().size());
    }

    @Test
    public void shouldGetFileNameCorrectlyWhenGetsDiscussionId() {
        assertEquals("src/test/resources/room/228.txt", roomPath + rooms.getFileName("228"));
    }
}