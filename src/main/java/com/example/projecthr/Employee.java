package com.example.projecthr;

import java.util.ArrayList;

public class Employee extends User{
    ArrayList<Task> tasks;  //aggregation

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
