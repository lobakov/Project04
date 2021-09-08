package com.db.edu;

import com.db.edu.server.Server;

import java.util.Scanner;

public class ServerStart {
    public static void main(String[] args) {
        Server server = new Server();
        server.setDaemon(true);

        server.start();

        Thread exitThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (scanner.hasNext()) {
                    String s = scanner.next();
                    if (s.equals("/exit")) {
                        server.interrupt();
                        System.out.println("Server shut down.");
                        return;
                    }
                }
            }
        });
        exitThread.start();
    }
}
