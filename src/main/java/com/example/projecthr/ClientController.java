package com.example.projecthr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class ClientController {
    User currentUser;

    @FXML private Label namelabel, totalProj, ongoingProj;
    @FXML private PieChart projectPieChart;
    @FXML Pane recentPane;

    @FXML
    public void initialize() {
        setCurrentUser();
        int total = ((Client)currentUser).getProjectCount(null);
        int ongoing = ((Client)currentUser).getProjectCount("Active");
        int complete = ((Client)currentUser).getProjectCount("Completed");
        int pending = ((Client)currentUser).getFilteredProposals("Pending").size();
        if(projectPieChart != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Completed Projects", complete), // 60% completed
                    new PieChart.Data("Ongoing Projects", ongoing),   // 40% ongoing
                    new PieChart.Data("Pending Proposals", pending) // pending project proposals
            );
            projectPieChart.setData(pieChartData);
        }
        checkSetupStatus();
        namelabel.setText(currentUser.getFirstName() + "'s Dashboard");
        totalProj.setText("Total Projects: " + total);
        ongoingProj.setText("Ongoing Projects: " + ongoing);
    }

    @FXML protected void onLogoutButtonClick() {
        ProjectApplication.switchScene("LoginForm.fxml");
    }
    @FXML protected void onProfileButtonClick() {
        ProjectApplication.switchScene("ClientProfile.fxml", 1000, 600);
    }
    @FXML protected void onProjectButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("ClientProject.fxml", 1000, 600);
        }
    }
    @FXML protected void onProposalButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else {
            ProjectApplication.switchScene("ClientProposals.fxml", 1000, 600);
        }
    }
    @FXML protected void onMeetingButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        ProjectApplication.showAlert(alertType, title, message);
    }

    private void setCurrentUser(){
        currentUser = Factory.getFactory().getMainApp().getUser();
    }

    private void checkSetupStatus() {
        recentPane.getChildren().clear();
        if (!currentUser.setUpComplete()) {
            Label promptLabel = new Label("Finish setting up your profile to unlock more features!");
            promptLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff5555; -fx-font-weight: bold;");
            promptLabel.setWrapText(true);
            promptLabel.setTextAlignment(TextAlignment.CENTER);

            Button profileButton = new Button("Go to Profile Setup");
            profileButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            profileButton.setPadding(new Insets(10, 20, 10, 20));
            profileButton.setOnAction(_ -> onProfileButtonClick());

            VBox messageBox = new VBox(15);
            messageBox.setAlignment(Pos.CENTER);
            messageBox.setPadding(new Insets(20));
            messageBox.getChildren().addAll(promptLabel, profileButton);
            recentPane.getChildren().add(messageBox);
        }
    }

}
