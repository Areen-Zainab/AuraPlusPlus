package com.example.projecthr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ClientMeetingController {
    User currentUser;
    ArrayList<Meeting> meetings;

    @FXML private VBox parentPanel;
    @FXML private HBox projparentPanel;
    @FXML private Label pendingLabel, upcomingLabel, projectIDTextField, projectTitleTextField;
    @FXML private TextField searchBar, pathTextField, addressTextField, nameTextField;
    @FXML private TextArea descripTextField;
    @FXML private ComboBox<String> filterComboBox, hourComboBox, minComboBox, whenComboBox, priorityComboBox;
    @FXML private CheckBox elsewhereCheck, virtualCheck, officeCheck;
    @FXML private DatePicker meetingDatePicker;
    @FXML private Button saveButton;

    @FXML
    public void initialize() {
        currentUser = Factory.getFactory().getMainApp().getUser();
        if(filterComboBox != null) {
            filterComboBox.getItems().addAll("All", "Scheduled", "Cancelled", "Request Pending", "Completed", "Latest", "Oldest");
            filterComboBox.setValue("All");
            filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    applyFilter(newValue);
                }
            });
        }
        if(searchBar != null) {
            searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterMeetings(newValue));
        }
        if(projectIDTextField != null && projectTitleTextField != null) {
            projectIDTextField.setVisible(false);
            projectTitleTextField.setVisible(false);
        }
        if(hourComboBox != null && minComboBox != null && whenComboBox != null) {
            descripTextField.setWrapText(true);
            whenComboBox.getItems().addAll("AM", "PM");
            minComboBox.getItems().addAll("00", "15", "30", "45");
            hourComboBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        }
        if(priorityComboBox != null){
            priorityComboBox.getItems().addAll("Low", "Medium", "High", "Critical");
        }
        if (virtualCheck != null && officeCheck != null && elsewhereCheck != null && addressTextField != null) {
            virtualCheck.setOnAction(event -> {
                if (virtualCheck.isSelected()) {
                    officeCheck.setSelected(false);
                    elsewhereCheck.setSelected(false);
                    addressTextField.clear();
                    addressTextField.setDisable(true);
                }
            });

            officeCheck.setOnAction(event -> {
                if (officeCheck.isSelected()) {
                    virtualCheck.setSelected(false);
                    elsewhereCheck.setSelected(false);
                    addressTextField.setDisable(false);
                }
            });

            elsewhereCheck.setOnAction(event -> {
                if (elsewhereCheck.isSelected()) {
                    virtualCheck.setSelected(false);
                    officeCheck.setSelected(false);
                    addressTextField.setDisable(false);
                }
            });

            addressTextField.setDisable(false);
        }
        if(parentPanel != null) {
            loadClientMeetings(null);
        }
        if(projparentPanel != null){
            populateProjectPanels();
        }
        if(pendingLabel != null && upcomingLabel != null) {
            pendingLabel.setText("Pending: "+ ((Client)currentUser).getPendingMeetingCount());
            upcomingLabel.setText("Upcoming: " + ((Client)currentUser).getScheduledMeetingCount());
        }
    }

    @FXML protected void onLogoutButtonClick() { ProjectApplication.switchScene("LoginForm.fxml");}

    @FXML protected void onProfileButtonClick() {
        ProjectApplication.switchScene("ClientProfile.fxml", 1000, 600);
    }

    @FXML protected void onProjectButtonClick() {
        if(!currentUser.setUpComplete()){
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("ClientProject.fxml", 1000, 600);
        }
    }

    @FXML protected void onProposalButtonClick() {
        if(!currentUser.setUpComplete()){
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else {
            ProjectApplication.switchScene("ClientProposals.fxml", 1000, 600);
        }
    }

    @FXML protected void onMeetingButtonClick() {
        if(!currentUser.setUpComplete()){
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("ClientMeetings.fxml", 1000, 600);
        }
    }

    @FXML protected void onUpdateButtonClick() {
        System.out.println("Update Button clicked");
    }

    @FXML protected void onDashboardButtonClick() {
        ProjectApplication.switchScene("ClientDashboard.fxml", 1000, 600);
    }

    @FXML public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            // Get the source of the event
            button.setStyle("-fx-background-color: rgba(120, 80, 50, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML public void onOtherMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(80, 45, 20, 0.6); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(10, 10, 10, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(40, 25, 10, 0.8); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML public void searchButtonClick(){
        System.out.println("Search Button clicked");
    }

    @FXML
    public void addButtonClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddMeeting.fxml"));
            Parent newFormRoot = loader.load();
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Schedule New Meeting");
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void closeForm() { ((Stage) saveButton.getScene().getWindow()).close();}

    @FXML
    private void saveButtonClick() {
        try {
            String title = nameTextField.getText();
            String agenda = descripTextField.getText();
            LocalDate date = meetingDatePicker.getValue();
            String hour = hourComboBox.getValue();
            String minute = minComboBox.getValue();
            String amPm = whenComboBox.getValue();
            String location = virtualCheck.isSelected() ? "Virtual" :
                    officeCheck.isSelected() ? "Office" :
                            elsewhereCheck.isSelected() ? "Elsewhere" : null;
            String address = virtualCheck.isSelected() ? null : addressTextField.getText();
            int projectId = Integer.parseInt(projectIDTextField.getText());

            if (title == null || title.isEmpty() || agenda == null || agenda.isEmpty() ||
                    date == null || hour == null || minute == null || amPm == null || projectIDTextField.getText().isEmpty()) {
                throw new IllegalArgumentException("All fields are required except address for virtual meetings.");
            }

            LocalDate today = LocalDate.now();
            if (date.isBefore(today)) {
                throw new IllegalArgumentException("The selected meeting date cannot be in the past.");
            }

            // Convert time to 24-hour format
            int hourInt = Integer.parseInt(hour);
            if (amPm.equals("PM") && hourInt != 12) hourInt += 12;
            if (amPm.equals("AM") && hourInt == 12) hourInt = 0;
            Time time = Time.valueOf(hourInt + ":" + minute + ":00");

            ((Client)currentUser).scheduleNewMeeting(title, agenda, date, time, location, address, projectId);
            ProjectApplication.showAlert(Alert.AlertType.INFORMATION, "Success", "Your meeting has been successfully scheduled. Wait for Manager confirmation.");
            closeForm();
            meetings = ((Client)currentUser).loadMeetings();
            loadClientMeetings(null);
        } catch (IllegalArgumentException e) {
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Invalid meeting details.");
        } catch (Exception e) {
            e.printStackTrace();
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occured. Please try again later.");
            closeForm();
        }
    }

    //set up meetings
    public void loadClientMeetings(String filter) {
        if (parentPanel != null) {
            parentPanel.getChildren().clear();
            parentPanel.setSpacing(10);
            ArrayList<Meeting> meetings = null;

            // Get the client's meetings, filtered if necessary
            if (filter == null || filter.equals("All")) {
                meetings = ((Client) currentUser).getMeetings();
            } else {
                if(filter.equalsIgnoreCase("Scheduled") || filter.equalsIgnoreCase("Completed")
                        || filter.equalsIgnoreCase("Cancelled") || filter.equalsIgnoreCase("Request Pending")) {
                    // String status, String keyword, LocalDate startDate, LocalDate endDate, boolean isUpcoming
                    meetings = ((Client) currentUser).FilterMeetings(filter, null, null, null, false);
                }
                else if(filter.equalsIgnoreCase("Latest") || filter.equalsIgnoreCase("Oldest")){
                    meetings = ((Client) currentUser).FilterMeetings(filter, null, null, null, false);
                }
            }

            assert meetings != null;
            for (Meeting meeting : meetings) {
                HBox meetingPanel = createMeetingPanel(meeting);
                meetingPanel.setOnMouseClicked(event -> openPopupMeeting(meeting));
                parentPanel.getChildren().add(meetingPanel);
            }
        } else {
            ProjectApplication.switchScene("ClientMeetings.fxml", 1000, 600);
        }
    }
    private HBox createMeetingPanel(Meeting meeting) {
        HBox meetingPanel = new HBox(5); // Space between items
        meetingPanel.setFillHeight(true);
        meetingPanel.setSpacing(5);
        meetingPanel.setPadding(new Insets(5, 20, 5, 10)); // Padding for the panel
        meetingPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.4); -fx-background-radius: 10; -fx-text-fill: white;");
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
        meetingPanel.setOnMouseEntered(event -> meetingPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));
        meetingPanel.setOnMouseExited(event -> meetingPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));
        return meetingPanel;
    }
    private void openPopupMeeting(Meeting meeting) {
        StackPane rootPane = new StackPane();
        rootPane.setStyle("-fx-background-image: url('file:images/wp.jpg'); " +
                "-fx-background-size: cover; " +
                "-fx-background-repeat: no-repeat;");
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
        if(meeting.getHostId() == currentUser.getUserId()){
            hostLabel.setText("Meeting Host: " + currentUser.getFirstName() + " " + currentUser.getLastName() + " (You)");
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
        loadClientMeetings(filter);
    }
    private void filterMeetings(String query) {
        if (meetings == null || meetings.isEmpty()) return;
        if (query == null || query.trim().isEmpty()) {
            loadClientMeetings(null);
            return;
        }
        parentPanel.getChildren().clear();
        String lowerCaseQuery = query.toLowerCase();

        ArrayList<Meeting> filteredMeetings = new ArrayList<>(
                meetings.stream()
                        .filter(meeting -> meeting.getAgenda().toLowerCase().contains(lowerCaseQuery)
                                || meeting.getLocation().toLowerCase().contains(lowerCaseQuery)
                                || meeting.getTitle().toLowerCase().contains(lowerCaseQuery)
                                || meeting.getStatus().toLowerCase().contains(lowerCaseQuery))
                        .toList()
        );

        for (Meeting meeting : filteredMeetings) {
            HBox meetingPanel = createMeetingPanel(meeting);
            parentPanel.getChildren().add(meetingPanel);
        }

        if (filteredMeetings.isEmpty()) {
            Label noResultsLabel = new Label("No matching meetings found.");
            parentPanel.getChildren().add(noResultsLabel);
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
                if(meeting.getHostId() == currentUser.getUserId()){
                    meeting.setCancellationReason("Host cancelled the meeting: " + cancellationReason);
                }
                else{
                    meeting.setCancellationReason("Attendee cancelled the meeting: " + cancellationReason);
                }
                meeting.setStatus("Cancelled");
                ((Client) currentUser).updateMeeting(meeting);
                loadClientMeetings(null);
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
            ((Client) currentUser).updateMeeting(meeting);
            loadClientMeetings(null);
        } catch (Exception e) {
            System.err.println("Error while acceoting the meeting request: " + e.getMessage());
        }
    }
    private void rescheduleMeeting(Meeting meeting) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditMeeting.fxml"));
            Parent newFormRoot = loader.load();

            EditMeetingController controller = loader.getController();
            controller.setMeeting(meeting);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Reschedule Meeting - " + meeting.getTitle());
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();

            loadClientMeetings(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //UI/UX here
    //add meeting kaam here
    private void populateProjectPanels() {
        projparentPanel.getChildren().clear();
        ArrayList<Project> projectList = ((Client)currentUser).getProjects();
        for (Project project : projectList) {
            VBox projectPanel = new VBox(5); // 5px spacing between elements
            projectPanel.setPadding(new Insets(10));
            projectPanel.setAlignment(Pos.CENTER_LEFT);
            projectPanel.setSpacing(5);
            projectPanel.setStyle("-fx-background-color:  rgba(30, 40, 50, 0.6); -fx-background-radius: 10; -fx-text-fill: white;");

            Label titleLabel = new Label(project.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;  -fx-text-fill: white;");

            Label descriptionLabel = new Label(project.getDescription());
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-text-fill: white;");

            Label statusLabel = new Label("Status: " + project.getStatus());
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                    (project.getStatus().equalsIgnoreCase("Completed") ? "green" :
                            project.getStatus().equalsIgnoreCase("In Progress") ? "blue" : "red"));

            projectPanel.getChildren().addAll(titleLabel, descriptionLabel, statusLabel);
            projectPanel.setOnMouseClicked(event -> selectProject(project));

            projparentPanel.getChildren().add(projectPanel);
        }
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

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(_ -> cancelMeeting(meeting));
        cancelButton.setPrefSize(150, 40);
        cancelButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.3)");
        buttonBox.getChildren().add(cancelButton);

        Button rescheduleButton = new Button("Reschedule");
        rescheduleButton.setOnAction(e -> rescheduleMeeting(meeting));
        rescheduleButton.setPrefSize(150, 40);
        rescheduleButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(255,165,0,0.3)");
        buttonBox.getChildren().add(rescheduleButton);

    }
    private void completeMeetingPopupPanel(Meeting meeting, VBox overlayBox){
                Label statusLabel = new Label("Status: Completed");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green; -fx-font-weight: bold; ");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        if (meeting.getHostId() == currentUser.getUserId() && (meeting.getMinutesFilePath() == null || meeting.getMinutesFilePath().isEmpty())) {
            Button attachMinutesButton = new Button("Attach Meeting Minutes");
            attachMinutesButton.setOnAction(e -> ProjectApplication.getAttachment(meeting, buttonBox.getScene().getWindow()));
            attachMinutesButton.setPrefSize(150, 40);
            attachMinutesButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(0,0,255,0.3)");
            buttonBox.getChildren().add(attachMinutesButton);
        }
        else {
            Button viewMinutesButton = new Button("View Meeting Minutes");
            viewMinutesButton.setOnAction(e -> ProjectApplication.viewAttachment(meeting.getMinutesFilePath()));
            viewMinutesButton.setPrefSize(200, 40);
            viewMinutesButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(0,128,0,0.3)");
            if(meeting.getMinutesFilePath() == null)
                viewMinutesButton.setDisable(true);
            buttonBox.getChildren().add(viewMinutesButton);
        }

        overlayBox.getChildren().addAll(statusLabel, buttonBox);
    }
    private void pendingMeetingPopupPanel(Meeting meeting, VBox overlayBox){
        Label statusLabel = new Label("Status: Request Pending");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green; -fx-font-weight: bold; ");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        if (meeting.getRecipientId() == currentUser.getUserId()) {
            Button attachMinutesButton = new Button("Schedule Meeting");
            attachMinutesButton.setOnAction(e -> acceptMeetingRequest(meeting));
            attachMinutesButton.setPrefSize(150, 40);
            attachMinutesButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(0,0,255,0.3)");
            buttonBox.getChildren().add(attachMinutesButton);

            Button rejectButton = new Button("Cancel Meeting Request");
            rejectButton.setOnAction(e -> cancelMeeting(meeting));
            rejectButton.setPrefSize(150, 40);
            rejectButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(0,128,0,0.3)");
            buttonBox.getChildren().add(rejectButton);
        }
        else {
            Button rejectButton = new Button("Cancel Meeting Request");
            rejectButton.setOnAction(e -> cancelMeeting(meeting));
            rejectButton.setPrefSize(150, 40);
            rejectButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(0,128,0,0.3)");
            buttonBox.getChildren().add(rejectButton);
        }

        overlayBox.getChildren().addAll(statusLabel, buttonBox);
    }

    // main panel meeting controls
    private void pendingControl(Meeting meeting, VBox controls){
        Button cancelButton = new Button("Reject Request");
        cancelButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.3)");
        cancelButton.setOnAction(e -> cancelMeeting(meeting));
        cancelButton.setPrefSize(100, 30);

        Button rescheduleButton = new Button("Accept Request");
        rescheduleButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,200,0,0.3)");
        rescheduleButton.setOnAction(e -> acceptMeetingRequest(meeting));
        rescheduleButton.setPrefSize(100, 30);
        controls.getChildren().addAll(cancelButton, rescheduleButton);
    }
    private void cancelControl(Meeting meeting, VBox controls){
        Button rescheduleButton = new Button("Reschedule");
        rescheduleButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,200,0,0.3)");
        rescheduleButton.setOnAction(e -> rescheduleMeeting(meeting));
        rescheduleButton.setPrefSize(100, 30);
        controls.getChildren().addAll(rescheduleButton);
    }
    private void scehduleControl(Meeting meeting, VBox controls){
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.3)");
        cancelButton.setOnAction(e -> cancelMeeting(meeting));
        cancelButton.setPrefSize(100, 30);

        Button rescheduleButton = new Button("Reschedule");
        rescheduleButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,200,0,0.3)");
        rescheduleButton.setOnAction(e -> rescheduleMeeting(meeting));
        rescheduleButton.setPrefSize(100, 30);
        controls.getChildren().addAll(cancelButton, rescheduleButton);
    }
    private void completeControl(Meeting meeting, VBox controls){
        Button attachFileButton;
        if ((meeting.getMinutesFilePath() == null || meeting.getMinutesFilePath().isEmpty()) && meeting.getHostId() == currentUser.getUserId()) {
            attachFileButton = new Button("Attach Meeting Minutes");
            attachFileButton.setOnAction(e -> ProjectApplication.getAttachment(meeting, controls.getScene().getWindow()));
        } else {
            attachFileButton = new Button("View Meeting Minutes");
            attachFileButton.setOnAction(e -> ProjectApplication.viewAttachment(meeting.getMinutesFilePath()));
            if(meeting.getMinutesFilePath() == null) {
                attachFileButton.setDisable(true);
            }
        }
        attachFileButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(0,0,200,0.3)");
        attachFileButton.setPrefSize(150, 30);
        controls.getChildren().add(attachFileButton);
    }
}