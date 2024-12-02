package controller.hr;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;
import java.io.IOException;

public class ManageEmployeeController {
    @FXML
    protected void onProfileClick() throws IOException {
        ProjectApplication.switchScene("/hr/adminProfile.fxml");
    }

    @FXML
    protected void onDashboardClick() throws IOException {
        ProjectApplication.switchScene("/hr/adminDashboard.fxml");
    }

    @FXML
    protected void onEmployeesClick() throws IOException {
        ProjectApplication.switchScene("/hr/adminViewEmployees.fxml");
    }

    @FXML
    protected void onGeneratePayrollClick() throws IOException {
        ProjectApplication.switchScene("/hr/adminGeneratePayroll.fxml");
    }

    @FXML
    protected void onViewAttendanceClick() throws IOException {
        ProjectApplication.switchScene("/hr/adminViewAttendance.fxml");
    }

    @FXML
    protected void onLeaveRequestsClick() throws IOException {
        ProjectApplication.switchScene("/hr/adminLeaveRequests.fxml");
    }
}
