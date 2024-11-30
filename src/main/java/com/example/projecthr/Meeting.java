package com.example.projecthr;

import utility.Factory;

import java.sql.SQLException;
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

    //constructors
    public Meeting() {}
    public Meeting(int meetingId, int hostId, int recipientId, int project_id, String title, String agenda, LocalDate meetingDate,
                   LocalTime meetingTime, String location, String status,  String priority, String minutesFilePath, String cancellationReason, String rescheduleNotes) {
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
        this.status = status != null ? status : "Scheduled"; // Default value
        this.minutesFilePath = minutesFilePath;
        this.cancellationReason = cancellationReason;
        this.rescheduleNotes = rescheduleNotes;
    }

    public void saveMeeting() throws SQLException {
        String query = "INSERT INTO Meetings (host_id, project_id, recipient_id, agenda, meeting_date, " +
                "meeting_time, location, meeting_title, status, request_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Request Pending', CURRENT_TIMESTAMP)";

        Factory.getFactory().getDb().executeUpdateQuery(query, hostId, project_id, recipientId, agenda, meetingDate, meetingTime, location, title);
    }

    public boolean updateMeeting() throws SQLException {
        String updateQuery = "UPDATE Meetings SET agenda = ?, meeting_date = ?, meeting_time = ?, location = ?, meeting_title = ?, "
                + "priority = ?, status = ?, minutes_file_path = ?, cancellation_reason = ?, reschedule_notes = ? WHERE meeting_id = ?";

        Object[] params = {
                this.getAgenda(),
                this.getMeetingDate(),
                this.getMeetingTime(),
                this.getLocation(),
                this.getTitle(),
                this.getPriority(),
                this.getStatus(),
                this.getMinutesFilePath(),
                this.getCancellationReason(),
                this.getRescheduleNotes(),
                this.getMeetingId()
        };

        return Factory.getFactory().getDb().executeUpdateQuery(updateQuery, params) > 0;
    }

    // getters
    public int getMeetingId() {
        return meetingId;
    }
    public int getHostId() {
        return hostId;
    }
    public int getRecipientId() {
        return recipientId;
    }
    public int getProject_id() {
        return project_id;
    }
    public LocalDate getMeetingDate() {
        return meetingDate;
    }
    public LocalTime getMeetingTime() {
        return meetingTime;
    }
    public String getLocation() {
        return location;
    }
    public String getTitle() {
        return title;
    }
    public String getPriority() {
        return priority;
    }
    public String getAgenda() {
        return agenda;
    }
    public String getStatus() {
        return status;
    }
    public String getMinutesFilePath() {
        return minutesFilePath;
    }
    public String getCancellationReason() {
        return cancellationReason;
    }
    public String getRescheduleNotes() {
        return rescheduleNotes;
    }

    //setters
    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }
    public void setHostId(int hostId) {
        this.hostId = hostId;
    }
    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }
    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }
    public void setMeetingDate(LocalDate meetingDate) {
        this.meetingDate = meetingDate;
    }
    public void setMeetingTime(LocalTime meetingTime) {
        this.meetingTime = meetingTime;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setMinutesFilePath(String minutesFilePath) {
        this.minutesFilePath = minutesFilePath;
    }
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    public void setRescheduleNotes(String rescheduleNotes) {
        this.rescheduleNotes = rescheduleNotes;
    }
}