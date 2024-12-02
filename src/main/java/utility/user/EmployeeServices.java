package utility.user;

import com.example.projecthr.Employee;
import com.example.projecthr.User;
import com.example.projecthr.LeaveRequest;
import com.example.projecthr.project.Task;
import utility.DBHandler;
import utility.Factory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class EmployeeServices {

    public static EmployeeServices instance;
    private EmployeeServices(){}

    public static EmployeeServices getInstance(){
        if(instance == null)
            instance = new EmployeeServices();
        return instance;
    }

    //load user profile
    public boolean loadProfile(User user) {
        ResultSet empResult = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM Employees WHERE employee_id = ?";
            empResult = Factory.getDb().executeSelectQuery(query, user.getUserId());
            if (empResult != null && empResult.next()) {
                ((Employee) user).setDepartment(empResult.getString("department"));
                ((Employee) user).setPosition(empResult.getString("position"));
                ((Employee) user).setSalary(empResult.getBigDecimal("salary"));
                ((Employee) user).setJoinDate(empResult.getTimestamp("join_date"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Factory.getDb().closeResources(empResult, null, connection);
        }
    }

    //load all leaveRequests
    public ArrayList<LeaveRequest> getLeaveRequests(int employeeId){
        ArrayList<LeaveRequest> leaveRequests = new ArrayList<>();
        ResultSet leaveResult = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM LeaveRequests WHERE EmployeeID = ?";
            leaveResult = Factory.getDb().executeSelectQuery(query, employeeId);
            while (leaveResult != null && leaveResult.next()) {
                LeaveRequest leaveRequest = new LeaveRequest();
                leaveRequest.setLeaveID(leaveResult.getInt("LeaveID"));
                leaveRequest.setEmployeeID(leaveResult.getInt("EmployeeID"));
                leaveRequest.setLeaveType(leaveResult.getString("LeaveType"));
                leaveRequest.setStartDate(leaveResult.getDate("StartDate"));
                leaveRequest.setEndDate(leaveResult.getDate("EndDate"));
                leaveRequest.setReason(leaveResult.getString("Reason"));
                leaveRequest.setStatus(leaveResult.getString("status"));
                leaveRequest.setAppliedDate(leaveResult.getTimestamp("AppliedDate"));

                leaveRequests.add(leaveRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Factory.getDb().closeResources(leaveResult, null, connection);
        }
        return leaveRequests;
    }

    //submit new leave request:
    public boolean addLeaveRequest(String leaveType, Date startDate, Date endDate, String reason, int employeeId) {
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "INSERT INTO LeaveRequests (EmployeeID, LeaveType, StartDate, EndDate, Reason) VALUES (?, ?, ?, ?, ?)";
            int result = Factory.getDb().executeUpdateQuery(query, employeeId, leaveType, startDate, endDate, reason);
            return result > 0; // Return true if the insert was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Factory.getDb().closeResources(null, null, connection);
        }
    }

    public ArrayList<Task> getTasksForEmployee(int employeeId) {
        ArrayList<Task> tasks = new ArrayList<>();
        ResultSet taskResult = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM Tasks WHERE assigned_to = ?";
            taskResult = Factory.getDb().executeSelectQuery(query, employeeId);

            while (taskResult != null && taskResult.next()) {
                Task task = new Task();
                task.setTaskId(taskResult.getInt("task_id"));
                task.setMilestoneId(taskResult.getInt("milestone_id"));
                task.setTaskName(taskResult.getString("task_name"));
                task.setDescription(taskResult.getString("description"));
                task.setDeadline(taskResult.getDate("deadline"));
                task.setAssignedTo(taskResult.getInt("assigned_to"));
                task.setAssignedDate(taskResult.getDate("assigned_date"));
                task.setPriority(taskResult.getString("priority"));
                task.setStatus(taskResult.getString("status"));
                task.setComments(taskResult.getString("comments"));
                task.setTaskDetails(taskResult.getString("task_details"));
                task.setTaskAttachment(taskResult.getString("task_attachment"));
                task.setUpdatedAt(taskResult.getTimestamp("updated_at"));

                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Factory.getDb().closeResources(taskResult, null, connection);
        }

        return tasks;
    }

    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        String query = "SELECT u.user_id, u.email, u.password, u.fname, u.lname, u.gender, u.dob, u.phone_no, u.address, e.department, e.position, e.salary, e.join_date FROM Users u JOIN Employees e ON u.user_id = e.employee_id";

        try (ResultSet rs = Factory.getDb().executeSelectQuery(query)) {
            while (rs.next()) {
                // Extract user-related fields
                int userId = rs.getInt("user_id");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String firstName = rs.getString("fname");
                String lastName = rs.getString("lname");
                String gender = rs.getString("gender");
                String dob = rs.getString("dob");
                String phoneNo = rs.getString("phone_no");
                String address = rs.getString("address");

                // Extract employee-specific fields
                String department = rs.getString("department");
                String position = rs.getString("position");
                BigDecimal salary = rs.getBigDecimal("salary");
                Timestamp joinDate = rs.getTimestamp("join_date");

                // Create an Employee object and add it to the list
                Employee employee = new Employee(userId, email, password, firstName, lastName, gender, dob,
                        phoneNo, address, department, position, salary, joinDate);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }



}
