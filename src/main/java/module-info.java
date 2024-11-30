module com.example.projecthr {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens com.example.projecthr to javafx.fxml;
    exports com.example.projecthr;
    exports controller;
    opens controller to javafx.fxml;
    exports controller.manager;
    opens controller.manager to javafx.fxml;
    exports controller.client;
    opens controller.client to javafx.fxml;
    exports controller.employee;
    opens controller.employee to javafx.fxml;
    exports com.example.projecthr.project;
    opens com.example.projecthr.project to javafx.fxml;
    exports utility;
    opens utility to javafx.fxml;
    exports utility.user;
    opens utility.user to javafx.fxml;
}