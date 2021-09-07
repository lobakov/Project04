package com.db.edu.server.storage;

import com.db.edu.server.UsersController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static com.db.edu.server.storage.RoomStorage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class RoomStorageTest {
    String roomPath = "src/test/resources/room/";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(roomPath));
        Files.createFile(Paths.get(roomPath + "/room1.txt"));
        Files.createFile(Paths.get(roomPath + "/room2.txt"));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Paths.get(roomPath + "/room1.txt"));
        Files.delete(Paths.get(roomPath + "/room2.txt"));
        Files.delete(Paths.get(roomPath));
    }

    @Test
    void mapsMustGenerateWhenLoadAllRooms() {
        loadAllRooms(getRoomFolder("src/test/resources/room/"));

        assertEquals(getNamesToIds().keySet(), Set.of("room1", "room2"));
        assertEquals(getIdsToMembers().size(), 2);
    }
}