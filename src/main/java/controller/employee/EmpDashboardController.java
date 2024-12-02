package controller.employee;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;

public class EmpDashboardController {
    @FXML
    protected void onViewTasksClick() {
        ProjectApplication.switchScene("/employee/employeeViewTasks.fxml");
    }

    @FXML
    protected void onSubmitTaskClick() {
        ProjectApplication.switchScene("/employee/employeeSubmitTask.fxml");
    }

    @FXML
    protected void onViewProjectsClick() {
        ProjectApplication.switchScene("/employee/employeeViewProjects.fxml");
    }

    @FXML
    protected void onEditAttendanceClick() {
        ProjectApplication.switchScene("/employee/employeeEditAttendance.fxml");
    }

    @FXML
    protected void onMyLeaveRequestsClick() {
        ProjectApplication.switchScene("/employee/employeeLeaveRequests.fxml");
    }

    @FXML
    protected void onWorkCalendarClick(){
        ProjectApplication.switchScene("/employee/employeeWorkCalendar.fxml");
    }

    @FXML
    protected void onProfileClick() {
        ProjectApplication.switchScene("/employee/employeeProfile.fxml");
    }
}
