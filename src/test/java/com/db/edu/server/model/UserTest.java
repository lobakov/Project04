package com.db.edu.server.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private final int userId = 10;
    private final String roomname = "general";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(userId, roomname);
    }

    @AfterEach
    void cleanUp() {
        user = null;
    }

    @Test
    public void userShouldGetIdWhenUserExists() {
        assertEquals(userId, user.getId());
    }

    @Test
    public void userShouldSetNicknameWhenNicknameExists() {
        user.setNickname("Musk");
        assertEquals("Musk", user.getNickname());
    }

    @Test
    public void userShouldGetNicknameWhenNicknameExists() {
        user.setNickname("Musk");
        assertEquals("Musk", user.getNickname());
    }

    @Test
    public void userShouldSetRoomIdWhenRoomIdExists() {
        user.setRoomId(10);
        assertEquals(10, user.getRoomId());
    }

    @Test
    public void userShouldGetRoomIdWhenRoomIdExists() {
        user.setRoomId(10);
        assertEquals(10, user.getRoomId());
    }

    @Test
    public void userShouldSetRoomNameWhenRoomNameExists() {
        user.setRoomName("first room");
        assertEquals("first room", user.getRoomName());
    }

    @Test
    public void userShouldGetRoomNameWhenRoomNameExists() {
        user.setRoomName("first room");
        assertEquals("first room", user.getRoomName());
    }
}
