package com.example.projecthr;

import com.example.projecthr.project.Task;
import utility.Factory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Employee extends User{
    ArrayList<Task> tasks;  //aggregation
    ArrayList<LeaveRequest> leaveRequests;

    private String department;        // Corresponds to department
    private String position;          // Corresponds to position
    private BigDecimal salary;        // Corresponds to salary
    private Timestamp joinDate;       // Corresponds to join_date

    Employee(int userId, String email) {
        super(userId, email);
    }

    public Employee(int userId, String email, String department, String position, BigDecimal salary, Timestamp joinDate) {
        super(userId, email);
        this.department = department;
        this.position = position;
        this.salary = salary;
        this.joinDate = joinDate;
    }

    @Override
    public boolean loadUserProfile() {
        return super.loadUserProfile() && Factory.getEmployeeServices().loadProfile(this);
    }

    @Override
    public void loadDashboard() {
        loadUserProfile();
        ProjectApplication.switchScene("/employee/employeeDashboard.fxml");
    }

    public void loadLeaveRequests() {
        leaveRequests = Factory.getEmployeeServices().getLeaveRequests(this.userId);
    }

    public void loadTasks() {
        tasks = Factory.getEmployeeServices().getTasksForEmployee(this.userId);
    }

    // getters and setters
    public ArrayList<LeaveRequest> getLeaveRequests() {
        return leaveRequests;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public Employee(int userId, String email, String password, String firstName, String lastName, String gender,
                    String dob, String phoneNo, String address, String department, String position,
                    BigDecimal salary, Timestamp joinDate) {
        super(userId, email, password, firstName, lastName, gender, dob, phoneNo, address);
        this.department = department;
        this.position = position;
        this.salary = salary;
        this.joinDate = joinDate;
    }
}
