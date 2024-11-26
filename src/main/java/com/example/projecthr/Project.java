package com.example.projecthr;

import java.util.ArrayList;
import java.util.Date;

public class Project {
    ArrayList<Milestone> milestones; // composition

    private int projectId;
    private String title;   //get from proposal
    private int clientId;
    private int managerId;
    private Date startDate;
    private Date endDate;
    private String status;
    private String description; //get from proposal
    private double final_cost;
    private int budget;
    private String descripPath; //get from proposal

    // Constructor
    public Project(int projectId, String projectName, int clientId, int managerId, Date startDate, Date endDate,
                   String status, String description, double final_cost, int budget, String projectProposalFilePath) {
        this.projectId = projectId;
        this.title = projectName;
        this.clientId = clientId;
        this.managerId = managerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.description = description;
        this.final_cost = final_cost;
        this.budget = budget;
        this.descripPath = projectProposalFilePath;
    }

    // Getters and Setters
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFinal_cost() {
        return final_cost;
    }

    public void setFinal_cost(double final_cost) {
        this.final_cost = final_cost;
    }

    public String getProjectProposalFilePath() {
        return descripPath;
    }

    public void setProjectProposalFilePath(String projectProposalFilePath) {
        this.descripPath = projectProposalFilePath;
    }

    public String info() {
        return "Project{" +
                "projectId=" + projectId +
                ", projectName='" + title + '\'' +
                ", clientId=" + clientId +
                ", managerId=" + managerId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", budget=" + final_cost +
                ", projectProposalFilePath='" + descripPath + '\'' +
                '}';
    }
}

