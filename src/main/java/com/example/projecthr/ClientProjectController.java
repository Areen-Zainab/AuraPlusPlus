package com.example.projecthr;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ClientProjectController {
    User currentUser;
    @FXML private Label pendingLabel, upcomingLabel;
    @FXML private TextField searchBar;
    @FXML private Button saveButton;

    @FXML
    public void initialize() {
        currentUser = Factory.getFactory().getMainApp().getUser();
        if(searchBar != null) {
            searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
                //filterMeetings(newValue);
            });
        }
        if(pendingLabel != null && upcomingLabel != null) {
            pendingLabel.setText("Pending: ");
            upcomingLabel.setText("Upcoming: ");
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
    @FXML public void onPropMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Label label) {
            label.setStyle("-fx-background-color:   rgba(80, 180, 140, 0.3); -fx-text-fill: white;  -fx-background-radius: 10;");
        }
    }
    @FXML public void onPropMouseEnter(MouseEvent event) {
        if (event.getSource() instanceof Label label) {
            label.setStyle("-fx-background-color:   rgba(80, 180, 140, 0.5); -fx-text-fill: white;  -fx-background-radius: 10;");
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
    @FXML private void closeForm() { ((Stage) saveButton.getScene().getWindow()).close();}

}