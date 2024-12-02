package utility;

import com.example.projecthr.Meeting;
import com.example.projecthr.ProjectManager;
import com.example.projecthr.project.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
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
            resultSet = Factory.getDb().executeSelectQuery(query, project_id);
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
                    String filename = resultSet.getString("milestone_report");

                    // Add the milestone to the list
                    milestones.add(new Milestone(milestoneID, projectID, milestoneName, description, startDate, endDate, comments, status, priority, created_at, updated_at, filename));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Factory.getDb().closeResources(resultSet, null, null);
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
            resultSet = Factory.getDb().executeSelectQuery(query, project_id);

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
            Factory.getDb().closeResources(resultSet, null, connection);
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

    public int getActive(ArrayList<Project> projects) {
        int count = 0;
        for (Project proj : projects) {
            if (proj.getStatus().equalsIgnoreCase("Active")) {
                count++;
            }
        }
        return count;
    }

    public int getCompleted(ArrayList<Project> projects) {
        int count = 0;
        for (Project proj : projects) {
            if (proj.getStatus().equalsIgnoreCase("Completed")) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Project> getCompleteProjects(ArrayList<Project> projects) {
        ArrayList<Project> completeProjects = new ArrayList<>();
        for (Project proj : projects) {
            if (proj.getStatus().equalsIgnoreCase("Completed")) {
                completeProjects.add(proj);
            }
        }
        return completeProjects;
    }

    public ArrayList<Project> getActiveProjects(ArrayList<Project> projects) {
        ArrayList<Project> activeProjects = new ArrayList<>();
        for (Project proj : projects) {
            if (proj.getStatus().equalsIgnoreCase("Active")) {
                activeProjects.add(proj);
            }
        }
        return activeProjects;
    }

    public ArrayList<Project> getUnstartedProjects(ArrayList<Project> projects) {
        ArrayList<Project> activeProjects = new ArrayList<>();
        for (Project proj : projects) {
            if (proj.getStatus().equalsIgnoreCase("Not Started")) {
                activeProjects.add(proj);
            }
        }
        return activeProjects;
    }

    public ArrayList<ProjectLog> getLogs(int project_id) {
        ArrayList<ProjectLog> projectLogs = new ArrayList<>();
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT h.history_id, h.project_id, COALESCE(pp.title, '') AS project_title, h.milestone_id, COALESCE(m.milestone_name, '') AS milestone_name, h.task_id, COALESCE(t.task_name, '') AS task_name, h.timestamp, h.actionType, h.description, h.user_id, CONCAT(u.fname, ' ', u.lname) as username FROM ProjectHistory h LEFT JOIN projects p ON h.project_id = p.project_id LEFT JOIN ProjectProposal pp ON p.proposal_id = pp.ProposalID LEFT JOIN milestones m ON h.milestone_id = m.milestoneID LEFT JOIN tasks t ON h.task_id = t.task_id LEFT JOIN users u ON h.user_id = u.user_id WHERE h.project_id = ? ORDER BY h.timestamp DESC";
            resultSet = Factory.getDb().executeSelectQuery(query, project_id);

            while (resultSet != null && resultSet.next()) {
                ProjectLog projectHistory = new ProjectLog();

                projectHistory.setProjectHistory(
                        resultSet.getInt("history_id"),
                        resultSet.getInt("project_id"),
                        resultSet.getString("project_title"),
                        resultSet.getInt("milestone_id"),
                        resultSet.getString("milestone_name"),
                        resultSet.getInt("task_id"),
                        resultSet.getString("task_name"),
                        resultSet.getTimestamp("timestamp"),
                        resultSet.getString("actionType"),
                        resultSet.getString("description"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("username")
                );

                projectLogs.add(projectHistory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Factory.getDb().closeResources(resultSet, null, connection);
        }

        return projectLogs;
    }

    public ArrayList<Task> getTasksByMilestoneId(int milestoneId) {
        ArrayList<Task> tasks = new ArrayList<>();
        ResultSet rs;
        String query = "SELECT t.task_id, t.milestone_id, t.task_name, t.description, t.deadline, t.assigned_to, t.assigned_date, t.priority, t.status, t.comments, t.task_attachment, t.updated_at, CONCAT(u.fname, ' ', u.lname) AS assigned_to_name FROM Tasks t LEFT JOIN Users u ON t.assigned_to = u.user_id WHERE t.milestone_id = ? ORDER BY t.assigned_date ASC;";
        try {
            rs = Factory.getDb().executeSelectQuery(query, milestoneId);
            // Loop through the result set and create Task objects
            while ((rs != null && rs.next())) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("task_name");
                String description = rs.getString("description");
                Date deadline = rs.getDate("deadline");
                int assignedTo = rs.getInt("assigned_to");
                Date assignedDate = rs.getDate("assigned_date");
                String priority = rs.getString("priority");
                String status = rs.getString("status");
                String comments = rs.getString("comments");
                String taskAttachment = rs.getString("task_attachment");
                Timestamp updatedAt = rs.getTimestamp("updated_at");
                String assignedName = rs.getString("assigned_to_name");

                // Create a Task object and add it to the list
                Task task = new Task(taskId, milestoneId, taskName, description, deadline, assignedTo, assignedDate,
                        priority, status, comments, taskAttachment, updatedAt, assignedName);
                tasks.add(task);
            }
            Factory.getDb().closeResources(rs, null, null);

        } catch (SQLException e) {
            e.printStackTrace();  // Handle any SQL exceptions
        }

        return tasks;
    }

    public ArrayList<Task> fetchNextTasks(ArrayList<Project> projects) {
        ArrayList<Task> tasks = new ArrayList<>();

        for(Project project : projects) {
            ArrayList<Milestone> ms = project.getMilestones();
            for(Milestone m : ms) {
                ArrayList<Task> tsk = m.getTasks();
                for(Task task : tsk) {
                    Date today = Date.valueOf(LocalDate.now());
                    if(!task.getDeadline().before(today))
                        tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public int getPendingTaskCount(ArrayList<Task> tasks){
        int count = 0;
        for(Task task : tasks) {
            if(task.getStatus().equalsIgnoreCase("Pending")){
                count++;
            }
        }
        return count;
    }

    //incomplete
    public int getPendingExtensionRequests(ArrayList<Project> pjs){
        int count = 0;
        // will complete implementation
        return count;
    }

    public ArrayList<Task> filterTasks(ArrayList<Task> tasks, String filter) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        if(filter.equalsIgnoreCase("Pending") || filter.equalsIgnoreCase("Completed") || filter.equalsIgnoreCase("Not Started")) {
            for (Task task : tasks) {
                if (task.getStatus().equalsIgnoreCase(filter)) {
                    filteredTasks.add(task);
                }
            }
        }
        else if(filter.equalsIgnoreCase("Urgent") || filter.equalsIgnoreCase("High") || filter.equalsIgnoreCase("Medium") || filter.equalsIgnoreCase("Low")) {
            for (Task task : tasks) {
                if (task.getPriority().equalsIgnoreCase(filter)) {
                    filteredTasks.add(task);
                }
            }
        }
        return filteredTasks;

    }

    public boolean insertProject(int proposalId, int clientId, int managerId, Date endDate, String status, double finalCost, String finalReport) {
        String query = "INSERT INTO Projects (proposal_id, client_id, manager_id, start_date, end_date, status, final_cost, final_report, created_at) " +
                "VALUES (?, ?, ?, NOW(), ?, 'Not Started', ?, ?, NOW())";

        Object[] params;
        if(finalCost == -1.0) {
            query = "INSERT INTO Projects (proposal_id, client_id, manager_id, start_date, end_date, status, final_report, created_at) " +
                    "VALUES (?, ?, ?, NOW(), ?, 'Not Started', ?, NOW())";
            params = new Object[]{proposalId, clientId, managerId, endDate, finalReport};
        }
        else
            params = new Object[]{proposalId, clientId, managerId, endDate, finalCost, finalReport};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLatestProject() {
        String query = "SELECT project_id FROM Projects ORDER BY created_at DESC LIMIT 1";

        try {
            ResultSet resultSet = Factory.getDb().executeSelectQuery(query);
            try {
                if (resultSet.next()) {
                    return resultSet.getInt("project_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Factory.getDb().closeResources(resultSet, null, null);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public boolean insertTask(String taskName, String description, Date deadline, String taskDetails, String priority, int milestoneId, int assignedTo) {
        String query = "INSERT INTO Tasks (task_name, description, deadline, task_details, priority, milestone_id, assigned_to, assigned_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        Object[] params = {taskName, description, deadline, taskDetails, priority, milestoneId, assignedTo};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertMilestone(String milestoneName, String description, Date deadline, String priority, int projectID) {
        String query = "INSERT INTO Milestones (milestone_name, description, start_date, end_date, priority, status, project_id) VALUES (?, ?, NOW(), ?, ?, 'In Progress', ?)";
        Object[] params = {milestoneName, description, deadline, priority, projectID};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTaskStatusToCompleted(Task task) {
        String query = "UPDATE tasks SET status = 'Completed', task_attachment = ?, updated_at = NOW() WHERE task_id = ?";
        Object[] params = {task.getTaskAttachment(), task.getTaskId()};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertTaskExtension(int taskId, int requestedBy, String reason, Date newDeadline) {
        String query = "INSERT INTO TaskExtensions (task_id, requested_by, reason, new_deadline, status) VALUES (?, ?, ?, ?, 'Pending')";
        Object[] params = {taskId, requestedBy, reason, newDeadline};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean areAllTasksComplete(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            if (!task.getStatus().equals("Completed")) {
                return false;
            }
        }
        return true;
    }

    public static boolean areAllMilestonesComplete(ArrayList<Milestone> milestones) {
        for (Milestone m : milestones) {
            if (!m.getStatus().equals("Completed")) {
                return false;
            }
        }
        return true;
    }

    public boolean updateProjectStatus(int projectId, String status) {
        String query = "UPDATE Projects SET status = ? WHERE project_id = ?";
        Object[] params = {status, projectId};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMilestoneStatus(int milestoneID, String status) {
        String query = "UPDATE Milestones SET status = ?, updated_at = NOW() WHERE milestoneID = ?";
        Object[] params = {status, milestoneID};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProjectHistory(int projectId, Integer milestoneId, Integer taskId, String actionType, String description, int userId) {
        String query = "INSERT INTO ProjectHistory (project_id, milestone_id, task_id, actionType, description, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        Object[] params = {projectId, milestoneId, taskId, actionType, description, userId};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Task getLatestUpdatedTask(ArrayList<Task> tasks) {
        Task latestTask = null;

        for (Task task : tasks) {
            if (latestTask == null || task.getUpdatedAt().after(latestTask.getUpdatedAt())) {
                latestTask = task;
            }
        }

        return latestTask;
    }

    public boolean updateTaskAssignment(int taskId, int employeeId) {
        String query = "UPDATE Tasks SET assigned_to = ?, status = 'Pending' WHERE task_id = ?";

        Object[] params = {employeeId, taskId};

        try {
            return Factory.getDb().executeUpdateQuery(query, params) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
