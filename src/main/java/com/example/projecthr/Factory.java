package com.example.projecthr;

public class Factory {
    private DBHandler db;
    private static UserServices userServices;
    private static ClientServices clientServices;
    private static ProjectUtility projectUtility;
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
        return UserServices.getInstance();
    }

    public static ClientServices getClientServices() {
        return ClientServices.getInstance();
    }

    public ProjectApplication getMainApp() {
        if (mainApp == null) {
            mainApp = new ProjectApplication();
        }
        return mainApp;
    }

    public ProjectUtility getProjectServices() {
        return ProjectUtility.getInstance();
    }

    public ManagerServices getManagerServices() {
        return ManagerServices.getInstance();
    }
}
