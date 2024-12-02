package controller.manager;

import com.example.projecthr.ProjectApplication;
import com.example.projecthr.ProjectManager;
import com.example.projecthr.project.Project;
import controller.client.ClientViewProjectController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.Factory;

import java.io.IOException;
import java.util.ArrayList;

import static javafx.geometry.Pos.CENTER;

public class PMProjectController {
    ProjectManager manager;
    ArrayList<Project> projects;

    @FXML private Label ongoingLab, completeLab, upcomingLab;
    @FXML private VBox activeVBox, completeVBox, notVBox;
    public void initialize() {
        manager = (ProjectManager) (Factory.getFactory().getMainApp().getUser());
        projects = manager.getProjects();
        ongoingLab.setText(String.valueOf(Factory.getProjectServices().getActive(projects)));
        completeLab.setText(String.valueOf(Factory.getProjectServices().getCompleted(projects)));
        upcomingLab.setText(String.valueOf(Factory.getProjectServices().getPendingCount(manager.getProposals())));

        loadActiveProjects(Factory.getProjectServices().getActiveProjects(projects));
        loadCompleteProjects(Factory.getProjectServices().getCompleteProjects(projects));
        loadUnstartedProjects(Factory.getProjectServices().getUnstartedProjects(projects));
    }

    public void loadActiveProjects(ArrayList<Project> activeProjects) {
        activeVBox.getChildren().clear();
        for (Project project : activeProjects) {
            activeVBox.getChildren().add(projectBox(project));
        }
        if(activeProjects.isEmpty()){
            activeVBox.getChildren().add(noneBox());
        }
    }
    public void loadCompleteProjects(ArrayList<Project> completeProjects) {
        completeVBox.getChildren().clear();
        for (Project project : completeProjects) {
            completeVBox.getChildren().add(projectBox(project));
        }
        if(completeProjects.isEmpty()){
            completeVBox.getChildren().add(noneBox());
        }
    }
    public void loadUnstartedProjects(ArrayList<Project> unstartedProjects) {
        notVBox.getChildren().clear();
        for (Project project : unstartedProjects) {
            notVBox.getChildren().add(projectBox(project));
        }
        if(unstartedProjects.isEmpty()){
            notVBox.getChildren().add(noneBox());
        }
    }
    private HBox projectBox(Project project) {
        HBox projectBox = new HBox(10);
        projectBox.setFillHeight(true);
        projectBox.setSpacing(5);
        projectBox.setPadding(new Insets(5, 20, 5, 10)); // Padding for the panel  rgba(20,40,50,255)
        projectBox.setStyle("-fx-background-color: rgba(20,40,50, 0.5); -fx-background-radius: 10; -fx-text-fill: white;");
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
        projectBox.setOnMouseEntered(_ -> projectBox.setStyle("-fx-background-color: rgba(10, 60, 100, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        projectBox.setOnMouseExited(_ -> projectBox.setStyle("-fx-background-color: rgba(20,40,50, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));
        return projectBox;
    }
    private HBox noneBox(){
        HBox messBox = new HBox(10);
        messBox.setFillHeight(true);
        messBox.setSpacing(5);
        messBox.setPadding(new Insets(5, 20, 5, 10)); // Padding for the panel  rgba(20,40,50,255)
        messBox.setStyle("-fx-background-color: rgba(20,40,50, 0.5); -fx-background-radius: 10; -fx-text-fill: white;");
        messBox.setPrefHeight(100);
        messBox.setAlignment(CENTER);

        Label messageBox = new Label("No projects available currently!");
        messageBox.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");
        messBox.getChildren().add(messageBox);
        messBox.setCursor(Cursor.HAND);
        messBox.setOnMouseEntered(_ -> messBox.setStyle("-fx-background-color: rgba(10, 60, 100, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
        messBox.setOnMouseExited(_ -> messBox.setStyle("-fx-background-color: rgba(20,40,50, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));
        return messBox;
    }

    private void showProject(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/PMViewProject.fxml"));
            Parent newFormRoot = loader.load();

            PMViewProjectController controller = loader.getController();
            controller.setProject(project);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Project - " + project.getTitle());
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
            loadActiveProjects(Factory.getProjectServices().getActiveProjects(projects));
            loadCompleteProjects(Factory.getProjectServices().getCompleteProjects(projects));
            loadUnstartedProjects(Factory.getProjectServices().getUnstartedProjects(projects));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void onLogoutButtonClick() {
        ProjectApplication.switchScene("LoginForm.fxml");
    }
    @FXML protected void onProfileButtonClick() throws IOException {
        ProjectApplication.switchScene("/manager/PMProfile.fxml");
    }
    @FXML protected void onProjectButtonClick()throws IOException {
        ProjectApplication.switchScene("/manager/PMProjects.fxml");
    }
    @FXML protected void onProposalButtonClick() throws IOException {
        ProjectApplication.switchScene("/manager/PMProposal.fxml");
    }
    @FXML protected void onMeetingButtonClick() {
        ProjectApplication.switchScene("/manager/manageMeetings.fxml");
    }
    @FXML protected void onWorkcalendarButtonClick() {
        System.out.println("Update Button clicked");
    }
    @FXML protected void onDashboardButtonClick() throws IOException {
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

    @FXML public void onSearchButtonClick() {
        System.out.println("Update Button clicked");
    }
}
