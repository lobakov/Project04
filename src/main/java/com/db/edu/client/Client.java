package com.db.edu.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private final String host;
    private final int port;
    private boolean stop;

    /**
     * Configure HOST and PORT for client.
     * @param host (String)
     * @param port (int)
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Set up connection with the server. Define input and output streams.
     * Settle up isolated thread for message receiving.
     */
    public void connect() {
        try (final Socket connection = new Socket(host, port);
             final PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8), true);
             final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
             final ServerSocket serverSocket = new ServerSocket(5000)) {

            String str;

            MessageReceiver receiver = new MessageReceiver(in);
            receiver.start();

            Thread listenerThread = new Thread(() -> {
                try (final Socket socket = serverSocket.accept();
                     final BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                    String commandFromReceiver = input.readLine();
                    System.out.println(commandFromReceiver);
                    stop = true;
                } catch (IOException ioException) {
                    System.err.println("Can't read from the receiver");
                }

            });
            listenerThread.start();

            do {
                str = sc.readLine();
                out.println(str);
            } while (!str.equals("exit") && !stop);

            receiver.setStop();
        } catch (IOException ioException) {
            System.err.println("Restart me");
        }
    }

}
