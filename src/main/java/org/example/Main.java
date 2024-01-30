package org.example;

public class Main {
    public static void main(String[] args) {
//        Database db = new Database();
//        boolean a = db.runMain("son", "123");
//        System.out.println(a);
        ProcessClientMessage server = new ProcessClientMessage();
        server.start(5000);

    }
}