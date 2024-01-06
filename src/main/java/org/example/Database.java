package org.example;

import java.sql.*;

public class Database {
    private Connection connection;
    private static String url = "jdbc:postgresql://localhost:5432/chess";
    private static String username = "postgres";
    private static String password = "namchamdien1";
    // Establishes the connection to the database
    public void connect(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
//            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            System.out.println("Connection failed. Error: " + e.getMessage());
        }
    }

    public Database() {
        connect(url, username, password);
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

    // Closes the connection
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

    public boolean login(String usr, String pass) {
        String queryString = "SELECT pass_word FROM users WHERE user_name = '" + usr + "'";
        ResultSet resultSet = executeQuery(queryString);

        System.out.println("messageType: Login, User name: " + usr + ", Password: " + pass);

        try {
            if (resultSet != null && resultSet.next()) {
                String passWord = resultSet.getString("pass_word");

//                if (!pass.equals(passWord)) {
//                    System.out.println("Invalid account");
//                } else {
//                    System.out.println("Login succesful");
//                }

                return pass.equals(passWord);
            }
        } catch (SQLException var7) {
            System.out.println("Error processing ResultSet: " + var7.getMessage());
        }

        return false;
    }

    public boolean signup(String usr, String pass, String char_name) {
        String checkExistingUserQuery = "SELECT user_name FROM users WHERE user_name = '" + usr + "'";
        ResultSet existingUserResult = executeQuery(checkExistingUserQuery);

        System.out.println("messageType: Signup, User name: " + usr + ", Password: " + pass + ", Character name: " + char_name);

        try {
            if (existingUserResult != null && existingUserResult.next()) {
                System.out.println("Username already exists. Please choose a different username.");
                return false; // Username already exists
            } else {
                String insertUserQuery = "INSERT INTO users (user_name, pass_word, character_name) VALUES ('" + usr + "', '" + pass + "', '" + char_name + "')";
                int rowsAffected = executeUpdate(insertUserQuery);

                if (rowsAffected > 0) {
                    System.out.println("Signup successful for user: " + usr + " with character_name: " + char_name);
                    return true; // Signup successful
                } else {
                    System.out.println("Signup failed. Please try again.");
                    return false; // Signup failed
                }
            }
        } catch (SQLException e) {
            System.out.println("Error processing signup: " + e.getMessage());
            return false; // Error during signup
        }
    }

    public static void main(String[] args) {
        Database connector = new Database();

        connector.login("1", "1");
        connector.signup("21", "21", "21");
    }
}