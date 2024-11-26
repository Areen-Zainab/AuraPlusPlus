package com.example.projecthr;

import java.util.Date;

public class Project {

    private int projectId;
    private String title;
    private int clientId;
    private int managerId;
    private Date startDate;
    private Date endDate;
    private String status;
    private String description;
    private double budget;
    private String descripPath;

    // Constructor
    public Project(int projectId, String projectName, int clientId, int managerId, Date startDate, Date endDate,
                   String status, String description, double budget, String projectProposalFilePath) {
        this.projectId = projectId;
        this.title = projectName;
        this.clientId = clientId;
        this.managerId = managerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.description = description;
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

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
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
                ", budget=" + budget +
                ", projectProposalFilePath='" + descripPath + '\'' +
                '}';
    }
}

