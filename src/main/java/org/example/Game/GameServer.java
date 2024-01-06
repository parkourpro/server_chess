package org.example.Game;

import org.example.Database;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private Map<String, String> users; // Store user_name and pass_word (for demonstration)
    private List<String> onlinePlayers; // Store online players (for demonstration)

    public GameServer() {
        users = new HashMap<>();
        onlinePlayers = new ArrayList<>();
        // Initialize users or connect to a database here
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Handle client request in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String request = in.readLine();
            String[] parts = request.split(",");

            String messageType = parts[0].trim();

            // Handle different request types
            switch (messageType) {
                case "login":
                    String userNameLG = parts[1].trim();
                    String passwordLG = parts[2].trim();
                    boolean loginSuccess = login(userNameLG, passwordLG);
                    out.println(loginSuccess ? "success" : "fail");
                    break;
                case "signup":
                    String userNameSU = parts[1].trim();
                    String passWordSU = parts[2].trim();
                    String characterNameSU = parts[3].trim();
                    boolean signupSuccess = signUp(userNameSU, passWordSU, characterNameSU);
                    out.println(signupSuccess ? "success" : "fail");
                    break;
                case "getOnlinePlayers":
                    // Retrieve online players and send response
                    // For demonstration, sending a simple JSON string
                    out.println("{\"users\": " + onlinePlayers + "}");
                    break;
                // Handle other request types similarly
                default:
                    out.println("Invalid request");
                    break;
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean login(String userName, String password) {
        Database db = new Database();
        return db.login(userName, password);
    }

    private boolean signUp(String userName, String passWord, String characterName) {
        Database db = new Database();
        return db.signup(userName, passWord, characterName);
    }


}
