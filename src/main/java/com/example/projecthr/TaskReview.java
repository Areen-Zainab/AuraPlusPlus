package com.example.projecthr;

import java.sql.Timestamp;

public class TaskReview {
    private int reviewId;
    private int taskId;
    private int reviewerId;
    private Timestamp reviewDate;
    private String status;
    private String comments;
    private String feedback;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public TaskReview(int reviewId, int taskId, int reviewerId, Timestamp reviewDate, String status,
                      String comments, String feedback, Timestamp createdAt, Timestamp updatedAt) {
        this.reviewId = reviewId;
        this.taskId = taskId;
        this.reviewerId = reviewerId;
        this.reviewDate = reviewDate;
        this.status = status;
        this.comments = comments;
        this.feedback = feedback;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString() {
        return "TaskReview{" +
                "reviewId=" + reviewId +
                ", taskId=" + taskId +
                ", reviewerId=" + reviewerId +
                ", reviewDate=" + reviewDate +
                ", status='" + status + '\'' +
                ", comments='" + comments + '\'' +
                ", feedback='" + feedback + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

