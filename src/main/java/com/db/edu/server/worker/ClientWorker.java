package com.db.edu.server.worker;

import com.db.edu.exception.*;
import com.db.edu.exception.CommandProcessException;
import com.db.edu.exception.DuplicateNicknameException;
import com.db.edu.exception.MessageTooLongException;
import com.db.edu.server.model.User;
import com.db.edu.server.service.Service;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ClientWorker extends Thread {
    private Socket socket;
    private Service userService;
    private User user;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running;

    public ClientWorker(Socket socket, User user, Service userService) {
        this.socket = socket;
        this.userService = userService;
        this.user = user;
        this.running = true;
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        sendGreeting();
        while (running) {
            try {
                processCommand(in.readLine());
            } catch (SocketException e) {
                stopRunning();
            } catch (UserNotIdentifiedException e) {
                sendMessage("You have not set your nickname yet! Please do so by using '/chid nickname'.");
            } catch (UnknownCommandException e) {
                sendMessage("Unknown command! To get all possible commands, use the '/help' command.");
            } catch (InvalidNicknameException e) {
                sendMessage("Your nickname contains a whitespace character! Please choose a nickname " +
                        "without any whitespace characters.");
            } catch (DuplicateNicknameException e) {
                sendMessage("Your nickname is already taken! Please choose another one.");
            } catch (MessageTooLongException e) {
                sendMessage("Your message is too long! Our chat only supports messages up to 150 symbols.");
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void sendGreeting() {
        sendMessage("Welcome to the chat!");
        sendMessage("Before you start chatting, please set your nickname using the '/chid nickname' command.");
        help();
    }

    public void help() {
        sendMessage("Available commands:");
        sendMessage("/help - list all possible commands");
        sendMessage("/snd message - sends a message to all users in the chat");
        sendMessage("/hist - get the full chat history");
        sendMessage("/chid nickname - set your nickname");
        sendMessage("/chroom roomname - change a room");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void stopRunning() {
        this.running = false;
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void processCommand(String command) throws CommandProcessException, IOException {
        String[] tokens = command.trim().split("\\s+");
        String commandType = tokens[0];

        switch (commandType) {
            case "/hist":
                if (tokens.length > 1) {
                    throw new UnknownCommandException();
                }
                userService.getMessagesFromRoom(user);
                break;
            case "/chid":
                if (tokens.length == 1) {
                    throw new UnknownCommandException();
                }
                if (tokens.length > 2) {
                    throw new InvalidNicknameException();
                }
                userService.setUserNickname(tokens[1], user);
                break;
            case "/chroom":
                if (tokens.length != 2) {
                    throw new UnknownCommandException();
                }
                userService.setUserRoom(tokens[1], user);
                break;
            case "/snd":
                if (tokens.length == 1) {
                    throw new UnknownCommandException();
                }
                userService.saveAndSendMessage(extractMessage(command), user);
                break;
            case "/help":
                help();
                break;
            default:
                throw new UnknownCommandException();
        }
    }

    private String extractMessage(String command) {
        return command.substring(command.indexOf(" ") + 1);
    }
}
