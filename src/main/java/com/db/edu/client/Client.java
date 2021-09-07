package com.db.edu.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private final String host;
    private final int port;

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
             final BufferedReader sc = new BufferedReader(new InputStreamReader(System.in))) {

            String str;

            MessageReceiver receiver = new MessageReceiver(in);
            receiver.start();

            do {
                str = sc.readLine();
                out.println(str);
            } while (!str.equals("exit"));

            receiver.setStop();
        } catch (IOException ioException) {
            System.err.println("Restart me");
        }
    }

}
