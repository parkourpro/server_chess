package org.example;

import org.example.Game.GameServer;

public class Main {
    public static void main(String[] args) {
//        Database db = new Database();
//        boolean a = db.runMain("son", "123");
//        System.out.println(a);
        GameServer server = new GameServer();
        server.start(5000);

    }
}