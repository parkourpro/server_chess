package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        try {
            if (resultSet != null && resultSet.next()) {
                String passWord = resultSet.getString("pass_word");

                if (!pass.equals(passWord)) {
                    System.out.println("Invalid account");
                } else {
                    System.out.println("Login succesful");
                    // Update is_online to true for the user who successfully logs in
                    String updateOnlineStatusQuery = "UPDATE users SET is_online = true WHERE user_name = '" + usr + "'";
                    int rowsAffected = executeUpdate(updateOnlineStatusQuery);

                    if (rowsAffected > 0) {
                        System.out.println("update onlinestatus successful");
                        return true;
                    } else {
                        System.out.println("update failed. Please try again.");
                        return false;
                    }
                }

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

        try {
            if (existingUserResult != null && existingUserResult.next()) {
                System.out.println("Username already exists. Please choose a different username.");
                return false; // Username already exists
            } else {
                String insertUserQuery = "INSERT INTO users (user_name, pass_word, character_name) VALUES ('" + usr + "', '" + pass + "', '" + char_name + "')";
                int rowsAffected = executeUpdate(insertUserQuery);

                if (rowsAffected > 0) {
                    System.out.println("Signup successful");
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

    public boolean logout(String usr) {
        // Update is_online to true for the user who successfully logs in
        String updateOnlineStatusQuery = "UPDATE users SET is_online = false WHERE user_name = '" + usr + "'";
        int rowsAffected = executeUpdate(updateOnlineStatusQuery);

        if (rowsAffected > 0) {
            System.out.println("update online status successful");
            return true;
        } else {
            System.out.println("update failed. Please try again.");
            return false;

        }
    }

    public List<String> getOnlinePlayers() {
        List<String> onlinePlayers = new ArrayList<>();
        String query = "SELECT character_name FROM users WHERE is_online = true";
        ResultSet resultSet = executeQuery(query);

        try {
            while (resultSet != null && resultSet.next()) {
                String characterName = resultSet.getString("character_name");
                onlinePlayers.add(characterName);
            }
        } catch (SQLException e) {
            System.out.println("Error processing ResultSet: " + e.getMessage());
        }
//        System.out.println(onlinePlayers);
        return onlinePlayers;
    }

    public String getUserProfile(String userName) {
        String queryString = "SELECT * FROM users WHERE user_name = '" + userName + "'";
        ResultSet resultSet = executeQuery(queryString);

        try {
            if (resultSet != null && resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String characterName = resultSet.getString("character_name");
                int elo = resultSet.getInt("elo");
                int totalMatch = resultSet.getInt("total_match");
                int winMatch = resultSet.getInt("win_match");
                boolean isOnline = resultSet.getBoolean("is_online");

                return userId + "," + userName + "," + characterName + "," + elo + "," + totalMatch + "," + winMatch + "," + isOnline;
            }
        } catch (SQLException e) {
            String s = "User not found";
            return s;
        }

        return null; // Trả về null nếu không tìm thấy thông tin người chơi
    }

    public String getUserProfilec(String characterName) {
        String queryString = "SELECT * FROM users WHERE character_name = '" + characterName + "'";
        ResultSet resultSet = executeQuery(queryString);

        try {
            if (resultSet != null && resultSet.next()) {
                int elo = resultSet.getInt("elo");
                int totalMatch = resultSet.getInt("total_match");
                int winMatch = resultSet.getInt("win_match");

                return characterName + "," + elo + "," + totalMatch + "," + winMatch;
            } else {
                return "fail";
            }
        } catch (SQLException e) {
            return "User not found";
        }
    }

    public int createRoom(String username) {
        int roomId = -1;

        try {
            // Sử dụng PreparedStatement để chứa câu lệnh SQL và thực hiện nó
            String createRoomQuery = "INSERT INTO rooms (player1_id, status) " +
                    "VALUES ((SELECT user_id FROM users WHERE user_name = ?), 'waiting') " +
                    "RETURNING room_id";

            // Tạo PreparedStatement với khả năng trả về các giá trị được chọn
            PreparedStatement preparedStatement = connection.prepareStatement(createRoomQuery);
            preparedStatement.setString(1, username);

            // Thực hiện câu lệnh SQL và nhận kết quả
            ResultSet resultSet = preparedStatement.executeQuery();

            // Kiểm tra xem có dòng kết quả nào được trả về hay không
            if (resultSet.next()) {
                // Lấy giá trị room_id từ ResultSet
                roomId = resultSet.getInt("room_id");
//                System.out.println("Room created with ID: " + roomId);
            } else {
                System.out.println("Failed to create room.");
            }

            // Đóng PreparedStatement và ResultSet
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            System.out.println("Error creating room: " + e.getMessage());
        }

        return roomId;
    }

    public static void main(String[] args) {
        Database connector = new Database();
        connector.getOnlinePlayers();
//        System.out.println(connector.getUserProfile("son"));
//        connector.login("1", "1");
//        connector.signup("21", "21", "21");

    }
}