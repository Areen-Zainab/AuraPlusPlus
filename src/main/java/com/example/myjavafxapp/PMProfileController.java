package com.example.myjavafxapp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PMProfileController {
    private boolean changed = false;

    @FXML private TextField fnameTextField;
    @FXML private TextField lnameTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField addressField;

    @FXML
    private ComboBox<String> numCodeBox;

    @FXML
    private ImageView editPFP;

    @FXML
    public void initialize() {
        if (numCodeBox != null) {
            String[] countryCodes = {
                    "+92",   //Pakistan
                    "+1",    // USA/Canada
                    "+44",   // United Kingdom
                    "+91",   // India
                    "+86",   // China
                    "+81",   // Japan
                    "+61",   // Australia
                    "+49",   // Germany
                    "+33",   // France
                    "+55",   // Brazil
                    "+34",   // Spain
                    "+39",   // Italy
                    "+7",    // Russia
                    "+31",   // Netherlands
                    "+46",   // Sweden
                    "+64"    // New Zealand
            };
            numCodeBox.getItems().addAll(countryCodes);
            numCodeBox.getSelectionModel().select(0);
        }

        genderComboBox.getItems().addAll("Female", "Male", "Other");
    }

    @FXML
    protected void onLogoutButtonClick() {
        System.out.println("Logout Button clicked");
        //ProjectApplication.switchScene("LoginForm.fxml");
    }

    @FXML
    protected void onProfileButtonClick() throws IOException {
        System.out.println("Profile Button clicked");
        HelloApplication.switchScene("PMProfile.fxml");
    }

    @FXML
    protected void onProjectButtonClick()throws IOException {
        System.out.println("Project Button clicked");
        HelloApplication.switchScene("PMProjects.fxml");
    }

    @FXML
    protected void onProposalButtonClick() {
        System.out.println("Create Project Button clicked");
        //ProjectApplication.switchScene("ClientProposals.fxml", 1000, 600);
    }

    @FXML
    protected void onMeetingButtonClick() {
        System.out.println("Meeting Button clicked");
    }

    @FXML
    protected void onUpdateButtonClick() {
        System.out.println("Update Button clicked");
    }

    @FXML
    protected void onDashboardButtonClick() throws IOException {
        System.out.println("Dashboard Button clicked");
       HelloApplication.switchScene("PMDashboard.fxml");
    }

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
    public void onEditPFPClick(){
        System.out.println("Edit PFP clicked");
    }

    @FXML
    public void cancelChanges() {
        loadProfile(); // Reload the profile details from the database
    }

    @FXML
    public void saveProfile(){
        String fname = fnameTextField.getText();
        String lname = lnameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordField.getText();
        String gender = genderComboBox.getSelectionModel().getSelectedItem();
        String address = addressField.getText();

        // Validate fields
        if (fname.isBlank() || lname.isBlank() || email.isBlank() || password.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First Name, Last Name, Email, and Password cannot be blank.");
            return;
        }

    }

    public void loadProfile() {

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
