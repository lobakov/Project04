package com.db.edu.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

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
    }

    @Override
    public void run() {
        try {
            while (!stopped) {
                String message = in.readLine();
                if (message != null) {
                    System.out.println(message);
                } else {
                    setStop();
                    throw new SocketException();
                }

            }
        } catch (SocketException socketException) {
            System.err.println("Can't connect to server");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
