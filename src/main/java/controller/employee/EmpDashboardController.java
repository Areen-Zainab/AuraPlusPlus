package controller.employee;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;

public class EmpDashboardController {
    @FXML
    protected void onViewTasksClick() {
        ProjectApplication.switchScene("employeeViewTasks.fxml");
    }

    @FXML
    protected void onSubmitTaskClick() {
        ProjectApplication.switchScene("employeeSubmitTask.fxml");
    }

    @FXML
    protected void onViewProjectsClick() {
        ProjectApplication.switchScene("employeeViewProjects.fxml");
    }

    @FXML
    protected void onEditAttendanceClick() {
        ProjectApplication.switchScene("employeeEditAttendance.fxml");
    }

    @FXML
    protected void onMyLeaveRequestsClick() {
        ProjectApplication.switchScene("employeeLeaveRequests.fxml");
    }

    @FXML
    protected void onWorkCalendarClick(){
        ProjectApplication.switchScene("employeeWorkCalendar.fxml");
    }

    @FXML
    protected void onProfileClick() {
        ProjectApplication.switchScene("employeeProfile.fxml");
    }
}
