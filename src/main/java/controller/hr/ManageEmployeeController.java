package controller.hr;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;
import java.io.IOException;

public class ManageEmployeeController {
    @FXML
    protected void onProfileClick() throws IOException {
        ProjectApplication.switchScene("adminProfile.fxml");
    }

    @FXML
    protected void onDashboardClick() throws IOException {
        ProjectApplication.switchScene("adminDashboard.fxml");
    }

    @FXML
    protected void onEmployeesClick() throws IOException {
        ProjectApplication.switchScene("adminViewEmployees.fxml");
    }

    @FXML
    protected void onGeneratePayrollClick() throws IOException {
        ProjectApplication.switchScene("adminGeneratePayroll.fxml");
    }

    @FXML
    protected void onViewAttendanceClick() throws IOException {
        ProjectApplication.switchScene("adminViewAttendance.fxml");
    }

    @FXML
    protected void onLeaveRequestsClick() throws IOException {
        ProjectApplication.switchScene("adminLeaveRequests.fxml");
    }
}
