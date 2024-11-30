package controller.manager;

import com.example.projecthr.Client;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.project.ProjectProposal;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utility.Factory;
import utility.ProjectUtility;
import java.io.IOException;
import static com.example.projecthr.ProjectApplication.*;

public class proposalDetailsController {
    ProjectProposal proj;
    Client client;

    @FXML private Label projName, descripLabel, budget, duration, attachment, infoBox, statusLab;
    @FXML private Button download, view, accept, reject, sendCommentButton;
    @FXML private TextField commentBox;

    @FXML
    protected void onLogoutButtonClick() {
        System.out.println("Logout Button clicked");
        ProjectApplication.switchScene("LoginForm.fxml");
    }

    @FXML
    protected void onProfileButtonClick() throws IOException {
        System.out.println("Profile Button clicked");
        ProjectApplication.switchScene("/manager/PMProfile.fxml");
    }
    @FXML
    protected void onProjectButtonClick()throws IOException {
        System.out.println("Project Button clicked");
        ProjectApplication.switchScene("/manager/PMProjects.fxml");
    }

    @FXML
    protected void onProposalButtonClick() throws IOException {
        System.out.println("Create Project Button clicked");
        ProjectApplication.switchScene("/manager/PMProposal.fxml");
    }

    @FXML protected void onMeetingButtonClick() {
        ProjectApplication.switchScene("/manager/manageMeetings.fxml");
    }


    @FXML
    protected void onWorkCalendarButtonClick() {
        System.out.println("Update Button clicked");
    }

    @FXML
    protected void onDashboardButtonClick() throws IOException {
        System.out.println("Dashboard Button clicked");
        ProjectApplication.switchScene("/manager/PMDashboard.fxml");
    }

    @FXML
    public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(30,60,100,0.5); -fx-text-fill: white;  -fx-background-radius: 5;");
        }
    }

    @FXML
    public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(41,59,73,255); -fx-text-fill: white;  -fx-background-radius: 5;");
        }
    }

    @FXML
    public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(60,60,100,0.5);; -fx-text-fill: white;  -fx-background-radius: 5;");
        }
    }

    @FXML
    public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  white; -fx-text-fill: black;  -fx-background-radius: 5;");
        }
    }

    @FXML
    public void onAcceptButtonClick() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Project Acceptance");
        confirmationAlert.setHeaderText("Are you sure?");

        Text content = new Text("Please confirm your acceptance. An accepted proposal is a responsibility that cannot be reversed. Please read the company proposal terms and conditions before accepting!");
        content.setWrappingWidth(300);
        confirmationAlert.getDialogPane().setContent(content);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                proj.setStatus("Approved");
                proj.setComment(commentBox.getText());
                int manager = Factory.getFactory().getMainApp().getUser().getUserId();
                proj.setManager_id(manager);
                if(Factory.getProjectServices().updateProposal(proj)){
                    showAlert(Alert.AlertType.INFORMATION, "Project Approved!", "The Project Proposal, " + proj.getTitle()  + ", has been accepted. The client has been informed. Click okay to begin project management.");
                    closeform();
                    ProjectApplication.switchScene("/manager/createProject.fxml");
                }
                else{
                    showAlert(Alert.AlertType.ERROR, "Project Proposal Approval Failed!", "An unexpected error occurred. Please try again later.");
                }
            }
        });
    }

    private void closeform() {
        ((Stage) projName.getScene().getWindow()).close();
    }

    @FXML
    public void onRejectButtonClick() {
        if(commentBox.getText().isBlank()){
            showAlert(Alert.AlertType.ERROR, "Add Comments!", "Inform the client why you are rejecting their proposal!");
        }
        else{
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Rejection");
            confirmationAlert.setHeaderText("Are you sure?");
            confirmationAlert.setContentText("Confirm your project rejection. A rejected proposal cannot be reversed!");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    proj.setStatus("Rejected");
                    proj.setComment(commentBox.getText());
                    int manager = Factory.getFactory().getMainApp().getUser().getUserId();
                    proj.setManager_id(manager);
                    if(Factory.getProjectServices().updateProposal(proj)){
                        showAlert(Alert.AlertType.INFORMATION, "Project Rejected!", "The proposal has been rejected and the client has been informed.");
                        setUp();
                    }
                    else{
                        showAlert(Alert.AlertType.ERROR, "Project Rejection Failed!", "An unexpected error occurred. Please try again later.");
                    }
                }
            });
        }
    }

    @FXML
    public void onSendButtonClick() {
        System.out.println("Accept Button clicked");
    }

    @FXML
    public void onDownloadButtonClick() {
        downloadFile(proj.getPdfPath());
    }

    @FXML
    public void onViewButtonClick() {
        viewAttachment(proj.getPdfPath());
    }

    public void setProposal(ProjectProposal project) {
        this.proj = project;
        setUp();
    }

    private void setUp(){
        projName.setText(proj.getTitle());
        descripLabel.setText(proj.getDescription());
        budget.setText("Budget: " + proj.getBudget());
        duration.setText("Duration: " + proj.getDuration());
        if(proj.getPdfPath() == null || proj.getPdfPath().isBlank()) {
            attachment.setDisable(true);
            download.setDisable(true);
            view.setDisable(true);
        }
        else {
            String filename = ProjectUtility.extractFileName(proj.getPdfPath());
            if(filename != null) {
                attachment.setText(filename);
            }
            else{
                attachment.setDisable(true);
                download.setDisable(true);
                view.setDisable(true);
            }
        }
        if(proj.getStatus().equals("Approved") || proj.getStatus().equals("Rejected")) {
            accept.setDisable(true);
            reject.setDisable(true);
            commentBox.setDisable(true);
            sendCommentButton.setDisable(true);
        }
        if(proj.getClient_id() > 0){
            client = new Client(proj.getClient_id());
            infoBox.setText("Name: " + client.getFirstName() + " " + client.getLastName() + "\n\n" + "Company: " + (client.isIndependent() ? "Independence Client" : client.getCompanyName()) + "\n\nAddress: " + client.getAddress() + "\n\nContact No: " + client.getPhoneNo());
        }

        statusLab.setText(proj.getStatus());
        switch (proj.getStatus()) {
            case "Approved" -> statusLab.setStyle("-fx-text-fill: #44FF88; -fx-font-size: 16; -fx-font-weight: bold");
            case "Rejected" -> statusLab.setStyle("-fx-text-fill: maroon; -fx-font-size: 16; -fx-font-weight: bold");
            case "Pending" -> statusLab.setStyle("-fx-text-fill: orange; -fx-font-size: 16; -fx-font-weight: bold");
        }
    }
}


