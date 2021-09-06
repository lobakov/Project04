package com.db.edu.server.model;

public class User {
    private final int id;
    private String nickname;
    private int roomId;

    public User(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Integer getId() {
        return id;
    }
}
