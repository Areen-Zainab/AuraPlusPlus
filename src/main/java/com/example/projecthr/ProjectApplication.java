package com.example.projecthr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.File;
import java.io.IOException;

//primarily acts as a utility class
public class ProjectApplication extends Application {
    static Stage stage;
    private User currentUser;

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage(){
        if (stage == null) {
            stage = new Stage();
        }
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        //ProjectApplication.switchScene("hello-view.fxml", 950, 550);
        ProjectApplication mainApp = Factory.getFactory().getMainApp();
        User user = login("aounjee@company.com", "jeejee");
        mainApp.setUser(user);
        user.loadDashboard();
    }

    // Login function to be called from controller
    public User login(String email, String password) {
        DBHandler dbHandler = Factory.getFactory().getDb();
        return dbHandler.loginValidationUser(email, password);
    }

    //static function that handles the switching of scenes
    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectApplication.class.getResource(fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root, 950, 550);
            stage.setTitle("WorkSphere");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getUser() {
        return currentUser;
    }

    //static function that handles the switching of scenes but overloaded
    public static void switchScene(String fxmlFile, int vv, int hh) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectApplication.class.getResource(fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root, vv, hh);
            stage.setTitle("WorkSphere");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //handles user object creation
    static public User createUserFromRole(int userId, String userEmail, String role) {
        return switch (role) {
            case "Client" -> new Client(userId, userEmail);
            case "Employee" -> new Employee(userId, userEmail);
            case "ProjectManager" -> new ProjectManager(userId, userEmail);
            case "Admin" -> new HRAdmin(userId, userEmail);
            default -> null;
        };
    }

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void viewAttachment(String pdfPath) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            File pdfFile = new File(pdfPath);

            if (pdfFile.exists()) {
                try {
                    desktop.open(pdfFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "PDF file not found!");
            }
        } else {
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Desktop is not supported!");
        }
    }

    public static String getAttachment(Meeting meeting, Window parentWindow) {
        if (meeting == null || parentWindow == null) {
            throw new IllegalArgumentException("Meeting and parentWindow cannot be null.");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File selectedFile = fileChooser.showOpenDialog(parentWindow);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

}