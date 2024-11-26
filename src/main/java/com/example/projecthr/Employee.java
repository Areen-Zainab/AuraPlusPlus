package com.example.projecthr;

public class Employee extends User{
    Employee(int userId, String email) {
        super(userId, email);
    }

    @Override
    public boolean loadUserProfile() {
        return false;
    }

    @Override
    public void loadDashboard() {
        ProjectApplication.switchScene("ClientDashboard.fxml", 1000, 600);
    }
}
