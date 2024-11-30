package controller.manager;

import com.example.projecthr.*;
import com.example.projecthr.project.Project;
import controller.client.ClientMeetingController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import utility.Factory;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.example.projecthr.ProjectApplication.showAlert;
import static controller.ProjectController.createButton;
import static controller.ProjectController.getTime;

public class PMMeetingController {
    User manager;
    ArrayList<Meeting> meetings;

    @FXML private VBox parentPanel;
    @FXML private HBox projparentPanel;
    @FXML private Label nameLabel, pendingLabel, upcomingLabel, noResultsLabel, projectIDTextField, projectTitleTextField;
    @FXML private TextField searchBar, addressTextField, nameTextField;
    @FXML private TextArea descripTextField;
    @FXML private ComboBox<String> filterComboBox, hourComboBox, minComboBox, whenComboBox, priorityComboBox;
    @FXML private CheckBox elsewhereCheck, virtualCheck, officeCheck;
    @FXML private DatePicker meetingDatePicker;
    @FXML private Button saveButton;
    @FXML private ImageView addButton;

    @FXML
    public void initialize() {
        manager = Factory.getFactory().getMainApp().getUser();

        if(projectIDTextField != null && projectTitleTextField != null) {
            projectIDTextField.setVisible(false);
            projectTitleTextField.setVisible(false);
        }
        meetingSetUp(hourComboBox, minComboBox, whenComboBox, descripTextField, priorityComboBox, virtualCheck, officeCheck, elsewhereCheck, addressTextField);
        if(parentPanel != null) {
            if(nameLabel != null) nameLabel.setText(manager.getFirstName() + "'s Meetings");
            if(filterComboBox != null) {
                filterComboBox.getItems().addAll("All", "Scheduled", "Cancelled", "Request Pending", "Completed", "Latest", "Oldest");
                filterComboBox.setValue("All");
                filterComboBox.valueProperty().addListener((_, _, newValue) -> {
                    if (newValue != null) {
                        applyFilter(newValue);
                    }
                });
            }
            if(searchBar != null) {
                searchBar.textProperty().addListener((_, _, newValue) -> filterMeetings(newValue));
            }

            if(pendingLabel != null && upcomingLabel != null) {
                pendingLabel.setText("Pending: "+ ((ProjectManager) manager).getPendingMeetingCount());
                upcomingLabel.setText("Upcoming: " + ((ProjectManager) manager).getScheduledMeetingCount());
            }

            if(addButton != null) {
                addButton.setOnMouseEntered(_-> applyGlowEffect());
                addButton.setOnMouseExited(_ -> removeGlowEffect());
            }

            noResultsLabel = new Label("No matching meetings found.");
            noResultsLabel.setVisible(false);
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 20"); noResultsLabel.setAlignment(Pos.CENTER);
            loadManagerMeetings(null);
        }
        if(projparentPanel != null){
            populateProjectPanels();
        }
    }
    static void meetingSetUp(ComboBox<String> hourComboBox, ComboBox<String> minComboBox, ComboBox<String> whenComboBox, TextArea descripTextField, ComboBox<String> priorityComboBox, CheckBox virtualCheck, CheckBox officeCheck, CheckBox elsewhereCheck, TextField addressTextField) {
        ClientMeetingController.meetingSetUp(hourComboBox, minComboBox, whenComboBox, descripTextField, priorityComboBox, virtualCheck, officeCheck, elsewhereCheck, addressTextField);
    }

