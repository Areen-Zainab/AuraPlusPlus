package controller.hr;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;
import java.io.IOException;

public class HRProfileController {

    @FXML
    protected void onManageEmployeesClick() throws IOException {
        ProjectApplication.switchScene("adminManageEmployees.fxml");
    }

    @FXML
    protected void onDashboardClick() throws IOException {
        ProjectApplication.switchScene("adminDashboard.fxml");
    }

}
