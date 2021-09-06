package com.db.edu.server;

import com.db.edu.server.service.Service;
import com.db.edu.server.storage.RoomStorage;
import com.db.edu.server.worker.ClientWorker;
import com.db.edu.server.dao.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    public static void main(String[] args) {
        AtomicInteger ids = new AtomicInteger();
        Service userService = new Service();
        try (final ServerSocket listener = new ServerSocket(10000)) {
            while (true) {
                Socket userSocket = listener.accept();
                int id = ids.getAndIncrement();
                User user = new User(id);
                ClientWorker worker = new ClientWorker(userSocket, user, userService);
                UsersController.addUserConnection(id, worker);
                userService.setUserRoom("general", user);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
