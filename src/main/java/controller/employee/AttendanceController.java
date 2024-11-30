package controller.employee;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;

import java.io.IOException;

public class AttendanceController {
    @FXML
    protected void onDashboardClick() throws IOException {
        ProjectApplication.switchScene("employeeDashboard.fxml");
    }

    @FXML
    protected void onViewTasksClick() throws IOException {
        ProjectApplication.switchScene("employeeViewTasks.fxml");
    }

    @FXML
    protected void onSubmitTaskClick() throws IOException {
        ProjectApplication.switchScene("employeeSubmitTask.fxml");
    }

    @FXML
    protected void onViewProjectsClick() throws IOException {
        ProjectApplication.switchScene("employeeViewProjects.fxml");
    }

    @FXML
    protected void onMyLeaveRequestsClick() throws IOException {
        ProjectApplication.switchScene("employeeLeaveRequests.fxml");
    }

    @FXML
    protected void onWorkCalendarClick() throws IOException {
        ProjectApplication.switchScene("employeeWorkCalendar.fxml");
    }

    @FXML
    protected void onProfileClick() throws IOException {
        ProjectApplication.switchScene("employeeProfile.fxml");
    }
}
