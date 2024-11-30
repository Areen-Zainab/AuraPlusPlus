package utility;

import com.example.projecthr.Meeting;
import com.example.projecthr.ProjectManager;
import com.example.projecthr.project.Milestone;
import com.example.projecthr.project.Project;
import com.example.projecthr.project.ProjectProposal;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

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

    public Project getProjectById(ArrayList<Project> projects, int projectId){
        for(Project prop : projects){
            if(prop.getProjectId() == projectId){
                return prop;
            }
        }
        return null;
    }

    public ArrayList<ProjectProposal> getFilteredProposals(ArrayList<ProjectProposal> proposals, String filter){
        if(proposals == null)
            return null;
        ArrayList<ProjectProposal> filteredProposals = new ArrayList<>();

        if (filter.equalsIgnoreCase("Accepted") ||
                filter.equalsIgnoreCase("Rejected") ||
                filter.equalsIgnoreCase("Pending")) {

            for (ProjectProposal proposal : proposals) {
                if (proposal.getStatus().equalsIgnoreCase(filter)) {
                    filteredProposals.add(proposal);
                }
            }
        } else if (filter.equalsIgnoreCase("Latest")) {
            proposals.sort((p1, p2) -> p2.getSubmission_date().compareTo(p1.getSubmission_date()));
            filteredProposals.addAll(proposals);
        } else if (filter.equalsIgnoreCase("Oldest")) {
            proposals.sort(Comparator.comparing(ProjectProposal::getSubmission_date));
            filteredProposals.addAll(proposals);
        } else {
            filteredProposals.addAll(proposals);
        }

        return filteredProposals;

    }

    public static String extractFileName(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return path.getFileName().toString();
        } catch (Exception e) {
            System.out.println("Invalid file path: " + filePath);
            return null;
        }
    }

    public int getPendingCount(ArrayList<ProjectProposal> proposals) {
        int count = 0;
        for (ProjectProposal proposal : proposals) {
            if (proposal.getStatus().equalsIgnoreCase("Pending")) {
                count++;
            }
        }
        return count;
    }

    public int getAcceptedCount(ArrayList<ProjectProposal> proposals) {
        int count = 0;
        for (ProjectProposal proposal : proposals) {
            if (proposal.getStatus().equalsIgnoreCase("Approved")) {
                count++;
            }
        }
        return count;
    }

    public boolean updateProposal(ProjectProposal proj) {
        String query = "UPDATE ProjectProposal SET status = ?, comments = ?, project_manager_id = ? WHERE ProposalID = ?";

        Object[] params = {
                //proj.getTitle(),
                //proj.getDescription(),
                //proj.getDuration(),
                //proj.getPdfPath(),
                //proj.getBudget(),
                proj.getStatus(),
                proj.getComment(),
                proj.getManager_id(),
                proj.getProposal_id()
        };

        Connection connection = null;
        try {
            connection = DBHandler.getConnection();
            int rowsAffected = Factory.getDb().executeUpdateQuery(query, params);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Factory.getDb().closeResources(null, null, connection);
        }
        return false;
    }

}
