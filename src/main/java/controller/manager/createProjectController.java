package controller.manager;

import com.example.projecthr.ProjectApplication;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class createProjectController {
    @FXML
    private ComboBox<String> numCodeBox;
    private boolean changed = false;

    @FXML private TextField mNameTextField;
    @FXML private TextField tNameTextField;
    @FXML private TextField taskNoTextField;
    @FXML private DatePicker dobPicker;
    @FXML private DatePicker dobPicker1;
    @FXML private ComboBox<String> numCodeBox1;
    @FXML private ComboBox<String> numCodeBox11;
    @FXML private TextField taskDescriptionField;
    @FXML
    public void initialize()
    {
        numCodeBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

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


    @FXML public void onWorkcalendarButtonClick(){}
    @FXML
    public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource(); // Get the source of the event
            button.setStyle("-fx-background-color:  rgba(88,109,128,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onOtherMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color:   rgba(23,39,52,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color: rgba(10, 10, 10, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color: rgba(68,85,101,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    protected void onDashboardButtonClick() throws IOException {
        System.out.println("Dashboard Button clicked");
        ProjectApplication.switchScene("/manager/PMDashboard.fxml");
    }


    @FXML
    public void cancelChanges() {
        loadProfile(); // Reload the profile details from the database
    }
    public void loadProfile() {

    }
    @FXML
    public void saveProfile(){
        String fname = mNameTextField.getText();
        String lname = tNameTextField.getText();
        String email = taskNoTextField.getText();
        String dead1 = dobPicker.toString();
        String dead2 = dobPicker1.toString();
        String combo = numCodeBox.getSelectionModel().getSelectedItem();
        String combo1 = numCodeBox.getSelectionModel().getSelectedItem();
        String address = taskDescriptionField.getText();

        // Validate fields
        if (fname.isBlank() || lname.isBlank() || email.isBlank() || address.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Fields cannot be blank.");
            return;
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
