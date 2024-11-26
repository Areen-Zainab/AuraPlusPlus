package com.example.projecthr;
import javafx.scene.control.Alert;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

//client services is the adapter class that is used by Client to communicate with the database (DBHandler)
public class Client extends User{
    ArrayList<ProjectProposal> proposals;   //aggregation
    ArrayList<Meeting> meetings;    //aggregation
    ArrayList<Project> projects;
    protected String companyName;
    protected boolean independent;

    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setIndependent(boolean independent) { this.independent = independent; }
    public String getCompanyName() { return companyName; }
    public boolean isIndependent() { return independent; }

    public Client(int userId, String email) {
        super(userId, email);
    }

    @Override
    public void loadDashboard() {
        loadUserProfile();
        ProjectApplication.switchScene("ClientProject.fxml", 1000, 600);
    }

    @Override
    public boolean loadUserProfile(){
        return super.loadUserProfile() && Factory.getClientServices().loadProfile(this);
    }

    @Override
    public boolean setUpComplete(){
        if(!independent && (companyName == null || companyName.isEmpty())){
            return false;
        }
        return super.setUpComplete();
    }

    @Override
    public boolean saveUserProfile() {
        return super.saveUserProfile() && Factory.getClientServices().updateProfile(this);
    }

    public ArrayList<ProjectProposal> getProposals(){
        proposals = Factory.getClientServices().getProposals(userId);
        return proposals;
    }

    public ArrayList<ProjectProposal> getFilteredProposals(String filter){
        if(proposals == null){
            getProposals();
        }
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

    public ArrayList<Meeting> loadMeetings(){
        meetings = Factory.getClientServices().getMeetings(userId);
        return meetings;
    }

    public ArrayList<Meeting> getMeetings(){
        if(meetings == null){
            meetings = Factory.getClientServices().getMeetings(userId);
        }
        return meetings;
    }

    public ArrayList<Meeting> FilterMeetings(String status, String keyword, LocalDate startDate, LocalDate endDate, boolean isUpcoming){
        ArrayList<Meeting> filteredMeetings = new ArrayList<>();
        if (status.equalsIgnoreCase("Latest")) {
            meetings.sort((m1, m2) -> m2.getMeetingDate().compareTo(m1.getMeetingDate()));
            filteredMeetings.addAll(meetings);
            return filteredMeetings;
        } else if (status.equalsIgnoreCase("Oldest")) {
            meetings.sort(Comparator.comparing(Meeting::getMeetingDate));
            filteredMeetings.addAll(meetings);
            return filteredMeetings;
        }

        return getMeetings().stream()
            .filter(meeting -> (meeting.getStatus().equalsIgnoreCase(status))
                    && (keyword == null || meeting.getAgenda().toLowerCase().contains(keyword.toLowerCase()))
                    && (startDate == null || !meeting.getMeetingDate().isBefore(startDate))
                    && (endDate == null || !meeting.getMeetingDate().isAfter(endDate))
                    && (!isUpcoming || meeting.getMeetingDate().isAfter(LocalDate.now())))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public void scheduleNewMeeting(String title, String agenda, LocalDate date, Time time,
                                   String location, String address, int projectId){
        try {
            int manager = -1;
            for (Project project : projects) {
                if (project.getProjectId() == projectId) {
                    manager = project.getManagerId();
                    break; // Exit the loop once the matching project is found
                }
            }
            if (manager == -1) {
                return;
            }
            Factory.getClientServices().saveMeeting(userId, title, agenda, date, time, location, address, projectId, manager);
        }
        catch (Exception e) {
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Unexpected Error", "Meeting could not be saved!");
        }
    }

    public void updateMeeting(Meeting meeting) throws Exception {
        if (meeting == null || meeting.getMeetingId() <= 0) {
            throw new Exception("Invalid meeting provided.");
        }
        if(!ClientServices.updateMeeting(meeting))
            throw new Exception("Failed to make changes.");
    }

    public ArrayList<Project> getProjects(){
        if(projects == null){
            projects = Factory.getClientServices().getProjects(userId);
        }
        return projects;
    }

    public Project getProjectById(int projectId){
        projects = getProjects();
        for(Project prop : projects){
            if(prop.getProjectId() == projectId){
                return prop;
            }
        }
        return null;
    }

    public int getPendingMeetingCount(){
        int count  = 0;
        for(Meeting prop : meetings){
            if(prop.getStatus().equalsIgnoreCase("Request Pending")){ count++;}
        }
        return count;
    }

    public int getScheduledMeetingCount(){
        int count  = 0;
        for(Meeting prop : meetings){
            if(prop.getStatus().equalsIgnoreCase("Scheduled")){ count++;}
        }
        return count;
    }

    public int getProjectCount(String filter){
        if(filter == null || filter.isEmpty()){
            return projects.size();
        }
        int count  = 0;
        for(Project proj : projects){
            if(proj.getStatus().equalsIgnoreCase(filter)){ count++;}
        }
        return count;
    }
    public ArrayList<Project> getFilteredProjects(String filter){
        ArrayList<Project> filteredProjects = new ArrayList<>();
        for(Project proj : projects){
            if(proj.getStatus().equalsIgnoreCase(filter)){
                filteredProjects.add(proj);
            }
        }
        return filteredProjects;
    }
}
