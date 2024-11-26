package com.example.projecthr;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProjectController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private CheckBox showPassCheckBox;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField companyTextField;

    @FXML
    private CheckBox independentCheckBox;

    @FXML
    protected void onSignUpPageButtonClick() {
        ProjectApplication.switchScene("SignupForm.fxml");
    }

    @FXML
    protected void onLoginPageButtonClick() {
        ProjectApplication.switchScene("LoginForm.fxml");
    }

    @FXML
    protected void onLoginButtonClick() {
        if (emailTextField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Email and Password cannot be empty.");
        } else {
            System.out.println("Login Button clicked");
            ProjectApplication mainApp = getMainApp();
            User user = mainApp.login(emailTextField.getText(), passwordField.getText());
            mainApp.setUser(user);
            try{
                user.loadDashboard();
            }
            catch(Exception e){
                showAlert(Alert.AlertType.ERROR, "Login Failed", e.getMessage());
            }
        }
    }

    @FXML
    protected void onSignUpButtonClick() {
        System.out.println("SignUp Button clicked");
        setPasswordText();
        String email = emailTextField.getText();
        String fullName = nameTextField.getText();
        String password = passwordField.getText();
        String companyName = companyTextField.getText();
        boolean independent = independentCheckBox.isSelected();

        if (email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            passTextField.setText(""); passTextField.setText("");
            showAlert(Alert.AlertType.ERROR, "SignUp Error", "Please fill all the required fields.");
            return;
        }

        if(UserServices.isEmailValid(email)) {
            passTextField.setText(""); passTextField.setText("");
            showAlert(Alert.AlertType.ERROR, "SignUp Error", "Use a valid email address.");
            return;
        }

        if(!UserServices.isEmailUnique(email)){
            passTextField.setText(""); passTextField.setText("");
            showAlert(Alert.AlertType.ERROR, "SignUp Error", "Email already used.");
            return;
        }

        String[] nameParts = fullName.split(" ", 2);
        if (nameParts.length < 2) {
            showAlert(Alert.AlertType.ERROR, "Error", "Full name must include both first and last names.");
            return;
        }

        if(!independentCheckBox.isSelected() && companyName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Company name cannot be empty.");
            return;
        }

        String firstName = nameParts[0];
        String lastName = nameParts[1];

        try {
            boolean success = Factory.getFactory().getDb().registerClient(firstName, lastName, email, password, companyName, independent);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "You have successfully signed up!");
                User user = getMainApp().login(email, password);
                try{
                    user.loadDashboard();
                }
                catch(Exception e){
                    showAlert(Alert.AlertType.ERROR, "Login Failed", e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Sign Up Error", "An error occurred while signing up. Please try again.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // Method to display alert messages
    private void showAlert(Alert.AlertType type, String title, String message) {
        ProjectApplication.showAlert(type, title, message);
    }

    @FXML
    protected void selectShowPassCheckBox(){
        showPassCheckBox.setSelected(showPassCheckBox.isSelected());
        if (showPassCheckBox.isSelected()) {
            passwordField.setVisible(false);
            passTextField.setVisible(true);
            passTextField.setText(passwordField.getText());
        } else {
            passwordField.setVisible(true);
            passTextField.setVisible(false);
            passwordField.setText(passTextField.getText());
        }
    }

    protected void setPasswordText(){
        showPassCheckBox.setSelected(showPassCheckBox.isSelected());
        if (!showPassCheckBox.isSelected()) {
            passTextField.setText(passwordField.getText());
        } else {
            passwordField.setText(passTextField.getText());
        }
    }

    //ensure only one object instance is used
    public ProjectApplication getMainApp() {
        return Factory.getFactory().getMainApp();
    }

    @FXML
    private void handleIndependentCheck() {
        companyTextField.setDisable(independentCheckBox.isSelected());
        companyTextField.setText(!independentCheckBox.isSelected()? companyTextField.getText() : "");
    }
}