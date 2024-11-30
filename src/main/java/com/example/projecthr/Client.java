package com.example.projecthr;

import com.example.projecthr.project.Project;
import com.example.projecthr.project.ProjectProposal;
import utility.Factory;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

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
    public Client(int userId) {
        super(userId);
        super.loadUserProfile();
        loadUserProfile();
    }

    @Override
    public void loadDashboard() {
        loadUserProfile();
        ProjectApplication.switchScene("/client/ClientDashboard.fxml");
    }

    @Override
    public boolean loadUserProfile(){
        super.loadUserProfile();
        Factory.getClientServices().loadProfile(this);
        loadMeetings();
        getProjects();
        getProposals();
        return true;
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

    public void scheduleMeeting(int projectId, String title, String agenda, LocalDate date, Time time, String location, String address, String priority){
        int manager = -1;
        for (Project project : projects) {
            if (project.getProjectId() == projectId) {
                manager = project.getManagerId();
                break;
            }
        }
        if (manager == -1) {
            return;
        }
        Meeting meet = Factory.getMeetingServices().scheduleMeeting(userId, manager, projectId, title, agenda, date, time, location, address, priority);
        meetings.add(meet);
    }

    public void updateMeeting(Meeting meeting) throws Exception {
        if (meeting == null || meeting.getMeetingId() <= 0) {
            throw new Exception("Invalid meeting provided.");
        }
        if(!meeting.updateMeeting())
            throw new Exception("Failed to make changes.");
    }

    public ArrayList<Project> getProjects(){
        if(projects == null){
            projects = Factory.getClientServices().getProjects(userId);
        }
        return projects;
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
        if(meetings == null)
            return 0;
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

    public int getProposalCount(){
        if(proposals == null){
            return 0;
        }
        return proposals.size();
    }
}
