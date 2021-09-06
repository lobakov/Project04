package com.db.edu.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Scanner sc = new Scanner(System.in);
    String str = "";
    private PrintWriter out;
    BufferedReader in;
    private Socket connection;
    private void initialize() throws IOException {
        connection = new Socket("localhohst", 10_000);
        out = new PrintWriter(connection.getOutputStream());
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    public Client() {
        try {
            initialize();

            MessageReceiver receiver = new MessageReceiver(this);
            receiver.start();

            while (!str.equals("exit")) {
                str = sc.nextLine();
                int stringEnd = str.indexOf(" ");
                String command;
                if (stringEnd != -1) {
                    command = str.substring(0, stringEnd);
                } else {
                    command = str;
                }
                switch (command) {
                    case "/hist":
                        System.out.print("Nu /hist i /hist");
                        break;
                    case "/chid":
                        System.out.print("Nu /chid i /chid");
                        break;
                    case "/snd":
                        System.out.print(str.substring(stringEnd));
                        break;
                    default:
                        System.out.println("Error");
                        break;
                }
            }
            receiver.setStop();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Problems with closing");
            e.printStackTrace();
        }
    }

}
