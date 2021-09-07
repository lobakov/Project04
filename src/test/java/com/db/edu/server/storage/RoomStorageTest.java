package com.db.edu.server.storage;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import static com.db.edu.server.storage.RoomStorage.*;
import static org.junit.jupiter.api.Assertions.*;

class RoomStorageTest {
    String roomPath = "src/test/resources/room/";

    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }

    @Test
    void mapsMustGenerateWhenLoadAllRooms() throws IOException {
        Files.createDirectories(Paths.get(roomPath));
        Files.createFile(Paths.get(roomPath + "/room1.txt"));
        Files.createFile(Paths.get(roomPath + "/room2.txt"));

        loadAllRooms(getRoomFolder("src/test/resources/room/"));

        assertEquals(getNamesToIds().keySet(), new HashSet<>(Arrays.asList("room1", "room2")));
        assertEquals(getIdsToMembers().size(), 2);

        Files.delete(Paths.get(roomPath + "/room1.txt"));
        Files.delete(Paths.get(roomPath + "/room2.txt"));
        Files.delete(Paths.get(roomPath));
    }

    @Test
    void mapsMustGenerateWhenFolderDoesntExist() {

        loadAllRooms(getRoomFolder("src/test/resources/room/"));

        assertEquals(getNamesToIds().keySet(), new HashSet<>());
        assertEquals(getIdsToMembers().size(), 0);
    }
}