package com.example.projecthr;

import java.io.IOException;

public class ProjectManager extends User{
    private String department;
    private int experienceYears;

    ProjectManager(int userId, String email) {
        super(userId, email);
    }
    ProjectManager(int userId){
        super(userId);
        loadUserProfile();
    }

    @Override
    public boolean loadUserProfile() {
        return super.loadUserProfile() && Factory.getFactory().getManagerServices().loadProfile(this);
    }

    @Override
    public void loadDashboard() {
        ProjectApplication.switchScene("ClientDashboard.fxml", 1000, 600);
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
    public String getDepartment() {
        return department;
    }
    public int getExperienceYears() {
        return experienceYears;
    }
}
