package com.example.projecthr;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class EditMeetingController {
    User currentUser;
    Meeting meet;
    Project proj;

    @FXML private HBox projparentPanel;
    @FXML private TextField addressTextField, nameTextField;
    @FXML private TextArea descripTextField;
    @FXML private ComboBox<String> hourComboBox, minComboBox, whenComboBox, priorityComboBox;
    @FXML private CheckBox elsewhereCheck, virtualCheck, officeCheck;
    @FXML private DatePicker meetingDatePicker;
    @FXML private Button saveButton;

    @FXML
    public void initialize() {
        currentUser = Factory.getFactory().getMainApp().getUser();
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
            virtualCheck.setOnAction(_ -> {
                if (virtualCheck.isSelected()) {
                    officeCheck.setSelected(false);
                    elsewhereCheck.setSelected(false);
                    addressTextField.clear();
                    addressTextField.setDisable(true);
                }
            });

            officeCheck.setOnAction(_ -> {
                if (officeCheck.isSelected()) {
                    virtualCheck.setSelected(false);
                    elsewhereCheck.setSelected(false);
                    addressTextField.setDisable(false);
                }
            });

            elsewhereCheck.setOnAction(_ -> {
                if (elsewhereCheck.isSelected()) {
                    virtualCheck.setSelected(false);
                    officeCheck.setSelected(false);
                    addressTextField.setDisable(false);
                }
            });

            addressTextField.setDisable(false);
        }
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

    @FXML private void closeForm() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML private void saveButtonClick() {
        try {
            LocalDate date = meetingDatePicker.getValue();
            String hour = hourComboBox.getValue();
            String minute = minComboBox.getValue();
            String amPm = whenComboBox.getValue();
            String location = virtualCheck.isSelected() ? "Virtual" :
                    officeCheck.isSelected() ? "Office: " + addressTextField.getText() :
                            elsewhereCheck.isSelected() ? "Elsewhere: " + addressTextField : null;

            if (date == null || hour == null || minute == null || amPm == null || location == null) {
                throw new IllegalArgumentException("All fields are required except address for virtual meetings.");
            }

            LocalDate today = LocalDate.now();
            if (date.isBefore(today)) {
                throw new IllegalArgumentException("The selected meeting date cannot be in the past.");
            }

            int hourInt = Integer.parseInt(hour);
            if (amPm.equals("PM") && hourInt != 12) hourInt += 12;
            if (amPm.equals("AM") && hourInt == 12) hourInt = 0;
            Time time = Time.valueOf(hourInt + ":" + minute + ":00");
            meet.setMeetingTime(time.toLocalTime());    // time set hogaya
            meet.setMeetingDate(date);
            meet.setLocation(location);
            meet.setStatus("Scheduled");
            meet.setCancellationReason("");

            ((Client)currentUser).updateMeeting(meet);
            ProjectApplication.showAlert(Alert.AlertType.INFORMATION, "Success", "Your meeting has been successfully rescheduled. Wait for Manager confirmation.");
            closeForm();
        } catch (IllegalArgumentException e) {
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Invalid meeting details.");
        } catch (Exception e) {
            e.printStackTrace();
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occured. Please try again later.");
            closeForm();
        }
    }

    private void populateProjectPanel() {
        projparentPanel.getChildren().clear();
        proj = ((Client)currentUser).getProjectById(meet.getProject_id());

        VBox projectPanel = new VBox(5); // 5px spacing between elements
        projectPanel.setPadding(new Insets(10));
        projectPanel.setAlignment(Pos.CENTER_LEFT);
        projectPanel.setSpacing(5);
        projectPanel.setPrefWidth(450);
        projectPanel.setStyle("-fx-background-color:  rgba(30, 40, 50, 0.6); -fx-background-radius: 10; -fx-text-fill: white;");

        if(proj != null) {
            Label titleLabel = new Label(proj.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;  -fx-text-fill: white;");

            Label descriptionLabel = new Label(proj.getDescription());
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-text-fill: white;");

            Label statusLabel = new Label("Status: " + proj.getStatus());
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                    (proj.getStatus().equalsIgnoreCase("Completed") ? "green" :
                            proj.getStatus().equalsIgnoreCase("In Progress") ? "blue" : "red"));

            projectPanel.getChildren().addAll(titleLabel, descriptionLabel, statusLabel);
        }
        else {
            closeForm();
        }
        projparentPanel.getChildren().add(projectPanel);
    }

    public void setMeeting(Meeting meeting) {
        this.meet = meeting;
        nameTextField.setText(meeting.getTitle());
        descripTextField.setText(meeting.getAgenda());
        if(meet.getLocation().equals("Virtual")){
            addressTextField.setDisable(true);
            virtualCheck.setSelected(true);
        }
        else{
            try {
                String[] parsed = meet.getLocation().split(":");
                if (parsed[0].equals("Office")) {
                    officeCheck.setSelected(true);
                } else {
                    elsewhereCheck.setSelected(true);
                }
                addressTextField.setText(parsed[1]);
            }
            catch(Exception e) {
                addressTextField.setDisable(true);
                virtualCheck.setSelected(true);
                meet.setLocation("Virtual");
            }
        }
        priorityComboBox.setValue(meeting.getPriority());
        populateProjectPanel();
        meetingDatePicker.setValue(meeting.getMeetingDate());
        setTimeInComboBoxes(meeting.getMeetingTime());
    }

    private void setTimeInComboBoxes(LocalTime time) {
        if (time != null) {
            int hour = time.getHour();
            int minute = time.getMinute();
            String amPm = (hour < 12) ? "AM" : "PM";

            if (hour > 12) {
                hour -= 12;
            } else if (hour == 0) {
                hour = 12;
            }
            hourComboBox.setValue(String.valueOf(hour));
            minComboBox.setValue(String.format("%02d", (minute / 15) * 15));
            whenComboBox.setValue(amPm);
        }
    }
}