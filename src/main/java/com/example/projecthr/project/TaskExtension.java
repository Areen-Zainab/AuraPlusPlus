package com.example.projecthr.project;

import java.sql.Date;

public class TaskExtension {
    private int extensionId;
    private int taskId;
    private int requestedBy;
    private String reason;
    private Date newDeadline;
    private String status;

    // Constructor
    public TaskExtension(int extensionId, int taskId, int requestedBy, String reason, Date newDeadline, String status) {
        this.extensionId = extensionId;
        this.taskId = taskId;
        this.requestedBy = requestedBy;
        this.reason = reason;
        this.newDeadline = newDeadline;
        this.status = status;
    }

    // Getters and Setters
    public int getExtensionId() {
        return extensionId;
    }

    public void setExtensionId(int extensionId) {
        this.extensionId = extensionId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(int requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getNewDeadline() {
        return newDeadline;
    }

    public void setNewDeadline(Date newDeadline) {
        this.newDeadline = newDeadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return "TaskExtension{" +
                "extensionId=" + extensionId +
                ", taskId=" + taskId +
                ", requestedBy=" + requestedBy +
                ", reason='" + reason + '\'' +
                ", newDeadline=" + newDeadline +
                ", status='" + status + '\'' +
                '}';
    }
}
