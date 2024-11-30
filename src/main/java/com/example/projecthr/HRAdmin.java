package com.example.projecthr;

public class HRAdmin extends User{
    HRAdmin(int userId, String email) {
        super(userId, email);
    }

    @Override
    public boolean loadUserProfile() {
        return false;
    }

    @Override
    public void loadDashboard() {
        ProjectApplication.switchScene("ClientDashboard.fxml");
    }
}