    @FXML protected void onLogoutButtonClick() {
        ProjectApplication.switchScene("LoginForm.fxml");
    }
    @FXML protected void onProfileButtonClick() throws IOException {
        System.out.println("Profile Button clicked");
        ProjectApplication.switchScene("/manager/PMProfile.fxml");
    }
    @FXML protected void onProjectButtonClick()throws IOException {
        System.out.println("Project Button clicked");
        ProjectApplication.switchScene("/manager/PMProjects.fxml");
    }
    @FXML protected void onProposalButtonClick() {
        ProjectApplication.switchScene("/manager/PMProposal.fxml");
    }
    @FXML protected void onMeetingButtonClick() {
        ProjectApplication.switchScene("/manager/manageMeetings.fxml");
    }
    @FXML protected void onUpdateButtonClick() {
        System.out.println("Update Button clicked");
    }
    @FXML protected void onDashboardButtonClick() throws IOException {
        System.out.println("Dashboard Button clicked");
        ProjectApplication.switchScene("/manager/PMDashboard.fxml");
    }
    @FXML public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(88,109,128,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onOtherMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:   rgba(23,39,52,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(10, 10, 10, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(68,85,101,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onWorkcalendarButtonClick(){}

    @FXML
    public void addButtonClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/AddMeeting.fxml"));
            Parent newFormRoot = loader.load();
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Schedule New Meeting");
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: " + e.getMessage());
        }
    }
    @FXML private void closeForm() { ((Stage) saveButton.getScene().getWindow()).close();}

    @FXML
    private void saveButtonClick() {
        try {
            String title = nameTextField.getText();
            String agenda = descripTextField.getText();
            LocalDate date = meetingDatePicker.getValue();
            String hour = hourComboBox.getValue();
            String minute = minComboBox.getValue();
            String amPm = whenComboBox.getValue();
            String location = virtualCheck.isSelected() ? "Virtual" : officeCheck.isSelected() ? "Office" : elsewhereCheck.isSelected() ? "Elsewhere" : null;
            String address = virtualCheck.isSelected() ? null : addressTextField.getText();

            if (title == null || title.isBlank() || agenda == null || agenda.isBlank() ||
                    date == null || hour == null || minute == null || amPm == null ||
                    priorityComboBox.getValue() == null|| priorityComboBox.getValue().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid meeting details: No fields should be empty!");
            }

            if(projectIDTextField == null || projectIDTextField.getText().isEmpty()){
                showAlert(Alert.AlertType.ERROR, "Error", "Select a project!");
            }

            LocalDate today = LocalDate.now();
            if (date == null || date.isBefore(today) || date.equals(today)) {
                throw new IllegalArgumentException("The selected meeting date is invalid!");
            }
            Time time = convertToTime(hour, minute, amPm);

            int projectId = Integer.parseInt(projectIDTextField.getText());
            ((ProjectManager) manager).scheduleMeeting(projectId, title, agenda, date, time, location, address, priorityComboBox.getValue());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your meeting has been successfully scheduled. Wait for Manager confirmation.");
            closeForm();
            meetings = ((ProjectManager) manager).getMeetings();
            loadManagerMeetings(null);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid meeting details: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occured. Please try again later." + e.getMessage());
            closeForm();
        }
    }

    //set up meetings
    public void loadManagerMeetings(String filter) {
        if (parentPanel != null) {
            parentPanel.getChildren().clear();
            parentPanel.setSpacing(10);

            meetings = ((ProjectManager)manager).getMeetings();
            meetings = Factory.getMeetingServices().Filter(meetings, filter);

            assert meetings != null;
            if(meetings.isEmpty()) {
                parentPanel.getChildren().add(noResultsLabel);
                noResultsLabel.setVisible(true);
            }
            for (Meeting meeting : meetings) {
                HBox meetingPanel = createMeetingPanel(meeting);
                meetingPanel.setOnMouseClicked(_ -> openPopupMeeting(meeting));
                parentPanel.getChildren().add(meetingPanel);
            }
        } else {
            ProjectApplication.switchScene("/manager/manageMeetings.fxml");
        }
    }
    private HBox createMeetingPanel(Meeting meeting) {
        HBox meetingPanel = new HBox(5); // Space between items
        meetingPanel.setFillHeight(true);
        meetingPanel.setSpacing(5);
        meetingPanel.setPadding(new Insets(5, 20, 5, 10)); // Padding for the panel
        meetingPanel.setStyle("-fx-background-color: rgba(30, 30, 50, 0.7); -fx-background-radius: 10; -fx-text-fill: white;");
        meetingPanel.setPrefHeight(Region.USE_COMPUTED_SIZE); // Dynamic height

        VBox infoBox = new VBox(5);
        infoBox.setPadding(new Insets(5, 20, 5, 10));
        infoBox.setPrefWidth(500);
        infoBox.setPrefHeight(60);

        Label titleLabel = new Label(meeting.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");

        Label agendaLabel = new Label(meeting.getAgenda());
        agendaLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11;");
        agendaLabel.setPrefWidth(400);

        Label dateTimeLabel = new Label("Date: " + meeting.getMeetingDate() + ", Time: " + meeting.getMeetingTime());
        dateTimeLabel.setStyle("-fx-text-fill: white;");

        Label statusLabel = new Label(meeting.getStatus());
        statusLabel.setPrefSize(100, 30);
        statusLabel.setAlignment(Pos.CENTER_LEFT);

        VBox controls = new VBox(5);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10, 10, 10, 20));
        switch (meeting.getStatus()) {
            case "Completed" -> {
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 12;");
                completeControl(meeting, controls);
            }
            case "Scheduled" -> {
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 12;");
                scehduleControl(meeting, controls);
            }
            case "Request Pending" -> {
                statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold; -fx-font-size: 12;");
                pendingControl(meeting, controls);
            }
            case "Rejected", "Cancelled" -> {
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12;");
                cancelControl(meeting, controls);
            }
        }
        infoBox.getChildren().addAll(titleLabel, agendaLabel, dateTimeLabel, statusLabel);
        meetingPanel.getChildren().addAll(infoBox, controls);
        meetingPanel.setCursor(Cursor.HAND);
        meetingPanel.setOnMouseEntered(_ -> meetingPanel.setStyle("-fx-background-color: rgba(30, 30, 50, 255);; -fx-background-radius: 10; -fx-text-fill: white;"));
        meetingPanel.setOnMouseExited(_ -> meetingPanel.setStyle("-fx-background-color: rgba(30, 30, 50, 0.7);; -fx-background-radius: 10; -fx-text-fill: white;"));
        return meetingPanel;
    }
    private void openPopupMeeting(Meeting meeting) {
        StackPane rootPane = new StackPane();
        rootPane.setStyle("-fx-background-image: url('file:images/manBlue.png'); -fx-background-size: cover; -fx-background-repeat: no-repeat;");
        VBox overlayBox = new VBox(5);
        overlayBox.setStyle("-fx-background-color: rgba(10,10,10,0.7); -fx-text-fill: white");
        overlayBox.setPadding(new Insets(15));
        overlayBox.setSpacing(10);
        overlayBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(meeting.getTitle());
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        Label agendaLabel = new Label(meeting.getAgenda());
        agendaLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13;");
        agendaLabel.setWrapText(true);

        Label descriptionLabel = new Label("Location: " + meeting.getLocation());
        descriptionLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");
        descriptionLabel.setWrapText(true);

        Label dateTimeLabel = new Label("Date: " + meeting.getMeetingDate() + ", Time: " + meeting.getMeetingTime());
        dateTimeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");

        Label hostLabel = new Label();
        if(meeting.getHostId() == manager.getUserId()){
            hostLabel.setText("Meeting Host: " + manager.getFirstName() + " " + manager.getLastName() + " (You)");
        }
        else{
            hostLabel.setText("Meeting Host: Project Manager");
        }
        hostLabel.setStyle("-fx-font-size: 12; -fx-text-fill: lightgray;");

        Label priorityLabel = getPriorityLabel(meeting);

        overlayBox.getChildren().addAll(titleLabel, agendaLabel, descriptionLabel, dateTimeLabel, hostLabel, priorityLabel);
        switch(meeting.getStatus()){
            case "Cancelled":
                cancelledMeetingPopupPanel(meeting, overlayBox);
                break;
            case "Completed":
                completeMeetingPopupPanel(meeting, overlayBox);
                break;
            case "Request Pending":
                pendingMeetingPopupPanel(meeting, overlayBox);
                break;
            default:
                scheduledMeetingPopupPanel(meeting, overlayBox);
        }
        rootPane.getChildren().add(overlayBox);
        Scene scene = new Scene(rootPane, 450, 400);
        Stage popupStage = new Stage();
        popupStage.setTitle("Meeting Details");
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }
    private static Label getPriorityLabel(Meeting meeting) {
        Label priorityLabel = new Label("Priority: " + meeting.getPriority());
        if (Objects.equals(meeting.getPriority(), "Low")) {
            priorityLabel.setStyle("-fx-text-fill: green;");
        } else if (Objects.equals(meeting.getPriority(), "Medium")) {
            priorityLabel.setStyle("-fx-text-fill: orange;");
        } else if (Objects.equals(meeting.getPriority(), "High")) {
            priorityLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if (Objects.equals(meeting.getPriority(), "Urgent")) {
            priorityLabel.setStyle("-fx-text-fill: darkred; -fx-font-weight: bold; -fx-underline: true;");
        }
        return priorityLabel;
    }

    private void applyFilter(String filter) {
        loadManagerMeetings(filter);
    }
    private void filterMeetings(String query) {
        if (meetings == null || meetings.isEmpty()) return;
        if (query == null || query.trim().isEmpty()) {
            loadManagerMeetings(null);
            return;
        }
        parentPanel.getChildren().clear();
        ArrayList<Meeting> filteredMeetings = Factory.getMeetingServices().filterMeetings(meetings, query);

        for (Meeting meeting : filteredMeetings) {
            HBox meetingPanel = createMeetingPanel(meeting);
            parentPanel.getChildren().add(meetingPanel);
        }
        if(filteredMeetings.isEmpty()) {
            parentPanel.getChildren().add(noResultsLabel);
            noResultsLabel.setVisible(true);
        }
    }

    //update meeting info ----> accept request, cancel meetingm reschedule meeting
    private void cancelMeeting(Meeting meeting) {   //done
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancel Meeting");
        dialog.setHeaderText("Provide a reason for cancelling the meeting:");
        dialog.setContentText("Cancellation Reason:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cancellationReason -> {
            try {
                if(meeting.getHostId() == manager.getUserId()){
                    meeting.setCancellationReason("Host cancelled the meeting: " + cancellationReason);
                }
                else{
                    meeting.setCancellationReason("Attendee cancelled the meeting: " + cancellationReason);
                }
                meeting.setStatus("Cancelled");
                ((ProjectManager) manager).updateMeeting(meeting);
                loadManagerMeetings(null);
            } catch (Exception e) {
                System.err.println("Error while canceling the meeting: " + e.getMessage());
            }
        });
        if (result.isEmpty()) {
            System.out.println("Meeting cancellation was aborted by the user.");
        }
    }
    private void acceptMeetingRequest(Meeting meeting) {
        try {
            meeting.setStatus("Scheduled");
            ((ProjectManager) manager).updateMeeting(meeting);
            loadManagerMeetings(null);
        } catch (Exception e) {
            System.err.println("Error while acceoting the meeting request: " + e.getMessage());
        }
    }
    private void rescheduleMeeting(Meeting meeting) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/managerEditMeeting.fxml"));
            Parent newFormRoot = loader.load();

            editMeetController controller = loader.getController();
            controller.setMeeting(meeting);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Reschedule Meeting - " + meeting.getTitle());
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();

            loadManagerMeetings(null);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: " + e.getMessage());
        }
    }

    //new meeting ka kaam here:
    private void populateProjectPanels() {
        projparentPanel.getChildren().clear();
        projparentPanel.setFillHeight(true);
        projparentPanel.setSpacing(5);
        ArrayList<Project> projectList = ((ProjectManager) manager).getProjects();
        for (Project project : projectList) {
            VBox projectPanel = new VBox(5); // 5px spacing between elements
            projectPanel.setPadding(new Insets(10));
            projectPanel.setAlignment(Pos.CENTER_LEFT);
            projectPanel.setPrefWidth(300);
            projectPanel.setStyle("-fx-background-color:  rgba(30, 40, 50, 0.6); -fx-background-radius: 5; -fx-text-fill: white;");

            meetingPanelSetUp(project, projectPanel);
            projectPanel.setOnMouseClicked(_ -> selectProject(project));
            projparentPanel.getChildren().add(projectPanel);
        }
    }
    static void meetingPanelSetUp(Project project, VBox projectPanel) {
        ClientMeetingController.meetingPanelSetUp(project, projectPanel);
    }
    private void selectProject(Project project) {
        projectIDTextField.setText(String.valueOf(project.getProjectId()));
        projectTitleTextField.setVisible(true);
        projectTitleTextField.setText("Selected: " + project.getTitle());
    }

    //show information in popup
    private void cancelledMeetingPopupPanel(Meeting meeting, VBox overlayBox){
        Label statusLabel = new Label("Status: Cancelled");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold; ");

        Label reasonLabel = new Label("Reason: " + meeting.getCancellationReason());
        reasonLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        overlayBox.getChildren().addAll(statusLabel, reasonLabel);
    }
    private void scheduledMeetingPopupPanel(Meeting meeting, VBox overlayBox){
        Label statusLabel = new Label("Status: Scheduled");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: yellow; -fx-font-weight: bold; ");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        overlayBox.getChildren().addAll(statusLabel, buttonBox);

        Button cancelButton = createButton("Cancel Meeting", "red");
        cancelButton.setOnAction(_ -> cancelMeeting(meeting));

        Button rescheduleButton = createButton("Reschedule Meeting", "rgba(255,165,0,0.3)");
        rescheduleButton.setOnAction(_ -> rescheduleMeeting(meeting));
        buttonBox.getChildren().addAll(rescheduleButton, cancelButton);

    }
    private void completeMeetingPopupPanel(Meeting meeting, VBox overlayBox){
        Label statusLabel = new Label("Status: Completed");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green; -fx-font-weight: bold; ");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        Button minutes;
        if (meeting.getHostId() == manager.getUserId() && (meeting.getMinutesFilePath() == null || meeting.getMinutesFilePath().isEmpty())) {
            minutes = createButton("Attach Minutes File", "rgba(0,0,255,0.3)");
            minutes.setOnAction(_ -> ProjectApplication.getAttachment(buttonBox.getScene().getWindow()));
        }
        else {
            minutes = createButton("View Meeting Minutes", "rgba(0,128,0,0.3)");
            minutes.setOnAction(_ -> ProjectApplication.viewAttachment(meeting.getMinutesFilePath()));
            if(meeting.getMinutesFilePath() == null)
                minutes.setDisable(true);
        }
        buttonBox.getChildren().add(minutes);
        overlayBox.getChildren().addAll(statusLabel, buttonBox);
    }
    private void pendingMeetingPopupPanel(Meeting meeting, VBox overlayBox){
        Label statusLabel = new Label("Status: Request Pending");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green; -fx-font-weight: bold; ");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        if (meeting.getRecipientId() == manager.getUserId()) {
            Button scheduleMeeting = createButton("Schedule Meeting", "rgba(0,0,255,0.3)");
            scheduleMeeting.setOnAction(_ -> acceptMeetingRequest(meeting));
            buttonBox.getChildren().add(scheduleMeeting);

            Button rejectButton = createButton("Cancel Meeting Request", "rgba(0,128,0,0.3)");
            rejectButton.setOnAction(_ -> cancelMeeting(meeting));
            buttonBox.getChildren().addAll(rejectButton);
        }
        else {
            Button rejectButton = createButton("Cancel Meeting Request", "rgba(0,128,0,0.3)");
            rejectButton.setOnAction(_ -> cancelMeeting(meeting));
            buttonBox.getChildren().add(rejectButton);
        }
        overlayBox.getChildren().addAll(statusLabel, buttonBox);
    }

    // main panel meeting controls
    private void pendingControl(Meeting meeting, VBox controls){
        Button cancelButton = createButton("Reject Request", "red");
        if(meeting.getHostId() == manager.getUserId()) {
            cancelButton.setText("Cancel Meeting Request");
        }
        cancelButton.setOnAction(_ -> cancelMeeting(meeting));
        controls.getChildren().addAll(cancelButton);

        if(meeting.getRecipientId() == manager.getUserId()) {
            Button rescheduleButton = createButton("Accept Request", "green");
            rescheduleButton.setOnAction(_ -> acceptMeetingRequest(meeting));
            controls.getChildren().addAll(rescheduleButton);
        }
    }
    private void cancelControl(Meeting meeting, VBox controls){
        Button rescheduleButton = createButton("Reschedule Meeting", "green");
        rescheduleButton.setOnAction(_ -> rescheduleMeeting(meeting));
        controls.getChildren().addAll(rescheduleButton);
    }
    private void scehduleControl(Meeting meeting, VBox controls){
        Button cancelButton = createButton("Cancel Meeting", "red");
        cancelButton.setOnAction(_ -> cancelMeeting(meeting));

        Button rescheduleButton = createButton("Reschedule Meeting", "green");
        rescheduleButton.setOnAction(_ -> rescheduleMeeting(meeting));
        controls.getChildren().addAll(cancelButton, rescheduleButton);
    }
    private void completeControl(Meeting meeting, VBox controls){
        Button attachFileButton = createButton("View Meeting Minutes", "rgba(0,0,200,0.3)");
        if ((meeting.getMinutesFilePath() == null || meeting.getMinutesFilePath().isEmpty()) && meeting.getHostId() == manager.getUserId()) {
            attachFileButton.setText("Attach Meeting Minutes");
            attachFileButton.setOnAction(_ -> ProjectApplication.getAttachment(controls.getScene().getWindow()));
        } else {
            attachFileButton.setOnAction(_ -> ProjectApplication.viewAttachment(meeting.getMinutesFilePath()));
            if(meeting.getMinutesFilePath() == null) {
                attachFileButton.setDisable(true);
            }
        }
        controls.getChildren().add(attachFileButton);
    }

    // helper functions
    static Time convertToTime(String hour, String minute, String amPm) {
        return getTime(hour, minute, amPm);
    }

    // label act as button
    @FXML private void onAddEnter(MouseEvent event) {
        if (event.getSource() instanceof Label lab) {
            lab.setStyle("-fx-background-color:  rgba(230, 60, 60, 0.6); -fx-text-fill: white;  -fx-background-radius: 10;");
        }
    }
    @FXML private void onAddExit(MouseEvent event) {
        if (event.getSource() instanceof Label lab) {
            lab.setStyle("-fx-background-color:  rgba(230, 60, 60, 0.3); -fx-text-fill: white;  -fx-background-radius: 10;");
        }
    }

    private void applyGlowEffect() {
        Glow glow = new Glow();
        glow.setLevel(0.8);
        addButton.setEffect(glow);
    }
    private void removeGlowEffect() { addButton.setEffect(null); }
}