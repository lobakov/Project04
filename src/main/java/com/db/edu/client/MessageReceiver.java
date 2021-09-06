package com.db.edu.client;

import java.io.IOException;

class MessageReceiver extends Thread {
    private final Client client;
    private boolean stopped;

    public MessageReceiver(Client client) {
        this.client = client;
    }

    public void setStop() {
        stopped = true;
    }

    @Override
    public void run() {
        try {
            while (!stopped) {
                String message = client.in.readLine();
                System.out.println(message);

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
