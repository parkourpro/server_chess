package org.example;

import org.example.Database;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessClientMessage {

    //create database and connect to it
    private final Database db = new Database();
    public ProcessClientMessage() {
        // Initialize users or connect to a database here
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
//                System.out.println("New client connected: " + clientSocket);

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
            System.out.println("client message: "+request);
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
                case "logout":
                    String userNameLO = parts[1].trim();
                    boolean logoutSuccess = logOut(userNameLO);
                    out.println(logoutSuccess ? "success" : "fail");
                    break;
                case "profile":
                    String userNamePR = parts[1].trim();
                    String user = getProfile(userNamePR);
                    out.println(user);
                    break;
                case "profilec":
                    String characternamePRC = parts[1].trim();
                    String userc = getProfilec(characternamePRC);
                    out.println(userc);
                    break;
                case "getOnlinePlayers":
                    List <String> listOnlinePlayers = new ArrayList<>();
                    listOnlinePlayers = getOnlinePlayers();
                    out.println(listOnlinePlayers);
                    break;
                case "createroom":
                    String usernameCR = parts[1].trim();
                    int roomId = createRoom(usernameCR);
                    if (roomId != -1) {
                        out.println("roomcreated," + roomId);
                    } else {
                        out.println("roomcreatefail");
                    }
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
        return db.login(userName, password);
    }

    private boolean signUp(String userName, String passWord, String characterName) {
        return db.signup(userName, passWord, characterName);
    }
    private boolean logOut(String userName) {
        return db.logout(userName);
    }
    private List<String> getOnlinePlayers() {
        return db.getOnlinePlayers();
    }
    private String getProfile(String userName) {
        return db.getUserProfile(userName);
    }
    private String getProfilec(String characterName) {
        return db.getUserProfilec(characterName);
    }
    private int createRoom(String userName){
        return db.createRoom(userName);
    }

}
