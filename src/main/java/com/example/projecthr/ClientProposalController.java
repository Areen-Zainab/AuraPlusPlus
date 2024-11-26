package com.example.projecthr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.ScrollPane;
import java.io.File;
import javafx.geometry.Insets;
import java.util.ArrayList;

public class ClientProposalController {
    User currentUser;
    ArrayList<ProjectProposal> proposals;

    @FXML private VBox parentPanel;
    @FXML private Label namelabel, descripLabel, titleLabel;
    @FXML private TextField searchBar, nameTextField, pathTextField, costTextField;
    @FXML private TextArea descripTextField;
    @FXML private ImageView attachFileButton;
    @FXML private ComboBox<String> filterComboBox, timeComboBox, timeUnitComboBox;
    @FXML private ScrollPane rootPanel;
    @FXML private Button saveButton, closeButton;

    @FXML
    public void initialize() {
        currentUser = Factory.getFactory().getMainApp().getUser();
        if(filterComboBox != null) {
            filterComboBox.getItems().addAll("All", "Accepted", "Rejected", "Pending", "Latest", "Oldest");
            filterComboBox.setValue("All");
            filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    applyFilter(newValue);
                }
            });
        }
        if(searchBar != null) {
            searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterProposals(newValue));
        }
        if(namelabel != null) {
            namelabel.setText("Project Proposals");
        }
        if(timeComboBox != null && timeUnitComboBox != null) {
            descripTextField.setWrapText(true);
            descripTextField.setStyle("-fx-background-color: transparent; -fx-border-color: white;");
            timeUnitComboBox.getItems().addAll("Weeks", "Months");
            timeComboBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }
        if(parentPanel != null) {
            loadClientProposals(null);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        ProjectApplication.showAlert(alertType, title, message);
    }

    @FXML
    protected void onLogoutButtonClick() {
        ProjectApplication.switchScene("LoginForm.fxml");
    }

    @FXML
    protected void onProfileButtonClick() {
        ProjectApplication.switchScene("ClientProfile.fxml", 1000, 600);
    }

    @FXML
    protected void onProjectButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("ClientProject.fxml", 1000, 600);
        }
    }

    @FXML
    protected void onProposalButtonClick() {
        System.out.println("Create Project Button clicked");
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else {
            ProjectApplication.switchScene("ClientProposals.fxml", 1000, 600);
        }
    }

    @FXML
    protected void onMeetingButtonClick() {
        if(!currentUser.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("ClientMeetings.fxml", 1000, 600);
        }
    }

    @FXML
    protected void onUpdateButtonClick() {
        System.out.println("Update Button clicked");
    }

    @FXML
    protected void onDashboardButtonClick() {
        ProjectApplication.switchScene("ClientDashboard.fxml", 1000, 600);
    }

    @FXML
    public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(120, 80, 50, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onOtherMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(80, 45, 20, 0.6); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(10, 10, 10, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(40, 25, 10, 0.8); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void searchButtonClick(){
        System.out.println("Search Button clicked");
    }

    @FXML
    public void addButtonClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProposal.fxml"));
            Parent newFormRoot = loader.load();

            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Submit New Proposal");
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void closeForm() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void saveButtonClick() {
        String title = nameTextField.getText();
        String description = descripTextField.getText();
        String pdfPath = pathTextField.getText();
        String costStr = costTextField.getText();
        String durationValue = timeComboBox.getValue();
        String durationUnit = timeUnitComboBox.getValue();

        if (title.isEmpty() || description.isEmpty() || costStr.isEmpty() || durationValue == null || durationUnit == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter all the fields correctly!");
            return;
        }

        int cost;
        try {
            cost = Integer.parseInt(costStr);
            if (cost < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid cost!");
            return;
        }

        String duration = durationValue + " " + durationUnit;
        closeForm();
        boolean success = Factory.getClientServices().submitNewProposal(title, description, cost, duration, pdfPath, currentUser.getUserId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "New Proposal submitted!");
            loadClientProposals(null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!");
        }
    }

    @FXML
    private void attachProjectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File selectedFile = fileChooser.showOpenDialog(attachFileButton.getScene().getWindow());
        if (selectedFile != null) {
            pathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    //set up proposals
    public void loadClientProposals(String filter) {
        if(parentPanel != null) {
            parentPanel.getChildren().clear();
            parentPanel.setSpacing(10);
            if (filter == null || filter.equals("All")) {
                proposals = ((Client) currentUser).getProposals();
            } else {
                proposals = ((Client) currentUser).getFilteredProposals(filter);
            }

            for (ProjectProposal proposal : proposals) {
                HBox proposalPanel = createProposalPanel(proposal);
                proposalPanel.setOnMouseClicked(_ -> openPopupProject(proposal));
                parentPanel.getChildren().add(proposalPanel);
            }
        }
        else{
            ProjectApplication.switchScene("ClientProposals.fxml", 1000, 600);
        }
    }

    private HBox createProposalPanel(ProjectProposal proposal) {
        HBox proposalPanel = new HBox(10); // Space between items
        proposalPanel.setSpacing(5);
        proposalPanel.setPadding(new Insets(10, 20, 10, 20)); // (top, right, bottom, left)
        proposalPanel.setStyle("-fx-background-color:  rgba(25, 13, 5, 0.4); -fx-background-radius: 10; -fx-text-fill: white;");
        proposalPanel.setPrefHeight(Region.USE_COMPUTED_SIZE); // Dynamic size

        VBox infoBox = new VBox(5);

        Label titleLabel = new Label(proposal.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        infoBox.getChildren().add(titleLabel);

        Label descriptionLabel = new Label("Description: " + proposal.getDescription());
        if(proposal.getDescription().length() > 100) {
            descriptionLabel.setPrefSize(550, 60);
            descriptionLabel.setWrapText(true);
        }
        else{
            descriptionLabel.setPrefSize(550, 30);
        }
        descriptionLabel.setStyle(" -fx-text-fill: white;");
        infoBox.getChildren().add(descriptionLabel);

        Label durationLabel = new Label("Duration: " + proposal.getDuration() + ",   Cost: " + proposal.getBudget() + "PKR");
        durationLabel.setStyle(" -fx-text-fill: white;  -fx-font-weight: bold; -fx-font-size: 10;");
        infoBox.getChildren().add(durationLabel);

        Button openPdfButton = new Button("Open PDF");
        openPdfButton.setPrefSize(100, 30);
        openPdfButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.3)");
        if(proposal.getPdfPath().isEmpty()) {
            openPdfButton.setDisable(true);
        }
        else {
            openPdfButton.setOnAction(_ -> ProjectApplication.viewAttachment(proposal.getPdfPath()));
        }

        Label status = getStatus(proposal);
        VBox controls = new VBox(5);
        controls.setPadding(new Insets(10, 10, 10, 30)); // (top, right, bottom, left)
        proposalPanel.getChildren().addAll(infoBox, controls);
        controls.getChildren().addAll(openPdfButton, status);
        return proposalPanel;
    }

    private static Label getStatus(ProjectProposal proposal) {
        Label status = new Label(proposal.getStatus());
        status.setPrefSize(100, 30);
        status.setAlignment(Pos.CENTER);
        if(status.getText().equals("Accepted")) {
            status.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 12;");
        }
        else if(status.getText().equals("Rejected")) {
            status.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12;");
        }
        else{
            status.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold; -fx-font-size: 12;");
        }
        return status;
    }

    private void applyFilter(String filter) {
        loadClientProposals(filter);
    }

    private void filterProposals(String query) {
        proposals = ((Client) currentUser).getProposals(); // Fetch all proposals
        if (proposals == null || proposals.isEmpty()) return;

        // If the query is null or empty, reload all proposals
        if (query == null || query.trim().isEmpty()) {
            loadClientProposals(null);
            return;
        }

        parentPanel.getChildren().clear();
        String lowerCaseQuery = query.toLowerCase();

        // Create a new mutable ArrayList with filtered proposals
        ArrayList<ProjectProposal> filteredProposals = new ArrayList<>(
                proposals.stream()
                        .filter(proposal -> proposal.getTitle().toLowerCase().contains(lowerCaseQuery)
                                || proposal.getDescription().toLowerCase().contains(lowerCaseQuery))
                        .toList()
        );

        for (ProjectProposal proposal : filteredProposals) {
            HBox proposalPanel = createProposalPanel(proposal);
            parentPanel.getChildren().add(proposalPanel);
        }

        if (filteredProposals.isEmpty()) {
            Label noResultsLabel = new Label("No matching proposals found.");
            parentPanel.getChildren().add(noResultsLabel);
        }
    }

    private void openPopupProject(ProjectProposal proposal) {
        StackPane rootPane = new StackPane();
        rootPane.setStyle("-fx-background-image: url('file:images/wp.jpg'); " +
                "-fx-background-size: cover; " +
                "-fx-background-repeat: no-repeat;");

        VBox overlayBox = new VBox();
        overlayBox.setStyle("-fx-background-color: rgba(10,10,10,0.7);");
        overlayBox.setPadding(new Insets(15));
        overlayBox.setSpacing(10);
        overlayBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(proposal.getTitle());
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descriptionLabel = new Label("Description: " + proposal.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");
        descriptionLabel.setWrapText(true);

        Label durationLabel = new Label("Duration: " + proposal.getDuration());
        durationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: lightgray;");

        Label costLabel = new Label("Cost: PKR. " + proposal.getBudget());
        costLabel.setStyle("-fx-font-size: 12; -fx-text-fill: lightgray;");

        Label submissionDateLabel = new Label("Submitted On: " + proposal.getSubmission_date());
        submissionDateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: lightgray;");

        Label statusLabel = new Label("Status: " + proposal.getStatus());
        switch (proposal.getStatus().toLowerCase()) {
            case "accepted":
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
                break;
            case "rejected":
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
                break;
            case "pending":
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: goldenrod;");
                break;
            default:
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button openPdfButton = new Button("Open PDF");
        openPdfButton.setOnAction(_ -> ProjectApplication.viewAttachment(proposal.getPdfPath()));
        if(proposal.getPdfPath().isEmpty()) {
            openPdfButton.setDisable(true);
        }
        openPdfButton.setPrefSize(100, 40);
        openPdfButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.3)");
        buttonBox.getChildren().add(openPdfButton);

        if (proposal.getProject_id() != 0) {
            Button openProjectButton = new Button("Open Project");
            openProjectButton.setPrefSize(100, 40);
            openProjectButton.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(26, 29, 16,0.3)");
            openProjectButton.setOnAction(_ -> {
                // open project idhar aaey ga
                System.out.println("Opening project with ID: " + proposal.getProject_id());
            });
            buttonBox.getChildren().add(openProjectButton);
        }

        overlayBox.getChildren().addAll(titleLabel, descriptionLabel, durationLabel, costLabel,
                submissionDateLabel, statusLabel, buttonBox);

        rootPane.getChildren().add(overlayBox);
        Scene scene = new Scene(rootPane, 450, 400);
        Stage popupStage = new Stage();
        popupStage.setTitle("Proposal Details");
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

}

//1. call project when chosen