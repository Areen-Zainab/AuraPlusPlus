package controller.manager;

import com.example.projecthr.*;
import com.example.projecthr.project.Milestone;
import com.example.projecthr.project.Project;
import com.example.projecthr.project.ProjectLog;
import com.example.projecthr.project.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.Factory;
import utility.ProjectUtility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class PMViewProjectController {
    ProjectManager manager;
    Project proj;
    ArrayList<Milestone> milestones;
    Client client;
    ArrayList<Meeting> meetings;
    Milestone chosen;

    @FXML private AnchorPane anchor;
    @FXML private VBox parentVBox, historyVBox;
    @FXML public void initialize() {
        manager = (ProjectManager) Factory.getFactory().getMainApp().getUser();
    }
    @FXML public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(120, 80, 50, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onOtherMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(80, 45, 20, 0.6); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML private void closeForm() { ((Stage) anchor.getScene().getWindow()).close();}

    public void setChosenMilestone(Milestone chosen) {
        this.chosen = chosen;
        try{
            showMilestone();
        }catch (Exception e){
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Milestone Details could not load.");
            closeForm();
        }
    }
    public void setProject(Project project) {
        this.proj = project;
        try {
            manager = ProjectUtility.getInstance().getProjectManager(proj.getManagerId());
            meetings = ProjectUtility.getInstance().getProjectMeetings(proj.getProjectId());
            client = new Client(proj.getClientId());
            if(parentVBox != null) {
                displayProjectDetails();
            }
            else {
                ArrayList<ProjectLog> logs = Factory.getProjectServices().getLogs(proj.getProjectId());
                showLogs(logs);
            }
        }
        catch (Exception e) {
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Project could not load.");
            closeForm();
        }
    }
    private void displayProjectDetails() {
        if(proj == null) {
            closeForm();
        }
        parentVBox.getChildren().clear();
        parentVBox.getChildren().add(titleRow("Project Title: " + proj.getTitle()));// 1st Row: Project Title
        parentVBox.getChildren().add(descriptionRow(proj.getDescription()));// 2nd Row: Description (wrapped text description)

        //3rd Row
        HBox thirdRow = new HBox(10);
        thirdRow.setPadding(new Insets(5, 0, 5, 0));
        thirdRow.setAlignment(Pos.CENTER);
        Button vpButton = getButton("View Proposal Attachment");
        vpButton.setOnMouseClicked(_ ->ProjectApplication.viewAttachment(proj.getProjectProposalFilePath()));
        if(!ProjectApplication.isValidFilePath(proj.getProjectProposalFilePath())){
            vpButton.setDisable(true);vpButton.setText("No Proposal File Found");
        }
        thirdRow.getChildren().addAll(vpButton, genericDateBox("Proposal Submitted: " + proj.getStartDate(), "Proposal Approved: " + (proj.getStartDate() != null ? proj.getStartDate() : "N/A")), projectDates());
        parentVBox.getChildren().add(thirdRow); // 3rd Row: Proposal Attachment, Start Date, and End Date

        // 4th and 5th Rows: Submitted By Details
        parentVBox.getChildren().addAll(clientDetailsBox(), managerDetailsBox());

        // 6th button row
        HBox row6 = new HBox(5);
        row6.setPadding(new Insets(5, 0, 0, 0));
        row6.setAlignment(Pos.CENTER);
        Button viewHistoryButton = getButton("View Project History");
        viewHistoryButton.setOnMouseClicked(_ -> openHistory());
        viewHistoryButton.setPadding(new Insets(10, 10, 10, 0));
        Button management = getButton("Project Management");
        management.setOnMouseClicked(_ -> manageScreen());
        management.setPadding(new Insets(10, 10, 10, 0));

        Button completeButton = getButton("Mark As Complete");
        completeButton.setOnMouseClicked(_ -> {
            if(Factory.getProjectServices().updateProjectStatus(proj.getProjectId(), "Completed")) {
                proj.setStatus("Completed");
                ProjectApplication.showAlert(Alert.AlertType.INFORMATION, "Success", "Project completed!");
                closeForm();
            }
            else{
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Project could not be completed.");
            }
        });
        completeButton.setDisable(!ProjectUtility.areAllMilestonesComplete(proj.getMilestones()));
        completeButton.setPadding(new Insets(10, 10, 10, 0));
        if(proj.getStatus().equals("Completed")) {
            row6.getChildren().addAll(viewHistoryButton);
        }
        else
            row6.getChildren().addAll(viewHistoryButton, management, completeButton);

        parentVBox.getChildren().add(row6);

        // milestones rows
        milestones = proj.getMilestones();
        if(milestones != null && !milestones.isEmpty()) {
            Label milestonesLabel = new Label("Project Milestones: ");
            milestonesLabel.setPadding(new Insets(15, 0, 0, 0));
            milestonesLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: lightgray;");
            parentVBox.getChildren().add(milestonesLabel);
            displayMilestones();
        }

        //meetings rows
        if(meetings!= null && !meetings.isEmpty()) {
            Label meetingLabel = new Label("Project Meetings: ");
            meetingLabel.setPadding(new Insets(15, 0, 0, 0));
            meetingLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: lightgray;");
            parentVBox.getChildren().add(meetingLabel);
            displayMeetings();
        }
    }

    private HBox titleRow(String text){
        HBox titleRowObj = new HBox(10);
        titleRowObj.setPadding(new Insets(10, 0, 10, 0));
        Label projectTitleLabel = new Label(text);
        projectTitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        titleRowObj.getChildren().add(projectTitleLabel);
        return titleRowObj;
    }
    private HBox descriptionRow(String text){
        HBox descripRow = new HBox(10);
        descripRow.setPadding(new Insets(5, 0, 5, 0));
        Text descriptionText = new Text(text);
        descriptionText.wrappingWidthProperty().set(450); // Set wrap width for text
        descriptionText.setStyle("-fx-fill: white;"); // Set text color to white
        TextFlow descriptionFlow = new TextFlow(descriptionText);
        descriptionFlow.setStyle("-fx-background-image: url('file:images/manbg.png'); -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5;");
        HBox.setHgrow(descriptionFlow, Priority.ALWAYS); // Allow growth if needed
        descripRow.getChildren().addAll(descriptionFlow);
        return descripRow;
    }
    private VBox genericDateBox(String date1, String date2){
        VBox datesBox = new VBox(5); // Vertical spacing between dates
        datesBox.setPadding(new Insets(0, 10, 0, 10)); // Add padding for alignment
        Label submissionDateLabel = new Label(date1);
        submissionDateLabel.setStyle("-fx-text-fill: #64e3a1; -fx-font-weight: bold;");
        Label acceptanceDateLabel = new Label(date2);
        acceptanceDateLabel.setStyle("-fx-text-fill: #f0674c; -fx-font-weight: bold;");
        datesBox.getChildren().addAll(submissionDateLabel, acceptanceDateLabel);
        return datesBox;
    }
    private VBox projectDates(){
        VBox datesVBox = new VBox(5);
        datesVBox.setPadding(new Insets(0, 10, 0, 10)); // Add padding for right alignment
        Label startDateLabel = new Label("Start Date: " + proj.getStartDate()); startDateLabel.setStyle("-fx-text-fill: #64e3a1; -fx-font-weight: bold");
        Label endDateLabel = new Label("End Date: " + (proj.getEndDate() != null ? proj.getEndDate() : "N/A")); endDateLabel.setStyle("-fx-text-fill:  #f0674c; -fx-font-weight: bold");
        datesVBox.getChildren().addAll(startDateLabel, endDateLabel);
        return datesVBox;
    }
    private VBox clientDetailsBox(){
        VBox clientDetails = new VBox(5);
        clientDetails.setPadding(new Insets(10, 10, 10, 5));
        Label clientBy = new Label("Submitted By: ");
        Label clientName = new Label(client.getFirstName() + " " + client.getLastName() + " (" + (client.isIndependent() ? "Independent Client" : "Company: " + client.getCompanyName()) + ")");
        Label contactLabel = new Label("Contact: " + client.getEmail() + ", " + client.getPhoneNo());
        clientBy.setStyle("-fx-text-fill: white; -fx-font-size: 9");
        clientName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;  -fx-font-size: 14");
        contactLabel.setStyle("-fx-text-fill: white;;");
        clientDetails.getChildren().addAll(clientBy, clientName, contactLabel);
        return clientDetails;
    }
    private VBox managerDetailsBox(){
        VBox managerDetailsVBox = new VBox(5);
        managerDetailsVBox.setPadding(new Insets(10, 0, 10, 5));
        Label managedByLabel = new Label("Managed By:");
        Label managerNameLabel = new Label(manager.getFirstName() + " " + manager.getLastName() + " (Project Manager)");
        Label managerQualificationsLabel = new Label("Department: " + manager.getDepartment() + ", " + manager.getExperienceYears() + " Years of Experience.");
        managedByLabel.setStyle("-fx-text-fill: white; -fx-font-size: 9");
        managerNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;  -fx-font-size: 14");
        managerQualificationsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        managerDetailsVBox.getChildren().addAll(managedByLabel, managerNameLabel, managerQualificationsLabel);
        return managerDetailsVBox;
    }

    private Button getButton(String text) {
        Button button = new Button(text);
        button.setCursor(Cursor.HAND);
        button.setPrefSize(200, 40);
        button.setStyle("-fx-background-color: rgba(10, 100, 100, 0.7); -fx-text-fill: white; -fx-background-radius: 10;");
        button.setOnMouseEntered(_ -> button.setStyle("-fx-background-color: rgba(10, 60, 100, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        button.setOnMouseExited(_ -> button.setStyle("-fx-background-color: rgba(10, 100, 100, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));

        return button;
    }
    private void displayMilestones() {
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

            parentVBox.getChildren().add(milestoneBox);
        }
    }
    private void displayMeetings(){
        for(Meeting meeting : meetings) {
            VBox meetingBox = new VBox(10);
            meetingBox.setPadding(new Insets(10));
            meetingBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10;");
            meetingBox.setSpacing(8);

            Label titleLabel = new Label("Title: " + meeting.getTitle());
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

            Label agendaLabel = new Label("Agenda: " + meeting.getAgenda());
            agendaLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");
            agendaLabel.setWrapText(true);

            Label location = new Label("Location: " + meeting.getLocation());
            location.setStyle("-fx-text-fill: lightgray; -fx-font-size: 11;");
            Label dateLabel = new Label("Date: " + meeting.getMeetingDate() + ", Time: " + meeting.getMeetingTime());
            dateLabel.setStyle("-fx-text-fill: lightgray; -fx-font-size: 11; -fx-font-weight: bold");

            Label statusLabel = new Label("Status: " + meeting.getStatus());
            switch (meeting.getStatus()) {
                case "Completed": statusLabel.setStyle("-fx-text-fill: darkgreen; -fx-font-size: 12; -fx-font-weight: bold");
                    break;
                case "Scheduled":statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12; -fx-font-weight: bold");
                    break;
                case "Cancelled": statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12; -fx-font-weight: bold");
                    break;
                default:
                    statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 12; -fx-font-weight: bold");
                    break;
            }

            // Organize date and time in a small VBox
            VBox dateTimeBox = new VBox(5, location, dateLabel);
            dateTimeBox.setAlignment(Pos.CENTER_LEFT);
            meetingBox.getChildren().addAll(titleLabel, agendaLabel, dateTimeBox, statusLabel);
            meetingBox.setOnMouseEntered(_ -> meetingBox.setStyle("-fx-background-color: rgba(20, 60, 90, 255); -fx-background-radius: 10;"));
            meetingBox.setOnMouseExited(_ -> meetingBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10;"));
            parentVBox.getChildren().add(meetingBox);
        }
    }
    private void displayTask(ArrayList<Task> tasks) {
        if(tasks == null)
            return;
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

            historyVBox.getChildren().add(taskBox);
        }
    }

    private void openMilestone(Milestone milestone) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/PMScrollView.fxml"));
            Parent newFormRoot = loader.load();
            PMViewProjectController controller = loader.getController();
            controller.setChosenMilestone(milestone);

            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Milestone - " + proj.getTitle());

            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
            displayProjectDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showMilestone(){
        if(chosen == null) {
            closeForm();
        }
        historyVBox.getChildren().add(titleRow("Milestone Title: " + chosen.getMilestoneName()));// 1st Row: Project Title
        historyVBox.getChildren().add(descriptionRow(chosen.getDescription()));// 2nd Row: Description (wrapped text description)

        //3rd Row
        HBox thirdRow = new HBox(10);
        thirdRow.setPadding(new Insets(5, 0, 5, 0));
        thirdRow.setAlignment(Pos.CENTER);
        Button vpButton = getButton("View Milestone Report");
        vpButton.setOnMouseClicked(_ ->ProjectApplication.viewAttachment(chosen.getFilePath()));
        if(!ProjectApplication.isValidFilePath(chosen.getFilePath())){
            LocalDate today = LocalDate.now();
            if(chosen.getEndDate().toLocalDate().isBefore(today)) {
                vpButton.setText("Attach Milestone Report");
                vpButton.setOnMouseClicked(_ -> chosen.setFilePath(ProjectApplication.getAttachment(anchor.getScene().getWindow())));
            }
            else {
                vpButton.setDisable(true);
                vpButton.setText("No Report Attached");
            }
        }
        thirdRow.getChildren().addAll(vpButton, genericDateBox("Milestone Started: " + chosen.getStartDate(), "Milestone Deadline: " + (chosen.getEndDate() != null ? chosen.getEndDate() : "N/A")));
        historyVBox.getChildren().add(thirdRow);

        Button completeButton = getButton("Mark As Complete");
        completeButton.setOnMouseClicked(_ -> {
            if(Factory.getProjectServices().updateMilestoneStatus(chosen.getMilestoneId(), "Completed")) {
                chosen.setStatus("Completed");completeButton. setDisable(true);
                ProjectApplication.showAlert(Alert.AlertType.INFORMATION, "Success", "Milestone has been updated!");
            }
            else{
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Milestone could not be completed.");
            }
        });
        completeButton.setDisable(!ProjectUtility.areAllTasksComplete(chosen.getTasks()));
        completeButton.setPadding(new Insets(10, 10, 10, 0));
        HBox row6 = new HBox(5);
        row6.setPadding(new Insets(5, 0, 0, 0));
        row6.setAlignment(Pos.CENTER);
        row6.getChildren().add(completeButton);
        historyVBox.getChildren().add(row6);

        ArrayList<Task> tasks = chosen.getTasks();
        Label taskLabel = new Label("Tasks: ");
        taskLabel.setPadding(new Insets(15, 0, 0, 0));
        taskLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: lightgray;");
        historyVBox.getChildren().add(taskLabel);
        displayTask(tasks);
    }

    private void manageScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/createProject.fxml"));
            Parent newFormRoot = loader.load();

            createProjectController controller = loader.getController();
            controller.setProject(proj);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Project SetUp - " + proj.getTitle());

            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //done, done
    private void openHistory(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/PMScrollView.fxml"));
            Parent newFormRoot = loader.load();

            PMViewProjectController controller = loader.getController();
            controller.setProject(proj);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Project History - " + proj.getTitle());

            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showLogs(ArrayList<ProjectLog> logs){
        historyVBox.getChildren().clear();
        Label lab = new Label(proj.getTitle() + "- Project Logs");
        lab.setStyle("-fx-font-weight: bold; -fx-font-size: 17px; -fx-text-fill: white");
        lab.setWrapText(true);
        lab.setPadding(new Insets(10));
        historyVBox.getChildren().add(lab);

        for(ProjectLog log : logs){
            HBox logBox = new HBox(10);
            logBox.setPadding(new Insets(5, 10, 5, 10));  // Padding for the HBox
            logBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10;");
            logBox.setSpacing(10);

            Label logTitle = new Label(log.getDisplayName());
            logTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: white");

            Label logDescription = new Label(log.getDescription());
            logDescription.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 12px;");
            logDescription.setMaxWidth(450); // Set the max width to wrap text
            logDescription.setWrapText(true); // Enable text wrapping

            Label doneBy = new Label("Done By: " + log.getUsername());
            doneBy.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 10px;");
            doneBy.setMaxWidth(450); // Set the max width to wrap text
            doneBy.setWrapText(true); // Enable text wrapping

            Label timeLab = new Label("Timestamp: " + log.getTimestamp().toString());
            timeLab.setStyle("-fx-font-size: 12px; -fx-text-fill: #5ed1e5");

            VBox milestoneDetailsBox = new VBox(5);
            milestoneDetailsBox.setPadding(new Insets(10, 0, 10, 0));
            milestoneDetailsBox.getChildren().addAll(logTitle, logDescription, doneBy, timeLab);
            logBox.getChildren().addAll(milestoneDetailsBox);

            logBox.setOnMouseEntered(_ -> logBox.setStyle("-fx-background-color: rgba(20, 60, 90, 255); -fx-background-radius: 10; -fx-text-fill: white;"));
            logBox.setOnMouseExited(_ -> logBox.setStyle("-fx-background-color: rgba(20, 60, 90, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));

            historyVBox.getChildren().add(logBox);
            
        }
    }
}

//---> iske saath updates wala section rehta tbh idk usme kya karna
