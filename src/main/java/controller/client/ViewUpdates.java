package controller.client;

import com.example.projecthr.*;
import com.example.projecthr.logs.ClientLog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import utility.Factory;
import java.util.ArrayList;
import java.util.Collections;


import static com.example.projecthr.ProjectApplication.showAlert;

public class ViewUpdates {
    User currentUser;
    ArrayList<ClientLog> logs;

    @FXML private VBox parentPanel;
    @FXML private Label newLabel, meetLabel, projLabel;
    @FXML private TextField searchBar;
    @FXML private ComboBox<String> filterComboBox;

    @FXML
    public void initialize() {
        currentUser = Factory.getFactory().getMainApp().getUser();
        logs = Factory.getClientServices().getClientLogs(currentUser.getUserId());
        if(filterComboBox != null) {
            filterComboBox.getItems().addAll("All", "Meetings", "Projects", "Proposals", "Latest", "Oldest");
            filterComboBox.setValue("All");
            filterComboBox.valueProperty().addListener((_, _, newValue) -> {
                if (newValue != null) {
                    applyFilter(newValue);
                }
            });
        }
        if(searchBar != null) {
            searchBar.textProperty().addListener((_, _, newValue) -> searchButtonClick(newValue));
        }
        if(parentPanel != null) {
            loadClientMeetings(logs);
        }
        if(newLabel != null && meetLabel != null) {
            newLabel.setText("New Notifications: " );
            meetLabel.setText("Meetings: " );
            projLabel.setText("Projects: ");
        }
    }

    @FXML public void searchButtonClick(String filter){
        if(filter.trim().isEmpty()){
            return;
        }
        else{
            ArrayList<ClientLog> filteredLogs = new ArrayList<>();
            String lowerFilter = filter.toLowerCase();

            for (ClientLog log : logs) {
                if (log.getDescription().toLowerCase().contains(lowerFilter) ||
                        log.getTableName().toLowerCase().contains(lowerFilter)) {
                    filteredLogs.add(log);
                }
            }
            loadClientMeetings(filteredLogs);
        }
    }

    //set up meetings
    public void loadClientMeetings(ArrayList<ClientLog> logo) {
        if (parentPanel != null) {
            parentPanel.getChildren().clear();
            parentPanel.setSpacing(10);

            for (ClientLog log : logo) {
                HBox meetingPanel = createClientLogPanel(log);
                parentPanel.getChildren().add(meetingPanel);
            }
        } else {
            ProjectApplication.switchScene("/client/ClientMeetings.fxml");
        }
    }
    private HBox createClientLogPanel(ClientLog log) {
        HBox logPanel = new HBox(5); // Space between items
        logPanel.setFillHeight(true);
        logPanel.setSpacing(5);
        logPanel.setPadding(new Insets(5, 20, 5, 10)); // Padding for the panel
        logPanel.setPrefHeight(Region.USE_COMPUTED_SIZE); // Dynamic height

        VBox infoBox = new VBox(5);
        infoBox.setPadding(new Insets(5, 20, 5, 10));
        infoBox.setPrefWidth(500);
        infoBox.setPrefHeight(60);

        // Display log information
        Label logIdLabel = new Label(log.getDisplayName() + " " + log.getActionType());
        logIdLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");

        Label descriptionLabel = new Label(log.getDescription());
        descriptionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");

        Label changeDateLabel = new Label("Update Time: " + log.getChangeDate());
        changeDateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");

        // Add labels to infoBox
        infoBox.getChildren().addAll(logIdLabel, descriptionLabel, changeDateLabel);

        logPanel.getChildren().addAll(infoBox);
        logPanel.setCursor(Cursor.HAND);

        if(log.getNotificationSeen().equals("No")){
            logPanel.setStyle("-fx-background-color: rgba(120, 70, 60, 0.5); -fx-background-radius: 10; -fx-text-fill: white;");
            logPanel.setOnMouseEntered(_ -> logPanel.setStyle("-fx-background-color: rgba(120, 70, 60, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));
            logPanel.setOnMouseExited(_ -> logPanel.setStyle("-fx-background-color:rgba(120, 70, 60, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));
        }
        else {
            logPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.4); -fx-background-radius: 10; -fx-text-fill: white;");
            logPanel.setOnMouseEntered(_ -> logPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));
            logPanel.setOnMouseExited(_ -> logPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));
        }
        logPanel.setOnMouseClicked(MouseEvent_ -> {log.setNotificationSeen("Yes");
            logPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.4); -fx-background-radius: 10; -fx-text-fill: white;");
            logPanel.setOnMouseEntered(_ -> logPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.7); -fx-background-radius: 10; -fx-text-fill: white;"));
            logPanel.setOnMouseExited(_ -> logPanel.setStyle("-fx-background-color: rgba(25, 13, 5, 0.5); -fx-background-radius: 10; -fx-text-fill: white;"));
        });
        return logPanel;
    }

    private void applyFilter(String filter) {
        if(filter.trim().isEmpty() || filter.equals("All")) {
            loadClientMeetings(logs);
        }
        else{
            ArrayList<ClientLog> filteredLogs = new ArrayList<>(logs);  // Start with a copy of the original list

            switch (filter) {
                case "Meetings":
                case "Projects":
                    filteredLogs.removeIf(log -> !log.getTableName().equalsIgnoreCase(filter));
                    break;
                case "Proposals":
                    filteredLogs.removeIf(log -> !log.getTableName().equalsIgnoreCase("ProjectProposal"));
                    break;
                case "Oldest":
                    Collections.reverse(filteredLogs);
                    break;
                default:
                    break;
            }
            loadClientMeetings(filteredLogs);
        }
    }


    @FXML protected void onLogoutButtonClick() { ProjectApplication.switchScene("LoginForm.fxml");}
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

}