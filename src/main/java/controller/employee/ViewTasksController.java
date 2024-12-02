package controller.employee;

import com.example.projecthr.Employee;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.project.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.Factory;

import java.awt.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import static controller.ProjectController.createButton;

public class ViewTasksController {
    Employee employee;
    ArrayList<Task> tasks;

    @FXML private VBox taskVBox;

    @FXML void initialize(){
        employee = (Employee) Factory.getFactory().getMainApp().getUser();
        employee.loadTasks();
        tasks = employee.getTasks();

        displayTask(tasks);
    }


    private void displayTask(ArrayList<Task> tasks) {
        if(tasks == null)
            return;
        taskVBox.getChildren().clear();
        tasks.sort(Comparator.comparing(Task::getDeadline));
        for (Task task : tasks) {
            HBox taskBox = new HBox(10);
            taskBox.setPadding(new Insets(5, 10, 5, 10));  // Padding for the HBox
            taskBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10;");
            taskBox.setSpacing(10);

            Label title = new Label("Title: " + task.getTaskName());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: white");

            Label descrip = new Label(task.getDescription());
            descrip.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 12px;");
            descrip.setMaxWidth(450); // Set the max width to wrap text
            descrip.setWrapText(true); // Enable text wrapping


            Label startDateLabel = new Label("Start: " + task.getAssignedDate().toString());
            startDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5ed1e5");
            Label endDateLabel = new Label("Deadline: " + task.getDeadline().toString());
            endDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #fa7741");

            VBox tsktskBoxBox = new VBox(5);
            tsktskBoxBox.setPadding(new Insets(10, 0, 10, 0));
            tsktskBoxBox.getChildren().addAll(title, descrip, startDateLabel, endDateLabel);
            taskBox.getChildren().addAll(tsktskBoxBox);
            tsktskBoxBox.setPrefWidth(500);

            VBox buttonBox = new VBox(5);
            buttonBox.setPadding(new Insets(10, 0, 10, 0));
            buttonBox.setAlignment(Pos.CENTER);
            Label statusLabel = new Label("Status: " + task.getStatus());
            statusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold");
            buttonBox.getChildren().add(statusLabel);

            switch (task.getStatus()) {
                case "Pending":
                    statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    Button submitTaskButton = createButton("Submit Task", "green");
                    Button applyExtensionButton = createButton("Apply for Extension", "red");

                    submitTaskButton.setOnAction(_ -> {
                        submitTask(task);
                        //task.setTaskAttachment(ProjectApplication.getAttachment(taskVBox.getScene().getWindow()));
                    });

                    applyExtensionButton.setOnAction(_ -> {
                        extensionRequest(task);
                    });

                    buttonBox.getChildren().addAll(submitTaskButton, applyExtensionButton);
                    break;

                case "Completed":
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    Button viewSubmissionButton = createButton("View Submission", "green");


                    viewSubmissionButton.setOnAction(_ -> {
                        ProjectApplication.viewAttachment(task.getTaskAttachment());
                    });

                    buttonBox.getChildren().add(viewSubmissionButton);
                    break;

                case "Missed/Delayed":
                    statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    Button applyExtensionForMissedButton = createButton("Apply for Extension", "red");


                    applyExtensionForMissedButton.setOnAction(_ -> {
                    });

                    buttonBox.getChildren().add(applyExtensionForMissedButton);
                    break;
                default:
                    statusLabel.setStyle("-fx-text-fill: gray;");
                    break;
            }

            taskBox.getChildren().add(buttonBox);
            taskBox.setOnMouseEntered(_ -> taskBox.setStyle("-fx-background-color: rgba(20, 60, 90, 255); -fx-background-radius: 10; -fx-text-fill: white;"));
            taskBox.setOnMouseExited(_ -> taskBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));
            taskVBox.getChildren().add(taskBox);
        }
    }

    void submitTask(Task task) {
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Submit Task - " + task.getTaskName());

        VBox popUpBox = new VBox(10);
        popUpBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label taskNameLabel = new Label("Task: " + task.getTaskName());
        taskNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label taskDescriptionLabel = new Label("Description: " + task.getDescription());
        taskDescriptionLabel.setWrapText(true);
        Label statusLabel = new Label("Status: " + task.getStatus());
        Label currentDeadlineLabel = new Label("Current Deadline: " + task.getDeadline().toString());

        Button attachButton = createButton("Attach File", "rgba(200, 160, 40, 0.6)");
        attachButton.setStyle("-fx-background-radius: 10; -fx-text-fill: white; -fx-background-color: rgba(200, 160, 40, 0.6);");
        attachButton.setOnAction(e -> {
            task.setTaskAttachment(ProjectApplication.getAttachment(taskVBox.getScene().getWindow()));
        });

        HBox buttonHBox = new HBox(10);
        buttonHBox.setAlignment(Pos.CENTER);
        Button submitButton = createButton("Submit Task", "rgba(191, 174, 60, 0.8)");
        submitButton.setStyle("-fx-background-radius: 10; -fx-text-fill: white; -fx-background-color: rgba(191, 174, 60, 0.8);");
        submitButton.setStyle("-fx-background-radius: 10;");
        Button closeButton = createButton("Close", "gray");
        closeButton.setStyle("-fx-background-radius: 10;");

        submitButton.setOnAction(e -> {
            if (task.getTaskAttachment() == null) {
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Attach your task submission!");
            } else {
                if(Factory.getProjectServices().updateTaskStatusToCompleted(task)) {
                    ProjectApplication.showAlert(Alert.AlertType.INFORMATION, "Success!", "Task has been submitted successfully!");
                    task.setStatus("Completed");
                    displayTask(tasks);
                }
                else{
                    ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error!", "An unexpected error occurred. Please try again later!");
                }
                popUpStage.close();
            }
        });

        closeButton.setOnAction(e -> {
            popUpStage.close();
        });
        buttonHBox.getChildren().addAll(closeButton, submitButton);

        popUpBox.getChildren().addAll(taskNameLabel, taskDescriptionLabel, statusLabel, currentDeadlineLabel, attachButton, buttonHBox);
        Scene popUpScene = new Scene(popUpBox, 400, 300);
        popUpStage.setScene(popUpScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }

    void extensionRequest(Task task) {
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Apply for Extension - " + task.getTaskName());

        VBox popUpBox = new VBox(10);
        popUpBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.3)");
        popUpBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label taskNameLabel = new Label("Task: " + task.getTaskName());
        taskNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label taskDescriptionLabel = new Label("Description: " + task.getDescription());
        taskDescriptionLabel.setWrapText(true);
        Label currentDeadlineLabel = new Label("Current Deadline: " + task.getDeadline().toString());

        DatePicker newDeadlinePicker = new DatePicker();
        newDeadlinePicker.setValue(LocalDate.now().plusDays(1)); // Set to at least tomorrow
        newDeadlinePicker.setMinWidth(200);

        Label reasonLabel = new Label("Reason for Extension:");
        TextField reasonTextField = new TextField();
        reasonTextField.setPromptText("Enter reason for the extension");
        reasonTextField.setMinWidth(200);

        Button submitButton = createButton("Submit Extension", "rgba(191, 174, 40, 0.6)");
        Button closeButton = createButton("Close", "gray");

        String reason = reasonTextField.getText();

        submitButton.setOnAction(e -> {
            Date newDeadline = Date.valueOf(newDeadlinePicker.getValue());
            Date today = Date.valueOf(LocalDate.now());
            if (newDeadline.after(today) && newDeadline.after(task.getDeadline())) {
                if(Factory.getProjectServices().insertTaskExtension(task.getTaskId(), employee.getUserId(), reason, newDeadline)) {
                    ProjectApplication.showAlert(Alert.AlertType.INFORMATION, "Success!", "Request has been submitted successfully. Any updates will be updated here!");
                    task.setDeadline(java.sql.Date.valueOf(newDeadline.toLocalDate()));
                    displayTask(tasks);
                    popUpStage.close();
                }
                else{
                    ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred. Please try again later!");
                }
            } else {
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Please provide valid details for the extension.");
            }
        });

        closeButton.setOnAction(e -> {
            popUpStage.close();
        });

        popUpBox.getChildren().addAll(taskNameLabel, taskDescriptionLabel, currentDeadlineLabel, newDeadlinePicker, reasonLabel, reasonTextField, submitButton, closeButton);
        Scene popUpScene = new Scene(popUpBox, 400, 300);
        popUpStage.setScene(popUpScene);
        popUpStage.showAndWait();
    }


    //UI / UX
    @FXML
    protected void onDashboardClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeDashboard.fxml");
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
    protected void onWorkCalendarClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeWorkCalendar.fxml");
    }

    @FXML
    protected void onProfileClick() throws IOException {
        ProjectApplication.switchScene("/employee/employeeProfile.fxml");
    }
}
