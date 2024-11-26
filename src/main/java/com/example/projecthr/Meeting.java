package com.example.projecthr;

import java.time.LocalDate;
import java.time.LocalTime;

public class Meeting {
    private int meetingId;
    private int hostId;
    private int recipientId;
    private int project_id;
    private String agenda;  //
    private LocalDate meetingDate;  //
    private LocalTime meetingTime;  //
    private String location;    //
    private String title;   //
    private String priority;    //
    private String status; //
    private String minutesFilePath; //
    private String cancellationReason;  //
    private String rescheduleNotes;

    public Meeting(int meetingId, int hostId, int recipientId, int project_id, LocalDate meetingDate,
                   LocalTime meetingTime, String location, String agenda, String status, String title, String priority, String minutesFilePath, String cancellationReason, String rescheduleNotes) {
        this.meetingId = meetingId;
        this.hostId = hostId;
        this.recipientId = recipientId;
        this.project_id = project_id;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.location = location;
        this.title = title;
        this.priority = priority;
        this.agenda = agenda;
        this.status = status != null ? status : "Scheduled"; // Default to "Scheduled"
        this.minutesFilePath = minutesFilePath;
        this.cancellationReason = cancellationReason;
        this.rescheduleNotes = rescheduleNotes;
    }

    // Getters and Setters
    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    public int getHostId() {
        return hostId;
    }
    public void setHostId(int hostId) {
        this.hostId = hostId;
    }
    public int getRecipientId() {
        return recipientId;
    }
    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }
    public int getProject_id() {
        return project_id;
    }
    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public LocalDate getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(LocalDate meetingDate) {
        this.meetingDate = meetingDate;
    }

    public LocalTime getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(LocalTime meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMinutesFilePath() {
        return minutesFilePath;
    }
    public void setMinutesFilePath(String minutesFilePath) {
        this.minutesFilePath = minutesFilePath;
    }
    public String getCancellationReason() {
        return cancellationReason;
    }
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    public String getRescheduleNotes() {
        return rescheduleNotes;
    }
    public void setRescheduleNotes(String rescheduleNotes) {
        this.rescheduleNotes = rescheduleNotes;
    }

}
