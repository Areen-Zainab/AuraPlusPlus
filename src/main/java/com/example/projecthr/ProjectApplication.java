package com.example.projecthr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import utility.DBHandler;
import utility.Factory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


//Project Manager:
/*
1. create project
2. add milestone
3. add task
4. edit upar wali cheezain
6. workcalender
7. skills
 */


//primarily acts as a utility class
public class ProjectApplication extends Application {
    static Stage stage;
    private User currentUser;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        //ProjectApplication.switchScene("hello-view.fxml");
        ProjectApplication mainApp = Factory.getFactory().getMainApp();

        //client credentials
        //User user = login("aounjee@company.com", "jeejee");

        //manager credentials
        User user = login("hafsa@gmail.com", "hafsa7076");
        mainApp.setUser(user);
        user.loadDashboard();
    }

    // Login function to be called from controller
    public User login(String email, String password) {
        DBHandler dbHandler = Factory.getDb();
        return dbHandler.loginValidationUser(email, password);
    }

    public void setUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getUser() {
        return currentUser;
    }

    //static function that handles the switching of scenes
    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectApplication.class.getResource(fxmlFile));
            Parent root = loader.load();

            Scene scene;
            if(fxmlFile.equals("hello-view") || fxmlFile.equals("LoginForm") || fxmlFile.equals("SignupForm")){
                scene = new Scene(root, 950, 550);
            }
            else scene = new Scene(root, 1000, 600);
            stage.setTitle("WorkSphere");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: " + e.getMessage());
        }
    }

    //handles user object creation
    static public User createUserFromRole(int userId, String userEmail, String role) {
        return switch (role) {
            case "Client" -> new Client(userId, userEmail);
            case "Employee" -> new Employee(userId, userEmail);
            case "Project Manager" -> new ProjectManager(userId, userEmail);
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
                    ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error: " + e.getMessage());
                }
            } else {
                ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "PDF file not found!");
            }
        } else {
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Error", "Desktop is not supported!");
        }
    }

    public static String getAttachment(Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        try {
            return fileChooser.showOpenDialog(parentWindow).getAbsolutePath();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidFilePath(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) || Files.isReadable(path);

        } catch (Exception e) {
            return false;
        }
    }

    public static void downloadFile(String pdfPath) {
        try {
            Path sourcePath = Path.of(pdfPath);
            String fileName = sourcePath.getFileName().toString();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName(fileName); // Default file name
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile != null) {
                Files.copy(sourcePath, selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File downloaded successfully to: " + selectedFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error while downloading the file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

}