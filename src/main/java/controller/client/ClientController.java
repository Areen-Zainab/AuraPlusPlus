package controller.client;

import com.example.projecthr.Client;
import com.example.projecthr.logs.ClientLog;
import com.example.projecthr.project.Project;
import javafx.scene.layout.HBox;
import utility.Factory;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

public class ClientController {
    User currentUser;

    @FXML private Label namelabel, updateInfo, meetInfo, proposalInfo, ongoingProj, ongoingList;
    @FXML Pane recentPane;
    @FXML HBox updatePanel;

    @FXML
    public void initialize() {
        setCurrentUser();
        int ongoing = ((Client)currentUser).getProjectCount("Active");
        //checkSetupStatus();
        namelabel.setText(currentUser.getFirstName() + "'s Dashboard");
        ongoingProj.setText("Ongoing Projects: " + ongoing);
        meetInfo.setText("Upcoming Meetings: " + ((Client)currentUser).getScheduledMeetingCount());
        proposalInfo.setText("Pending Proposals: " + ((Client)currentUser).getProposalCount());

        if(!currentUser.setUpComplete()){
            ongoingList.setText("Finish setting up your profile to get started!");
            ongoingList.setWrapText(true);
        }
        else {
            ArrayList<Project> pj = (((Client)currentUser).getProjects());
            if (pj == null || pj.isEmpty()) {
                ongoingList.setText("You currently have no ongoing projects. Submit a proposal to get started!");
                ongoingList.setWrapText(true);
            } else {
                StringBuilder titles = new StringBuilder("\n");
                int i = 1;
                for(Project proj : pj){
                    titles.append("\t").append(i++).append(". ").append(proj.getTitle()).append("\n");
                }
                ongoingList.setText("Ongoing Projects: " + titles);
                ongoingList.setWrapText(true);
            }
        }

        ArrayList<ClientLog> logs = Factory.getClientServices().getClientLogs(currentUser.getUserId());
        int count = 0;
        for(ClientLog log : logs){
            if(log.getNotificationSeen().equals("No"))
                count++;
        }
        updateInfo.setText("Updates: " + count);
        populateActivity(logs);
    }

    @FXML protected void onLogoutButtonClick() {
        ProjectApplication.switchScene("LoginForm.fxml");
    }
    @FXML protected void onProfileButtonClick() {
        ProjectApplication.switchScene("/client/ClientProfile.fxml");
    }
    @FXML protected void onProjectButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("/client/ClientProject.fxml");
        }
    }
    @FXML protected void onProposalButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else {
            ProjectApplication.switchScene("/client/ClientProposals.fxml");
        }
    }
    @FXML protected void onMeetingButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("/client/ClientMeetings.fxml");
        }
    }
    @FXML protected void onUpdateButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("/client/ClientUpdates.fxml");
        }
    }
    @FXML protected void onDashboardButtonClick() {
        ProjectApplication.switchScene("/client/ClientDashboard.fxml");
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        ProjectApplication.showAlert(alertType, title, message);
    }
    private void setCurrentUser(){
        currentUser = Factory.getFactory().getMainApp().getUser();
    }

    private void populateActivity(ArrayList<ClientLog> logs) {
        updatePanel.getChildren().clear();
        updatePanel.setFillHeight(true);
        updatePanel.setSpacing(10);
        int count  = 0;
        for (ClientLog log: logs) {
            if(count >= 3)
                break;
            count++;
            VBox logPanel = new VBox(5); // 5px spacing between elements
            logPanel.setPadding(new Insets(5,10,5,10));
            logPanel.setAlignment(Pos.CENTER_LEFT);
            logPanel.setPrefWidth(350);
            logPanel.setPrefHeight(100);
            logPanel.setStyle("-fx-background-color:  rgba(60, 60, 50, 0.6); -fx-background-radius: 10; -fx-text-fill: white;");
            meetingPanelSetUp(log, logPanel);
            updatePanel.getChildren().add(logPanel);
        }
    }

    private void meetingPanelSetUp(ClientLog log, VBox logPanel) {
        Label logIdLabel = new Label(log.getDisplayName() + " " + log.getActionType());
        logIdLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");

        Label descriptionLabel = new Label(log.getDescription());
        descriptionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");

        Label changeDateLabel = new Label("Update Time: " + log.getChangeDate());
        changeDateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        logPanel.getChildren().addAll(logIdLabel, descriptionLabel, changeDateLabel);
    }

}
