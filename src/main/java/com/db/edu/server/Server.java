package com.db.edu.server;

import com.db.edu.server.service.Service;
import com.db.edu.server.worker.ClientWorker;
import com.db.edu.server.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static com.db.edu.server.storage.RoomStorage.getRoomFolder;
import static com.db.edu.server.storage.RoomStorage.loadAllRooms;

public class Server {
    public static void main(String[] args) {
        AtomicInteger ids = new AtomicInteger();
        Service userService = new Service();
        loadAllRooms(getRoomFolder("src/main/resources/room/"));
        try (final ServerSocket listener = new ServerSocket(10000)) {
            while (true) {
                Socket userSocket = listener.accept();
                int id = ids.getAndIncrement();
                User user = new User(id, "general");
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
