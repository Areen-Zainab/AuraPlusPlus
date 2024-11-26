package com.example.projecthr;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

//Singleton -Utility
public class ClientServices {
    private static ClientServices instance;

    private ClientServices() {}
    public static ClientServices getInstance() {
        if(instance==null){
            instance = new ClientServices();
        }
        return instance;
    }

    public boolean loadProfile(User user) {
        ResultSet clientResult = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM Clients WHERE client_id = ?";
            clientResult = Factory.getFactory().getDb().executeSelectQuery(query, user.getUserId());
            if (clientResult != null && clientResult.next()) {
                ((Client)user).setCompanyName(clientResult.getString("company_name"));
                ((Client)user).setIndependent(clientResult.getBoolean("independent"));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Factory.getFactory().getDb().closeResources(clientResult, null, connection);
        }
    }

    public boolean updateProfile(User user) {
        String updateQuery = "UPDATE Clients SET independent = ?, company_name = ? WHERE client_id = ?";
        boolean independent = true;
        String companyName = "";
        if (user instanceof Client client) {
            independent = client.isIndependent();
            companyName = client.getCompanyName();
        } else {
            System.err.println("User is not a Client instance.");
        }
        try {
            int rowsUpdated = Factory.getFactory().getDb().executeUpdateQuery(updateQuery, independent, companyName, user.getUserId());
            return rowsUpdated > 0; // Return true if any rows were updated
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean submitNewProposal(String title, String description, int cost, String duration, String pdfPath, int clientId) {
        String query = "INSERT INTO ProjectProposal (title, description, budget, duration, pdf_path, status, submission_date, created_at, client_id) " +
                "VALUES (?, ?, ?, ?, ?, 'Pending', NOW(), NOW(), ?)";

        Object[] params = { title, description, cost, duration, pdfPath, clientId };
        try {
            return Factory.getFactory().getDb().executeUpdateQuery(query, params) > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<ProjectProposal> getProposals(int clientId) {
        String query = "SELECT pp.*, p.project_id AS project_id, p.start_date, p.end_date, p.status AS project_status, p.final_cost " +
                "FROM ProjectProposal pp " +
                "LEFT JOIN Projects p ON pp.ProposalID = p.proposal_id " +  // Join based on the proposal_id in Projects
                "WHERE pp.client_id = ?";
        ArrayList<ProjectProposal> proposals = new ArrayList<>();
        Connection connection;

        try {
            connection = DBHandler.getConnection();
            ResultSet resultSet = Factory.getFactory().getDb().executeSelectQuery(query, clientId);

            try {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    String duration = resultSet.getString("duration");
                    String pdfPath = resultSet.getString("pdf_path");
                    int budget = resultSet.getInt("budget");
                    String status = resultSet.getString("status");
                    String submissionDate = resultSet.getString("submission_date");
                    int proposalId = resultSet.getInt("ProposalID");
                    int projectId = resultSet.getInt("project_id");
                    int clientIdFromDB = resultSet.getInt("client_id");
                    int managerId = resultSet.getInt("project_manager_id");

                    ProjectProposal proposal = new ProjectProposal(title, description, duration, pdfPath, budget,
                            status, submissionDate, proposalId, projectId, clientIdFromDB, managerId, managerId);

                    proposals.add(proposal);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                Factory.getFactory().getDb().closeResources(resultSet, null, connection);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return proposals;
    }

    public ArrayList<Meeting> getMeetings(int userId) {
        String query = "SELECT * FROM Meetings WHERE host_id = ? OR recipient_id = ?";
        ArrayList<Meeting> meetings = new ArrayList<>();
        Connection connection;

        try {
            connection = DBHandler.getConnection();
            ResultSet resultSet = Factory.getFactory().getDb().executeSelectQuery(query, userId, userId);

            try {
                while (resultSet.next()) {
                    // Extract values from the ResultSet
                    int meetingId = resultSet.getInt("meeting_id");
                    int hostId = resultSet.getInt("host_id");
                    int recipientId = resultSet.getInt("recipient_id");
                    int projectId = resultSet.getInt("project_id");
                    LocalDate meetingDate = resultSet.getDate("meeting_date").toLocalDate();
                    LocalTime meetingTime = resultSet.getTime("meeting_time").toLocalTime();
                    String location = resultSet.getString("location");
                    String agenda = resultSet.getString("agenda");
                    String type = resultSet.getString("meeting_title");
                    String priority = resultSet.getString("priority");
                    String status = resultSet.getString("status");
                    String minutesFilePath = resultSet.getString("minutes_file_path");
                    String cancel = resultSet.getString("cancellation_reason");
                    String reschedule = resultSet.getString("reschedule_notes");

                    Meeting meeting = new Meeting(meetingId, hostId, recipientId, projectId, meetingDate, meetingTime,
                            location, agenda, status, type, priority, minutesFilePath, cancel, reschedule);
                    meetings.add(meeting);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Factory.getFactory().getDb().closeResources(resultSet, null, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Meeting meet : meetings){
            LocalDate aaj = LocalDate.now();
            if(Objects.equals(meet.getStatus(), "Scheduled")) {
                if (meet.getMeetingDate().isBefore(aaj)) {
                    meet.setStatus("Completed");
                }
            }
            else if (Objects.equals(meet.getStatus(), "Request Pending")) {
                if (meet.getMeetingDate().isBefore(aaj)) {
                    meet.setStatus("Cancelled");
                    meet.setCancellationReason("No follow-up on meeting request from recipient.");
                }
            }
        }
        return meetings;
    }

    public ArrayList<Meeting> getFilteredMeetings(int userId, String status, String agendaKeyword, LocalDate startDate, LocalDate endDate, boolean isUpcoming) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Meetings WHERE (host_id = ? OR recipient_id = ?)");
        ArrayList<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(userId);

        if (status != null && !status.isEmpty()) {
            queryBuilder.append(" AND status = ?");
            params.add(status);
        }
        if (agendaKeyword != null && !agendaKeyword.isEmpty()) {
            queryBuilder.append(" AND agenda LIKE ?");
            params.add("%" + agendaKeyword + "%");
        }
        if (startDate != null) {
            queryBuilder.append(" AND meeting_date >= ?");
            params.add(java.sql.Date.valueOf(startDate));
        }
        if (endDate != null) {
            queryBuilder.append(" AND meeting_date <= ?");
            params.add(java.sql.Date.valueOf(endDate));
        }
        if (isUpcoming) {
            queryBuilder.append(" AND meeting_date >= ?");
            params.add(java.sql.Date.valueOf(LocalDate.now()));
        } else {
            queryBuilder.append(" AND meeting_date < ?");
            params.add(java.sql.Date.valueOf(LocalDate.now()));
        }

        ArrayList<Meeting> meetings = new ArrayList<>();
        try {
            ResultSet resultSet = Factory.getFactory().getDb().executeSelectQuery(queryBuilder.toString(), params);

            try {
                while (resultSet.next()) {
                    // Extract values from the ResultSet
                    int meetingId = resultSet.getInt("meeting_id");
                    int hostId = resultSet.getInt("host_id");
                    int recipientId = resultSet.getInt("recipient_id");
                    int projectId = resultSet.getInt("project_id");
                    LocalDate meetingDate = resultSet.getDate("meeting_date").toLocalDate();
                    LocalTime meetingTime = resultSet.getTime("meeting_time").toLocalTime();
                    String location = resultSet.getString("location");
                    String agenda = resultSet.getString("agenda");
                    String type = resultSet.getString("meeting_type"); //ye actual main title hai
                    String priority = resultSet.getString("priority");
                    String mstatus = resultSet.getString("status");
                    String minutesFilePath = resultSet.getString("minutes_file_path");
                    String cancel = resultSet.getString("cancellation_reason");
                    String reschedule = resultSet.getString("reschedule_notes");

                    Meeting meeting = new Meeting(meetingId, hostId, recipientId, projectId, meetingDate, meetingTime,
                            location, agenda, type, priority, mstatus, minutesFilePath, cancel, reschedule);

                    meetings.add(meeting);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Factory.getFactory().getDb().closeResources(resultSet, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meetings;
    }

    public ArrayList<Project> getProjects(int userId) {
        String query = "SELECT p.project_id, pp.title, p.client_id, p.manager_id, p.start_date, p.end_date, " +
                "p.status, pp.description, p.final_cost, pp.budget, pp.pdf_path AS proposal_pdf_path " +
                "FROM Projects p " +
                "JOIN ProjectProposal pp ON p.proposal_id = pp.ProposalID " +
                "WHERE p.client_id = ?";

        ArrayList<Project> projects = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            resultSet = Factory.getFactory().getDb().executeSelectQuery(query, userId);

            while (resultSet.next()) {
                int projectId = resultSet.getInt("project_id");
                String projectName = resultSet.getString("title");
                int clientId = resultSet.getInt("client_id");
                int managerId = resultSet.getInt("manager_id");
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");
                String status = resultSet.getString("status");
                String description = resultSet.getString("description");
                double final_cost= resultSet.getDouble("final_cost");
                int budget = resultSet.getInt("budget");
                String proposalPdfPath = resultSet.getString("proposal_pdf_path");

                Project project = new Project(projectId, projectName, clientId, managerId, startDate, endDate,
                        status, description, final_cost, budget, proposalPdfPath);

                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Factory.getFactory().getDb().closeResources(resultSet, null, null);
        }
        return projects;
    }

    public void saveMeeting(int hostId, String title, String agenda, LocalDate date, Time time,
                            String location, String address, int projectId, int manager) {
        String query = "INSERT INTO Meetings (host_id, project_id, recipient_id, agenda, meeting_date, " +
                "meeting_time, location, meeting_title, status, request_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Request Pending', CURRENT_TIMESTAMP)";

        DBHandler dbHandler = Factory.getFactory().getDb();

        try {
            dbHandler.executeUpdateQuery(query, hostId, projectId, manager, agenda,
                    date, time,
                    location != null ? (location + (address != null ? ": " + address : "")) : null,
                    title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean updateMeeting(Meeting meeting) throws SQLException {
        String updateQuery = "UPDATE Meetings SET agenda = ?, meeting_date = ?, meeting_time = ?, location = ?, meeting_title = ?, "
                + "priority = ?, status = ?, minutes_file_path = ?, cancellation_reason = ?, reschedule_notes = ? WHERE meeting_id = ?";

        Object[] params = {
                meeting.getAgenda(),
                meeting.getMeetingDate(),
                meeting.getMeetingTime(),
                meeting.getLocation(),
                meeting.getTitle(),
                meeting.getPriority(),
                meeting.getStatus(),
                meeting.getMinutesFilePath(),
                meeting.getCancellationReason(),
                meeting.getRescheduleNotes(),
                meeting.getMeetingId()
        };

        return Factory.getFactory().getDb().executeUpdateQuery(updateQuery, params) > 0;
    }
}
