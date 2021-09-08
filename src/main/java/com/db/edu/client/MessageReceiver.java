package com.db.edu.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

class MessageReceiver extends Thread {
    private final BufferedReader in;
    private boolean stopped;

    public MessageReceiver(BufferedReader in) {
        this.in = in;
    }

    /**
     * Boolean flag for stop initiation.
     */
    public void setStop() {
        stopped = true;
        System.out.println("Reconnect please");
    }

    @Override
    public void run() {
        try {
            String message;
            do {
                message = in.readLine();
                System.out.println(message);
            } while (!stopped && message != null);

        } catch (SocketException socketException) {
            System.err.println("Can't connect to server");
            try {
                Socket socket = new Socket("localhost", 5000);
                final PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                out.println("Restart please");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
