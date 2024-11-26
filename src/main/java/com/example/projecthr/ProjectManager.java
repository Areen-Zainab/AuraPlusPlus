package com.example.projecthr;

import java.io.IOException;

public class ProjectManager extends User{
    ProjectManager(int userId, String email) {
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
