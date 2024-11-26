package com.example.projecthr;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

//Singleton - Adapter Class
public class ProjectUtility {
    private static ProjectUtility instance;

    private ProjectUtility(){}
    public static ProjectUtility getInstance(){
        if(instance == null){
            instance = new ProjectUtility();
        }
        return instance;
    }

    public ArrayList<Milestone> loadProjectMilestones(int project_id) {
        ArrayList<Milestone> milestones = new ArrayList<>();
        String query = "SELECT * FROM Milestones WHERE project_id = ?";
        ResultSet resultSet = null;

        try {
            resultSet = Factory.getFactory().getDb().executeSelectQuery(query, project_id);
            try {
                while (resultSet.next()) {
                    // Create a Milestone object from the result set
                    int milestoneID = resultSet.getInt("milestoneID");
                    int projectID = resultSet.getInt("project_id");
                    String milestoneName = resultSet.getString("milestone_name");
                    String description = resultSet.getString("description");
                    Date startDate = resultSet.getDate("start_date");
                    Date endDate = resultSet.getDate("end_date");
                    String comments = resultSet.getString("comments");
                    String status = resultSet.getString("status");
                    String priority = resultSet.getString("priority");
                    Timestamp updated_at = resultSet.getTimestamp("updated_at");
                    Timestamp created_at = resultSet.getTimestamp("created_at");

                    // Add the milestone to the list
                    milestones.add(new Milestone(milestoneID, projectID, milestoneName, description, startDate, endDate, comments, status, priority, created_at, updated_at));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Factory.getFactory().getDb().closeResources(resultSet, null, null);
        }

        return milestones;
    }

    public ProjectManager getProjectManager(int manager_id) {
        return new ProjectManager(manager_id);
    }

    public ArrayList<Meeting> getProjectMeetings(int project_id) {
        ArrayList<Meeting> meetings = new ArrayList<>();
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM Meetings WHERE project_id = ? ORDER BY meeting_date DESC, meeting_time DESC";
            resultSet = Factory.getFactory().getDb().executeSelectQuery(query, project_id);

            while (resultSet != null && resultSet.next()) {
                Meeting meeting = new Meeting();
                meeting.setMeetingId(resultSet.getInt("meeting_id"));
                meeting.setHostId(resultSet.getInt("host_id"));
                meeting.setRecipientId(resultSet.getInt("recipient_id"));
                meeting.setProject_id(resultSet.getInt("project_id"));
                meeting.setAgenda(resultSet.getString("agenda"));
                meeting.setMeetingDate(resultSet.getDate("meeting_date").toLocalDate());
                meeting.setMeetingTime(resultSet.getTime("meeting_time").toLocalTime());
                meeting.setLocation(resultSet.getString("location"));
                meeting.setTitle(resultSet.getString("meeting_title"));
                meeting.setPriority(resultSet.getString("priority"));
                meeting.setStatus(resultSet.getString("status"));
                meeting.setMinutesFilePath(resultSet.getString("minutes_file_path"));
                meeting.setCancellationReason(resultSet.getString("cancellation_reason"));

                meetings.add(meeting);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Factory.getFactory().getDb().closeResources(resultSet, null, connection);
        }
        return meetings;
    }
}
