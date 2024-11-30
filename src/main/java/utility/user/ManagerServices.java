package utility.user;

import com.example.projecthr.Meeting;
import com.example.projecthr.ProjectManager;
import com.example.projecthr.User;
import com.example.projecthr.project.Project;
import com.example.projecthr.project.ProjectProposal;
import utility.DBHandler;
import utility.Factory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ManagerServices {
    private static ManagerServices instance;

    private ManagerServices() {}
    public static ManagerServices getInstance() {
        if (instance == null) {
            instance = new ManagerServices();
        }
        return instance;
    }

    public boolean loadProfile(User user) {
        ResultSet pmResult = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM ProjectManagers WHERE PM_id = ?";
            pmResult = Factory.getDb().executeSelectQuery(query, user.getUserId());
            if (pmResult != null && pmResult.next()) {
                ((ProjectManager) user).setDepartment(pmResult.getString("department"));
                ((ProjectManager) user).setExperienceYears(pmResult.getInt("years_of_experience"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Factory.getDb().closeResources(pmResult, null, connection);
        }
    }

    public ArrayList<Project> getProjects(int userId) {
        String query = "SELECT p.project_id, pp.title, p.client_id, p.manager_id, p.start_date, p.end_date, " +
                "p.status, pp.description, p.final_cost, pp.budget, pp.pdf_path AS proposal_pdf_path " +
                "FROM Projects p " +
                "JOIN ProjectProposal pp ON p.proposal_id = pp.ProposalID " +
                "WHERE p.manager_id = ?";

        ArrayList<Project> projects = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            resultSet = Factory.getDb().executeSelectQuery(query, userId);

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
            Factory.getDb().closeResources(resultSet, null, null);
        }
        return projects;
    }

    public ArrayList<Meeting> getMeetings(int userId) {
        String query = "SELECT * FROM Meetings WHERE host_id = ? OR recipient_id = ?";
        ArrayList<Meeting> meetings = new ArrayList<>();
        Connection connection;

        try {
            connection = DBHandler.getConnection();
            ResultSet resultSet = Factory.getDb().executeSelectQuery(query, userId, userId);

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
                    String title = resultSet.getString("meeting_title");
                    String priority = resultSet.getString("priority");
                    String status = resultSet.getString("status");
                    String minutesFilePath = resultSet.getString("minutes_file_path");
                    String cancel = resultSet.getString("cancellation_reason");
                    String reschedule = resultSet.getString("reschedule_notes");

                    Meeting meeting = new Meeting(meetingId, hostId, recipientId, projectId, title, agenda, meetingDate, meetingTime,
                            location, status, priority, minutesFilePath, cancel, reschedule);
                    meetings.add(meeting);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Factory.getDb().closeResources(resultSet, null, connection);
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

    public ArrayList<ProjectProposal> getProposals(int managerId) {
        String query = "SELECT pp.*, p.project_id AS project_id, p.start_date, p.end_date, p.status AS project_status, p.final_cost " +
                "FROM ProjectProposal pp " +
                "LEFT JOIN Projects p ON pp.ProposalID = p.proposal_id " +
                "WHERE pp.status = 'Pending' OR pp.project_manager_id = ?";
        ArrayList<ProjectProposal> proposals = new ArrayList<>();
        Connection connection;

        try {
            connection = DBHandler.getConnection();
            ResultSet resultSet = Factory.getDb().executeSelectQuery(query, managerId);

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
                    String comments = resultSet.getString("comments");

                    ProjectProposal proposal = new ProjectProposal(title, description, duration, pdfPath, budget,
                            status, submissionDate, proposalId, projectId, clientIdFromDB, managerId, comments);
                    proposals.add(proposal);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                Factory.getDb().closeResources(resultSet, null, connection);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return proposals;
    }

}
