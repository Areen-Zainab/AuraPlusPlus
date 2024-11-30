package com.example.projecthr;

import com.example.projecthr.project.*;
import utility.Factory;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

public class ProjectManager extends User{
    private String department;
    private int experienceYears;
    private ArrayList<Project> projects;
    private ArrayList<Meeting> meetings;
    private ArrayList<ProjectProposal> proposals;

    ProjectManager(int userId, String email) {
        super(userId, email);
    }
    public ProjectManager(int userId){
        super(userId);
        loadUserProfile();
    }

    @Override
    public boolean loadUserProfile() {
        super.loadUserProfile();
        getProjects();
        getMeetings();
        getProposals();
        return Factory.getManagerServices().loadProfile(this);
    }

    @Override
    public void loadDashboard() {
        loadUserProfile();
        ProjectApplication.switchScene("/manager/PMProposal.fxml");
        //ProjectApplication.switchScene("/manager/PMDashboard.fxml");
    }

    @Override
    public boolean saveUserProfile() {
        return super.saveUserProfile();
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
    public String getDepartment() {
        return department;
    }
    public int getExperienceYears() {
        return experienceYears;
    }

    public ArrayList<Project> getProjects() {
        if(projects == null){
            projects = Factory.getManagerServices().getProjects(userId);
        }
        return projects;
    }

    public ArrayList<Meeting> getMeetings() {
        if(meetings == null){
            meetings = Factory.getManagerServices().getMeetings(userId);
        }
        return meetings;
    }

    public ArrayList<ProjectProposal> getProposals(){
        if(proposals == null){
            proposals = Factory.getManagerServices().getProposals(userId);
        }
        return proposals;
    }

    public int getOngoingCount() {
        int count = 0;
        for(Project p : projects){
            if(p.getStatus().equals("Active")){
                count++;
            }
        }
        return count;
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

    public void scheduleMeeting(int projectId, String title, String agenda, LocalDate date, Time time, String location, String address, String priority){
        int client = -1;
        for (Project project : projects) {
            if (project.getProjectId() == projectId) {
                client = project.getClientId();
                break;
            }
        }
        if (client == -1) {
            return;
        }
        Meeting meet = Factory.getMeetingServices().scheduleMeeting(userId, client, projectId, title, agenda, date, time, location, address, priority);
        meetings.add(meet);
    }

    public void updateMeeting(Meeting meeting) throws Exception {
        if (meeting == null || meeting.getMeetingId() <= 0) {
            throw new Exception("Invalid meeting provided.");
        }
        if(!meeting.updateMeeting())
            throw new Exception("Failed to make changes.");
    }

}
