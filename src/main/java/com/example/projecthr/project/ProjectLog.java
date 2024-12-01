package com.example.projecthr.project;

import java.sql.Timestamp;

public class ProjectLog {

    private int historyId;              // Unique identifier for each history entry
    private int projectId;              // The related project ID
    private String projectName;         // Project Name
    private int milestoneId;            // Optional milestone ID
    private String milestoneName;       // Milestone Name
    private int taskId;                 // Optional task ID
    private String taskName;            // Task Name
    private Timestamp timestamp;        // Timestamp when the action occurred
    private String actionType;
    private String description;         // Description of the action
    private int userId;                 // ID of the user who performed the action
    private String username;            // Name of the user who performed the action

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProjectHistory(int historyId, int projectId, String projectName, int milestoneId,
                                  String milestoneName, int taskId, String taskName, Timestamp timestamp,
                                  String actionType, String description, int userId, String username) {
        this.historyId = historyId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.milestoneId = milestoneId;
        this.milestoneName = milestoneName;
        this.taskId = taskId;
        this.taskName = taskName;
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.description = description;
        this.userId = userId;
        this.username = username;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDisplayName(){
        if(taskId > 0){
            return "Task " +  actionType + " - " + taskName;
        }
        else if(milestoneId > 0){
            return "Milestone " +  actionType + " - " + milestoneName;
        }
        else
            return "Project " +  actionType + " - " + projectName;
    }
}

