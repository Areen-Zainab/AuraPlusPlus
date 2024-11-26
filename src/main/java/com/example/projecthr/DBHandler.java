package com.example.projecthr;
import java.sql.*;

public class DBHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/worksphere";
    private static final String USER = "root";
    private static final String PASSWORD = "hafsa7076";

    // Method to establish connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to verify credentials (Log-In)
    public User loginValidationUser(String email, String password){
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String userEmail = resultSet.getString("email");
                String role = resultSet.getString("role");

                return ProjectApplication.createUserFromRole(userId, userEmail, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no match
    }

    //Method to Create User (Client Sign-Up)
    public boolean registerClient(String firstName, String lastName, String email, String password, String companyName, boolean independent) throws SQLException {
        Connection connection = null;
        PreparedStatement userStatement = null;
        PreparedStatement clientStatement = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert into Users table
            String insertUserQuery = "INSERT INTO Users (Fname, Lname, email, password, role, join_date) VALUES (?, ?, ?, ?, 'Client', CURDATE())";
            userStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);
            userStatement.setString(1, firstName);
            userStatement.setString(2, lastName);
            userStatement.setString(3, email);
            userStatement.setString(4, password);
            userStatement.executeUpdate();

            // Get the generated user_id
            ResultSet generatedKeys = userStatement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to retrieve user ID.");
            }
            int userId = generatedKeys.getInt(1);

            // Insert into Clients table
            String insertClientQuery = "INSERT INTO Clients (user_id, company_name, independent) VALUES (?, ?, ?)";
            clientStatement = connection.prepareStatement(insertClientQuery);
            clientStatement.setInt(1, userId);
            clientStatement.setString(2, companyName);
            clientStatement.setBoolean(3, independent);
            clientStatement.executeUpdate();

            connection.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Rollback on error
            }
            throw e;
        } finally {
            // Close resources
            if (userStatement != null) userStatement.close();
            if (clientStatement != null) clientStatement.close();
            if (connection != null) connection.close();
        }
    }

    // Ensure the resources are closed properly
    public void closeResources(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Method to execute an UPDATE query
    public int executeUpdateQuery(String query, Object... params) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to execute a COUNT query
    public int executeCountQuery(String query, Object... params) throws SQLException {
        int count = 0;

        try (Connection connection = getConnection(); // Get database connection
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1); // Fetch the first column value (count)
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return count;
    }

    public ResultSet executeSelectQuery(String query, Object... params) throws SQLException {
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            resultSet = statement.executeQuery();
            return resultSet;  // Return the ResultSet to be used later
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching user details.");
        }
    }

}


