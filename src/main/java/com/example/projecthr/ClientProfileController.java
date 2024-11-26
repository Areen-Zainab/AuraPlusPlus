package com.example.projecthr;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ClientProfileController {
    User currentClient;

    @FXML private Label namelabel1, emaillabel1;
    @FXML private TextField phoneNumTextField;
    @FXML private TextField fnameTextField;
    @FXML private TextField lnameTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField addressField;
    @FXML private CheckBox independentCheck, companyCheck;
    @FXML private TextField companyNameField;
    @FXML private CheckBox showPasswordCheckBox;  // CheckBox to toggle visibility

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
        independentCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                companyCheck.setSelected(false);
            }
        });

        companyCheck.selectedProperty().addListener((observable, _, newValue) -> {
            if (newValue) {
                independentCheck.setSelected(false);
            }
        });
        genderComboBox.getItems().addAll("Female", "Male", "Other");
        loadProfile();
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
        if(!currentClient.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else{
            ProjectApplication.switchScene("ClientProject.fxml", 1000, 600);
        }
    }

    @FXML
    protected void onProposalButtonClick() {
        if(!currentClient.setUpComplete()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please, finish setting up your profile first!");
        }
        else {
            ProjectApplication.switchScene("ClientProposals.fxml", 1000, 600);
        }
    }

    @FXML
    protected void onMeetingButtonClick() {
        if(!currentClient.setUpComplete()){
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
            button.setStyle("-fx-background-color: rgba(80, 45, 20, 0.6); -fx-text-fill: white;  -fx-background-radius: 20;");
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
        String phoneNo = phoneNumTextField.getText();
        boolean independent = independentCheck.isSelected();
        String companyName = independent ? null : companyNameField.getText();

        // Validate fields
        if (fname.isBlank() || lname.isBlank() || email.isBlank() || password.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First Name, Last Name, Email, and Password cannot be blank.");
            return;
        }

        if(UserServices.isEmailValid(email)){
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Email is invalid.");
            return;
        }

        if (!UserServices.isNewEmailUnique(email, currentClient.getUserId())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email address.");
            return;
        }

        // Save changes
        currentClient.setFirstName(fname);
        currentClient.setLastName(lname);
        currentClient.setEmail(email);
        currentClient.setPassword(password);
        currentClient.setGender(gender);
        //currentClient.setDob(String.valueOf(dobPicker.getValue()));
        currentClient.setPhoneNo(phoneNo);
        currentClient.setAddress(address);
        ((Client)currentClient).setCompanyName(companyName);
        ((Client)currentClient).setIndependent(independent);

        boolean success = currentClient.saveUserProfile();
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile.");
        }
    }

    public void loadProfile() {
        setCurrentUser();
        namelabel1.setText(currentClient.getFirstName());
        emaillabel1.setText(currentClient.getEmail());
        fnameTextField.setText(currentClient.getFirstName());
        lnameTextField.setText(currentClient.getLastName());
        emailTextField.setText(currentClient.getEmail());
        passwordField.setText(currentClient.getPassword());
        addressField.setText(currentClient.getAddress());
        phoneNumTextField.setText(currentClient.getPhoneNo());

        dobPicker.setValue(currentClient.getDob());
        genderComboBox.setValue(currentClient.getGender());
        if (((Client)currentClient).isIndependent()) {
            independentCheck.setSelected(true);
            companyCheck.setSelected(false);
        } else {
            independentCheck.setSelected(false);
            companyCheck.setSelected(true);
            if (((Client)currentClient).getCompanyName() != null) {
                companyNameField.setText(((Client)currentClient).getCompanyName());
            } else {
                companyNameField.setText("");
            }
        }

        handleCompanyCheck();
        handleIndependentCheck();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        ProjectApplication.showAlert(alertType, title, message);
    }

    private void setCurrentUser(){
        currentClient = Factory.getFactory().getMainApp().getUser();
    }

    @FXML
    private void handleIndependentCheck() {
        companyNameField.setDisable(independentCheck.isSelected());
        companyCheck.setSelected(!independentCheck.isSelected());
        companyNameField.setText(!independentCheck.isSelected()? companyNameField.getText() : "");
    }

    @FXML
    private void handleCompanyCheck() {
        independentCheck.setSelected(!companyCheck.isSelected());
        companyNameField.setDisable(independentCheck.isSelected());
    }
}
