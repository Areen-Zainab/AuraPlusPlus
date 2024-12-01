package controller.manager;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.ProjectManager;
import com.example.projecthr.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import utility.Factory;
import utility.user.UserServices;

import java.io.IOException;
import java.time.LocalDate;

public class PMProfileController {
    User manager;

    @FXML private Label nameLabel, emailLabel, depLabel;
    @FXML private TextField fnameTextField;
    @FXML private TextField lnameTextField;
    @FXML private TextField emailTextField, phoneNumTextField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> numCodeBox;
    @FXML private ImageView editPFP;

    @FXML
    public void initialize() {
        manager = Factory.getFactory().getMainApp().getUser();
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

        loadProfile();
    }

    @FXML protected void onLogoutButtonClick() {
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
    @FXML protected void onProposalButtonClick() {
        ProjectApplication.switchScene("/manager/PMProposal.fxml");
    }
    @FXML protected void onMeetingButtonClick() {
        ProjectApplication.switchScene("/manager/manageMeetings.fxml");
    }
    @FXML protected void onUpdateButtonClick() {
        System.out.println("Update Button clicked");
    }
    @FXML protected void onDashboardButtonClick() throws IOException {
        System.out.println("Dashboard Button clicked");
        ProjectApplication.switchScene("/manager/PMDashboard.fxml");
    }
    @FXML public void onWorkcalendarButtonClick(){}
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

    @FXML public void onEditPFPClick(){
        System.out.println("Edit PFP clicked");
    }
    @FXML public void cancelChanges() {
        loadProfile(); // Reload the profile details from the database
    }

    @FXML public void saveProfile(){
        String fname = fnameTextField.getText();
        String lname = lnameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordField.getText();
        String phoneNum = phoneNumTextField.getText();
        String gender = genderComboBox.getSelectionModel().getSelectedItem();
        String address = addressField.getText();
        LocalDate dob = dobPicker.getValue();

        // Validate fields
        if (fname.isBlank() || lname.isBlank() || email.isBlank() || password.isBlank() || address.isBlank() || phoneNum.isBlank() || gender.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Update Error", "All fields must be filled!");
            return;
        }
        if(dob == null || dob.isAfter(LocalDate.now())){
            showAlert(Alert.AlertType.ERROR, "Update Error", "Please enter a valid birth-date!");
            return;
        }

        if(UserServices.isEmailValid(email)){
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Email is invalid.");
            return;
        }

        if (!UserServices.isNewEmailUnique(email, manager.getUserId())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email address.");
            return;
        }

        manager.setFirstName(fname);
        manager.setLastName(lname);
        manager.setEmail(email);
        manager.setPassword(password);
        manager.setDob(String.valueOf(dob));
        manager.setGender(gender);
        manager.setAddress(address);
        manager.setPhoneNo(phoneNum);

        boolean success = manager.saveUserProfile();
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile.");
        }
    }

    @FXML public void addSkill(){
    }

    public void loadProfile() {
        nameLabel.setText(manager.getFirstName() + " " + manager.getLastName());
        emailLabel.setText(manager.getEmail()); emailTextField.setDisable(true);
        depLabel.setText(((ProjectManager)manager).getDepartment() + " Department, Years: " + ((ProjectManager)manager).getExperienceYears());
        fnameTextField.setText(manager.getFirstName());
        lnameTextField.setText(manager.getLastName());
        emailTextField.setText(manager.getEmail());
        phoneNumTextField.setText(manager.getPhoneNo());
        passwordField.setText(manager.getPassword());
        addressField.setText(manager.getAddress());
        genderComboBox.getItems().addAll("Female", "Male", "Other");
        genderComboBox.setValue(manager.getGender());
        dobPicker.setValue(manager.getDob());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        ProjectApplication.showAlert(alertType, title, message);
    }
}
