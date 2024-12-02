package controller.manager;

import com.example.projecthr.Employee;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.project.Milestone;
import com.example.projecthr.project.Project;
import com.example.projecthr.project.Task;
import controller.client.EditMeetingController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.Factory;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import static com.example.projecthr.ProjectApplication.showAlert;
import static utility.ProjectUtility.getLatestUpdatedTask;

public class createProjectController {
    Project proj;
    ArrayList<Milestone> milestones;
    Milestone chosenMilestone;

    ArrayList<Employee> emp;

    @FXML private Line lineline;
    @FXML private ImageView addTaskBut;
    @FXML private Label nameLabel, taskLabel, newTaskLabel, newMilestoneLabel, attachment;
    @FXML private AnchorPane MilestoneAnchor, TaskAnchor;
    @FXML private VBox milestoneVBox, taskVBox;
    @FXML private TextField titleTextField;
    @FXML private TextArea descripTextField;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> priorityComboBox, employeeComboBox;
    @FXML Button saveButton, cancelButton, addTaskButton;

    @FXML public void initialize() {
        if(milestoneVBox != null) {
            TaskAnchor.setDisable(true);
            TaskAnchor.setVisible(false);
            taskLabel.setVisible(false);
            lineline.setVisible(false);
            addTaskBut.setVisible(false);
            addTaskBut.setDisable(true);

            MilestoneAnchor.setVisible(true);
            MilestoneAnchor.setPrefHeight(350);
        }
        if(priorityComboBox != null) {
            priorityComboBox.getItems().addAll("High", "Medium", "Low");
        }
        if(addTaskButton != null) {
            addTaskButton.setVisible(false);
            addTaskButton.setDisable(true);
        }
        if(employeeComboBox != null) {
            emp = Factory.getEmployeeServices().getAllEmployees();
            employeeComboBox.getItems().clear();
            for(Employee employee : emp) {
                String firstName = employee.getFirstName();
                String lastName = employee.getLastName();
                String fullName = firstName + " " + lastName + " - " + employee.getUserId();
                employeeComboBox.getItems().add(fullName);
            }
        }
    }
    @FXML private void loadPage() {
        if(proj == null) {
            closeForm();
        }
        TaskAnchor.setDisable(true);
        TaskAnchor.setVisible(false);
        taskLabel.setVisible(false);
        lineline.setVisible(false);
        addTaskBut.setVisible(false);
        addTaskBut.setDisable(true);

        MilestoneAnchor.setVisible(true);
        MilestoneAnchor.setPrefHeight(350);
        milestones = proj.getMilestones();
        displayMilestones();
    }
    private void displayMilestones() {
        milestoneVBox.getChildren().clear();
        milestones.sort(Comparator.comparing(Milestone::getStartDate));
        for (Milestone milestone : milestones) {
            HBox milestoneBox = new HBox(10);
            milestoneBox.setPadding(new Insets(5, 10, 5, 10));  // Padding for the HBox
            milestoneBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10;");
            milestoneBox.setSpacing(10);

            Label milestoneTitle = new Label("Title: " + milestone.getMilestoneName());
            milestoneTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: white");

            Label milestoneDescriptionLabel = new Label(milestone.getDescription());
            milestoneDescriptionLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 12px;");
            milestoneDescriptionLabel.setMaxWidth(450); // Set the max width to wrap text
            milestoneDescriptionLabel.setWrapText(true); // Enable text wrapping

            Label startDateLabel = new Label("Start: " + milestone.getStartDate().toString());
            startDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5ed1e5");
            Label endDateLabel = new Label("End: " + milestone.getEndDate().toString());
            endDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #fa7741");

            VBox milestoneDetailsBox = new VBox(5);
            milestoneDetailsBox.setPadding(new Insets(10, 0, 10, 0));
            milestoneDetailsBox.getChildren().addAll(milestoneTitle, milestoneDescriptionLabel, startDateLabel, endDateLabel);
            milestoneBox.getChildren().addAll(milestoneDetailsBox);

            milestoneBox.setOnMouseClicked((MouseEvent _) -> openMilestone(milestone));
            milestoneBox.setOnMouseEntered(_ -> milestoneBox.setStyle("-fx-background-color: rgba(20, 60, 90, 255); -fx-background-radius: 10; -fx-text-fill: white;"));
            milestoneBox.setOnMouseExited(_ -> milestoneBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));

            milestoneVBox.getChildren().add(milestoneBox);
        }
    }
    private void openMilestone(Milestone milestone){
        chosenMilestone = milestone;
        MilestoneAnchor.setPrefHeight(180);
        TaskAnchor.setVisible(true); TaskAnchor.setDisable(false); taskLabel.setVisible(true);
        lineline.setVisible(true);
        addTaskBut.setVisible(true); addTaskBut.setDisable(false);

        milestone.loadTasks();
        ArrayList<Task> tasks = milestone.getTasks();
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

            Label assigned = new Label("Assigned To: " + ((task.getEmpName() == null || task.getEmpName().isBlank())? "No one." :  task.getEmpName()));
            assigned.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 11px;");
            assigned.setMaxWidth(450);
            assigned.setWrapText(true);


            Label startDateLabel = new Label("Start: " + task.getAssignedDate().toString());
            startDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5ed1e5");
            Label endDateLabel = new Label("End: " + task.getDeadline().toString());
            endDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #fa7741");

            VBox tsktskBoxBox = new VBox(5);
            tsktskBoxBox.setPadding(new Insets(10, 0, 10, 0));
            tsktskBoxBox.getChildren().addAll(title, descrip, assigned, startDateLabel, endDateLabel);
            taskBox.getChildren().addAll(tsktskBoxBox);

            taskBox.setOnMouseEntered(_ -> taskBox.setStyle("-fx-background-color: rgba(20, 60, 90, 255); -fx-background-radius: 10; -fx-text-fill: white;"));
            taskBox.setOnMouseExited(_ -> taskBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));

            taskVBox.getChildren().add(taskBox);
        }
    }


    public void setProject(Project project) {
        this.proj = project;
        loadPage();
    }
    private void reloadMilestones() {
        proj.loadMilestones();
    }
    @FXML private void closeForm() {
        if(nameLabel != null)
            ((Stage) nameLabel.getScene().getWindow()).close();
        else if(newTaskLabel != null)
            ((Stage) newTaskLabel.getScene().getWindow()).close();
        else if(newMilestoneLabel != null)
            ((Stage) newMilestoneLabel.getScene().getWindow()).close();
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        ProjectApplication.showAlert(alertType, title, message);
    }

    @FXML private void addMilestone(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/addMilestone.fxml"));
            Parent newFormRoot = loader.load();

            createProjectController controller = loader.getController();
            controller.setMilestoneProject(proj);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("New Milestone - " + proj.getTitle());
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();

            reloadMilestones();
            loadPage();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: Please try again later.");
        }
    }
    private void newMilestoneSetUp(){
        newMilestoneLabel.setText("New Milestone - " + proj.getTitle());
        //newMilestoneSetUp();
    }
    @FXML private void saveMilestoneClick() {
        try {
            String title = titleTextField.getText();
            String descrip = descripTextField.getText();
            Date deadline = (deadlinePicker.getValue() == null) ? null : Date.valueOf(deadlinePicker.getValue());
            String priority = priorityComboBox.getValue();

            Date date = Date.valueOf(LocalDate.now());
            if (title.isBlank() || descrip.isBlank() || deadline == null || deadline.before(date) || priority == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill all the required fields!");
                return;
            }
            boolean success = Factory.getProjectServices().insertMilestone(title, descrip, deadline, priority, proj.getProjectId());
            if(success){
                showAlert(Alert.AlertType.INFORMATION, "Success", "New Milestone added successfully!");
                Factory.getProjectServices().updateProjectStatus(proj.getProjectId(), "Active");
                proj.loadMilestones();

                chosenMilestone = proj.getLatestMilestone();

                cancelButton.setDisable(true);
                saveButton.setDisable(true);
                cancelButton.setVisible(false);
                saveButton.setVisible(false);
                addTaskButton.setDisable(false);

                addTaskButton.setVisible(true);
            }

        }catch (Exception e) {
            e.printStackTrace();
            closeForm();
        }

    }

    @FXML private void addTask(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/addTask.fxml"));
            Parent newFormRoot = loader.load();

            createProjectController controller = loader.getController();
            controller.setTaskMilestone(chosenMilestone);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("New Task - " + proj.getTitle());
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();

            reloadMilestones();
            loadPage();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: " + e.getMessage());
        }
    }
    private void newTaskSetUp(){
        newTaskLabel.setText("New Task - " + chosenMilestone.getMilestoneName());
    }
    @FXML public void saveTaskClick(){
        saveTask();
    }
    private void saveTask(){
        try {
            String title = titleTextField.getText();
            String descrip = descripTextField.getText();
            Date deadline = (deadlinePicker.getValue() == null) ? null : Date.valueOf(deadlinePicker.getValue());
            String priority = priorityComboBox.getValue();
            String pdfPath = attachment.getText();
            int assignedTo = 1;

            String empSelected = employeeComboBox.getValue();
            if(empSelected != null) {
                String[] parts = empSelected.split(" - ");
                assignedTo = Integer.parseInt((parts[1].trim()));
            }

            Date date = Date.valueOf(LocalDate.now());
            if (title.isBlank() || descrip.isBlank() || deadline == null || deadline.before(date) || priority == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill all the required fields!");
                return;
            } else {
                boolean success = Factory.getProjectServices().insertTask(title, descrip, deadline, pdfPath, priority, chosenMilestone.getMilestoneId(), assignedTo);
                if(success){
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Task added successfully!");
                    chosenMilestone.loadTasks();
                    closeForm();
                }

            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: Please try again later.");
            e.printStackTrace();
            closeForm();
        }

    }
    @FXML public void attachFile(){
        String attachmentFile = ProjectApplication.getAttachment((newTaskLabel.getScene().getWindow()));
        if(attachmentFile == null)
            attachment.setText("No PDF attached...");
        else attachment.setText(attachmentFile);
    }

    private void setMilestoneProject(Project project) {
        this.proj = project;
        newMilestoneSetUp();
    }

    private void setTaskMilestone(Milestone milestone) {
        chosenMilestone = milestone;
        newTaskSetUp();
    }

    //UI/UX
    @FXML private void applyGlowEffect(MouseEvent mouseEvent) {
        ImageView im = (ImageView) mouseEvent.getSource();
        Glow glow = new Glow();
        glow.setLevel(0.8);
        im.setEffect(glow);
    }
    @FXML private void removeGlowEffect(MouseEvent mouseEvent) { ((ImageView) mouseEvent.getSource()).setEffect(null); }
    @FXML public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(88,109,128,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(41,59,73,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(10, 10, 10, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(69,94,114,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void cancelChanges() {
        closeForm();
    }
}
