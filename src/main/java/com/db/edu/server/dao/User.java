package com.db.edu.server.dao;

public class User {
    private final int id;
    private String login;

    public User(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
