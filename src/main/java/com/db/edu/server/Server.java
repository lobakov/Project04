package com.db.edu.server;

import com.db.edu.server.service.Service;
import com.db.edu.server.storage.BufferStorage;
import com.db.edu.server.storage.RoomStorage;
import com.db.edu.server.worker.ClientWorker;
import com.db.edu.server.model.User;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    public static void main(String[] args) {
        AtomicInteger ids = new AtomicInteger();
        BufferStorage buffers = new BufferStorage();
        RoomStorage rooms = new RoomStorage();
        UsersController controller = new UsersController();
        Service userService = new Service(buffers, rooms, controller);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            controller.deleteAllUsers();
            buffers.closeAllBuffers();
        }));
        rooms.loadAllRooms(rooms.getRoomFolder("src/main/resources/room/"));
        try (final ServerSocket listener = new ServerSocket(10000)) {
            while (true) {
                Socket userSocket = listener.accept();
                int id = ids.getAndIncrement();
                User user = new User(id, "general");
                ClientWorker worker = new ClientWorker(userSocket, user, userService);
                worker.start();
                controller.addUserConnection(id, worker);
                userService.setUserRoom("general", user);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
