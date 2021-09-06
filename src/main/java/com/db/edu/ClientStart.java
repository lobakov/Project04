package com.db.edu;

import com.db.edu.client.Client;

public class ClientStart {
    public static void main(String[] args) {
        Client client = new Client("localhost", 10_000);
        client.connect();
    }
}
