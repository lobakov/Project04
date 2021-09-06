package com.db.edu.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private final String HOST;
    private final int PORT;

    public Client(String host, int port) {
        HOST = host;
        PORT = port;
    }

    public void connect() {
        try (Socket connection = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(connection.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

            final Scanner sc = new Scanner(System.in);
            String str;

            MessageReceiver receiver = new MessageReceiver(in);
            receiver.start();

            do {
                str = sc.nextLine();
                out.println(str);
            } while (!str.equals("exit"));

            receiver.setStop();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
