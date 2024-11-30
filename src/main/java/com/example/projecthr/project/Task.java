package com.example.projecthr.project;

import java.sql.Date;
import java.sql.Timestamp;

public class Task {
    private int taskId;
    private int milestoneId;
    private String taskName;
    private String description;
    private Date deadline;
    private int assignedTo;  // References Employee or Project Manager
    private Date assignedDate;
    private String priority;
    private String status;
    private String comments;
    private String taskAttachment;
    private Timestamp updatedAt;

    // Constructor
    public Task(int taskId, int milestoneId, String taskName, String description, Date deadline,
                int assignedTo, Date assignedDate, String priority, String status, String comments,
                String taskAttachment, Timestamp updatedAt) {
        this.taskId = taskId;
        this.milestoneId = milestoneId;
        this.taskName = taskName;
        this.description = description;
        this.deadline = deadline;
        this.assignedTo = assignedTo;
        this.assignedDate = assignedDate;
        this.priority = priority;
        this.status = status;
        this.comments = comments;
        this.taskAttachment = taskAttachment;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTaskAttachment() {
        return taskAttachment;
    }

    public void setTaskAttachment(String taskAttachment) {
        this.taskAttachment = taskAttachment;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", milestoneId=" + milestoneId +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", assignedTo=" + assignedTo +
                ", assignedDate=" + assignedDate +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", comments='" + comments + '\'' +
                ", taskAttachment='" + taskAttachment + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
