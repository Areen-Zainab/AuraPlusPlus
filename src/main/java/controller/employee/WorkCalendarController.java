package controller.employee;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;

import java.io.IOException;

public class WorkCalendarController {

    @FXML
    protected void onDashboardClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeDashboard.fxml");
    }

    @FXML
    protected void onViewTasksClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeViewTasks.fxml");
    }

    @FXML
    protected void onSubmitTaskClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeSubmitTask.fxml");
    }

    @FXML
    protected void onViewProjectsClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeViewProjects.fxml");
    }

    @FXML
    protected void onEditAttendanceClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeEditAttendance.fxml");
    }

    @FXML
    protected void onMyLeaveRequestsClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeLeaveRequests.fxml");
    }

    @FXML
    protected void onProfileClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeProfile.fxml");
    }
}
