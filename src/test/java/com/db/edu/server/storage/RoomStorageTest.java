package com.db.edu.server.storage;

import org.junit.jupiter.api.Test;

import static com.db.edu.server.storage.RoomStorage.loadAllRooms;
import static org.junit.jupiter.api.Assertions.*;

class RoomStorageTest {
    @Test
    void mapsMustGenerateWhenLoadAllRooms() {
        loadAllRooms();
    }
}