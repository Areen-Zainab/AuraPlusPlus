package com.example.projecthr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ClientProjectController {
    User currentUser;
    ArrayList<Project> projects;
    @FXML private Label activeLabel, completeLabel;
    @FXML private TextField searchBar;
    @FXML private Button saveButton;
    @FXML private ScrollPane activeScroll, completeScroll;
    @FXML private VBox activeVBox, completeVBox;

    @FXML
    public void initialize() {
        currentUser = Factory.getFactory().getMainApp().getUser();
        loadProjects();
        loadActiveProjects(((Client)currentUser).getFilteredProjects("Active"));
        loadCompleteProjects(((Client)currentUser).getFilteredProjects("Completed"));
        if(searchBar != null) {
            searchBar.textProperty().addListener((observable, _, newValue) -> {
                //filterMeetings(newValue);
            });
        }
        if(activeLabel != null && completeLabel != null) {
            activeLabel.setText("In-Progress: " + ((Client)currentUser).getProjectCount("Active"));
            completeLabel.setText("Completed: " + ((Client)currentUser).getProjectCount("Completed"));
        }
    }

    private void loadProjects(){ projects = ((Client)currentUser).getProjects();}
    public void loadActiveProjects(ArrayList<Project> activeProjects) {
        activeVBox.getChildren().clear();
        for (Project project : activeProjects) {
            projectBox(project);
            activeVBox.getChildren().add(projectBox(project));
        }
        activeScroll.setContent(activeVBox);
    }
    public void loadCompleteProjects(ArrayList<Project> completeProjects) {
        completeVBox.getChildren().clear();
        for (Project project : completeProjects) {
            completeVBox.getChildren().add(projectBox(project));
        }
        completeScroll.setContent(completeVBox);
    }
    private HBox projectBox(Project project) {
        HBox projectBox = new HBox(10);
        projectBox.setFillHeight(true);
        projectBox.setSpacing(5);
        projectBox.setPadding(new Insets(5, 20, 5, 10)); // Padding for the panel
        projectBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10; -fx-text-fill: white;");
        projectBox.setPrefHeight(100);
        VBox projectDetailsBox = new VBox(5);
        Label projectTitleLabel = new Label(project.getTitle());
        Label projectdescripLabel = new Label(project.getDescription());
        Label projectStartDateLabel = new Label("Start Date: " + project.getStartDate().toString());
        Label projectEndDateLabel = new Label("End Date: " + (project.getEndDate() != null ? project.getEndDate().toString() : "N/A"));
        projectTitleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        projectdescripLabel.setStyle("-fx-text-fill: #f0f0f0; -fx-font-size: 13px;");
        projectStartDateLabel.setStyle("-fx-text-fill: #64e3a1; -fx-font-size: 11px;");
        projectEndDateLabel.setStyle("-fx-text-fill: #ff474c; -fx-font-size: 11px;");

        projectDetailsBox.getChildren().addAll(projectTitleLabel, projectdescripLabel, projectStartDateLabel, projectEndDateLabel);
        projectBox.getChildren().add(projectDetailsBox);
        projectBox.setCursor(Cursor.HAND);
        projectBox.setOnMouseClicked((MouseEvent event) -> showProject(project));
        projectBox.setOnMouseEntered(_ -> projectBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        projectBox.setOnMouseExited(_ -> projectBox.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));
        return projectBox;
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
    @FXML private void closeForm() { ((Stage) saveButton.getScene().getWindow()).close();}

    private void showProject(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientViewProject.fxml"));
            Parent newFormRoot = loader.load();

            ClientViewController controller = loader.getController();
            controller.setProject(project);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Project - " + project.getTitle());
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}