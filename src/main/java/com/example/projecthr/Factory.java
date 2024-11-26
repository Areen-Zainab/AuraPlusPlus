package com.example.projecthr;

public class Factory {
    private DBHandler db;
    private static UserServices userServices;
    private static ClientServices clientServices;
    private ProjectApplication mainApp;

    //factory is a singleton - only one object created
    private static Factory factory;
    private Factory() {}

    public static Factory getFactory(){
        if(factory == null) {
            factory = new Factory();
        }
        return factory;
    }

    public DBHandler getDb() {
        if (db == null) {
            db = new DBHandler();
        }
        return db;
    }

    public static UserServices getUserServices() {
        if (userServices == null) {
            userServices = new UserServices();
        }
        return userServices;
    }

    public static ClientServices getClientServices() {
        if (clientServices == null) {
            clientServices = new ClientServices();
        }
        return clientServices;
    }

    public ProjectApplication getMainApp() {
        if (mainApp == null) {
            mainApp = new ProjectApplication();
        }
        return mainApp;
    }
}
