package com.example.projecthr;

import com.example.projecthr.project.Task;

import java.util.ArrayList;

public class Employee extends User{
    ArrayList<Task> tasks;  //aggregation

    Employee(int userId, String email) {
        super(userId, email);
    }

    @Override
    public boolean loadUserProfile() {
        return super.loadUserProfile();
    }

    @Override
    public void loadDashboard() {
        loadUserProfile();
        ProjectApplication.switchScene("employeeDashboard.fxml");
    }
}
