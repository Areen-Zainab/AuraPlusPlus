package com.example.projecthr.logs;

public class ClientLog {

    private int logId;  // Unique identifier for the log entry
    private int clientId;  // The client who is affected by the change
    private String tableName;  // The name of the table where the change occurred
    private String actionType;  // Type of action (Created, Updated, Deleted)
    private int affectedRecordId;  // ID of the record in the affected table
    private String description;  // A detailed description of the action taken
    private String changeDate;  // The timestamp of when the change occurred
    private String notificationSeen;  // Whether the notification has been seen ("Yes", "No")

    // Constructor
    public ClientLog(int logId, int clientId, String tableName, String actionType,
                     int affectedRecordId, String description, String changeDate, String notificationSeen) {
        this.logId = logId;
        this.clientId = clientId;
        this.tableName = tableName;
        this.actionType = actionType;
        this.affectedRecordId = affectedRecordId;
        this.description = description;
        this.changeDate = changeDate;
        this.notificationSeen = notificationSeen;
    }

    public String getDisplayName(){
        return switch (tableName) {
            case "ProjectProposal" -> "Proposal";
            case "Projects" -> "Project";
            case "Milestones" -> "Milestone";
            case "Meetings" -> "Meeting";
            default -> "Log Updating";
        };

    }

    // Getters and setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getAffectedRecordId() {
        return affectedRecordId;
    }

    public void setAffectedRecordId(int affectedRecordId) {
        this.affectedRecordId = affectedRecordId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getNotificationSeen() {
        return notificationSeen;
    }

    public void setNotificationSeen(String notificationSeen) {
        this.notificationSeen = notificationSeen;
    }

    @Override
    public String toString() {
        return "ClientLog{" +
                "logId=" + logId +
                ", clientId=" + clientId +
                ", tableName='" + tableName + '\'' +
                ", actionType='" + actionType + '\'' +
                ", affectedRecordId=" + affectedRecordId +
                ", description='" + description + '\'' +
                ", changeDate='" + changeDate + '\'' +
                ", notificationSeen='" + notificationSeen + '\'' +
                '}';
    }
}
