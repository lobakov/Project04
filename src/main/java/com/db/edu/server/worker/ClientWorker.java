package com.db.edu.server.worker;

import com.db.edu.server.dao.User;
import com.db.edu.exception.InvalidNicknameException;
import com.db.edu.exception.UnknownCommandException;
import com.db.edu.exception.UserNotIdentifiedException;
import com.db.edu.server.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWorker extends Thread {
    private Socket socket;
    private Service userService;
    private User user;
    private int discussionId;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running;

    public ClientWorker(Socket socket, User user, Service userService) {
        this.socket = socket;
        this.userService = userService;
        this.user = user;
        this.running = true;
        this.discussionId = 1;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void run() {
        sendGreeting();
        while (running) {
            try {
                processCommand(in.readLine());
            } catch (IOException e) {
                e.printStackTrace(System.err);
            } catch (UserNotIdentifiedException e) {
                sendMessage("You have not set your nickname yet! Please do so by using '/chid nickname'.");
            } catch (UnknownCommandException e) {
                sendMessage("Unknown command! To get all possible commands, use the '/help' command.");
            } catch (InvalidNicknameException e) {
                sendMessage("Your nickname contains a whitespace character! Please choose a nickname " +
                        "without any whitespace characters.");
            }
        }
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void sendGreeting() {
        sendMessage("Welcome to the chat!");
        sendMessage("Before you start chatting, please set your nickname using the '/chid nickname' command.");
        help();
    }

    private void help() {
        sendMessage("Available commands:");
        sendMessage("/help - list all possible commands");
        sendMessage("/snd message - sends a message to all users in the chat");
        sendMessage("/hist - get the full chat history");
        sendMessage("/chid nickname - set your nickname");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void stopRunning() {
        this.running = false;
    }

    private void processCommand(String command) throws UserNotIdentifiedException,
            UnknownCommandException, InvalidNicknameException {
        String[] tokens = command.trim().split("\\s+");
        String commandType = tokens[0];

        switch (commandType) {
            case "/hist":
                if (tokens.length > 1) {
                    throw new UnknownCommandException();
                }
                userService.getMessagesFromDiscussion(discussionId, user);
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
            case "/snd":
                if (tokens.length == 1) {
                    throw new UnknownCommandException();
                }
                userService.saveAndSendMessage(extractMessage(command), discussionId, user);
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
