package org.example;

import java.sql.*;

public class Database {
    private String url = "jdbc:postgresql://localhost:5432/chess";
    private String username = "postgres";
    private String password = "namchamdien1";
    private Connection connection;
    // Establishes the connection to the database
    public void connect(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            System.out.println("Connection failed. Error: " + e.getMessage());
        }
    }

    // Executes a query and returns the ResultSet
    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            if (connection != null && !connection.isClosed()) {
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery(query);
            } else {
                System.out.println("Connection is closed or null. Cannot execute query.");
            }
        } catch (SQLException e) {
            System.out.println("Query execution failed. Error: " + e.getMessage());
        }
        return resultSet;
    }

    // Executes non-query SQL statements (INSERT, UPDATE, DELETE)
    public int executeUpdate(String query) {
        int rowsAffected = 0;
        try {
            Statement statement = connection.createStatement();
            rowsAffected = statement.executeUpdate(query);
            System.out.println("Query executed successfully. Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.out.println("Query execution failed. Error: " + e.getMessage());
        }
        return rowsAffected;
    }

//     Closes the connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error in closing connection: " + e.getMessage());
        }
    }

    public boolean runMain(String usr, String pw) {
        // Database credentials


        Database connector = new Database();
        connector.connect(url, username, password);

        // Example query execution - INSERT statements
        String insertQueryUsers = "SELECT pass_word FROM users WHERE user_name = '" + usr + "'" ;
        // Execute each insert query separately using executeUpdate()

        // Execute the query

        ResultSet resultSet = connector.executeQuery(insertQueryUsers);
        try {
            // Loop through the ResultSet to access each row
            while (resultSet != null && resultSet.next()) {
                String passWord  = resultSet.getString("pass_word");
                // Extract other columns as needed
                System.out.println("messageType: Login, User name: "+usr+", Password: "+ pw);
                if(!pw.equals(passWord)){
                    System.out.println("Invalid account");
                }
                else System.out.println("Login succesful");
                return pw.equals(passWord);
                // Print other information or perform operations
            }
        } catch (SQLException e) {
            System.out.println("Error processing ResultSet: " + e.getMessage());

        }
        return false;
    }


}