package controller;

import com.example.projecthr.project.ProjectProposal;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import utility.Factory;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.User;
import utility.user.UserServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Time;

public class ProjectController {

    @FXML private PasswordField passwordField;

    @FXML private TextField passTextField;

    @FXML private TextField emailTextField;

    @FXML private CheckBox showPassCheckBox;

    @FXML private TextField nameTextField;

    @FXML private TextField companyTextField;

    @FXML private CheckBox independentCheckBox;

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
            boolean success = Factory.getDb().registerClient(firstName, lastName, email, password, companyName, independent);
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

    public static Time convertToTime(String hour, String minute, String amPm) {
        return getTime(hour, minute, amPm);
    }

    public static Time getTime(String hour, String minute, String amPm) {
        int hourInt = Integer.parseInt(hour);
        if (amPm.equals("PM") && hourInt != 12) hourInt += 12;
        if (amPm.equals("AM") && hourInt == 12) hourInt = 0;
        return Time.valueOf(hourInt + ":" + minute + ":00");
    }

    public static Button createButton(String text, String color){
        Button button = new Button(text);
        if(color.equals("green")) {
            button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,200,0,0.3)");
            button.setOnMouseEntered(_ -> button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,200,0,0.6)"));
            button.setOnMouseExited(_ -> button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,200,0,0.3)"));
        }
        else if(color.equals("red")) {
            button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.3)");
            button.setOnMouseEntered(_->button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.6)"));
            button.setOnMouseExited(_ -> button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: rgba(200,0,0,0.3)"));
        }
        else {
            button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: " + color + ";");
            button.setOnMouseEntered(_ -> button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: " + color.replace("0.3", "0.6") + ";"));
            button.setOnMouseExited(_ -> button.setStyle("-fx-background-radius: 6; -fx-text-fill: white; -fx-background-color: " + color + ";"));
        }
        button.setPrefSize(150, 30);
        return button;
    }

    public static HBox GetProposalBox(ProjectProposal proposal, HBox proposalPanel, Label status2) {
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
        if(proposal.getPdfPath().isEmpty() || !ProjectApplication.isValidFilePath(proposal.getPdfPath())) {
            openPdfButton.setDisable(true);
        }
        else {
            openPdfButton.setOnAction(_ -> ProjectApplication.viewAttachment(proposal.getPdfPath()));
        }

        VBox controls = new VBox(5);
        controls.setPadding(new Insets(10, 10, 10, 30)); // (top, right, bottom, left)
        proposalPanel.getChildren().addAll(infoBox, controls);
        controls.getChildren().addAll(openPdfButton, status2);
        return proposalPanel;
    }

}