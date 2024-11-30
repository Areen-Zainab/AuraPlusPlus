package com.example.projecthr.project;

public class ProjectProposal {
    private String title;
    private String description;
    private String duration;
    private String pdfPath;
    private int budget;
    private String status;
    private String submission_date;
    private int proposal_id, project_id, client_id, manager_id;
    private String comment;

    public ProjectProposal(String title, String description, String duration, String pdfPath, int cost,
                           String status, String submission_date, int proposal_id, int project_id,
                           int client_id, int manager_id, String comment) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.pdfPath = pdfPath;
        this.budget = cost;
        this.status = status;
        this.submission_date = submission_date;
        this.proposal_id = proposal_id;
        this.project_id = project_id;
        this.client_id = client_id;
        this.manager_id = manager_id;
        this.comment = comment;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDuration() { return duration; }
    public String getPdfPath() { return pdfPath; }
    public String getStatus() { return status; }
    public String getSubmission_date() { return submission_date; }
    public int getBudget() { return budget; }
    public int getProposal_id() { return proposal_id; }
    public int getProject_id() { return project_id; }
    public int getClient_id() { return client_id; }
    public int getManager_id() { return manager_id; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public void setBudget(int budget) { this.budget = budget; }
    public void setStatus(String status) { this.status = status; }
    public void setSubmission_date(String submission_date) { this.submission_date = submission_date; }
    public void setProposal_id(int proposal_id) { this.proposal_id = proposal_id; }
    public void setProject_id(int project_id) { this.project_id = project_id; }
    public void setClient_id(int client_id) { this.client_id = client_id; }
    public void setManager_id(int manager_id) { this.manager_id = manager_id; }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
