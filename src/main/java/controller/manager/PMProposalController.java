package controller.manager;

import com.example.projecthr.ProjectApplication;
import com.example.projecthr.ProjectManager;
import com.example.projecthr.User;
import com.example.projecthr.project.ProjectProposal;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.Factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.projecthr.ProjectApplication.showAlert;
import static controller.ProjectController.GetProposalBox;

public class PMProposalController {
    User manager;
    ArrayList<ProjectProposal> proposals;

    @FXML private Label totRequest, acceptRequest, pendingRequest;

    @FXML private VBox parentPanel;
    @FXML private Label namelabel, descripLabel, titleLabel, noResult;
    @FXML private TextField searchBar, nameTextField, pathTextField, costTextField;
    @FXML private TextArea descripTextField;
    @FXML private ImageView attachFileButton;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Button saveButton, closeButton;

    @FXML
    public void initialize() {
        manager = Factory.getFactory().getMainApp().getUser();
        if(parentPanel != null) {
            noResult = new Label("No matching meetings found.");
            noResult.setVisible(true);
            noResult.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 20"); noResult.setAlignment(Pos.CENTER);

            if (filterComboBox != null) {
                filterComboBox.getItems().addAll("All", "Approved", "Rejected", "Pending", "Latest", "Oldest");
                filterComboBox.setValue("All");
                filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        applyFilter(newValue);
                    }
                });
            }
            if (searchBar != null) {
                searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterProposals(newValue));
            }
            loadPendingClients(null);
            totRequest.setText("Total Proposals: " + ((ProjectManager)manager).getProposals().size());
            pendingRequest.setText("Pending Requests: " + (Factory.getProjectServices().getPendingCount(((ProjectManager)manager).getProposals())));
            acceptRequest.setText("Approved Proposals: " + (Factory.getProjectServices().getAcceptedCount(((ProjectManager)manager).getProposals())));
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        ProjectApplication.showAlert(alertType, title, message);
    }

    @FXML protected void onLogoutButtonClick() {
        System.out.println("Logout Button clicked");
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
    @FXML protected void onProposalButtonClick() throws IOException {
        System.out.println("Create Project Button clicked");
        ProjectApplication.switchScene("/manager/PMProposal.fxml");
    }
    @FXML protected void onMeetingButtonClick() {
        ProjectApplication.switchScene("/manager/manageMeetings.fxml");
    }
    @FXML protected void onWorkcalendarButtonClick() {
        System.out.println("Update Button clicked");
    }
    @FXML protected void onDashboardButtonClick() throws IOException {
        System.out.println("Dashboard Button clicked");
        ProjectApplication.switchScene("/manager/PMDashboard.fxml");
    }
    @FXML protected void onViewProposalButtonClick() throws IOException {
        ProjectApplication.switchScene("/manager/proposalDetails.fxml");
    }
    @FXML public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource(); // Get the source of the event
            button.setStyle("-fx-background-color:  rgba(88,109,128,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onOtherMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color:   rgba(23,39,52,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color: rgba(10, 10, 10, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color: rgba(68,85,101,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }
    @FXML public void onSearchButtonClick() {
        filterProposals(searchBar.getText());
    }
    @FXML private void closeForm() {

    }
    @FXML private void attachProjectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File selectedFile = fileChooser.showOpenDialog(attachFileButton.getScene().getWindow());
        if (selectedFile != null) {
            pathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    //set up proposals
    public void loadPendingClients(String filter) {
        if(parentPanel != null) {
            parentPanel.getChildren().clear();
            parentPanel.setSpacing(10);
            proposals = ((ProjectManager) manager).getProposals();
            if (filter != null && !filter.equals("All")) {
                proposals = Factory.getProjectServices().getFilteredProposals(proposals, filter);
            }
            if(proposals.isEmpty())
                parentPanel.getChildren().add(noResult);

            for (ProjectProposal proposal : proposals) {
                HBox proposalPanel = createProposalPanel(proposal);
                proposalPanel.setCursor(Cursor.HAND);
                proposalPanel.setOnMouseEntered(event -> proposalPanel.setStyle("-fx-background-color: rgba(40, 40, 90, 0.9); -fx-background-radius: 10; -fx-text-fill: white;"));
                proposalPanel.setOnMouseExited(event -> proposalPanel.setStyle("-fx-background-color: rgba(10, 40, 90, 0.6); -fx-background-radius: 10; -fx-text-fill: white;"));
                proposalPanel.setOnMouseClicked(_ -> openProposal(proposal));
                parentPanel.getChildren().add(proposalPanel);
            }
        }
        else{
            ProjectApplication.switchScene("/manager/PMProposals.fxml");
        }
    }

    private HBox createProposalPanel(ProjectProposal proposal) {
        HBox proposalPanel = new HBox(10); // Space between items
        proposalPanel.setSpacing(5);
        proposalPanel.setPadding(new Insets(10, 20, 10, 20)); // (top, right, bottom, left)
        proposalPanel.setStyle("-fx-background-color:  rgba(10, 40, 90, 0.6); -fx-background-radius: 10; -fx-text-fill: white;");
        return GetProposalBox(proposal, proposalPanel, getStatus(proposal));
    }

    private static Label getStatus(ProjectProposal proposal) {
        Label status = new Label(proposal.getStatus());
        status.setPrefSize(100, 30);
        status.setAlignment(Pos.CENTER);
        if(status.getText().equals("Approved")) {
            status.setStyle("-fx-text-fill: #44FF88; -fx-font-weight: bold; -fx-font-size: 12;");
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
        loadPendingClients(filter);
    }

    private void filterProposals(String query) {
        proposals = ((ProjectManager) manager).getProposals(); // Fetch all proposals
        if (proposals == null || proposals.isEmpty()) return;

        // If the query is null or empty, reload all proposals
        if (query == null || query.trim().isEmpty()) {
            loadPendingClients(null);
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
            parentPanel.getChildren().add(noResult);
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
            case "approved":
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #44FF88;");
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

    private void openProposal(ProjectProposal proj){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/proposalDetails.fxml"));
            Parent newFormRoot = loader.load();

            proposalDetailsController controller = loader.getController();
            controller.setProposal(proj);
            Stage newFormStage = new Stage();
            newFormStage.setScene(new Scene(newFormRoot));
            newFormStage.setTitle("Project Proposal - " + proj.getTitle());
            newFormStage.initModality(Modality.APPLICATION_MODAL);
            newFormStage.showAndWait();

            loadPendingClients(null);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: " + e.getMessage());
        }
    }
}
