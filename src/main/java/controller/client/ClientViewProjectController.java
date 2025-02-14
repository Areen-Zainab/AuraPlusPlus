package controller.client;

import com.example.projecthr.*;
import com.example.projecthr.project.Milestone;
import com.example.projecthr.project.Project;
import com.example.projecthr.project.ProjectLog;
import controller.manager.PMViewProjectController;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.Factory;
import utility.ProjectUtility;

import java.util.ArrayList;
import java.util.Comparator;

public class ClientViewProjectController {
    User currentUser;
    Project proj;
    ArrayList<Milestone> milestones;
    ProjectManager manager;
    ArrayList<Meeting> meetings;

    @FXML private VBox parentVBox, historyVBox;
    @FXML public void initialize() {
        currentUser = Factory.getFactory().getMainApp().getUser();
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
    @FXML private void closeForm() { if(parentVBox != null) ((Stage) parentVBox.getScene().getWindow()).close();
    else ((Stage) historyVBox.getScene().getWindow()).close();}

    public void setProject(Project project) {
        this.proj = project;
        try {
            manager = ProjectUtility.getInstance().getProjectManager(proj.getManagerId());
            meetings = ProjectUtility.getInstance().getProjectMeetings(proj.getProjectId());
            if(parentVBox != null) {
                displayProjectDetails();
            }
            else{
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
        HBox titleRow = new HBox(10);
        titleRow.setPadding(new Insets(10, 0, 10, 0));
        Label projectTitleLabel = new Label("Project Title: " + proj.getTitle());
        projectTitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        titleRow.getChildren().add(projectTitleLabel);
        parentVBox.getChildren().add(titleRow);// 1st Row: Project Title

        HBox descriptionRow = new HBox(10);
        descriptionRow.setPadding(new Insets(5, 0, 5, 0));
        Text descriptionText = new Text(proj.getDescription());
        descriptionText.wrappingWidthProperty().set(450); // Set wrap width for text
        descriptionText.setStyle("-fx-fill: white;"); // Set text color to white
        TextFlow descriptionFlow = new TextFlow(descriptionText);
        descriptionFlow.setStyle("-fx-background-image: url('file:images/backdrop.png'); -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5;");
        HBox.setHgrow(descriptionFlow, Priority.ALWAYS); // Allow growth if needed
        descriptionRow.getChildren().addAll(descriptionFlow);
        parentVBox.getChildren().add(descriptionRow);// 2nd Row: Description and Dates (wrapped text description)

        // Dates (proposal submission + acceptance)
        VBox datesBox = new VBox(5); // Vertical spacing between dates
        datesBox.setPadding(new Insets(0, 10, 0, 10)); // Add padding for alignment
        Label submissionDateLabel = new Label("Submission: " + proj.getStartDate());
        submissionDateLabel.setStyle("-fx-text-fill: #64e3a1;");
        Label acceptanceDateLabel = new Label("Acceptance: " + (proj.getStartDate() != null ? proj.getStartDate() : "N/A"));
        acceptanceDateLabel.setStyle("-fx-text-fill: #ff474c;");
        datesBox.getChildren().addAll(submissionDateLabel, acceptanceDateLabel);


        HBox thirdRow = new HBox(10);
        thirdRow.setPadding(new Insets(5, 0, 5, 0));
        thirdRow.setAlignment(Pos.CENTER);

        Button viewProposalButton = getButton();
        VBox datesVBox = new VBox(5);
        datesVBox.setPadding(new Insets(0, 10, 0, 10)); // Add padding for right alignment
        Label startDateLabel = new Label("Start Date: " + proj.getStartDate()); startDateLabel.setStyle("-fx-text-fill: #64e3a1; -fx-font-weight: bold");
        Label endDateLabel = new Label("End Date: " + (proj.getEndDate() != null ? proj.getEndDate() : "N/A")); endDateLabel.setStyle("-fx-text-fill: #ff474c; -fx-font-weight: bold");
        datesVBox.getChildren().addAll(startDateLabel, endDateLabel);

        thirdRow.getChildren().addAll(viewProposalButton, datesBox, datesVBox);
        parentVBox.getChildren().add(thirdRow); // 3rd Row: Proposal Attachment, Start Date, and End Date

        // 4th Row: Managed By (Project Manager details)
        VBox managerDetailsVBox = new VBox(5);
        managerDetailsVBox.setPadding(new Insets(10, 0, 10, 5));
        Label managedByLabel = new Label("Managed By:");
        Label managerNameLabel = new Label(manager.getFirstName() + " " + manager.getLastName() + " (Project Manager)");
        Label managerQualificationsLabel = new Label("Department: " + manager.getDepartment() + ", " + manager.getExperienceYears() + " Years of Experience.");
        managedByLabel.setStyle("-fx-text-fill: white; -fx-font-size: 9");
        managerNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;  -fx-font-size: 14");
        managerQualificationsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        managerDetailsVBox.getChildren().addAll(managedByLabel, managerNameLabel, managerQualificationsLabel);
        parentVBox.getChildren().add(managerDetailsVBox);

        milestones = proj.getMilestones();
        if(milestones != null && !milestones.isEmpty()) {
            Label milestonesLabel = new Label("Project Milestones: ");
            milestonesLabel.setPadding(new Insets(15, 0, 0, 0));
            milestonesLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: gray;");
            parentVBox.getChildren().add(milestonesLabel);
            displayMilestones();
        }

        if(meetings!= null && !meetings.isEmpty()) {
            Label meetingLabel = new Label("Project Meetings: ");
            meetingLabel.setPadding(new Insets(15, 0, 0, 0));
            meetingLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: gray;");
            parentVBox.getChildren().add(meetingLabel);
            displayMeetings();
        }

        HBox row6 = new HBox(5);
        row6.setPadding(new Insets(5, 0, 0, 0));
        row6.setAlignment(Pos.CENTER);
        Button viewHistoryButton = new Button("View Project History");
        viewHistoryButton.setPrefSize(150, 30);
        viewHistoryButton.setStyle("-fx-background-color: rgba(50, 20, 10, 0.9); -fx-text-fill: white; -fx-background-radius: 10;");
        viewHistoryButton.setOnMouseEntered(_ -> viewHistoryButton.setStyle("-fx-background-color: rgba(25, 13, 5, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        viewHistoryButton.setOnMouseExited(_ -> viewHistoryButton.setStyle("-fx-background-color: rgba(50, 20, 10, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        viewHistoryButton.setOnMouseClicked(_ -> openHistory());
        viewHistoryButton.setPadding(new Insets(10, 10, 10, 0));
        row6.getChildren().addAll(viewHistoryButton);
        parentVBox.getChildren().add(row6);
    }

    private Button getButton() {
        Button viewProposalButton = new Button("View Proposal Attachment");
        viewProposalButton.setCursor(Cursor.HAND);
        viewProposalButton.setPrefSize(200, 40);
        viewProposalButton.setStyle("-fx-background-color: rgba(50, 20, 10, 0.9); -fx-text-fill: white; -fx-background-radius: 10;");
        viewProposalButton.setOnMouseClicked(_ ->ProjectApplication.viewAttachment(proj.getProjectProposalFilePath()));
        viewProposalButton.setOnMouseEntered(_ -> viewProposalButton.setStyle("-fx-background-color: rgba(25, 13, 5, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        viewProposalButton.setOnMouseExited(_ -> viewProposalButton.setStyle("-fx-background-color: rgba(50, 20, 10, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        return viewProposalButton;
    }
    private void displayMilestones() {
        milestones.sort(Comparator.comparing(Milestone::getStartDate));
        for (Milestone milestone : milestones) {
            HBox milestoneBox = new HBox(10);
            milestoneBox.setPadding(new Insets(5, 10, 5, 10));  // Padding for the HBox
            milestoneBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10;");
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
            milestoneBox.setOnMouseEntered(_ -> milestoneBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
            milestoneBox.setOnMouseExited(_ -> milestoneBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));

            parentVBox.getChildren().add(milestoneBox);
        }
    }
    private void displayMeetings(){
        for(Meeting meeting : meetings) {
            VBox meetingBox = new VBox(10);
            meetingBox.setPadding(new Insets(10));
            meetingBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10;");
            meetingBox.setSpacing(8);

            Label titleLabel = new Label("Title: " + meeting.getTitle());
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

            Label agendaLabel = new Label("Agenda: " + meeting.getAgenda());
            agendaLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");
            agendaLabel.setWrapText(true);

            Label location = new Label("Location: " + meeting.getLocation());
            location.setStyle("-fx-text-fill: gray; -fx-font-size: 11;");
            Label dateLabel = new Label("Date: " + meeting.getMeetingDate() + ", Time: " + meeting.getMeetingTime());
            dateLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11; -fx-font-weight: bold");

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
            meetingBox.setOnMouseEntered(_ -> meetingBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.9); -fx-background-radius: 10;"));
            meetingBox.setOnMouseExited(_ -> meetingBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10;"));
            parentVBox.getChildren().add(meetingBox);
        }
    }
    private void openMilestone(Milestone milestone) {

    }

    private void openHistory(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/historyScrollView.fxml"));
            Parent newFormRoot = loader.load();

            ClientViewProjectController controller = loader.getController();
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
//milestone pr click ho tou tasks khul janay chahiye ---> 1. function to fetch asll tasks and store, 2. panels wala sab repeat hoga
//project history dikhana
//show the project proposal's submission and acceptance dates
