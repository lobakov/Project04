package com.db.edu.server.dao;

public class User {
    private final int id;
    private String nickname;

    public User(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getId() {
        return id;
    }
}
