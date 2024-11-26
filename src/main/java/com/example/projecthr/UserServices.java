package com.example.projecthr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

//provide an interface for the DBHandler and User class to interact
public class UserServices {
    public static boolean isEmailUnique(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try {
            int count = Factory.getFactory().getDb().executeCountQuery(query, email);
            return count == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNewEmailUnique(String email, int userId) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?";
        try {
            int count = Factory.getFactory().getDb().executeCountQuery(query, email, userId);
            return count == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmailValid(String email) {
        if (email == null || email.isEmpty()) {
            return true; // Email must not be null or empty
        }

        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return !email.matches(emailRegex);
    }

    public boolean updateProfile(User user) {
        String updateQuery = "UPDATE users SET fname = ?, lname = ?, email = ?, password = ?, gender = ?, dob = ?, address = ?, phone_no = ? WHERE user_id = ?";

        try {
            int rowsUpdated = Factory.getFactory().getDb().executeUpdateQuery(
                    updateQuery,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getGender(),
                    user.getDob(),
                    user.getAddress(),
                    user.getPhoneNo(),
                    user.getUserId()
            );
            return rowsUpdated > 0; // Return true if any rows were updated
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadProfile(User user) {
        ResultSet userResult = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM Users WHERE user_id = ?";
            userResult = Factory.getFactory().getDb().executeSelectQuery(query, user.getUserId());
            if (userResult != null && userResult.next()) {
                user.setFirstName(userResult.getString("Fname"));
                user.setLastName(userResult.getString("Lname"));
                user.setEmail(userResult.getString("email"));
                user.setPassword(userResult.getString("password"));
                user.setGender(userResult.getString("gender"));
                user.setDob(userResult.getString("dob"));
                user.setPhoneNo(userResult.getString("phone_no"));
                user.setAddress(userResult.getString("address"));
            } else {
                return false; // User not found
            }

            return true; // Successfully loaded
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Factory.getFactory().getDb().closeResources(userResult, null, connection);
        }
    }
}
