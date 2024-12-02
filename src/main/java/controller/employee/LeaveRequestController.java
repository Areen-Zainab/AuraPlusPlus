package controller.employee;

import com.example.projecthr.Employee;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.LeaveRequest;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utility.Factory;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class LeaveRequestController {
    Employee employee;
    @FXML private TextArea leaveReason;
    @FXML private DatePicker leaveStartPicker, leaveEndPicker;
    @FXML private ComboBox<String> leaveTypePicker;
    @FXML private VBox parentPanel;


    @FXML void initialize() {
        employee = (Employee) Factory.getFactory().getMainApp().getUser();
        leaveTypePicker.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: white");
        leaveTypePicker.getItems().addAll(
                "Annual Leave",
                "Sick Leave",
                "Paternity Leave",
                "Maternity Leave",
                "Bereavement Leave",
                "Casual Leave"
        );
        leaveReason.setStyle("-fx-text-fill: white; -fx-background-color: black");
        employee.loadLeaveRequests();
        loadPrevious();

    }

    @FXML protected void saveButtonClick(){
        try {
            String reason = leaveReason.getText();
            Date startDate = Date.valueOf(leaveStartPicker.getValue());
            Date endDate = Date.valueOf(leaveEndPicker.getValue());
            String type = leaveTypePicker.getValue();

            if (reason == null || reason.isBlank() || type == null || type.isBlank()) {
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Incomplete Information", "Please fill in all the required fields!");
                return;
            }

            Date today = Date.valueOf(LocalDate.now());
            if (startDate == null || startDate.before(today)|| endDate == null || endDate.before(today) || endDate.before(startDate)) {
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Incorrect IInformation", "Invalid dates selected.");
                return;
            }

            boolean success = Factory.getEmployeeServices().addLeaveRequest(type, startDate, endDate, reason, employee.getUserId());
            if(success){
                ProjectApplication.showAlert(Alert.AlertType.INFORMATION, "Success!", "New request submitted! You will be notified of any confirmations.");
                employee.loadLeaveRequests();
                loadPrevious();
            }
            else{
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "ERROR", "Leave Request could not be submitted. Try again later!");
            }
            leaveTypePicker.getSelectionModel().clearSelection();
            leaveReason.clear();
            leaveStartPicker.setValue(null);
            leaveEndPicker.setValue(null);

        }catch (Exception e){
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "ERROR", "An unexpected error occurred!");
        }
    }

    private void loadPrevious() {
        ArrayList<LeaveRequest> leaves = employee.getLeaveRequests();
        if(leaves == null)
            return;
        parentPanel.getChildren().clear();
        leaves.sort(Comparator.comparing(LeaveRequest::getAppliedDate));
        for (LeaveRequest leaveRequest : leaves) {
            VBox leaveBox = new VBox(10);
            leaveBox.setPadding(new Insets(5, 5, 5, 5));  // Padding for the HBox
            leaveBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10;");
            leaveBox.setSpacing(10);
            leaveBox.setPrefHeight(100);

            Label title = new Label(leaveRequest.getLeaveType());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: white");

            Label descrip = new Label(leaveRequest.getReason());
            descrip.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 12px;");
            descrip.setMaxWidth(280); // Set the max width to wrap text
            descrip.setWrapText(true); // Enable text wrapping

            Label assigned = new Label("From: " + leaveRequest.getStartDate() + " - To: " + leaveRequest.getEndDate());
            assigned.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 11px;");
            assigned.setMaxWidth(280);
            assigned.setWrapText(true);

            leaveBox.setOnMouseEntered(_ -> leaveBox.setStyle("-fx-background-color: rgba(20, 60, 90, 255); -fx-background-radius: 10; -fx-text-fill: white;"));
            leaveBox.setOnMouseExited(_ -> leaveBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));

            leaveBox.getChildren().addAll(title, descrip, assigned);
            parentPanel.getChildren().add(leaveBox);
        }
    }



    // UI/UX
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
    protected void onWorkCalendarClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeWorkCalendar.fxml");
    }

    @FXML
    protected void onProfileClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeProfile.fxml");
    }

    @FXML
    protected void onLeaveRequestButtonClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeLeaveRequests.fxml");
    }
}
