package com.example.myjavafxapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage; // Static reference to the primary stage

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Assign the stage to the static variable
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("PMProjects.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    // Switch scene method
    public static void switchScene(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 600); // Adjust dimensions if needed
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}