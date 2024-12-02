package com.example.projecthr;

import java.sql.Date;
import java.sql.Timestamp;

public class LeaveRequest {
    private int leaveID;              // Corresponds to LeaveID
    private int employeeID;           // Corresponds to EmployeeID
    private String leaveType;         // Corresponds to LeaveType
    private Date startDate;           // Corresponds to StartDate
    private Date endDate;             // Corresponds to EndDate
    private String reason;            // Corresponds to Reason
    private String status;            // Corresponds to Status (Pending, Approved, Rejected)
    private Timestamp appliedDate;    // Corresponds to AppliedDate

    // Default constructor
    public LeaveRequest() {}

    // Parameterized constructor
    public LeaveRequest(int leaveID, int employeeID, String leaveType, Date startDate, Date endDate, String reason, String status, Timestamp appliedDate) {
        this.leaveID = leaveID;
        this.employeeID = employeeID;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.appliedDate = appliedDate;
    }

    // Getters and Setters
    public int getLeaveID() {
        return leaveID;
    }

    public void setLeaveID(int leaveID) {
        this.leaveID = leaveID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Timestamp appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String toString() {
        return "LeaveRequest{" +
                "leaveID=" + leaveID +
                ", employeeID=" + employeeID +
                ", leaveType='" + leaveType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", appliedDate=" + appliedDate +
                '}';
    }
}
